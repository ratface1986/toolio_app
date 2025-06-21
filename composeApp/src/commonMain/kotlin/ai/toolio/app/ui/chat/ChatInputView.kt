package ai.toolio.app.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun ChatInputView(
    onSendMessage: (String) -> Unit,
    onAttachmentClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInputEnabled: Boolean = true
) {
    Row(
        modifier = modifier.padding(bottom = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onPhotoClick,
            enabled = isInputEnabled
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Attach Photo"
            )
        }

        MessageInput(
            onSendMessage = onSendMessage,
            modifier = Modifier.weight(1f),
            enabled = isInputEnabled
        )

        IconButton(
            onClick = onVoiceClick,
            enabled = isInputEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice input"
            )
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true // Accept enabled
) {
    var text by remember { mutableStateOf("") }
    
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier,
        singleLine = true,
        enabled = enabled, // Pass enabled to TextField
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = KeyboardActions(
            onSend = {
                if (text.isNotBlank() && enabled) {
                    onSendMessage(text)
                    text = ""
                }
            }
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        placeholder = { Text("Type a message") },
        trailingIcon = {
            if (text.isNotBlank()) {
                IconButton(
                    onClick = {
                        if (enabled) {
                            onSendMessage(text)
                            text = ""
                        }
                    },
                    enabled = enabled
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    )
}