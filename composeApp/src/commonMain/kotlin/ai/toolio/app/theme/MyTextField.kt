package ai.toolio.app.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Regular

@Composable
fun MyTextField(
    nicknameState: String,
    onNicknameChange: (String) -> Unit,
) {
    TextField(
        value = nicknameState,
        onValueChange = {
            onNicknameChange(it)
        },
        placeholder = {
            Text(
                text = "Enter your nickname",
                color = Color(0xFFCCCCCC), // светло-серый как на скрине
                fontFamily = FontFamily(
                    Font(Res.font.Satoshi_Regular)
                )
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
            .clip(RoundedCornerShape(50)) // капсульное скругление
            .background(Color(0xFF616161)), // фон
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = Color(0xFFCCCCCC),
            unfocusedPlaceholderColor = Color(0xFFCCCCCC),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            fontFamily = FontFamily(
                Font(Res.font.Satoshi_Regular)
            ),
            fontSize = 16.sp
        )
    )

}