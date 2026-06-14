package com.example.gestion_de_cours.ui.delegue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestion_de_cours.data.model.Creneau
import com.example.gestion_de_cours.data.model.Imprevu
import com.example.gestion_de_cours.viewmodel.EdtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelegueScreen(
    nomDelegue: String = "Delegue",
    classeId: String = "B2-A",
    onLogout: () -> Unit = {},
    edtViewModel: EdtViewModel = viewModel()
) {
    val jours = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi")
    var jourSelectionne by remember { mutableStateOf("Lundi") }
    var showSignalementDialog by remember { mutableStateOf(false) }
    var signalementEnvoye by remember { mutableStateOf(false) }
    var signalementMessage by remember { mutableStateOf("") }

    val creneauxClasse = edtViewModel.creneaux.filter {
        it.filiereId == classeId && it.jour == jourSelectionne
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(nomDelegue, fontWeight = FontWeight.Bold)
                        Text("Classe $classeId", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF57C00),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSignalementDialog = true },
                containerColor = Color(0xFFD32F2F)
            ) {
                Icon(Icons.Default.ReportProblem, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Banniere signalement envoyé
            if (signalementEnvoye) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFD32F2F).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Signalement envoye a l'administration !",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F))
                            Text(signalementMessage,
                                fontSize = 12.sp, color = Color(0xFFD32F2F))
                        }
                    }
                }
            }

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCardDelegue("Total",
                    edtViewModel.creneaux.count { it.filiereId == classeId }.toString(),
                    Color(0xFFF57C00))
                StatCardDelegue("Annules",
                    edtViewModel.creneaux.count {
                        it.filiereId == classeId && it.statut == "annule"
                    }.toString(), Color(0xFFD32F2F))
                StatCardDelegue("Planifies",
                    edtViewModel.creneaux.count {
                        it.filiereId == classeId && it.statut == "planifie"
                    }.toString(), Color(0xFF2E7D32))
            }

            // Onglets jours
            ScrollableTabRow(
                selectedTabIndex = jours.indexOf(jourSelectionne),
                contentColor = Color(0xFFF57C00)
            ) {
                jours.forEach { jour ->
                    Tab(
                        selected = jourSelectionne == jour,
                        onClick = { jourSelectionne = jour },
                        text = { Text(jour, fontSize = 13.sp) }
                    )
                }
            }

            if (creneauxClasse.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Pas de cours ce jour",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(creneauxClasse) { creneau ->
                        CoursCardDelegue(creneau = creneau)
                    }
                }
            }
        }

        // Dialog signalement
        if (showSignalementDialog) {
            SignalementDialog(
                creneauxDuJour = creneauxClasse,
                classeId = classeId,
                onDismiss = { showSignalementDialog = false },
                onSignaler = { imprevu, message ->
                    edtViewModel.signalerImprevu(imprevu)
                    signalementEnvoye = true
                    signalementMessage = message
                    showSignalementDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalementDialog(
    creneauxDuJour: List<Creneau>,
    classeId: String,
    onDismiss: () -> Unit,
    onSignaler: (Imprevu, String) -> Unit
) {
    var typeSignalement by remember { mutableStateOf("") }
    var coursSelectionne by remember { mutableStateOf("") }
    var coursId by remember { mutableStateOf("") }
    var motif by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var coursExpanded by remember { mutableStateOf(false) }

    val typesSignalement = listOf(
        "Absence du Professeur",
        "Professeur en retard",
        "Salle non disponible",
        "Absence massive des etudiants",
        "Cours perturbe",
        "Autre"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.ReportProblem, null, tint = Color(0xFFD32F2F))
        },
        title = {
            Text("Signaler a l'administration", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Type de signalement
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = typeSignalement,
                        onValueChange = {}, readOnly = true,
                        label = { Text("Type de signalement") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typesSignalement.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Icone selon le type
                                        Icon(
                                            when (type) {
                                                "Absence du Professeur" ->
                                                    Icons.Default.PersonOff
                                                "Professeur en retard" ->
                                                    Icons.Default.Schedule
                                                "Salle non disponible" ->
                                                    Icons.Default.MeetingRoom
                                                "Absence massive des etudiants" ->
                                                    Icons.Default.Groups
                                                else -> Icons.Default.Warning
                                            },
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFFD32F2F)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(type, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    typeSignalement = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Cours concerné
                if (creneauxDuJour.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = coursExpanded,
                        onExpandedChange = { coursExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = coursSelectionne,
                            onValueChange = {}, readOnly = true,
                            label = { Text("Cours concerne") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(coursExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = coursExpanded,
                            onDismissRequest = { coursExpanded = false }
                        ) {
                            creneauxDuJour.forEach { creneau ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(creneau.matiereNom,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp)
                                            Text(
                                                "${creneau.heureDebut}-${creneau.heureFin}" +
                                                        " • ${creneau.professeurNom}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        coursSelectionne =
                                            "${creneau.matiereNom} (${creneau.heureDebut})"
                                        coursId = creneau.id
                                        coursExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Précisions
                OutlinedTextField(
                    value = motif,
                    onValueChange = { motif = it },
                    label = { Text("Precisions (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Info admin
                if (typeSignalement.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD32F2F).copy(alpha = 0.06f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "L'administration sera notifiee immediatement.",
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (typeSignalement.isNotBlank()) {
                        val message = buildString {
                            append(typeSignalement)
                            if (coursSelectionne.isNotBlank())
                                append(" - $coursSelectionne")
                            if (motif.isNotBlank()) append(" : $motif")
                        }
                        onSignaler(
                            Imprevu(
                                id = System.currentTimeMillis().toString(),
                                profNom = "Delegue $classeId",
                                motif = typeSignalement,
                                message = buildString {
                                    if (coursSelectionne.isNotBlank())
                                        append("Cours : $coursSelectionne\n")
                                    if (motif.isNotBlank())
                                        append("Details : $motif")
                                },
                                date = "Aujourd'hui",
                                statut = "en_attente"
                            ),
                            message
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F))
            ) { Text("Envoyer a l'admin") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun CoursCardDelegue(creneau: Creneau) {
    val couleur = when (creneau.statut) {
        "annule"     -> Color(0xFFD32F2F)
        "rattrapage" -> Color(0xFFF57C00)
        else         -> Color(0xFF2E7D32)
    }
    val labelStatut = when (creneau.statut) {
        "annule"     -> "Annule"
        "rattrapage" -> "Rattrapage"
        else         -> "Planifie"
    }
    val couleurMatiere = when (creneau.matiereNom) {
        "Algorithmique"    -> Color(0xFF1565C0)
        "Base de Donnees"  -> Color(0xFF6A1B9A)
        "Programmation OO" -> Color(0xFF2E7D32)
        "Reseaux"          -> Color(0xFFE65100)
        "Mathematiques"    -> Color(0xFF00695C)
        else               -> Color(0xFF37474F)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Surface(
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = couleurMatiere
            ) {}
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(creneau.matiereNom, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = couleurMatiere)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = couleur.copy(alpha = 0.15f)
                    ) {
                        Text(labelStatut,
                            modifier = Modifier.padding(
                                horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp, color = couleur,
                            fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${creneau.heureDebut} - ${creneau.heureFin}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(creneau.professeurNom, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${creneau.salleNom} • ${creneau.type}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun StatCardDelegue(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = color)
        }
    }
}