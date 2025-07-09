package ai.toolio.app.ui.dashboard

import ai.toolio.app.di.SubscriptionManager
import ai.toolio.app.di.SubscriptionManager.purchase
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.BodyTextLarge
import ai.toolio.app.theme.HeadlineMediumText
import ai.toolio.app.theme.TitleText
import ai.toolio.app.ui.shared.ScreenWrapper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.toolio_pack_1
import toolio.composeapp.generated.resources.toolio_pack_2
import toolio.composeapp.generated.resources.toolio_pack_3
import toolio.composeapp.generated.resources.toolio_pack_4

data class ToolioPack(
    val title: String,
    val description: String,
    val price: String,
    val productId: String,
    val backgroundColor: Color,
    val imageRes: DrawableResource
)

@Composable
fun SubscriptionScreen(onBackClick: () -> Unit) {

    val scope = rememberCoroutineScope()

    fun subscribe(productId: String) {
        scope.launch {
            try {
                val info = SubscriptionManager.getCustomerInfo()
                println("MYDATA info: ${info.activeProductIds}")
                val result = SubscriptionManager.purchase(productId)
                println("MYDATA result: $result")
            } catch (e: Exception) {
                println("MYDATA Subscription error: ${e.message}")
                e.printStackTrace()
            } finally {
                //showSheet = false
            }
        }
    }

    val packs = listOf(
        ToolioPack(
            "Starter Pack",
            "10 text + 1 premium session",
            "$2.99",
            "ai.tooio.app.starter_pack",
            Color(0xFF6aa5c0),
            Res.drawable.toolio_pack_1
        ),
        ToolioPack(
            "Weekly Helper",
            "20 text + 3 premium sessions",
            "$4.99",
            "ai.tooio.app.weekly_helper",
            Color(0xFFffbd7c),
            Res.drawable.toolio_pack_2
        ),
        ToolioPack(
            "Home Master",
            "50 text + 10 premium sessions",
            "$14.99",
            "ai.tooio.app.home_master",
            Color(0xFF4d9ca1),
            Res.drawable.toolio_pack_3
        ),
        ToolioPack(
            "Unlimited 30 Days",
            "Unlimited text & premium for 1 month",
            "$29.99",
            "ai.tooio.app.unlimited_30_days",
            Color(0xFF335e76),
            Res.drawable.toolio_pack_4
        )
    )

    var currentIndex by remember { mutableIntStateOf(0) }

    ScreenWrapper(
        modifier = Modifier
            .fillMaxSize(),
        useCustomBackground = true,
        customBackgroundColor = Color(0xFF141414)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Заголовок
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBackClick)
                Spacer(Modifier.width(16.dp))
                HeadlineMediumText("Enjoy more DIY sessions", Color.White)
            }

            // Список пакетов
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(packs) { pack ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = pack.backgroundColor
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(pack.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .padding(end = 16.dp)
                                )
                                Column {
                                    TitleText(pack.title)
                                    Spacer(Modifier.height(4.dp))
                                    BodyTextLarge(pack.description)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { subscribe(pack.productId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Buy for ${pack.price}")
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
fun SubscriptionScreenPreview() {
    SubscriptionScreen(onBackClick = {})
}