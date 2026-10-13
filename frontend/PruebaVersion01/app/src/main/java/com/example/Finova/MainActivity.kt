package com.example.Finova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Finova.core.design.theme.FinovaTheme
import com.example.Finova.core.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinovaTheme {
                NavGraph()
            }
        }
    }
}