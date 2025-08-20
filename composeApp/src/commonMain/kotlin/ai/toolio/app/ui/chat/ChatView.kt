package ai.toolio.app.ui.chat

import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.misc.Roles
import ai.toolio.app.misc.SessionType
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import ai.toolio.app.ui.shared.SessionEndDialog
import ai.toolio.app.ui.sidemenu.SideMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalEncodingApi::class, ExperimentalTime::class)
@Composable
fun ChatView(
    onShowSettings: () -> Unit,
    onBack: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            AppEnvironment.userProfile.sessions.lastOrNull()?.messages
                ?.filter { it.role != Roles.SYSTEM }
                ?.forEach { lastMessage ->
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


    LaunchedEffect(AppEnvironment.getSessionId()) {
        val session = AppEnvironment.userProfile.sessions.lastOrNull()

        if (AppEnvironment.getSessionId().isNotEmpty() && session?.messages?.isEmpty() == true) {
            val response = try {
                AppEnvironment.repo.sendInitialSystemPrompt(
                    systemPrompt = session.initialPrompt
                )
            } catch (e: Exception) {
                println("🔥 Failed to call /openai-system: ${e.message}")
                null
            }

            if (response != null) {
                messages.add(ChatMessage.Text(response.content, isUser = false))
            }
        }
    }

    var isTypingLoading by remember { mutableStateOf(false) }
    val inputHeightPx = remember { mutableStateOf(0) }
    var showOnStopDialog by remember { mutableStateOf(false) }
    var showSpeakOverlay by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    val isPremiumSession by remember { mutableStateOf(AppEnvironment.userProfile.sessions.lastOrNull()?.sessionType == SessionType.PREMIUM) }
    val snackbarHostState = remember { SnackbarHostState() }

    fun sendMessage(text: String) {
        scope.launch {
            isTypingLoading = true
            try {
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
                        status = status
                    )
                )
                AppEnvironment.repo.saveNewSession(AppEnvironment.userProfile.sessions.last())
            } catch (e: Exception) {
                println("Error saving new repair task session: ${e.message}")
            } finally {
                onBack()
            }
        }
    }

    fun transcriptVoice(audioBytes: ByteArray) {
        scope.launch {
            try {
                val chatResponse = AppEnvironment.repo.transcribeSpeech(audioBytes = audioBytes)
                messages.add(ChatMessage.Text(chatResponse.content, isUser = true))
                sendMessage(chatResponse.content)
            } finally {
                //isTypingLoading = false
            }
        }
    }

    if (showOnStopDialog) {
        SessionEndDialog(
            onAbort = {
                updateTaskSession(TaskStatus.ABORTED)
            },
            onDone = {
                updateTaskSession(TaskStatus.COMPLETED)
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
                        onPhotoClick = {
                            AppEnvironment.nativeFeatures.mediaManager.pickPhoto { photoBytes ->
                                photoBytes?.let { uploadUserPhoto(photoBytes) }
                            }
                        },
                        isInputEnabled = !isTypingLoading,
                        shouldShowPremiumButtons = isPremiumSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                inputHeightPx.value = it.size.height
                            }
                    )
                }
            }
        }

        if (showSpeakOverlay) {
            VoiceRecordingOverlay()
        }

        if (isPremiumSession) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val haptic = LocalHapticFeedback.current

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    startTime = Clock.System.now().toEpochMilliseconds()
                                    showSpeakOverlay = true
                                    AppEnvironment.nativeFeatures.mediaManager.startRecording()

                                    try {
                                        val releasedInside = tryAwaitRelease() // false если cancel
                                        val durationMs = Clock.System.now().toEpochMilliseconds() - startTime

                                        if (releasedInside && durationMs >= 1000) {
                                            AppEnvironment.nativeFeatures.mediaManager.stopRecording { audioBytes ->
                                                audioBytes?.let {
                                                    isTypingLoading = true
                                                    transcriptVoice(it)
                                                }
                                            }
                                        } else {
                                            // отмена или слишком короткая запись
                                            AppEnvironment.nativeFeatures.mediaManager.stopRecording { }
                                            haptic.performHapticFeedback(
                                                HapticFeedbackType.LongPress)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Recording too short")
                                            }
                                        }
                                    } finally {
                                        showSpeakOverlay = false
                                    }
                                }
                            )
                        }
                ) {
                    if (showSpeakOverlay) {
                        Box(
                            modifier = Modifier
                                .offset(x = 20.dp, y = 20.dp)
                                .size(100.dp)
                                .background(Color(0xFFFF9800), shape = CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
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
