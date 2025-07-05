package ai.toolio.app.ui.chat

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.misc.Roles
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import ai.toolio.app.ui.shared.SessionEndDialog
import ai.toolio.app.ui.sidemenu.SideMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ChatView(
    onShowSettings: () -> Unit,
    onBack: () -> Unit
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            AppEnvironment.userProfile.sessions.lastOrNull()?.messages?.forEach { lastMessage ->
                val isUser = lastMessage.role == Roles.USER
                val message = if (lastMessage.imageUrl?.isEmpty() == true) {
                    ChatMessage.Text(lastMessage.content, isUser = isUser)
                } else {
                    ChatMessage.Image(lastMessage.imageUrl ?: "", isUser = isUser)
                }
                add(message)
            }
        }
    }
    var isTypingLoading by remember { mutableStateOf(false) }
    val inputHeightPx = remember { mutableStateOf(0) }
    var showOnStopDialog by remember { mutableStateOf(false) }

    fun sendMessage(text: String) {
        scope.launch {
            isTypingLoading = true
            try {
                println("Sending message to GPT: $text")
                val response = AppEnvironment.repo.chatGpt(text)
                messages.add(ChatMessage.Text(content = response.content, isUser = false))
            } finally {
                isTypingLoading = false
            }
        }
    }

    fun uploadUserPhoto(imageBytes: ByteArray) {
        scope.launch {
            isTypingLoading = true
            try {
                val chatResponse = AppEnvironment.repo.chatGpt(prompt = "", imageBytes = imageBytes)
                chatResponse.imageUrl?.let { url ->
                    messages.add(ChatMessage.Image(url, isUser = true))
                }
                messages.add(ChatMessage.Text(chatResponse.content, isUser = false))

            } finally {
                isTypingLoading = false
            }
        }
    }

    fun updateTaskSession(status: TaskStatus) {
        scope.launch {
            try {
                AppEnvironment.updateSession(
                    task = AppEnvironment.userProfile.sessions.last().task.copy(
                        status = status,
                    ),
                )
                AppEnvironment.repo.saveNewSession(AppEnvironment.userProfile.sessions.last())
            } catch (e: Exception) {
                println("Error saving new repair task session: ${e.message}")
            }
        }
    }

    if (showOnStopDialog) {
        SessionEndDialog(
            onAbort = {
                updateTaskSession(TaskStatus.ABORTED)
                onBack()
            },
            onDone = {
                updateTaskSession(TaskStatus.COMPLETED)
                onBack()
             },
            onDismiss = { showOnStopDialog = false }
        )
    } else {


        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color(0xFF000000)
                ) {
                    SideMenu(
                        onAccountsClick = {
                            scope.launch { drawerState.close() }
                        },
                        onSettingsClick = {
                            scope.launch { onShowSettings() }
                        },
                        onExitClick = {
                            scope.launch { onBack() }
                        }
                    )
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ChatMessagesView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = with(LocalDensity.current) { inputHeightPx.value.toDp() }),
                    messagesInput = messages,
                    isWaitForAI = isTypingLoading,
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onStopClick = { showOnStopDialog = true }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFFf79d53)) // задаём нужный фон прямо тут
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
                        isInputEnabled = !isTypingLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                inputHeightPx.value = it.size.height
                            }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun FullChatPreview() {
    AppEnvironment.setUserProfile(
        UserProfile(
            userId = "123456789",
            inventory = mapOf(),
            settings = UserSettings(
                "123456789",
                "test",
                "en",
                MeasureType.INCH
            ),
            sessions = mutableListOf(
                RepairTaskSession(
                    sessionId = "123",
                    title = "test"
                )
            )
        )
    )
    ChatView({}, { })
}
