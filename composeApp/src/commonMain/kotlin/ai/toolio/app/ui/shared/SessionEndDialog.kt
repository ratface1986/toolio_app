package ai.toolio.app.ui.shared

import ai.toolio.app.theme.BodyTextMedium
import ai.toolio.app.theme.TitleText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SessionEndDialog(
    onAbort: () -> Unit,
    onDone: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(Modifier.padding(24.dp)) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TitleText("Update session?")
                        Spacer(Modifier.weight(1f))
                        IconButton(modifier = Modifier.padding(bottom = 12.dp), onClick = { onDismiss() }) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color.Black.copy(alpha = 0.2f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.Black
                                )
                            }
                        }
                    }


                    Spacer(Modifier.height(16.dp))
                    BodyTextMedium("Hit Done if you succeeded, or Abort if you couldn’t finish.", color = Color.Black)
                    Spacer(Modifier.height(32.dp))

                    Box(Modifier.fillMaxWidth()) {
                        // Abort button — bottom-left
                        Box(Modifier.fillMaxWidth()) {
                            // Abort — bottom-left
                            TextButton(
                                onClick = {
                                    onAbort()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFFFA5C65))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Abort", color = Color.White)
                            }

                            // Done + No — bottom-right
                            Row(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { onDone() },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xFF37DC94))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Done!", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun ToolioAlertPreview() {
    SessionEndDialog(
        onDone = {},
        onAbort = {},
        onDismiss = {}
    )
}