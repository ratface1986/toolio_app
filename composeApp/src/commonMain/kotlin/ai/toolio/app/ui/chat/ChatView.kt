package ai.toolio.app.ui.chat

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.ui.sidemenu.SideMenu
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ChatView(
    onBack: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // State for messages and loading
    val messages = remember {
        mutableStateListOf<ChatMessage>(
            ChatMessage.Text("Hello! How can I help you today?", isUser = false)
        )
    }
    var isLoading by remember { mutableStateOf(false) }

    fun sendMessage(text: String) {
        isLoading = true
        try {
            scope.launch {
                println("Sending message to GPT: $text")
                val response = AppEnvironment.repo.chatGpt(text)
                messages.add(ChatMessage.Text(content = response.content, isUser = true))
            }
        } finally {
            isLoading = false
        }
    }

    fun uploadUserPhoto(imageBytes: ByteArray) {
        isLoading = true
        try {
            scope.launch {
                val chatResponse = AppEnvironment.repo.chatGpt(prompt = "", imageBytes = imageBytes)
                chatResponse.imageUrl?.let { url ->
                    messages.add(ChatMessage.Image(url, isUser = true))
                }
                messages.add(ChatMessage.Text(chatResponse.content, isUser = false))
            }
        } finally {
            isLoading = false
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SideMenu(
                    onAccountsClick = {
                        scope.launch { drawerState.close() }
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ChatView(
                    messagesInput = messages,
                    isLoading = isLoading,
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .fillMaxWidth()
                )

                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                ) {
                    ChatInputView(
                        onSendMessage = { text ->
                            messages.add(ChatMessage.Text(text, isUser = true))
                            sendMessage(text)
                        },
                        onAttachmentClick = { },
                        onVoiceClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        isInputEnabled = !isLoading,
                        onPhotoClick = {
                            AppEnvironment.nativeFeatures.photoPicker.pickPhoto { photoBytes ->
                                photoBytes?.let {
                                    uploadUserPhoto(photoBytes)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}