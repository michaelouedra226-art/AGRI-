package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AgnesMessage
import com.example.data.model.AgronomicData
import com.example.data.model.DiagnosisRecord
import com.example.data.model.FarmIntervention
import com.example.data.model.FarmPlot
import com.example.data.model.LivestockGroup
import com.example.data.model.UserProfile
import com.example.data.repository.AgnesAiRepository
import com.example.data.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface UiMessage {
    val id: String
    val role: String
    val content: String
    val timestamp: Long
}

data class ChatMessageItem(
    override val id: String = java.util.UUID.randomUUID().toString(),
    override val role: String,
    override val content: String,
    override val timestamp: Long = System.currentTimeMillis()
) : UiMessage

data class AdvisorUiState(
    val messages: List<ChatMessageItem> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null
)

data class VisionUiState(
    val selectedCrop: String = "Maïs",
    val selectedPart: String = "Feuille",
    val symptomsInput: String = "",
    val isAnalyzing: Boolean = false,
    val latestDiagnosis: String? = null,
    val error: String? = null
)

enum class ThemePreference(val code: String, val title: String, val description: String) {
    SYSTEM("system", "Système (Automatique)", "Suit les réglages clair/sombre de votre appareil"),
    LIGHT("light", "Mode Clair", "Interface claire et lumineuse"),
    DARK("dark", "Mode Sombre", "Interface sombre reposante pour les yeux")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val firestoreRepo = FirestoreRepository(application)
    private val agnesRepo = AgnesAiRepository()

    // User Profile
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // Plots
    private val _plots = MutableStateFlow<List<FarmPlot>>(emptyList())
    val plots: StateFlow<List<FarmPlot>> = _plots.asStateFlow()

    // Interventions
    private val _interventions = MutableStateFlow<List<FarmIntervention>>(emptyList())
    val interventions: StateFlow<List<FarmIntervention>> = _interventions.asStateFlow()

    // Livestock
    private val _livestock = MutableStateFlow<List<LivestockGroup>>(emptyList())
    val livestock: StateFlow<List<LivestockGroup>> = _livestock.asStateFlow()

    // Diagnoses History
    private val _diagnoses = MutableStateFlow<List<DiagnosisRecord>>(emptyList())
    val diagnoses: StateFlow<List<DiagnosisRecord>> = _diagnoses.asStateFlow()

    // Advisor State
    private val _advisorState = MutableStateFlow(AdvisorUiState())
    val advisorState: StateFlow<AdvisorUiState> = _advisorState.asStateFlow()

    // Vision State
    private val _visionState = MutableStateFlow(VisionUiState())
    val visionState: StateFlow<VisionUiState> = _visionState.asStateFlow()

    // Selected Language
    private val _currentLanguage = MutableStateFlow("fr")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Selected Theme Preference (Clair / Sombre / Système)
    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    init {
        // Initialize welcome message in Advisor
        val welcomeMsg = ChatMessageItem(
            role = "assistant",
            content = "Ne y bèogo ! Bonjour ! Je suis AGRI-IA, votre assistant agricole pour le Burkina Faso et l'Afrique de l'Ouest.\n\nPosez-moi vos questions sur vos cultures (maïs, mil, sorgho, niébé, tomate, oignon), l'élevage ou la préparation de vos sols."
        )
        _advisorState.value = AdvisorUiState(messages = listOf(welcomeMsg))

        // Start listening to Firestore if user is already authenticated
        if (Firebase.auth.currentUser != null) {
            initUserDataObservers()
        }
    }

    fun onUserAuthenticated() {
        initUserDataObservers()
    }

    private fun initUserDataObservers() {
        val user = Firebase.auth.currentUser ?: return
        viewModelScope.launch {
            firestoreRepo.observeUserProfile().collectLatest { profile ->
                if (profile != null) {
                    _userProfile.value = profile
                    _currentLanguage.value = profile.preferredLanguage
                    _themePreference.value = when (profile.themeMode) {
                        "light" -> ThemePreference.LIGHT
                        "dark" -> ThemePreference.DARK
                        else -> ThemePreference.SYSTEM
                    }
                } else {
                    // Create default profile for user
                    val defaultProfile = UserProfile(
                        userId = user.uid,
                        name = user.displayName ?: "Producteur",
                        email = user.email ?: "",
                        region = "Centre (Ouagadougou)",
                        preferredLanguage = "fr"
                    )
                    firestoreRepo.saveUserProfile(defaultProfile)
                }
            }
        }

        viewModelScope.launch {
            firestoreRepo.observePlots().collectLatest { plotList ->
                _plots.value = plotList
            }
        }

        viewModelScope.launch {
            firestoreRepo.observeInterventions().collectLatest { interventionList ->
                _interventions.value = interventionList
            }
        }

        viewModelScope.launch {
            firestoreRepo.observeLivestock().collectLatest { livestockList ->
                _livestock.value = livestockList
            }
        }

        viewModelScope.launch {
            firestoreRepo.observeDiagnoses().collectLatest { diagList ->
                _diagnoses.value = diagList
            }
        }
    }

    // --- AGRI-ADVISOR ACTIONS ---
    fun sendAdvisorMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessageItem(role = "user", content = text)
        val currentMsgs = _advisorState.value.messages + userMsg
        _advisorState.value = _advisorState.value.copy(
            messages = currentMsgs,
            isGenerating = true,
            error = null
        )

        val apiHistory = currentMsgs.map {
            AgnesMessage(role = it.role, content = it.content)
        }

        val farmContext = buildString {
            val prof = _userProfile.value
            if (prof != null) {
                append("Région: ${prof.region}. Niveau: ${prof.experienceLevel}. ")
            }
            if (_plots.value.isNotEmpty()) {
                append("Parcelles actives: ")
                append(_plots.value.joinToString(", ") { "${it.name} (${it.cropName}, ${it.areaHectares} ha)" })
            }
        }

        viewModelScope.launch {
            val result = agnesRepo.sendChatMessage(
                conversationHistory = apiHistory.dropLast(1),
                userMessage = text,
                userContext = farmContext
            )

            result.fold(
                onSuccess = { answer ->
                    val assistantMsg = ChatMessageItem(role = "assistant", content = answer)
                    _advisorState.value = _advisorState.value.copy(
                        messages = _advisorState.value.messages + assistantMsg,
                        isGenerating = false
                    )
                },
                onFailure = { err ->
                    _advisorState.value = _advisorState.value.copy(
                        isGenerating = false,
                        error = err.message ?: "Impossible de joindre l'assistant IA."
                    )
                }
            )
        }
    }

    fun clearAdvisorChat() {
        val welcomeMsg = ChatMessageItem(
            role = "assistant",
            content = "Nouvelle conversation démarrée. Que souhaitez-vous savoir sur vos cultures ou votre exploitation ?"
        )
        _advisorState.value = AdvisorUiState(messages = listOf(welcomeMsg))
    }

    // --- AGRI-VISION DIAGNOSTIC ---
    fun setVisionCrop(crop: String) {
        _visionState.value = _visionState.value.copy(selectedCrop = crop)
    }

    fun setVisionPart(part: String) {
        _visionState.value = _visionState.value.copy(selectedPart = part)
    }

    fun setVisionSymptoms(symptoms: String) {
        _visionState.value = _visionState.value.copy(symptomsInput = symptoms)
    }

    fun runVisionDiagnosis() {
        val crop = _visionState.value.selectedCrop
        val part = _visionState.value.selectedPart
        val symptoms = _visionState.value.symptomsInput
        if (symptoms.isBlank()) return

        _visionState.value = _visionState.value.copy(isAnalyzing = true, error = null)

        viewModelScope.launch {
            val result = agnesRepo.diagnoseSymptom(crop, part, symptoms, _currentLanguage.value)
            result.fold(
                onSuccess = { diagnosisText ->
                    _visionState.value = _visionState.value.copy(
                        isAnalyzing = false,
                        latestDiagnosis = diagnosisText
                    )
                    // Save to Firestore
                    try {
                        val record = DiagnosisRecord(
                            cropName = crop,
                            partAffected = part,
                            symptoms = symptoms,
                            diagnosisResult = diagnosisText,
                            confidence = 88L
                        )
                        firestoreRepo.saveDiagnosis(record)
                    } catch (e: Exception) {
                        // Keep going if local save fails
                    }
                },
                onFailure = { err ->
                    _visionState.value = _visionState.value.copy(
                        isAnalyzing = false,
                        error = err.message ?: "Erreur lors de l'analyse diagnostique."
                    )
                }
            )
        }
    }

    // --- FARM OPERATIONS ---
    fun addPlot(name: String, crop: String, area: Double, soilType: String, irrigation: String) {
        viewModelScope.launch {
            try {
                val plot = FarmPlot(
                    name = name,
                    cropName = crop,
                    areaHectares = area,
                    soilType = soilType,
                    irrigationType = irrigation,
                    status = "En croissance"
                )
                firestoreRepo.addPlot(plot)
            } catch (e: Exception) {
                // Log or handle
            }
        }
    }

    fun deletePlot(plotId: String) {
        viewModelScope.launch {
            try {
                firestoreRepo.deletePlot(plotId)
            } catch (e: Exception) {
                // Log or handle
            }
        }
    }

    fun addIntervention(plotId: String, plotName: String, type: String, quantity: String, cost: Long, notes: String) {
        viewModelScope.launch {
            try {
                val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                val intervention = FarmIntervention(
                    plotId = plotId,
                    plotName = plotName,
                    type = type,
                    date = dateStr,
                    quantity = quantity,
                    costCfa = cost,
                    notes = notes
                )
                firestoreRepo.addIntervention(intervention)
            } catch (e: Exception) {
                // Log or handle
            }
        }
    }

    fun addLivestock(species: String, breed: String, count: Long, housing: String, notes: String) {
        viewModelScope.launch {
            try {
                val item = LivestockGroup(
                    species = species,
                    breed = breed,
                    count = count,
                    housingType = housing,
                    healthNotes = notes
                )
                firestoreRepo.addLivestock(item)
            } catch (e: Exception) {
                // Log or handle
            }
        }
    }

    fun updateLanguage(langCode: String) {
        _currentLanguage.value = langCode
        val current = _userProfile.value
        if (current != null) {
            viewModelScope.launch {
                firestoreRepo.saveUserProfile(current.copy(preferredLanguage = langCode))
            }
        }
    }

    fun updateRegion(region: String) {
        val current = _userProfile.value
        if (current != null) {
            viewModelScope.launch {
                firestoreRepo.saveUserProfile(current.copy(region = region))
            }
        }
    }

    fun updateFullUserProfile(
        name: String,
        phone: String,
        region: String,
        communeOrVillage: String,
        farmObjective: String,
        experienceLevel: String
    ) {
        val current = _userProfile.value
        val userId = current?.userId ?: Firebase.auth.currentUser?.uid ?: ""
        val email = current?.email ?: Firebase.auth.currentUser?.email ?: ""
        val updated = (current ?: UserProfile(userId = userId, email = email)).copy(
            name = name,
            phone = phone,
            region = region,
            communeOrVillage = communeOrVillage,
            farmObjective = farmObjective,
            experienceLevel = experienceLevel
        )
        viewModelScope.launch {
            try {
                firestoreRepo.saveUserProfile(updated)
            } catch (e: Exception) {
                // Log or handle
            }
        }
    }

    fun updateThemePreference(pref: ThemePreference) {
        _themePreference.value = pref
        val current = _userProfile.value
        if (current != null) {
            viewModelScope.launch {
                try {
                    firestoreRepo.saveUserProfile(current.copy(themeMode = pref.code))
                } catch (e: Exception) {
                    // Log or handle
                }
            }
        }
    }
}
