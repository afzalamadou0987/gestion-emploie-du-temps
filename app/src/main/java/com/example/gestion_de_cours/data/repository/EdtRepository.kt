package com.example.gestion_de_cours.data.repository

import com.example.gestion_de_cours.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EdtRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getFilieres(): List<Filiere> =
        db.collection("filieres").get().await()
            .documents.mapNotNull { it.toObject(Filiere::class.java)?.copy(id = it.id) }

    suspend fun addFiliere(filiere: Filiere): String =
        db.collection("filieres").add(filiere).await().id

    suspend fun getSalles(): List<Salle> =
        db.collection("salles").get().await()
            .documents.mapNotNull { it.toObject(Salle::class.java)?.copy(id = it.id) }

    suspend fun addSalle(salle: Salle): String =
        db.collection("salles").add(salle).await().id

    suspend fun getMatieres(): List<Matiere> =
        db.collection("matieres").get().await()
            .documents.mapNotNull { it.toObject(Matiere::class.java)?.copy(id = it.id) }

    suspend fun getMatieresByFiliere(filiereId: String): List<Matiere> =
        db.collection("matieres").whereEqualTo("filiereId", filiereId)
            .get().await()
            .documents.mapNotNull { it.toObject(Matiere::class.java)?.copy(id = it.id) }

    suspend fun addMatiere(matiere: Matiere): String =
        db.collection("matieres").add(matiere).await().id

    suspend fun getCreneauxByFiliere(filiereId: String): List<Creneau> =
        db.collection("creneaux").whereEqualTo("filiereId", filiereId)
            .get().await()
            .documents.mapNotNull { it.toObject(Creneau::class.java)?.copy(id = it.id) }

    suspend fun getCreneauxByProfesseur(professeurId: String): List<Creneau> =
        db.collection("creneaux").whereEqualTo("professeurId", professeurId)
            .get().await()
            .documents.mapNotNull { it.toObject(Creneau::class.java)?.copy(id = it.id) }

    suspend fun addCreneau(creneau: Creneau): String =
        db.collection("creneaux").add(creneau).await().id

    suspend fun updateCreneau(creneau: Creneau) =
        db.collection("creneaux").document(creneau.id).set(creneau).await()

    suspend fun deleteCreneau(creneauId: String) =
        db.collection("creneaux").document(creneauId).delete().await()

    suspend fun annulerCreneau(creneauId: String) =
        db.collection("creneaux").document(creneauId).update("statut", "annule").await()

    suspend fun getUtilisateur(uid: String): Utilisateur? =
        db.collection("utilisateurs").document(uid).get().await()
            .toObject(Utilisateur::class.java)?.copy(id = uid)

    suspend fun getProfesseurs(): List<Utilisateur> =
        db.collection("utilisateurs").whereEqualTo("role", "professeur")
            .get().await()
            .documents.mapNotNull { it.toObject(Utilisateur::class.java)?.copy(id = it.id) }
}
