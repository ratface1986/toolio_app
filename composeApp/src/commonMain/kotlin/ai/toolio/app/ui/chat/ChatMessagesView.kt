package ai.toolio.app.ui.chat

import ai.toolio.app.ui.shared.CrossPlatformImage
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Regular


sealed class ChatMessage(open val isUser: Boolean) {
    data class Text(
        val content: String,
        override val isUser: Boolean
    ) : ChatMessage(isUser)

    data class Image(
        val imageUrl: String,
        override val isUser: Boolean
    ) : ChatMessage(isUser)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatMessagesView(
    messagesInput: List<ChatMessage>,
    onMenuClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messagesInput.size) {
        if (messagesInput.isNotEmpty()) {
            listState.animateScrollToItem(messagesInput.lastIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F403E),
                        Color(0xFF1A1C1D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(
                    top = 8.dp,
                    bottom = 100.dp // чтобы последнее сообщение не уехало под инпут
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messagesInput) { message ->
                    ChatMessageItem(message = message)
                }

                if (isLoading) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                TypingLoading()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser

    val alignment = if (isUser) Alignment.End else Alignment.Start

    val customFont = FontFamily(
        Font(
            resource = Res.font.Satoshi_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 6.dp),
        horizontalAlignment = alignment
    ) {
        when (message) {
            is ChatMessage.Text -> {
                if (isUser) {
                    Text(
                        text = message.content,
                        color = Color.White,
                        fontFamily = customFont,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(0.70f)
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier
                            .padding(4.dp) // чтобы не прилипал к краям
                    ) {
                        Text(
                            text = message.content,
                            color = Color.Black,
                            modifier = Modifier.padding(12.dp),
                            softWrap = true
                        )
                    }
                }
            }

            is ChatMessage.Image -> {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(200.dp)
                        .fillMaxWidth(0.95f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        CrossPlatformImage(url = message.imageUrl)
                    }
                }
            }
        }
    }
}


@Composable
private fun TypingLoading(
    modifier: Modifier = Modifier
) {
    val dotDelay = listOf(0, 150, 300)
    val waveHeight = 7.dp
    val baseSize = 10.dp

    val infiniteTransition = rememberInfiniteTransition()

    val animatedHeights = dotDelay.map { delay ->
        infiniteTransition.animateFloat(
            initialValue = baseSize.value,
            targetValue = (baseSize + waveHeight).value,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 550, delayMillis = delay, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val animatedColors = dotDelay.map { delay ->
        infiniteTransition.animateColor(
            initialValue = Color.Gray.copy(alpha = 0.4f),
            targetValue = Color.Gray,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 550, delayMillis = delay, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
            // Remove label parameter for KMP compatibility
        )
    }

    Row(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        animatedHeights.zip(animatedColors).forEach { (heightAnim, colorAnim) ->
            Canvas(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(12.dp, heightAnim.value.dp)
                    .clip(RoundedCornerShape(50)),
                onDraw = {
                    drawCircle(
                        color = colorAnim.value,
                        radius = size.minDimension / 2
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun ChatViewPreviewNew() {
    ChatMessagesView(
        messagesInput = listOf(
            ChatMessage.Text("Hello", true),
            ChatMessage.Text("How are you doing?\nhere is the few cool things for you. Lets die into it!", false),
            ChatMessage.Text("I'm doing fine, thanks!", true),
        ),
        onMenuClick = {},
        modifier = Modifier.fillMaxSize(),
        isLoading = false
    )
}