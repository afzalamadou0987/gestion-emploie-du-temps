package com.example.gestion_de_cours.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestion_de_cours.data.model.Disponibilite
import com.example.gestion_de_cours.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscriptionScreen(
    onInscriptionSuccess: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var roleSelectionne by remember { mutableStateOf("") }
    var extra by remember { mutableStateOf("") }
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var classeMenuExpanded by remember { mutableStateOf(false) }

    val jours = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi")
    val roles = listOf("administration", "professeur", "delegue")
    val classes = listOf("B1-A","B1-B","B2-A","B2-B","B3-A","B3-B","MG1","MG2","MG3")

    // Disponibilités pour les profs
    val disponibilites = remember {
        mutableStateMapOf<String, Pair<Boolean, Boolean>>().also { map ->
            jours.forEach { jour -> map[jour] = Pair(false, false) }
        }
    }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.inscriptionSuccess) {
        if (uiState.inscriptionSuccess) onInscriptionSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inscription") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Creer votre compte", fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(value = nom, onValueChange = { nom = it },
                label = { Text("Nom") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = prenom, onValueChange = { prenom = it },
                label = { Text("Prenom") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = password, onValueChange = { password = it },
                label = { Text("Mot de passe") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmer mot de passe") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true,
                visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(12.dp))

            // Sélection rôle
            ExposedDropdownMenuBox(
                expanded = roleMenuExpanded,
                onExpandedChange = { roleMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = when (roleSelectionne) {
                        "administration" -> "Administration"
                        "professeur"     -> "Professeur"
                        "delegue"        -> "Delegue"
                        else             -> ""
                    },
                    onValueChange = {}, readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(roleMenuExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(when (role) {
                                    "administration" -> "Administration"
                                    "professeur"     -> "Professeur"
                                    "delegue"        -> "Delegue"
                                    else             -> role
                                })
                            },
                            onClick = {
                                roleSelectionne = role
                                extra = ""
                                roleMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Champs selon rôle
            when (roleSelectionne) {
                "professeur" -> {
                    OutlinedTextField(
                        value = extra, onValueChange = { extra = it },
                        label = { Text("Matiere enseignee") },
                        leadingIcon = { Icon(Icons.Default.School, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), singleLine = true)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Disponibilités
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mes disponibilites",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.weight(1.5f))
                                Text("Matin", modifier = Modifier.weight(1f),
                                    fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text("Apres-midi", modifier = Modifier.weight(1f),
                                    fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }

                            jours.forEach { jour ->
                                val (matin, apresMidi) = disponibilites[jour]!!
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(jour, modifier = Modifier.weight(1.5f),
                                        fontSize = 14.sp)
                                    Checkbox(
                                        checked = matin,
                                        onCheckedChange = {
                                            disponibilites[jour] = Pair(it, apresMidi)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Checkbox(
                                        checked = apresMidi,
                                        onCheckedChange = {
                                            disponibilites[jour] = Pair(matin, it)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                "delegue" -> {
                    ExposedDropdownMenuBox(
                        expanded = classeMenuExpanded,
                        onExpandedChange = { classeMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = extra, onValueChange = {}, readOnly = true,
                            label = { Text("Votre classe") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(classeMenuExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = classeMenuExpanded,
                            onDismissRequest = { classeMenuExpanded = false }) {
                            classes.forEach { classe ->
                                DropdownMenuItem(text = { Text(classe) },
                                    onClick = {
                                        extra = classe
                                        classeMenuExpanded = false
                                    })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val dispoList = if (roleSelectionne == "professeur") {
                        jours.map { jour ->
                            val (matin, apresMidi) = disponibilites[jour]!!
                            Disponibilite(jour = jour, matin = matin, apresMidi = apresMidi)
                        }
                    } else emptyList()

                    authViewModel.inscrire(
                        nom, prenom, email, password, confirmPassword,
                        roleSelectionne, extra, dispoList
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("S'inscrire", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}