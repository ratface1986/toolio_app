package ai.toolio.app.ui.sidemenu

import ai.toolio.app.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.Satoshi_Regular

@Composable
fun SettingsView(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    language: String,
    languages: List<String>,
    onLanguageChange: (String) -> Unit,
    useMm: Boolean,
    onUnitsChange: (Boolean) -> Unit,
    onDeleteAllData: () -> Unit,
    onBack: () -> Unit,
) {
    var nicknameState by remember { mutableStateOf(nickname) }
    var expandLangDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row {
            BackButton { onBack.invoke() }
            Spacer(modifier = Modifier.width(16.dp))
            HeadlineLargeText("Settings")
        }

        Spacer(modifier = Modifier.height(32.dp))
        // Nickname
        TitleMediumText("Nickname")
        Spacer(modifier = Modifier.height(8.dp))
        MyTextField(
            nicknameState = nicknameState,
            onNicknameChange = {
                onNicknameChange(it)
            }
        )

        // Language
        TitleMediumText(text = "Voice Language")
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)) {
            OutlinedButton(
                onClick = { expandLangDropdown = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BodyText(text = language)
            }
            DropdownMenu(
                expanded = expandLangDropdown,
                onDismissRequest = { expandLangDropdown = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = {
                            BodyText(text = lang)
                        },
                        onClick = {
                            onLanguageChange(lang)
                            expandLangDropdown = false
                        }
                    )
                }
            }
        }

        // Units
        TitleMediumText(text = "Units")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            UnitChip(
                text = "mm",
                selected = useMm,
                onClick = { onUnitsChange(true) }
            )
            UnitChip(
                text = "inches",
                selected = !useMm,
                onClick = { onUnitsChange(false) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Danger Zone
        Text(
            text = "Danger Zone",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = onDeleteAllData,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFE5E5),
                contentColor = Color.Red
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "Delete all my data",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UnitChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val customFont = FontFamily(
        Font(
            resource = Res.font.Satoshi_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        )
    )

    Surface(
        color = if (selected) Color.Black else Color.White,
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color.Black,
                fontWeight = FontWeight.Medium,
                fontFamily = customFont
            )
        }
    }
}

@Preview
@Composable
fun PreviewSettingsView() {
    SettingsView(
        nickname = "Toolio",
        onNicknameChange = {},
        language = "English",
        languages = listOf("English", "Deutsch"),
        onLanguageChange = {},
        useMm = true,
        onUnitsChange = {},
        onDeleteAllData = {},
        onBack = {}
    )
}