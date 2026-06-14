package com.example.gestion_de_cours.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gestion_de_cours.data.model.Creneau
import com.example.gestion_de_cours.data.model.Disponibilite
import com.example.gestion_de_cours.data.model.FakeData
import com.example.gestion_de_cours.data.model.Imprevu

class EdtViewModel : ViewModel() {

    val creneaux = mutableStateListOf<Creneau>().also {
        it.addAll(FakeData.creneaux)
    }

    val imprévus = mutableStateListOf<Imprevu>()

    // ─── GÉNÉRATION EDT TEMPORAIRE ──────────────────────────
    fun genererCreneauxTemporaires(
        profNom: String,
        matiere: String,
        disponibilites: List<Disponibilite>
    ) {
        disponibilites.forEach { dispo ->
            if (dispo.matin) {
                creneaux.add(
                    Creneau(
                        id = "temp_${System.currentTimeMillis()}_${dispo.jour}_matin",
                        matiereNom = matiere,
                        professeurNom = profNom,
                        salleNom = "A definir",
                        filiereNom = "A definir",
                        filiereId = "indefini",
                        jour = dispo.jour,
                        periode = "matin",
                        heureDebut = "08:00",
                        heureFin = "12:00",
                        type = "CM",
                        statut = "planifie",
                        estTemporaire = true
                    )
                )
            }
            if (dispo.apresMidi) {
                creneaux.add(
                    Creneau(
                        id = "temp_${System.currentTimeMillis()}_${dispo.jour}_apm",
                        matiereNom = matiere,
                        professeurNom = profNom,
                        salleNom = "A definir",
                        filiereNom = "A definir",
                        filiereId = "indefini",
                        jour = dispo.jour,
                        periode = "apres-midi",
                        heureDebut = "13:00",
                        heureFin = "17:00",
                        type = "CM",
                        statut = "planifie",
                        estTemporaire = true
                    )
                )
            }
        }
    }

    // Valider un créneau temporaire → devient officiel
    fun validerCreneau(id: String, salle: String, classe: String) {
        val index = creneaux.indexOfFirst { it.id == id }
        if (index != -1) {
            creneaux[index] = creneaux[index].copy(
                salleNom = salle,
                filiereId = classe,
                filiereNom = classe,
                estTemporaire = false
            )
        }
    }

    fun ajouterCreneau(creneau: Creneau) {
        creneaux.add(creneau)
    }

    fun annulerCreneau(id: String) {
        val index = creneaux.indexOfFirst { it.id == id }
        if (index != -1) {
            creneaux[index] = creneaux[index].copy(statut = "annule")
        }
    }

    fun supprimerCreneau(id: String) {
        creneaux.removeIf { it.id == id }
    }

    fun signalerImprevu(imprevu: Imprevu) {
        imprévus.add(imprevu)
    }

    fun traiterImprevu(id: String) {
        val index = imprévus.indexOfFirst { it.id == id }
        if (index != -1) {
            imprévus[index] = imprévus[index].copy(statut = "traite")
        }
    }
}