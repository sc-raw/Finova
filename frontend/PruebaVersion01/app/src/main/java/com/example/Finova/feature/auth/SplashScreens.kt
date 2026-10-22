package com.example.Finova.feature.auth
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Finova.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Temporizador de 2 segundos para avanzar a la siguiente pantalla
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF027F71)), // Color verde azulado de tu diseño
        contentAlignment = Alignment.Center
    ) {

        // Contenedor del Logo con la escala corregida
        Surface(
            modifier = Modifier.size(150.dp), // Subimos a 150dp para que tenga mejor presencia física
            shape = RoundedCornerShape(32.dp), // Esquinas redondeadas suaves como tu Figma
            color = Color(0xFF027F71), // Color de fondo idéntico al de tu logo
            shadowElevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(10.dp) // <-- Este margen evita que el logo toque los bordes del recuadro
            ) {
                Image(
                    painter = painterResource(id = R.drawable.screen),
                    contentDescription = "Finova Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }



        // Texto de versión posicionado exactamente abajo
        Text(
            text = "VERSIÓN 0.0.1",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter) //
                .padding(bottom = 48.dp)
        )
    }
}


// Pantalla breve que se muestra después de validar correctamente el inicio de sesión.
@Composable
fun CargandoFinovaScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(900)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.Finova.core.design.theme.FinovaBackground),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            FinovaLogo(size = 88)
            androidx.compose.foundation.layout.Spacer(Modifier.size(24.dp))
            Text(
                text = "Finova",
                color = com.example.Finova.core.design.theme.FinovaOnSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(10.dp))
            Text(
                text = "Preparando tus finanzas...",
                color = com.example.Finova.core.design.theme.FinovaOnSurfaceVariant,
                fontSize = 14.sp
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(24.dp))
            androidx.compose.material3.CircularProgressIndicator(
                color = com.example.Finova.core.design.theme.FinovaPrimary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
