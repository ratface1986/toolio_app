package ai.toolio.app.ui.inventory

import ai.toolio.app.models.FollowUpQuestion
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.HeadlineMediumText
import ai.toolio.app.theme.ListItemView
import ai.toolio.app.ui.shared.ScreenWrapper
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuestionsView(
    followUpQuestions: List<FollowUpQuestion>,
    onComplete: (List<Pair<FollowUpQuestion, String>>) -> Unit,
    onBack: () -> Unit,
) {
    var currentIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<Pair<FollowUpQuestion, String>>() }

    val currentQuestion = followUpQuestions.getOrNull(currentIndex) ?: return

    ScreenWrapper {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            AnimatedContent(
                targetState = currentQuestion,
                label = "FollowUpTransition",
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                }
            ) { question ->
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackButton(onClick = onBack)
                        Spacer(Modifier.width(16.dp))
                        HeadlineMediumText(currentQuestion.question)
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        question.options.forEach { option ->
                            ListItemView(
                                text = option,
                                textColor = Color.White,
                                alignment = Alignment.Center,
                                isLarge = true,
                                onClick = {
                                    answers.add(question to option)
                                    if (currentIndex + 1 < followUpQuestions.size) {
                                        currentIndex++
                                    } else {
                                        onComplete(answers.toList())
                                    }
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
fun PreviewQuestionsView() {
    QuestionsView(
        followUpQuestions = listOf(
            FollowUpQuestion.WallTypeQuestion,
        ),
        { _ -> },
        onBack = {}
    )
}