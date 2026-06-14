package com.example.gestion_de_cours.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gestion_de_cours.ui.SplashScreen
import com.example.gestion_de_cours.ui.auth.InscriptionScreen
import com.example.gestion_de_cours.ui.auth.LoginScreen
import com.example.gestion_de_cours.ui.admin.AdminScreen
import com.example.gestion_de_cours.ui.professeur.ProfesseurScreen
import com.example.gestion_de_cours.ui.delegue.DelegueScreen
import com.example.gestion_de_cours.viewmodel.AuthViewModel
import com.example.gestion_de_cours.viewmodel.EdtViewModel

object Routes {
    const val SPLASH      = "splash"
    const val LOGIN       = "login"
    const val INSCRIPTION = "inscription"
    const val ADMIN       = "admin"
    const val PROFESSEUR  = "professeur"
    const val DELEGUE     = "delegue"
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val edtViewModel: EdtViewModel   = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Connecte le callback : quand un prof s'inscrit → génère ses créneaux
    LaunchedEffect(Unit) {
        authViewModel.onProfInscrit = { nom, matiere, dispos ->
            edtViewModel.genererCreneauxTemporaires(nom, matiere, dispos)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "administration" -> Routes.ADMIN
                        "professeur"     -> Routes.PROFESSEUR
                        "delegue"        -> Routes.DELEGUE
                        else             -> Routes.LOGIN
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onInscription = {
                    navController.navigate(Routes.INSCRIPTION)
                },
                authViewModel = authViewModel
            )
        }

        composable(Routes.INSCRIPTION) {
            InscriptionScreen(
                onInscriptionSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INSCRIPTION) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                nomAdmin = authState.nomUtilisateur ?: "Administration",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                edtViewModel = edtViewModel,
                authViewModel = authViewModel
            )
        }

        composable(Routes.PROFESSEUR) {
            ProfesseurScreen(
                nomProf = authState.nomUtilisateur ?: "Professeur",
                matiereProf = authState.extra ?: "",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                edtViewModel = edtViewModel
            )
        }

        composable(Routes.DELEGUE) {
            DelegueScreen(
                nomDelegue = authState.nomUtilisateur ?: "Delegue",
                classeId = authState.classeId ?: "B2-A",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                edtViewModel = edtViewModel
            )
        }
    }
}