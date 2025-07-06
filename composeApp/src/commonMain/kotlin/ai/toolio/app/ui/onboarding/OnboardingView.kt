package ai.toolio.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import toolio.composeapp.generated.resources.*

data class OnboardingStep(
    val title: String,
    val backgroundColor: Color,
    val imageRes: DrawableResource
)


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingView(
    onConfirm: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val steps = listOf(
        OnboardingStep("TVs, shelves, mystery holes in walls — we’ve got you.", Color(0xFFFDE9CB), Res.drawable.onboarding_1),
        OnboardingStep("Wrench? Drill? Maybe a brave heart. Toolio helps you prep.", Color(0xFF9DBF82), Res.drawable.onboarding_2),
        OnboardingStep("Toolio shows what to do — you just do it.", Color(0xFFF79E54), Res.drawable.onboarding_3),
        OnboardingStep("Send a pic or talk — we’ll figure it out together.", Color(0xFFD8654C), Res.drawable.onboarding_4)
    )

    val animatedColor by animateColorAsState(
        targetValue = steps[currentIndex].backgroundColor,
        animationSpec = tween(durationMillis = 500),
        label = "animated bg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedColor) // 👈 сюда фон
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            },
            modifier = Modifier.fillMaxSize()
        ) { index ->
            OnboardingStepContent(
                step = steps[index],
                isLast = index == steps.lastIndex,
                onNext = {
                    if (index == steps.lastIndex) onConfirm() else currentIndex++
                }
            )
        }
    }
}

@Composable
private fun OnboardingStepContent(
    step: OnboardingStep,
    isLast: Boolean,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(step.backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(step.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = step.title,
                fontSize = 26.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp),
                maxLines = 2,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = if (isLast) "Get Started" else "Next",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}