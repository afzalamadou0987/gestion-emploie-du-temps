package com.example.gestion_de_cours.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.gestion_de_cours.viewmodel.AuthViewModel
import com.example.gestion_de_cours.viewmodel.EdtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    nomAdmin: String = "Administration",
    onLogout: () -> Unit = {},
    edtViewModel: EdtViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var tabSelectionne by remember { mutableStateOf(0) }
    val tabs = listOf("EDT", "Imprévus", "Professeurs")

    val nbTemporaires = edtViewModel.creneaux.count { it.estTemporaire }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(nomAdmin, fontWeight = FontWeight.Bold)
                        Text("Administration", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = tabSelectionne) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabSelectionne == index,
                        onClick = { tabSelectionne = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(title, fontSize = 13.sp)
                                // Badge imprévus
                                if (index == 1 &&
                                    edtViewModel.imprévus.any { it.statut == "en_attente" }
                                ) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFD32F2F)
                                    ) {
                                        Text(
                                            edtViewModel.imprévus
                                                .count { it.statut == "en_attente" }
                                                .toString(),
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                // Badge temporaires sur EDT
                                if (index == 0 && nbTemporaires > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF57C00)
                                    ) {
                                        Text(
                                            nbTemporaires.toString(),
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            when (tabSelectionne) {
                0 -> EdtTab(edtViewModel = edtViewModel, authViewModel = authViewModel)
                1 -> ImprévusTab(edtViewModel = edtViewModel)
                2 -> ProfsTab(authViewModel = authViewModel)
            }
        }
    }
}

// ─── ONGLET EDT ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdtTab(
    edtViewModel: EdtViewModel,
    authViewModel: AuthViewModel
) {
    val jours = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi")
    val classes = listOf("Toutes","B1-A","B1-B","B2-A","B2-B","B3-A","B3-B","MG1","MG2","MG3")
    var jourSelectionne by remember { mutableStateOf("Lundi") }
    var classeSelectionnee by remember { mutableStateOf("Toutes") }
    var showDialog by remember { mutableStateOf(false) }
    var classeMenuExpanded by remember { mutableStateOf(false) }
    var afficherTemporaires by remember { mutableStateOf(false) }

    val creneauxFiltres = edtViewModel.creneaux.filter { c ->
        c.jour == jourSelectionne &&
                (classeSelectionnee == "Toutes" || c.filiereId == classeSelectionnee) &&
                (if (afficherTemporaires) c.estTemporaire else !c.estTemporaire ||
                        classeSelectionnee != "Toutes")
    }

    val creneauxTemporaires = edtViewModel.creneaux.filter { it.estTemporaire }
    val creneauxOfficiels = edtViewModel.creneaux.filter { !it.estTemporaire }

    Column(modifier = Modifier.fillMaxSize()) {

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Officiels", creneauxOfficiels.size.toString(), Color(0xFF1565C0))
            StatCard("Temporaires", creneauxTemporaires.size.toString(), Color(0xFFF57C00))
            StatCard("Annules",
                edtViewModel.creneaux.count { it.statut == "annule" }.toString(),
                Color(0xFFD32F2F))
        }

        // Toggle temporaires/officiels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !afficherTemporaires,
                onClick = { afficherTemporaires = false },
                label = { Text("Officiels") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = afficherTemporaires,
                onClick = { afficherTemporaires = true },
                label = {
                    Text("A valider (${creneauxTemporaires.size})")
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFF57C00),
                    selectedLabelColor = Color.White
                )
            )
        }

        // Filtre classe (seulement pour officiels)
        if (!afficherTemporaires) {
            ExposedDropdownMenuBox(
                expanded = classeMenuExpanded,
                onExpandedChange = { classeMenuExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = classeSelectionnee, onValueChange = {}, readOnly = true,
                    label = { Text("Filtrer par classe") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(classeMenuExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = classeMenuExpanded,
                    onDismissRequest = { classeMenuExpanded = false }
                ) {
                    classes.forEach { classe ->
                        DropdownMenuItem(
                            text = { Text(classe) },
                            onClick = {
                                classeSelectionnee = classe
                                classeMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Onglets jours
        ScrollableTabRow(
            selectedTabIndex = jours.indexOf(jourSelectionne),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            jours.forEach { jour ->
                val nbTempCeJour = creneauxTemporaires.count { it.jour == jour }
                Tab(
                    selected = jourSelectionne == jour,
                    onClick = { jourSelectionne = jour },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(jour, fontSize = 13.sp)
                            if (afficherTemporaires && nbTempCeJour > 0) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF57C00)
                                ) {
                                    Text(nbTempCeJour.toString(),
                                        modifier = Modifier.padding(
                                            horizontal = 5.dp, vertical = 1.dp),
                                        fontSize = 9.sp, color = Color.White)
                                }
                            }
                        }
                    }
                )
            }
        }

        // Liste créneaux
        val listeAfficher = if (afficherTemporaires) {
            creneauxTemporaires.filter { it.jour == jourSelectionne }
        } else {
            edtViewModel.creneaux.filter { c ->
                c.jour == jourSelectionne && !c.estTemporaire &&
                        (classeSelectionnee == "Toutes" || c.filiereId == classeSelectionnee)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (listeAfficher.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (afficherTemporaires) Icons.Default.CheckCircle
                            else Icons.Default.EventBusy,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = if (afficherTemporaires) Color(0xFF2E7D32)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (afficherTemporaires) "Aucun creneau a valider ce jour"
                            else "Aucun cours ce jour",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listeAfficher) { creneau ->
                        CreneauAdminCard(
                            creneau = creneau,
                            onAnnuler = { edtViewModel.annulerCreneau(creneau.id) },
                            onSupprimer = { edtViewModel.supprimerCreneau(creneau.id) },
                            onValider = { salle, classe ->
                                edtViewModel.validerCreneau(creneau.id, salle, classe)
                            }
                        )
                    }
                }
            }

            if (!afficherTemporaires) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
    }

    if (showDialog) {
        AjouterCreneauDialog(
            onDismiss = { showDialog = false },
            onAjouter = { nouveau ->
                edtViewModel.ajouterCreneau(nouveau)
                showDialog = false
            },
            authViewModel = authViewModel
        )
    }
}

// ─── ONGLET IMPRÉVUS ────────────────────────────────────────
@Composable
fun ImprévusTab(edtViewModel: EdtViewModel) {
    if (edtViewModel.imprévus.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aucun imprevu signale",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(edtViewModel.imprévus) { imprevu ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (imprevu.statut == "en_attente")
                            Color(0xFFD32F2F).copy(alpha = 0.05f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(imprevu.profNom,
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (imprevu.statut == "en_attente")
                                    Color(0xFFD32F2F).copy(alpha = 0.15f)
                                else Color(0xFF2E7D32).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    if (imprevu.statut == "en_attente")
                                        "En attente" else "Traite",
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    color = if (imprevu.statut == "en_attente")
                                        Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF57C00))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Motif : ${imprevu.motif}",
                                fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        if (imprevu.message.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(imprevu.message, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(imprevu.date, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (imprevu.statut == "en_attente") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { edtViewModel.traiterImprevu(imprevu.id) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32))
                            ) {
                                Icon(Icons.Default.Check, null,
                                    modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Marquer comme traite")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── ONGLET PROFESSEURS ─────────────────────────────────────
@Composable
fun ProfsTab(authViewModel: AuthViewModel) {
    val profs = authViewModel.comptes.filter { it.role == "professeur" }

    if (profs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Group, null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aucun professeur inscrit",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(profs) { prof ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        prof.nom.firstOrNull()?.toString() ?: "P",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(prof.nom, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(prof.extra, fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary)
                                Text(prof.email, fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (prof.disponibilites.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Disponibilites :", fontSize = 13.sp,
                                fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            prof.disponibilites.filter {
                                it.matin || it.apresMidi
                            }.forEach { dispo ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color(0xFF2E7D32))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        buildString {
                                            append(dispo.jour)
                                            append(" : ")
                                            if (dispo.matin) append("Matin ")
                                            if (dispo.apresMidi) append("Apres-midi")
                                        },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── STAT CARD ──────────────────────────────────────────────
@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 12.sp, color = color)
        }
    }
}

// ─── CRENEAU ADMIN CARD ─────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreneauAdminCard(
    creneau: Creneau,
    onAnnuler: () -> Unit,
    onSupprimer: () -> Unit,
    onValider: ((salle: String, classe: String) -> Unit)? = null
) {
    var showValiderDialog by remember { mutableStateOf(false) }

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
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (creneau.estTemporaire)
                Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Surface(
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = if (creneau.estTemporaire) Color(0xFFF57C00) else couleurMatiere
            ) {}
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(creneau.matiereNom, fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = couleurMatiere)
                            if (creneau.estTemporaire) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF57C00).copy(alpha = 0.15f)
                                ) {
                                    Text("Temporaire",
                                        modifier = Modifier.padding(
                                            horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        color = Color(0xFFF57C00),
                                        fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            if (creneau.filiereNom == "A definir")
                                "Classe : A definir"
                            else creneau.filiereNom,
                            fontSize = 12.sp,
                            color = if (creneau.filiereNom == "A definir")
                                Color(0xFFF57C00)
                            else MaterialTheme.colorScheme.primary
                        )
                    }
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
                    Text(
                        "${creneau.heureDebut} - ${creneau.heureFin} • " +
                                if (creneau.periode == "matin") "Matin" else "Apres-midi",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    Text(
                        "${creneau.salleNom} • ${creneau.type}",
                        fontSize = 13.sp,
                        color = if (creneau.salleNom == "A definir")
                            Color(0xFFF57C00)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (creneau.estTemporaire) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showValiderDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(Icons.Default.Check, null,
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Valider", fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = onSupprimer,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD32F2F))
                        ) {
                            Icon(Icons.Default.Delete, null,
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refuser", fontSize = 13.sp)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (creneau.statut == "planifie") {
                            OutlinedButton(
                                onClick = onAnnuler,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFD32F2F))
                            ) { Text("Annuler", fontSize = 13.sp) }
                        }
                        OutlinedButton(
                            onClick = onSupprimer,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD32F2F))
                        ) {
                            Icon(Icons.Default.Delete, null,
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Supprimer", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    if (showValiderDialog) {
        ValiderCreneauDialog(
            creneau = creneau,
            onDismiss = { showValiderDialog = false },
            onValider = { salle, classe ->
                onValider?.invoke(salle, classe)
                showValiderDialog = false
            }
        )
    }
}

// ─── DIALOG VALIDER ─────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValiderCreneauDialog(
    creneau: Creneau,
    onDismiss: () -> Unit,
    onValider: (salle: String, classe: String) -> Unit
) {
    var salle by remember { mutableStateOf("") }
    var classeSelectionnee by remember { mutableStateOf("B1-A") }
    var classeExpanded by remember { mutableStateOf(false) }

    val classes = listOf("B1-A","B1-B","B2-A","B2-B","B3-A","B3-B","MG1","MG2","MG3")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32)) },
        title = { Text("Valider le creneau", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(creneau.matiereNom, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp)
                        Text("${creneau.professeurNom} • ${creneau.jour}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (creneau.periode == "matin")
                                "Matin 08h-12h" else "Apres-midi 13h-17h",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = salle, onValueChange = { salle = it },
                    label = { Text("Assigner une salle") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = classeExpanded,
                    onExpandedChange = { classeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = classeSelectionnee, onValueChange = {}, readOnly = true,
                        label = { Text("Assigner une classe") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(classeExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classeExpanded,
                        onDismissRequest = { classeExpanded = false }
                    ) {
                        classes.forEach { classe ->
                            DropdownMenuItem(
                                text = { Text(classe) },
                                onClick = {
                                    classeSelectionnee = classe
                                    classeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (salle.isNotBlank()) onValider(salle, classeSelectionnee)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32))
            ) { Text("Valider et publier") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

// ─── DIALOG AJOUTER CRÉNEAU ─────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterCreneauDialog(
    onDismiss: () -> Unit,
    onAjouter: (Creneau) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var matiere by remember { mutableStateOf("") }
    var profSelectionne by remember { mutableStateOf("") }
    var profNomComplet by remember { mutableStateOf("") }
    var salle by remember { mutableStateOf("") }
    var jourSelectionne by remember { mutableStateOf("Lundi") }
    var periodeSelectionnee by remember { mutableStateOf("matin") }
    var typeSelectionne by remember { mutableStateOf("CM") }
    var classeSelectionnee by remember { mutableStateOf("B1-A") }
    var jourExpanded by remember { mutableStateOf(false) }
    var periodeExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var classeExpanded by remember { mutableStateOf(false) }
    var profExpanded by remember { mutableStateOf(false) }

    val jours = listOf("Lundi","Mardi","Mercredi","Jeudi","Vendredi")
    val types = listOf("CM","TD","TP")
    val classes = listOf("B1-A","B1-B","B2-A","B2-B","B3-A","B3-B","MG1","MG2","MG3")
    val profs = authViewModel.comptes.filter { it.role == "professeur" }

    val profCompte = authViewModel.comptes.find { it.nom == profNomComplet }
    val dispoPourCeJour = profCompte?.disponibilites?.find { it.jour == jourSelectionne }
    val estDisponible = when (periodeSelectionnee) {
        "matin"      -> dispoPourCeJour?.matin == true
        "apres-midi" -> dispoPourCeJour?.apresMidi == true
        else         -> false
    }
    val profADesDispos = profCompte?.disponibilites?.any {
        it.matin || it.apresMidi
    } == true
    val afficherAvertissement = profNomComplet.isNotBlank() &&
            profADesDispos && !estDisponible

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un creneau", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = matiere, onValueChange = { matiere = it },
                    label = { Text("Matiere") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                if (profs.isEmpty()) {
                    OutlinedTextField(
                        value = profSelectionne,
                        onValueChange = {
                            profSelectionne = it
                            profNomComplet = it
                        },
                        label = { Text("Professeur") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = profExpanded,
                        onExpandedChange = { profExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = profSelectionne, onValueChange = {}, readOnly = true,
                            label = { Text("Professeur") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(profExpanded)
                            },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = profExpanded,
                            onDismissRequest = { profExpanded = false }
                        ) {
                            profs.forEach { prof ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(prof.nom, fontWeight = FontWeight.Medium,
                                                fontSize = 13.sp)
                                            Text(prof.extra, fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme
                                                    .onSurfaceVariant)
                                            val dispos = prof.disponibilites.filter {
                                                it.matin || it.apresMidi
                                            }
                                            if (dispos.isNotEmpty()) {
                                                Text(
                                                    dispos.joinToString(", ") { d ->
                                                        buildString {
                                                            append(d.jour.take(3))
                                                            if (d.matin) append(" M")
                                                            if (d.apresMidi) append(" AM")
                                                        }
                                                    },
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF2E7D32)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        profSelectionne = prof.nom
                                        profNomComplet = prof.nom
                                        profExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = salle, onValueChange = { salle = it },
                    label = { Text("Salle") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = classeExpanded,
                    onExpandedChange = { classeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = classeSelectionnee, onValueChange = {}, readOnly = true,
                        label = { Text("Classe") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(classeExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = classeExpanded,
                        onDismissRequest = { classeExpanded = false }
                    ) {
                        classes.forEach { classe ->
                            DropdownMenuItem(
                                text = { Text(classe) },
                                onClick = {
                                    classeSelectionnee = classe
                                    classeExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = jourExpanded,
                    onExpandedChange = { jourExpanded = it }
                ) {
                    OutlinedTextField(
                        value = jourSelectionne, onValueChange = {}, readOnly = true,
                        label = { Text("Jour") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(jourExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = jourExpanded,
                        onDismissRequest = { jourExpanded = false }
                    ) {
                        jours.forEach { jour ->
                            val dispoCeJour = profCompte?.disponibilites?.find {
                                it.jour == jour
                            }
                            val dispoTexte = when {
                                dispoCeJour == null -> ""
                                dispoCeJour.matin && dispoCeJour.apresMidi -> " ✓ M+AM"
                                dispoCeJour.matin -> " ✓ Matin"
                                dispoCeJour.apresMidi -> " ✓ AM"
                                else -> " ✗ Indispo"
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(jour)
                                        if (dispoTexte.isNotBlank()) {
                                            Text(dispoTexte, fontSize = 11.sp,
                                                color = if (dispoTexte.contains("✓"))
                                                    Color(0xFF2E7D32)
                                                else Color(0xFFD32F2F))
                                        }
                                    }
                                },
                                onClick = {
                                    jourSelectionne = jour
                                    jourExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = periodeExpanded,
                    onExpandedChange = { periodeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (periodeSelectionnee == "matin")
                            "Matin (08h-12h)" else "Apres-midi (13h-17h)",
                        onValueChange = {}, readOnly = true,
                        label = { Text("Periode") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(periodeExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = periodeExpanded,
                        onDismissRequest = { periodeExpanded = false }
                    ) {
                        val dispoMatin = dispoPourCeJour?.matin == true
                        val dispoAM = dispoPourCeJour?.apresMidi == true
                        DropdownMenuItem(
                            text = {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Matin (08h-12h)")
                                    if (profNomComplet.isNotBlank() && profADesDispos) {
                                        Text(if (dispoMatin) "✓ Dispo" else "✗ Indispo",
                                            fontSize = 11.sp,
                                            color = if (dispoMatin) Color(0xFF2E7D32)
                                            else Color(0xFFD32F2F))
                                    }
                                }
                            },
                            onClick = {
                                periodeSelectionnee = "matin"
                                periodeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Apres-midi (13h-17h)")
                                    if (profNomComplet.isNotBlank() && profADesDispos) {
                                        Text(if (dispoAM) "✓ Dispo" else "✗ Indispo",
                                            fontSize = 11.sp,
                                            color = if (dispoAM) Color(0xFF2E7D32)
                                            else Color(0xFFD32F2F))
                                    }
                                }
                            },
                            onClick = {
                                periodeSelectionnee = "apres-midi"
                                periodeExpanded = false
                            }
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = typeSelectionne, onValueChange = {}, readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { typeSelectionne = type; typeExpanded = false }
                            )
                        }
                    }
                }

                if (afficherAvertissement) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFD32F2F).copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$profSelectionne n'est pas disponible ce creneau.",
                                fontSize = 11.sp, color = Color(0xFFD32F2F))
                        }
                    }
                }

                if (profNomComplet.isNotBlank() && profADesDispos && estDisponible) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2E7D32).copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("$profSelectionne est disponible !",
                                fontSize = 11.sp, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (matiere.isNotBlank() && profSelectionne.isNotBlank() &&
                    salle.isNotBlank()) {
                    onAjouter(Creneau(
                        id = System.currentTimeMillis().toString(),
                        matiereNom = matiere,
                        professeurNom = profSelectionne,
                        salleNom = salle,
                        filiereId = classeSelectionnee,
                        filiereNom = classeSelectionnee,
                        jour = jourSelectionne,
                        periode = periodeSelectionnee,
                        heureDebut = if (periodeSelectionnee == "matin")
                            "08:00" else "13:00",
                        heureFin = if (periodeSelectionnee == "matin")
                            "12:00" else "17:00",
                        type = typeSelectionne,
                        statut = "planifie",
                        estTemporaire = false
                    ))
                }
            }) { Text("Ajouter") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}