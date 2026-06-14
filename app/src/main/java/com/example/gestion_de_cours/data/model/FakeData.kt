package com.example.gestion_de_cours.data.model

object FakeData {
    val creneaux = listOf(
        // LUNDI
        Creneau(id="c1", matiereNom="Algorithmique",
            professeurId="p1", professeurNom="Prof. Kokou",
            salleNom="Amphi A", filiereId="B2-A", filiereNom="B2-A",
            jour="Lundi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c2", matiereNom="Base de Donnees",
            professeurId="p3", professeurNom="Prof. Agbeko",
            salleNom="Salle 101", filiereId="B2-A", filiereNom="B2-A",
            jour="Lundi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="TD", statut="planifie"),

        // MARDI
        Creneau(id="c3", matiereNom="Programmation OO",
            professeurId="p2", professeurNom="Prof. Mensah",
            salleNom="Amphi B", filiereId="B2-A", filiereNom="B2-A",
            jour="Mardi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c4", matiereNom="Reseaux",
            professeurId="p4", professeurNom="Prof. Dossou",
            salleNom="Labo Info", filiereId="B2-A", filiereNom="B2-A",
            jour="Mardi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="TP", statut="planifie"),

        // MERCREDI
        Creneau(id="c5", matiereNom="Algorithmique",
            professeurId="p1", professeurNom="Prof. Kokou",
            salleNom="Amphi A", filiereId="B2-A", filiereNom="B2-A",
            jour="Mercredi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="TD", statut="annule"),
        Creneau(id="c6", matiereNom="Mathematiques",
            professeurId="p2", professeurNom="Prof. Mensah",
            salleNom="Amphi B", filiereId="B2-A", filiereNom="B2-A",
            jour="Mercredi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="CM", statut="planifie"),

        // JEUDI
        Creneau(id="c7", matiereNom="Systemes Exploitation",
            professeurId="p1", professeurNom="Prof. Kokou",
            salleNom="Salle 102", filiereId="B2-A", filiereNom="B2-A",
            jour="Jeudi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c8", matiereNom="Base de Donnees",
            professeurId="p3", professeurNom="Prof. Agbeko",
            salleNom="Labo Info", filiereId="B2-A", filiereNom="B2-A",
            jour="Jeudi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="TP", statut="rattrapage"),

        // VENDREDI
        Creneau(id="c9", matiereNom="Reseaux",
            professeurId="p4", professeurNom="Prof. Dossou",
            salleNom="Amphi A", filiereId="B2-A", filiereNom="B2-A",
            jour="Vendredi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c10", matiereNom="Programmation OO",
            professeurId="p2", professeurNom="Prof. Mensah",
            salleNom="Salle 101", filiereId="B2-A", filiereNom="B2-A",
            jour="Vendredi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="TD", statut="planifie"),

        // B1-A
        Creneau(id="c11", matiereNom="Introduction Info",
            professeurId="p1", professeurNom="Prof. Kokou",
            salleNom="Amphi A", filiereId="B1-A", filiereNom="B1-A",
            jour="Lundi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c12", matiereNom="Mathematiques",
            professeurId="p2", professeurNom="Prof. Mensah",
            salleNom="Salle 101", filiereId="B1-A", filiereNom="B1-A",
            jour="Lundi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="CM", statut="planifie"),

        // MG1
        Creneau(id="c13", matiereNom="Management",
            professeurId="p3", professeurNom="Prof. Agbeko",
            salleNom="Amphi B", filiereId="MG1", filiereNom="MG1",
            jour="Lundi", periode="matin",
            heureDebut="08:00", heureFin="12:00",
            type="CM", statut="planifie"),
        Creneau(id="c14", matiereNom="Comptabilite",
            professeurId="p4", professeurNom="Prof. Dossou",
            salleNom="Salle 102", filiereId="MG1", filiereNom="MG1",
            jour="Lundi", periode="apres-midi",
            heureDebut="13:00", heureFin="17:00",
            type="TD", statut="planifie")
    )
}