package com.example.gestion_de_cours.data.model

data class Compte(
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val nom: String = "",
    val classe: String = "",
    val extra: String = "",
    val disponibilites: List<Disponibilite> = emptyList()
)