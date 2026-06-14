package com.example.gestion_de_cours.data.model

data class Disponibilite(
    val jour: String = "",
    val matin: Boolean = false,
    val apresMidi: Boolean = false
)