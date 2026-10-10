package com.example.Finova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.Finova.core.navigation.NavGraph // Asegúrate de que coincida con tu paquete

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Si ya tienes creado tu Theme (por ejemplo FinovaTheme), úsalo aquí:
            // FinovaTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavGraph() // Llamamos al grafo que maneja tus 9 pantallas
            }
            // }
        }
    }
}