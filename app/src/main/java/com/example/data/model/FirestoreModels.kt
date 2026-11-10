package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val preferredLanguage: String = "fr", // fr, mos (Mooré), dyu (Dioula), ful (Fulfuldé), en
    val experienceLevel: String = "Intermédiaire", // Débutant, Intermédiaire, Expérimenté, Professionnel
    val region: String = "Centre (Ouagadougou)",
    val communeOrVillage: String = "",
    val farmObjective: String = "Augmenter le rendement et réduire les coûts",
    val primaryActivities: List<String> = emptyList(),
    val themeMode: String = "system", // system, light, dark
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FarmPlot(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val areaHectares: Double = 1.0,
    val cropName: String = "",
    val variety: String = "",
    val soilType: String = "Sol ferrugineux tropical",
    val plantingDate: String = "",
    val targetYield: String = "",
    val status: String = "En croissance", // Préparation, Semis, Croissance, Floraison, Récolte, Repos
    val irrigationType: String = "Pluvial", // Pluvial, Goutte-à-goutte, Gravitaire, Manuel
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FarmIntervention(
    @DocumentId val id: String = "",
    val userId: String = "",
    val plotId: String = "",
    val plotName: String = "",
    val type: String = "Labour", // Labour, Semis, Sarclage/Désherbage, Fertilisation, Arrosage, Traitement, Récolte, Vente
    val date: String = "",
    val quantity: String = "",
    val costCfa: Long = 0L,
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LivestockGroup(
    @DocumentId val id: String = "",
    val userId: String = "",
    val species: String = "Poulets locaux", // Poulets locaux, Poulets de chair, Pondeuses, Bovins, Ovins, Caprins
    val breed: String = "",
    val count: Long = 0L,
    val housingType: String = "Poulailler traditionnel amélioré",
    val healthNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class DiagnosisRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val cropName: String = "",
    val partAffected: String = "",
    val symptoms: String = "",
    val diagnosisResult: String = "",
    val confidence: Long = 85L,
    val recommendations: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val role: String = "user", // user, assistant
    val content: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)
