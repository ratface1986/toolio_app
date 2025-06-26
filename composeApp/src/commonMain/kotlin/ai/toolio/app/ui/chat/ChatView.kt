package ai.toolio.app.ui.chat

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.ui.sidemenu.SideMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    val inputHeightPx = remember { mutableStateOf(0) }

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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F403E),
                        Color(0xFF1A1C1D)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = Color.Transparent
            ) {
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
                        // Сообщения
                        ChatMessagesView(
                            messagesInput = messages,
                            isLoading = isLoading,
                            onMenuClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = with(LocalDensity.current) { inputHeightPx.value.toDp() })
                        )

                        // Ввод внизу
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .imePadding()
                                .background(Color(0xFF1A1A1A)) // задаём нужный фон прямо тут
                        ) {
                            ChatInputView(
                                onSendMessage = { text ->
                                    messages.add(ChatMessage.Text(text, isUser = true))
                                    sendMessage(text)
                                },
                                onVoiceClick = {},
                                onPhotoClick = {
                                    AppEnvironment.nativeFeatures.photoPicker.pickPhoto { photoBytes ->
                                        photoBytes?.let { uploadUserPhoto(photoBytes) }
                                    }
                                },
                                isInputEnabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .onGloballyPositioned {
                                        inputHeightPx.value = it.size.height
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FullChatPreview() {
    ChatView( {  })
}
