package ai.toolio.app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatInputView(
    onSendMessage: (String) -> Unit,
    onVoiceClick: () -> Unit,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean = true
) {
    // Убираем Surface, чтобы не накладывал фон + паддинги
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFA5C65), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) // если надо фон всё же
    ) {
        MessageInput(
            onSendMessage = onSendMessage,
            modifier = Modifier.fillMaxWidth(),
            enabled = isInputEnabled
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPhotoClick, enabled = isInputEnabled) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Attach photo",
                    tint = Color.Black
                )
            }

            IconButton(onClick = onVoiceClick, enabled = isInputEnabled) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        enabled = enabled,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Ask anything",
                color = Color.White
            )
        },
        singleLine = true,
        textStyle = TextStyle(color = Color.White),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        trailingIcon = {
            if (text.isNotBlank()) {
                IconButton(
                    onClick = {
                        onSendMessage(text)
                        text = ""
                    },
                    enabled = enabled
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = ""
                }
            }
        )
    )
}

@Preview
@Composable
fun ChatInputViewPreview() {
    ChatInputView(
        onSendMessage = {},
        onVoiceClick = {},
        onPhotoClick = {}
    )
}