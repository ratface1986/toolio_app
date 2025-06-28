package ai.toolio.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenWrapper(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    useGradientBackground: Boolean = false,
    content: @Composable BoxScope.(PaddingValues) -> Unit
) {
    val backgroundModifier = if (useGradientBackground) {
        Modifier.background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2F403E),
                    Color(0xFF1A1C1D)
                )
            )
        )
    } else {
        Modifier.background(Color(0xFF282F32))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(backgroundModifier)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = Color.Transparent
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content(innerPadding)
                    LoadingScreen(isLoading)
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Loading...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

