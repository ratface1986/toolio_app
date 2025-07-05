package ai.toolio.app.ui.inventory

import ai.toolio.app.di.openUrlInBrowser
import ai.toolio.app.models.Tool
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.HeadlineMediumText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.*

enum class StoreType {
    AMAZON, EBAY, GOOGLE_MAPS
}

@Composable
fun SearchTool(
    tool: Tool,
    onBack: () -> Unit
) {
    fun onStoreButtonClick(store: StoreType) {
        val query = tool.displayName.replace(" ", "+").lowercase()

        val url = when (store) {
            StoreType.AMAZON -> "https://www.amazon.com/s?k=$query"
            StoreType.EBAY -> "https://www.ebay.com/sch/i.html?_nkw=$query"
            StoreType.GOOGLE_MAPS -> "https://www.google.com/maps/search/$query"
        }

        openUrlInBrowser(url)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF95d7e7))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
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
                HeadlineMediumText("Search tools you need")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(Res.drawable.no_tools),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Look for ${tool.displayName} on:",
                fontSize = 26.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp),
                maxLines = 2,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StoreButton(
                    icon = Res.drawable.amazon_logo,
                    backColor = Color(0xFF089acc),
                    buttonText = "Amazon",
                    onClick = {
                        onStoreButtonClick(StoreType.AMAZON)
                    }
                )
                StoreButton(
                    icon = Res.drawable.ebay_logo,
                    backColor = Color.White,
                    buttonText = "Ebay",
                    onClick = {
                        onStoreButtonClick(StoreType.EBAY)
                    }
                )
                StoreButton(
                    icon = Res.drawable.maps_logo,
                    backColor = Color(0xFF72B340),
                    buttonText = "Nearby on Maps",
                    onClick = {
                        onStoreButtonClick(StoreType.GOOGLE_MAPS)
                    }
                )
            }


        }
    }
}

@Composable
fun StoreButton(
    icon: DrawableResource,
    backColor: Color,
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backColor)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified // чтобы была цветной
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buttonText,
            fontSize = 16.sp,
            color = Color.Black
        )
    }

}

@Preview
@Composable
fun PreviewSearchTool() {
    SearchTool(
        tool = Tool.UTILITY_KNIFE,
        onBack = {}
    )
}