package com.example.gestion_de_cours.data.model

data class Imprevu(
    val id: String = "",
    val profNom: String = "",
    val profEmail: String = "",
    val motif: String = "",
    val message: String = "",
    val date: String = "",
    val statut: String = "en_attente" // "en_attente", "traite"
)