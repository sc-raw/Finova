package com.example.Finova.core.navigation

import android.net.Uri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.Finova.feature.movimientos.EditarMovimientoScreen
import com.example.Finova.feature.movimientos.MovimientosViewModel
import com.example.Finova.feature.movimientos.NuevoMovimientoScreen

// Escaneo
import com.example.Finova.feature.escaneo.EscanearScreen
import com.example.Finova.feature.escaneo.ComprobanteRevisionScreen

// Estadísticas / Metas
import com.example.Finova.feature.estadisticas.EstadisticasScreen
import com.example.Finova.feature.estadisticas.MetasScreen
import com.example.Finova.feature.estadisticas.AnadirFondosScreen

// Perfil
import com.example.Finova.feature.perfil.PerfilScreen
import com.example.Finova.feature.perfil.MisDatosScreen
import com.example.Finova.feature.perfil.CambiarPasswordScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val historialViewModel: MovimientosViewModel = viewModel()

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
            SplashScreen(
                onTimeout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(onNavigate = goTo)
        }

        composable(Screen.Registro.route) {
            RegistroScreen(onNavigate = goTo)
        }

        // ---------- INICIO Y CUENTAS ----------
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
                onTransferClick = {
                    goTo(Screen.Transferir.route)
                }
            )
        }

        composable(Screen.Transferir.route) {
            TransferirScreen(
                onBack = {
                    navController.popBackStack()
                },
                onTransferenciaExitosa = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---------- MOVIMIENTOS ----------
        composable(Screen.Historial.route) {
            LaunchedEffect(Unit) {
                historialViewModel.cargarMovimientos()
            }

            HistorialScreen(
                onNavigate = goTo,
                onMovimientoClick = { movimientoId ->
                    navController.navigate(
                        Screen.DetalleMovimiento.createRoute(movimientoId)
                    )
                },
                viewModel = historialViewModel
            )
        }

        composable(
            route = "detalle_movimiento/{movimientoId}",
            arguments = listOf(
                navArgument("movimientoId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val movimientoId =
                backStackEntry.arguments?.getLong("movimientoId") ?: 0L

            DetalleMovimientoScreen(
                movimientoId = movimientoId,
                onBack = {
                    navController.popBackStack()
                },
                onEdit = { id ->
                    goTo("editar_movimiento/$id")
                }
            )
        }

        composable(
            route = "editar_movimiento/{movimientoId}",
            arguments = listOf(
                navArgument("movimientoId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val movimientoId =
                backStackEntry.arguments?.getLong("movimientoId") ?: 0L

            EditarMovimientoScreen(
                movimientoId = movimientoId,
                onBack = {
                    navController.popBackStack()
                },
                onGuardado = {
                    navController.navigate(
                        Screen.DetalleMovimiento.createRoute(movimientoId)
                    ) {
                        popUpTo("detalle_movimiento/$movimientoId") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---------- PERFIL ----------
        composable(Screen.Perfil.route) {
            PerfilScreen(
                onNavigate = goTo,
                onImagenSeleccionada = { uri ->
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set("imagen_comprobante_uri", uri.toString())
                    goTo(Screen.ComprobanteRevision.route)
                }
            )
        }

        composable(Screen.MisDatos.route) {
            MisDatosScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeguridadCuenta.route) {
            CambiarPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- ESCANEO ----------
        composable(Screen.Escanear.route) {
            EscanearScreen(
                onBack = {
                    navController.popBackStack()
                },
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

        composable(Screen.ComprobanteRevision.route) {
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
                onBack = {
                    navController.popBackStack()
                },
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

        // ---------- ESTADÍSTICAS Y METAS ----------
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(
                onNavigate = goTo,
                onBack = {
                    navController.popBackStack()
                },
                onImagenSeleccionada = { uri ->
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set("imagen_comprobante_uri", uri.toString())
                    goTo(Screen.ComprobanteRevision.route)
                }
            )
        }

        composable(Screen.Metas.route) {
            MetasScreen(
                onNavigate = goTo,
                onAnadirFondos = { metaId ->
                    goTo(Screen.AnadirFondos.createRoute(metaId))
                },
                onBack = {
                    navController.popBackStack()
                },
                onImagenSeleccionada = { uri ->
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set("imagen_comprobante_uri", uri.toString())
                    goTo(Screen.ComprobanteRevision.route)
                }
            )
        }


        composable(
            route = Screen.AnadirFondos.route,
            arguments = listOf(
                navArgument("metaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getLong("metaId") ?: return@composable
            AnadirFondosScreen(
                metaId = metaId,
                onBack = { navController.popBackStack() },
                onConfirmar = { goTo(Screen.Metas.route) }
            )
        }
    }
}
