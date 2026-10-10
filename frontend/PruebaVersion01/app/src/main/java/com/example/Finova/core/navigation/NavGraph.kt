package com.example.Finova.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Finova.feature.auth.SplashScreen // <-- Asegúrate de importar tu Splash
import com.example.Finova.feature.auth.LoginScreen
import com.example.Finova.feature.auth.RegistroScreen
import com.example.Finova.feature.inicio.DashboardScreen
import com.example.Finova.feature.cuentas.MisCuentasScreen
import com.example.Finova.feature.cuentas.TransferirScreen
import com.example.Finova.feature.movimientos.HistorialScreen
import com.example.Finova.feature.estadisticas.EstadisticasScreen
import com.example.Finova.feature.perfil.PerfilScreen
import com.example.Finova.feature.escaneo.EscanearScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val screens = Screen.values

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route // <-- Cambiado de Login a Splash
    ) {
        screens.forEachIndexed { index, screen ->
            val nextScreen = screens[(index + 1) % screens.size]

            composable(screen.route) {
                when (screen) {
                    is Screen.Splash -> {
                        // Al terminar los 2 segundos, va al Login limpiando el historial
                        SplashScreen(onTimeout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        })
                    }
                    is Screen.Login -> LoginScreen { navController.navigate(nextScreen.route) }
                    is Screen.Registro -> RegistroScreen { navController.navigate(nextScreen.route) }
                    is Screen.Dashboard -> DashboardScreen { navController.navigate(nextScreen.route) }
                    is Screen.MisCuentas -> MisCuentasScreen { navController.navigate(nextScreen.route) }
                    is Screen.Transferir -> TransferirScreen { navController.navigate(nextScreen.route) }
                    is Screen.Historial -> HistorialScreen { navController.navigate(nextScreen.route) }
                    is Screen.Estadisticas -> EstadisticasScreen { navController.navigate(nextScreen.route) }
                    is Screen.Perfil -> PerfilScreen { navController.navigate(nextScreen.route) }
                    is Screen.Escanear -> EscanearScreen { navController.navigate(nextScreen.route) }
                }
            }
        }
    }
}
