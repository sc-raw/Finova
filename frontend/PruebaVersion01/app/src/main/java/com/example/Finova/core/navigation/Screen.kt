package com.example.Finova.core.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Dashboard : Screen("dashboard")
    object MisCuentas : Screen("mis_cuentas")
    object Transferir : Screen("transferir")
    object Historial : Screen("historial")
    object Estadisticas : Screen("estadisticas")
    object Perfil : Screen("perfil")
    object Escanear : Screen("escanear")

    companion object {
        val values = listOf(Splash,Login, Registro, Dashboard, MisCuentas, Transferir, Historial, Estadisticas, Perfil, Escanear)
    }
}