package ai.toolio.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Your primary colors
private val Background = Color(0xFF1C1F1D)       // тёмный фон (как на 2–3 экране)
private val Primary = Color(0xFF9ED586)          // зелёный (слово "capabilities")
private val PrimaryVariant = Color(0xFF7CAB6C)   // чуть темнее
private val Surface = Color(0xFF2A2D2B)          // карточки, поля
private val Secondary = Color(0xFFB4B4B4)        // иконки, вторичный текст
private val OnPrimary = Color.Black              // текст на зелёных кнопках
private val OnSurface = Color.White              // текст на фоне
private val Outline = Color(0xFF3C403D)          // границы и разделители

val AppDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,

    secondary = Secondary,
    onSecondary = Color.Black,

    /*surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Background,
    onSurfaceVariant = OnSurface,

    background = Background,
    onBackground = OnSurface,

    outline = Outline*/
)
/*


val AppDarkColorScheme = darkColorScheme(
    primary = InteractiveColor,
    onPrimary = DarkerBackground,
    primaryContainer = DarkerInteractive,
    onPrimaryContainer = DarkerBackground,
    
    secondary = MiddleColor,
    onSecondary = DarkerBackground,
    secondaryContainer = MiddleColor,
    onSecondaryContainer = DarkerBackground,
    
    surface = BackgroundColor,
    onSurface = Color.White,
    surfaceVariant = DarkerBackground,
    onSurfaceVariant = Color.White,
    
    background = DarkerBackground,
    onBackground = Color.White,
    
    outline = DarkerInteractive
)*/
