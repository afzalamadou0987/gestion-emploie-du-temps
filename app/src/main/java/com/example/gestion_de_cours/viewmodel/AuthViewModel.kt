package com.example.gestion_de_cours.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestion_de_cours.data.model.Compte
import com.example.gestion_de_cours.data.model.Disponibilite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val role: String? = null,
    val nomUtilisateur: String? = null,
    val classeId: String? = null,
    val extra: String? = null,
    val email: String? = null,
    val disponibilites: List<Disponibilite> = emptyList(),
    val error: String? = null,
    val inscriptionSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val comptes = mutableListOf<Compte>()

    // Callback pour générer les créneaux après inscription prof
    var onProfInscrit: ((nom: String, matiere: String, dispos: List<Disponibilite>) -> Unit)? = null

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Remplis tous les champs")
            return
        }

        val compte = comptes.find { it.email == email.trim().lowercase() }

        if (compte == null) {
            _uiState.value = AuthUiState(error = "Email introuvable")
            return
        }

        if (compte.password != password) {
            _uiState.value = AuthUiState(error = "Mot de passe incorrect")
            return
        }

        _uiState.value = AuthUiState(
            role = compte.role,
            nomUtilisateur = compte.nom,
            classeId = compte.classe,
            extra = compte.extra,
            email = compte.email,
            disponibilites = compte.disponibilites
        )
    }

    fun inscrire(
        nom: String,
        prenom: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        extra: String = "",
        disponibilites: List<Disponibilite> = emptyList()
    ) {
        if (nom.isBlank() || prenom.isBlank() || email.isBlank() ||
            password.isBlank() || role.isBlank()
        ) {
            _uiState.value = _uiState.value.copy(error = "Remplis tous les champs")
            return
        }

        if (role == "professeur" && extra.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Indique ta matiere")
            return
        }

        if (role == "delegue" && extra.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Choisis ta classe")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                error = "Les mots de passe ne correspondent pas")
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                error = "Mot de passe trop court (min 6 caracteres)")
            return
        }

        if (comptes.any { it.email == email.trim().lowercase() }) {
            _uiState.value = _uiState.value.copy(error = "Cet email est deja utilise")
            return
        }

        if (role == "administration" && comptes.any { it.role == "administration" }) {
            _uiState.value = _uiState.value.copy(
                error = "Un compte administration existe deja")
            return
        }

        val nomComplet = "$prenom $nom"

        comptes.add(
            Compte(
                email = email.trim().lowercase(),
                password = password,
                role = role,
                nom = nomComplet,
                classe = if (role == "delegue") extra else "",
                extra = extra,
                disponibilites = disponibilites
            )
        )

        // Si c'est un prof → génère ses créneaux temporaires
        if (role == "professeur" && disponibilites.isNotEmpty()) {
            onProfInscrit?.invoke(nomComplet, extra, disponibilites)
        }

        _uiState.value = AuthUiState(inscriptionSuccess = true)
    }

    fun logout() {
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, inscriptionSuccess = false)
    }
}
