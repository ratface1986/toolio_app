package ai.toolio.app

import ai.toolio.app.di.AppSessions
import ai.toolio.app.models.Tasks
import ai.toolio.app.ui.LoginForm
import ai.toolio.app.ui.MainScreenController
import ai.toolio.app.ui.theme.AppDarkColorScheme
import ai.toolio.app.utils.NativeFeatures
import ai.toolio.app.utils.PhotoPicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun App(nativeFeatures: NativeFeatures) {

    LaunchedEffect(Unit) {
        // при старте
        AppSessions.setLastActiveTimestamp(Clock.System.now().toEpochMilliseconds())
    }

    DisposableEffect(Unit) {
        onDispose {
            // при закрытии / сворачивании
            AppSessions.setLastActiveTimestamp(Clock.System.now().toEpochMilliseconds())
        }
    }

    val Satoshi = FontFamily(
        Font(resource = Res.font.Satoshi_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
        Font(resource = Res.font.Satoshi_Black, weight = FontWeight.Bold),
        Font(resource = Res.font.Satoshi_Medium, weight = FontWeight.Medium),
        Font(resource = Res.font.Satoshi_Bold, weight = FontWeight.SemiBold),
        Font(resource = Res.font.Satoshi_Light, weight = FontWeight.Light)
    )

    val AppTypography = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Medium, weight = FontWeight.Medium)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 22.sp
        ),
        titleLarge = TextStyle(
            fontFamily = Satoshi,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        ),
        labelMedium = TextStyle(
            fontFamily = Satoshi,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily(Font(resource = Res.font.Satoshi_Black, weight = FontWeight.Bold)),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineMedium = TextStyle(
            fontFamily = Satoshi,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        ),
        bodyMedium = TextStyle(
            fontFamily = Satoshi,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
        // добавь другие стили при необходимости
    )

    var onLoginSuccess by mutableStateOf(false)

    MaterialTheme(
        colorScheme = AppDarkColorScheme,
        typography = AppTypography
    ) {
        if (onLoginSuccess) {
            MainScreenController(
                categories = Tasks.categories
            )
        } else {
            LoginForm(
                nativeFeatures = nativeFeatures,
                onLoginSuccess = { onLoginSuccess = true }
            )
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App(
        nativeFeatures = NativeFeatures(photoPicker = object : PhotoPicker {
            override fun pickPhoto(onResult: (ByteArray?) -> Unit) {
                TODO("Not yet implemented")
            }
        })
    )
}