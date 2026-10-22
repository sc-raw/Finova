package com.example.Finova.core.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object Registro : Screen("registro")
    object Dashboard : Screen("dashboard")

    object NuevoMovimiento : Screen("nuevo_movimiento")



    object MisCuentas : Screen("mis_cuentas")
    object Transferir : Screen("transferir")
    object Historial : Screen("historial")

    // ✅ Detalle con parámetro {movimientoId}
    object DetalleMovimiento : Screen("detalle_movimiento/{movimientoId}") {
        fun createRoute(movimientoId: Long) = "detalle_movimiento/$movimientoId"
    }

    object Estadisticas : Screen("estadisticas")
    object Metas : Screen("metas")


    object AnadirFondos : Screen("anadir_fondos/{metaId}") {
        fun createRoute(metaId: Long) = "anadir_fondos/$metaId"
    }


    object Perfil : Screen("perfil")
    object MisDatos : Screen("mis_datos")
    object SeguridadCuenta : Screen("seguridad_cuenta")
    object Escanear : Screen("escanear")
    object ComprobanteRevision : Screen("comprobante_revision")

    companion object {
        val values = listOf(
            Splash, Login, Registro, Dashboard, MisCuentas, Transferir,
            Historial, DetalleMovimiento, Estadisticas, Metas, AnadirFondos,
            Perfil, MisDatos, SeguridadCuenta, Escanear, ComprobanteRevision
        )
    }
}