package com.example.gestion_de_cours.ui.professeur

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
fun ProfesseurScreen(
    nomProf: String = "Professeur",
    matiereProf: String = "",
    onLogout: () -> Unit = {},
    edtViewModel: EdtViewModel = viewModel()
) {
    val jours = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi")
    var jourSelectionne by remember { mutableStateOf("Lundi") }
    var showSignalement by remember { mutableStateOf(false) }
    var signalementEnvoye by remember { mutableStateOf(false) }
    var signalementMessage by remember { mutableStateOf("") }

    val mesCreneaux = edtViewModel.creneaux.filter {
        it.professeurNom == nomProf && it.jour == jourSelectionne
    }

    val totalMesCours = edtViewModel.creneaux.count { it.professeurNom == nomProf }
    val totalAnnules = edtViewModel.creneaux.count {
        it.professeurNom == nomProf && it.statut == "annule"
    }
    val totalPlanifies = edtViewModel.creneaux.count {
        it.professeurNom == nomProf && it.statut == "planifie"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(nomProf, fontWeight = FontWeight.Bold)
                        Text(
                            if (matiereProf.isNotBlank()) matiereProf
                            else "Mon Planning",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSignalement = true },
                containerColor = Color(0xFFD32F2F)
            ) {
                Icon(Icons.Default.ReportProblem, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp,
                    vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCardProf("Mes Cours", totalMesCours.toString(), Color(0xFF2E7D32))
                StatCardProf("Annules", totalAnnules.toString(), Color(0xFFD32F2F))
                StatCardProf("Planifies", totalPlanifies.toString(), Color(0xFF1565C0))
            }

            // Onglets jours
            ScrollableTabRow(
                selectedTabIndex = jours.indexOf(jourSelectionne),
                contentColor = Color(0xFF2E7D32)
            ) {
                jours.forEach { jour ->
                    Tab(
                        selected = jourSelectionne == jour,
                        onClick = { jourSelectionne = jour },
                        text = { Text(jour, fontSize = 13.sp) }
                    )
                }
            }

            if (mesCreneaux.isEmpty()) {
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
                    items(mesCreneaux) { creneau ->
                        CoursCard(
                            creneau = creneau,
                            onAnnuler = { edtViewModel.annulerCreneau(creneau.id) }
                        )
                    }
                }
            }
        }

        // Dialog signalement
        if (showSignalement) {
            SignalementProfDialog(
                nomProf = nomProf,
                creneauxDuJour = mesCreneaux,
                onDismiss = { showSignalement = false },
                onEnvoyer = { imprevu, message ->
                    edtViewModel.signalerImprevu(imprevu)
                    signalementEnvoye = true
                    signalementMessage = message
                    showSignalement = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalementProfDialog(
    nomProf: String,
    creneauxDuJour: List<Creneau>,
    onDismiss: () -> Unit,
    onEnvoyer: (Imprevu, String) -> Unit
) {
    var typeSignalement by remember { mutableStateOf("") }
    var coursSelectionne by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var coursExpanded by remember { mutableStateOf(false) }

    val typesSignalement = listOf(
        "Absence",
        "Retard",
        "Maladie",
        "Urgence familiale",
        "Empechement administratif",
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

                Text(
                    "L'administration sera notifiee et mettra a jour l'EDT.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                                        Icon(
                                            when (type) {
                                                "Absence" -> Icons.Default.PersonOff
                                                "Retard"  -> Icons.Default.Schedule
                                                "Maladie" -> Icons.Default.LocalHospital
                                                "Urgence familiale" -> Icons.Default.Home
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
                            label = { Text("Cours concerne (optionnel)") },
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
                                                        " • ${creneau.filiereNom}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        coursSelectionne =
                                            "${creneau.matiereNom} (${creneau.heureDebut})"
                                        coursExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Message
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Info
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
                                "L'admin recevra votre signalement et " +
                                        "mettra a jour l'EDT si necessaire.",
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
                        val msg = buildString {
                            append(typeSignalement)
                            if (coursSelectionne.isNotBlank())
                                append(" - $coursSelectionne")
                            if (message.isNotBlank()) append(" : $message")
                        }
                        onEnvoyer(
                            Imprevu(
                                id = System.currentTimeMillis().toString(),
                                profNom = nomProf,
                                motif = typeSignalement,
                                message = buildString {
                                    if (coursSelectionne.isNotBlank())
                                        append("Cours : $coursSelectionne\n")
                                    if (message.isNotBlank())
                                        append("Message : $message")
                                },
                                date = "Aujourd'hui",
                                statut = "en_attente"
                            ),
                            msg
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F))
            ) { Text("Envoyer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun StatCardProf(label: String, value: String, color: Color) {
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

@Composable
fun CoursCard(creneau: Creneau, onAnnuler: () -> Unit) {
    val couleur = when (creneau.statut) {
        "annule"     -> Color(0xFFD32F2F)
        "rattrapage" -> Color(0xFFF57C00)
        else         -> Color(0xFF2E7D32)
    }
    val label = when (creneau.statut) {
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
                        Text(label,
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
                    Icon(Icons.Default.Group, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${creneau.filiereNom} • ${creneau.type}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(creneau.salleNom, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (creneau.statut == "planifie") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onAnnuler,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.Cancel, null,
                            modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Annuler ce cours", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}