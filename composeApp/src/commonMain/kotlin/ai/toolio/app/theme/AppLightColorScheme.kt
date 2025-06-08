package ai.toolio.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Your primary colors
private val BackgroundColor = Color(0xFF474954)
private val InteractiveColor = Color(0xFF7CDEDC)
private val MiddleColor = Color(0xFF62B3B8)

// Additional shades for better contrast and hierarchy
private val DarkerBackground = Color(0xFF373943)
private val LighterBackground = Color(0xFF575A66)
private val DarkerInteractive = Color(0xFF5FB3B1)
private val LighterInteractive = Color(0xFF8FE5E3)

val AppLightColorScheme = lightColorScheme(
    primary = InteractiveColor,
    onPrimary = Color.White,
    primaryContainer = DarkerInteractive,
    onPrimaryContainer = Color.White,
    
    secondary = MiddleColor,
    onSecondary = Color.White,
    secondaryContainer = MiddleColor,
    onSecondaryContainer = Color.White,
    
    surface = BackgroundColor,
    onSurface = Color.White,
    surfaceVariant = LighterBackground,
    onSurfaceVariant = Color.White,
    
    background = BackgroundColor,
    onBackground = Color.White,
    
    outline = LighterInteractive
)

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
)