package com.example.data.repository

import com.example.data.api.AgnesAiClient
import com.example.data.api.AgnesMessage
import com.example.data.api.ChatCompletionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgnesAiRepository {
    private val service = AgnesAiClient.service
    private val authHeader: String
        get() = "Bearer ${AgnesAiClient.apiKey}"

    private val baseSystemPrompt = """
        Tu es AGRI-IA, l'assistant agricole intelligent expert pour le Burkina Faso et l'Afrique de l'Ouest.
        Ton rôle est d'accompagner les producteurs agricoles et éleveurs avec des conseils agronomiques précis, pratiques et adaptés aux réalités locales :
        - Zones agroécologiques sahéliennes et soudaniennes (Ouagadougou, Bobo-Dioulasso, Koudougou, Fada, Dédougou, Dori, etc.).
        - Sols sahéliens (sols ferrugineux tropicaux, sols gravillonnaires, sols sablonneux, vertisols).
        - Climat : alternance saison sèche et saison des pluies (hivernage).
        - Pratiques recommandées : techniques de conservation des eaux et des sols (zaï, cordons pierreux, demi-lunes), fertilisation équilibrée (fumier organique/compost, NPK, Urée), rotation des cultures (céréales-légumineuses), lutte intégrée contre les ravageurs (chenilles légionnaires, pucerons, flétrissement).
        - Élevage adapté : volailles locales améliorées, petits ruminants (moutons du Sahel, chèvres rousses de Maradi), embouche bovine, calendrier vaccinal.
        - Respecte les traditions linguistiques : si l'utilisateur demande du Mooré, Dioula ou Fulfuldé, fournis les termes clés correspondants.
        Sois structuré, bienveillant, clair, et cite toujours les précautions de sécurité sanitaire ou environnementale.
    """.trimIndent()

    suspend fun sendChatMessage(
        conversationHistory: List<AgnesMessage>,
        userMessage: String,
        userContext: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemContent = buildString {
                append(baseSystemPrompt)
                if (!userContext.isNullOrBlank()) {
                    append("\n\nContexte de l'exploitation de l'agriculteur :\n")
                    append(userContext)
                }
            }

            val messages = mutableListOf<AgnesMessage>()
            messages.add(AgnesMessage(role = "system", content = systemContent))
            // Include up to last 10 messages for context
            messages.addAll(conversationHistory.takeLast(10))
            messages.add(AgnesMessage(role = "user", content = userMessage))

            val request = ChatCompletionRequest(
                model = "agnes-3.0-flash",
                messages = messages,
                temperature = 0.7,
                maxTokens = 1500
            )

            val response = service.getChatCompletion(authHeader, request)
            if (response.isSuccessful) {
                val body = response.body()
                val content = body?.choices?.firstOrNull()?.message?.content
                if (!content.isNullOrBlank()) {
                    Result.success(content)
                } else {
                    Result.failure(Exception("Réponse vide reçue de l'IA."))
                }
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Erreur HTTP ${response.code()}"
                Result.failure(Exception("Erreur de l'assistant IA: $errorMsg"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun diagnoseSymptom(
        crop: String,
        affectedPart: String,
        symptoms: String,
        language: String = "fr"
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = """
            [DIAGNOSTIC AGRONOMIQUE AGRI-VISION]
            Culture : $crop
            Partie affectée : $affectedPart
            Symptômes observés par le producteur : $symptoms
            Langue demandée : $language

            Fournis une analyse agronomique structurée contenant impérativement :
            1. 🔍 Hypothèse principale de la cause (maladie fongique, bactérienne, virus, carence minérale ou ravageur).
            2. 📊 Niveau de confiance estimé (en pourcentage et explication).
            3. 💡 Solutions curatives immédiates (remèdes bio/naturels d'abord, produits homologués si nécessaire).
            4. 🛡️ Mesures de prévention pour la suite.
            5. ⚠️ Précautions sanitaires et environnementales.
        """.trimIndent()

        sendChatMessage(emptyList(), prompt)
    }
}
