package ai.toolio.app.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import ai.toolio.app.ui.wizard.model.FollowUpQuestion
import ai.toolio.app.models.WallType
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuestionsView(
    followUpQuestions: List<FollowUpQuestion>,
    onConfirm: (FollowUpQuestion, String) -> Unit
) {
    if (followUpQuestions.isEmpty()) return

    val question = followUpQuestions.firstOrNull() ?: return

    val options = when (question) {
        is FollowUpQuestion.WallTypeQuestion -> question.options.map { it.toString() }
        is FollowUpQuestion.TvSizeQuestion -> question.options.map { it.toString() }
        is FollowUpQuestion.ShelfTypeQuestion -> listOf("Wall shelf", "Corner shelf", "Floating shelf")
        is FollowUpQuestion.WindowWidthQuestion -> listOf("Small (<100cm)", "Medium (100-180cm)", "Large (>180cm)")
        is FollowUpQuestion.WeightClassQuestion -> listOf("Light (<5kg)", "Medium (5-20kg)", "Heavy (>20kg)")
        is FollowUpQuestion.OutletTypeQuestion -> listOf("Type A", "Type B", "Type C", "Other")
        is FollowUpQuestion.LockTypeQuestion -> listOf("Deadbolt", "Padlock", "Smart Lock", "Other")
        is FollowUpQuestion.CeilingTypeQuestion -> listOf("Drywall", "Concrete", "Wood", "Other")
        is FollowUpQuestion.LightTypeQuestion -> listOf("Ceiling light", "Wall sconce", "Lamp", "Other")
        is FollowUpQuestion.DrainTypeQuestion -> listOf("Sink", "Bathtub", "Floor", "Other")
    }

    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, bottom = 100.dp), // enough space for confirm
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 32.dp)
                    .fillMaxWidth(),
                fontSize = 25.sp
            )

            Column(
                modifier = Modifier.weight(1f, fill = false)
            ) {
                options.forEachIndexed { idx, option ->
                    val isSelected = idx == selectedOptionIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                else Color(0xFFF2F2F2)
                            )
                            .clickable {
                                selectedOptionIndex = idx
                            }
                            .padding(vertical = 18.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = option,
                            fontSize = 18.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (selectedOptionIndex != null) {
                        onConfirm(question, options[selectedOptionIndex!!])
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = selectedOptionIndex != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedOptionIndex != null)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Confirm", fontSize = 19.sp)
            }
        }
    }
}

@Preview
@Composable
fun PreviewQuestionsView() {
    QuestionsView(
        followUpQuestions = listOf(
            FollowUpQuestion.WallTypeQuestion(WallType.entries),
        ),
        {_, _ -> }
    )
}