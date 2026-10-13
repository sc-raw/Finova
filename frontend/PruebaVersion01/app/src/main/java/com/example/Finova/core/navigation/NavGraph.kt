package com.example.Finova.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Finova.feature.auth.LoginScreen
import com.example.Finova.feature.auth.RegistroScreen
import com.example.Finova.feature.auth.SplashScreen
import com.example.Finova.feature.cuentas.MisCuentasScreen
import com.example.Finova.feature.cuentas.TransferirScreen
import com.example.Finova.feature.escaneo.EscanearScreen
import com.example.Finova.feature.estadisticas.EstadisticasScreen
import com.example.Finova.feature.inicio.DashboardScreen
import com.example.Finova.feature.movimientos.DetalleMovimientoScreen
import com.example.Finova.feature.movimientos.HistorialScreen
import com.example.Finova.feature.perfil.MisDatosScreen
import com.example.Finova.feature.perfil.PerfilScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    // Helper: navega evitando duplicados en el back stack
    val goTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // ---------- Auth ----------
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigate = goTo
            )
        }
        composable(Screen.Registro.route) {
            RegistroScreen(
                onNavigate = goTo
            )
        }

        // ---------- Vertical 2: Inicio y Cuentas (TU VERTICAL) ----------
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = goTo,
                onScanClick = { goTo(Screen.Escanear.route) }
            )
        }
        composable(Screen.MisCuentas.route) {
            MisCuentasScreen(
                onNavigate = goTo,
                onTransferClick = { goTo(Screen.Transferir.route) }
            )
        }
        composable(Screen.Transferir.route) {
            TransferirScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- Vertical 3: Movimientos ----------
        composable(Screen.Historial.route) {
            HistorialScreen(
                onNavigate = goTo,
                onMovimientoClick = { goTo(Screen.DetalleMovimiento.route) }
            )
        }
        composable(Screen.DetalleMovimiento.route) {
            DetalleMovimientoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- Vertical 1: Perfil (con MisDatos) ----------
        composable(Screen.Perfil.route) {
            PerfilScreen(
                onNavigate = goTo
            )
        }
        composable(Screen.MisDatos.route) {
            MisDatosScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- Otras verticales (placeholders) ----------
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen { goTo(Screen.Dashboard.route) }
        }
        composable(Screen.Escanear.route) {
            EscanearScreen { goTo(Screen.Dashboard.route) }
        }
    }
}