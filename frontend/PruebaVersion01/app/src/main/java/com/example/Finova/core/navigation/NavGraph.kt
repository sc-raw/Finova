package com.example.Finova.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Auth
import com.example.Finova.feature.auth.LoginScreen
import com.example.Finova.feature.auth.RegistroScreen
import com.example.Finova.feature.auth.SplashScreen

// Inicio y Cuentas
import com.example.Finova.feature.inicio.DashboardScreen
import com.example.Finova.feature.cuentas.MisCuentasScreen
import com.example.Finova.feature.cuentas.TransferirScreen

// Movimientos
import com.example.Finova.feature.movimientos.HistorialScreen
import com.example.Finova.feature.movimientos.DetalleMovimientoScreen

// Escaneo
import com.example.Finova.feature.escaneo.EscanearScreen
import com.example.Finova.feature.escaneo.ComprobanteRevisionScreen
import com.example.Finova.feature.movimientos.NuevoMovimientoScreen

// Estadísticas / Metas
import com.example.Finova.feature.estadisticas.EstadisticasScreen
import com.example.Finova.feature.estadisticas.MetasScreen
import com.example.Finova.feature.estadisticas.AnadirFondosScreen

// Perfil
import com.example.Finova.feature.perfil.PerfilScreen
import com.example.Finova.feature.perfil.MisDatosScreen

import android.net.Uri

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

        // ---------- AUTH ----------
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(onNavigate = goTo)
        }
        composable(Screen.Registro.route) {
            RegistroScreen(onNavigate = goTo)
        }

        // ---------- VERTICAL 2: INICIO Y CUENTAS ----------
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigate = goTo,
                onScanClick = {
                    goTo(Screen.Escanear.route)
                },
                onImagenSeleccionada = { uri ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("imagen_comprobante_uri", uri.toString())

                    goTo(Screen.ComprobanteRevision.route)
                }
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
                onBack = { navController.popBackStack() },
                onTransferenciaExitosa = {
                    navController.navigate(Screen.MisCuentas.route) {
                        popUpTo(Screen.MisCuentas.route) { inclusive = true }
                    }
                }
            )
        }

        // ---------- VERTICAL 3: MOVIMIENTOS ----------
        composable(Screen.Historial.route) {
            HistorialScreen(
                onNavigate = goTo,
                onMovimientoClick = { movimientoId ->
                    navController.navigate(
                        Screen.DetalleMovimiento.createRoute(movimientoId)
                    )
                }
            )
        }

        // ✅ Detalle con argumento movimientoId
        composable(
            route = "detalle_movimiento/{movimientoId}",
            arguments = listOf(
                navArgument("movimientoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val movimientoId = backStackEntry.arguments?.getLong("movimientoId") ?: 0L
            DetalleMovimientoScreen(
                movimientoId = movimientoId,
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- VERTICAL 1: PERFIL ----------
        composable(Screen.Perfil.route) {
            PerfilScreen(onNavigate = goTo)
        }
        composable(Screen.MisDatos.route) {
            MisDatosScreen(onBack = { navController.popBackStack() })
        }

        // ---------- VERTICAL 4: ESCANEO ----------
        composable(Screen.Escanear.route) {
            EscanearScreen(
                onBack = { navController.popBackStack() },
                onComprobanteCapturado = {
                    goTo(Screen.ComprobanteRevision.route)
                },
                onImagenSeleccionada = { uri ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("imagen_comprobante_uri", uri.toString())

                    goTo(Screen.ComprobanteRevision.route)
                }
            )
        }
        composable(Screen.ComprobanteRevision.route) { backStackEntry ->

            val imagenUri = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("imagen_comprobante_uri")
                ?.let { Uri.parse(it) }

            ComprobanteRevisionScreen(
                imagenUri = imagenUri,
                onBack = {
                    navController.popBackStack()
                },
                onConfirmar = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onDescartar = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.NuevoMovimiento.route) {
            NuevoMovimientoScreen(
                onBack = { navController.popBackStack() },
                onGuardado = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---------- VERTICAL 5: ESTADÍSTICAS Y METAS ----------
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(
                onNavigate = goTo,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Metas.route) {
            MetasScreen(
                onNavigate = goTo,
                onAnadirFondos = { goTo(Screen.AnadirFondos.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AnadirFondos.route) {
            AnadirFondosScreen(
                onBack = { navController.popBackStack() },
                onConfirmar = { goTo(Screen.Metas.route) }
            )
        }
    }
}