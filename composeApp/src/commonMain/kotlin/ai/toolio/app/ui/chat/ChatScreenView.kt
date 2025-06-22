package ai.toolio.app.ui.chat

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.utils.io.ByteReadChannel

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
fun ChatView(
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp),
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
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        when (message) {
            is ChatMessage.Text -> {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            is ChatMessage.Image -> {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = backgroundColor,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(200.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .aspectRatio(3f / 4f) // вертикальное фото
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        KamelImage(
                            resource = asyncPainterResource(data = message.imageUrl),
                            contentDescription = "Shared image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
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