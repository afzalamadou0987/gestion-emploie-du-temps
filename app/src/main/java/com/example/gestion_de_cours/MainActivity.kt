package com.example.gestion_de_cours

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.gestion_de_cours.navigation.NavGraph
import com.example.gestion_de_cours.ui.theme.Gestion_de_coursTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gestion_de_coursTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}