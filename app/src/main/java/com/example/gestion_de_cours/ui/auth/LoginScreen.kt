package com.example.gestion_de_cours.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestion_de_cours.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit,
    onInscription: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var compteSelectionne by remember { mutableStateOf("") }
    var emailSelectionne by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()
    val comptes = authViewModel.comptes

    LaunchedEffect(uiState.role) {
        uiState.role?.let { onLoginSuccess(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icône
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Gestion EDT",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Universite",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Dropdown liste des comptes
        if (comptes.isEmpty()) {
            // Aucun compte — message informatif
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Aucun compte enregistre.\nCreez un compte pour commencer.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else {
            // Dropdown avec tous les comptes
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = compteSelectionne,
                    onValueChange = {}, readOnly = true,
                    label = { Text("Choisir un compte") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    comptes.forEach { compte ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Badge rôle
                                    val (badgeColor, badgeLabel) = when (compte.role) {
                                        "administration" -> Pair(
                                            MaterialTheme.colorScheme.primary,
                                            "Admin"
                                        )
                                        "professeur" -> Pair(
                                            Color(0xFF2E7D32),
                                            "Prof"
                                        )
                                        "delegue" -> Pair(
                                            Color(0xFFF57C00),
                                            "Delegue"
                                        )
                                        else -> Pair(Color.Gray, "?")
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = badgeColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            badgeLabel,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp, vertical = 3.dp),
                                            fontSize = 10.sp,
                                            color = badgeColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            compte.nom,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            buildString {
                                                append(compte.email)
                                                if (compte.extra.isNotBlank())
                                                    append(" • ${compte.extra}")
                                                if (compte.classe.isNotBlank())
                                                    append(" • ${compte.classe}")
                                            },
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            onClick = {
                                compteSelectionne = compte.nom
                                emailSelectionne = compte.email
                                dropdownExpanded = false
                                // Reset erreur
                                authViewModel.clearError()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Champ mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            leadingIcon = { Icon(Icons.Default.Lock, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message erreur
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton Se connecter
        Button(
            onClick = {
                authViewModel.login(emailSelectionne, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading && compteSelectionne.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Se connecter", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "─────────── ou ───────────",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton S'inscrire
        OutlinedButton(
            onClick = onInscription,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Pas de compte ? S'inscrire",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}