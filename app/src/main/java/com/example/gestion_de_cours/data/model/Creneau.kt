package com.example.gestion_de_cours.data.model
data class Creneau(
    val id: String = "",
    val matiereId: String = "",
    val matiereNom: String = "",
    val professeurId: String = "",
    val professeurNom: String = "",
    val salleId: String = "",
    val salleNom: String = "",
    val filiereId: String = "",
    val filiereNom: String = "",
    val jour: String = "",
    val periode: String = "",
    val heureDebut: String = "",
    val heureFin: String = "",
    val type: String = "",
    val statut: String = "planifie",
    val estTemporaire: Boolean = false
)
