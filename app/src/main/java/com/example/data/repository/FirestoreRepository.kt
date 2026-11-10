package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.DiagnosisRecord
import com.example.data.model.FarmIntervention
import com.example.data.model.FarmPlot
import com.example.data.model.LivestockGroup
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        getSafeFirestoreInstance(context)
    )

    companion object {
        fun getSafeFirestoreInstance(context: Context): FirebaseFirestore {
            return try {
                val dbId = try {
                    context.applicationContext.getString(R.string.firestore_database_id)
                } catch (e: Exception) {
                    "ai-studio-android-agriia-d01a091e-4bec-4b59-bcf9-0729154c38c4"
                }
                FirebaseFirestore.getInstance(dbId)
            } catch (e: Exception) {
                Log.w("FirestoreRepo", "Repli sur instance Firestore par défaut: ${e.message}")
                FirebaseFirestore.getInstance()
            }
        }
    }

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("L'utilisateur doit être connecté avec Google pour accéder aux données.")
    }

    // --- USER PROFILE ---
    fun observeUserProfile(): Flow<UserProfile?> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).snapshots()
                .map { snapshot -> snapshot.toObject(UserProfile::class.java) }
                .catch { e ->
                    Log.e("FirestoreRepo", "Erreur lecture profil: ${e.message}", e)
                    emit(null)
                }
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "name" to profile.name.ifBlank { auth.currentUser?.displayName ?: "Agriculteur" },
            "email" to profile.email.ifBlank { auth.currentUser?.email ?: "" },
            "phone" to profile.phone,
            "preferredLanguage" to profile.preferredLanguage,
            "experienceLevel" to profile.experienceLevel,
            "region" to profile.region,
            "communeOrVillage" to profile.communeOrVillage,
            "farmObjective" to profile.farmObjective,
            "primaryActivities" to profile.primaryActivities,
            "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Erreur sauvegarde profil: ${e.message}", e)
            throw e
        }
    }

    // --- FARM PLOTS (PARCELLES) ---
    fun observePlots(): Flow<List<FarmPlot>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("plots")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FarmPlot::class.java) }
                .catch { e ->
                    Log.e("FirestoreRepo", "Erreur lecture parcelles: ${e.message}", e)
                    emit(emptyList())
                }
        )
    }

    suspend fun addPlot(plot: FarmPlot) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("plots").document()
        val payload = mapOf(
            "userId" to uid,
            "name" to plot.name,
            "areaHectares" to plot.areaHectares,
            "cropName" to plot.cropName,
            "variety" to plot.variety,
            "soilType" to plot.soilType,
            "plantingDate" to plot.plantingDate,
            "targetYield" to plot.targetYield,
            "status" to plot.status,
            "irrigationType" to plot.irrigationType,
            "notes" to plot.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun deletePlot(plotId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("plots").document(plotId).delete().await()
    }

    // --- INTERVENTIONS (JOURNAL AGRICOLE) ---
    fun observeInterventions(): Flow<List<FarmIntervention>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("interventions")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(FarmIntervention::class.java) }
                .catch { e ->
                    Log.e("FirestoreRepo", "Erreur lecture interventions: ${e.message}", e)
                    emit(emptyList())
                }
        )
    }

    suspend fun addIntervention(intervention: FarmIntervention) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("interventions").document()
        val payload = mapOf(
            "userId" to uid,
            "plotId" to intervention.plotId,
            "plotName" to intervention.plotName,
            "type" to intervention.type,
            "date" to intervention.date,
            "quantity" to intervention.quantity,
            "costCfa" to intervention.costCfa,
            "notes" to intervention.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- LIVESTOCK GROUPS ---
    fun observeLivestock(): Flow<List<LivestockGroup>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("livestock")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(LivestockGroup::class.java) }
                .catch { e ->
                    Log.e("FirestoreRepo", "Erreur lecture élevages: ${e.message}", e)
                    emit(emptyList())
                }
        )
    }

    suspend fun addLivestock(livestock: LivestockGroup) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("livestock").document()
        val payload = mapOf(
            "userId" to uid,
            "species" to livestock.species,
            "breed" to livestock.breed,
            "count" to livestock.count,
            "housingType" to livestock.housingType,
            "healthNotes" to livestock.healthNotes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- DIAGNOSES HISTORY ---
    fun observeDiagnoses(): Flow<List<DiagnosisRecord>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("diagnoses")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(DiagnosisRecord::class.java) }
                .catch { e ->
                    Log.e("FirestoreRepo", "Erreur lecture diagnostics: ${e.message}", e)
                    emit(emptyList())
                }
        )
    }

    suspend fun saveDiagnosis(diagnosis: DiagnosisRecord) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("diagnoses").document()
        val payload = mapOf(
            "userId" to uid,
            "cropName" to diagnosis.cropName,
            "partAffected" to diagnosis.partAffected,
            "symptoms" to diagnosis.symptoms,
            "diagnosisResult" to diagnosis.diagnosisResult,
            "confidence" to diagnosis.confidence,
            "recommendations" to diagnosis.recommendations,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }
}
