
package com.example.Finova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.Finova.core.design.theme.FinovaTheme
import com.example.Finova.core.navigation.NavGraph
import com.example.Finova.core.network.ApiClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.initialize(applicationContext)

        setContent {
            FinovaTheme {
                NavGraph()
            }
        }
    }
}
