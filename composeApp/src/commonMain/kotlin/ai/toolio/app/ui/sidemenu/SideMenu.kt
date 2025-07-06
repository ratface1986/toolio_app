package ai.toolio.app.ui.sidemenu

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDisplayText
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.misc.MeasureType
import ai.toolio.app.models.RepairTaskSession
import ai.toolio.app.models.UserProfile
import ai.toolio.app.models.UserSettings
import ai.toolio.app.theme.TaskView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SideMenu(
    onSettingsClick: () -> Unit = {},
    onExitClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        color = Color(0xFF191a1c)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TasksSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            BottomPanel(
                onSettingsClick = onSettingsClick,
                onExitClick = onExitClick
            )
        }
    }
}

@Composable
private fun TasksSection(
    modifier: Modifier = Modifier
) {
    val sessions = remember { AppEnvironment.userProfile.sessions }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sessions) { session ->
            TaskView(
                header = session.title,
                subHeader = session.task.status.toDisplayText(),
                subHeaderColor = session.task.status.toColor(),
                icon = session.category.type.toDrawableResource()
            )
        }
    }
}

@Composable
private fun BottomPanel(
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1D1E20)) // тёмный фон
            .padding(vertical = 8.dp)
    ) {
        DrawerButton(
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = onSettingsClick
        )

        DrawerButton(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            label = "Exit",
            onClick = onExitClick
        )
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFCBD5E1), // иконка: светло-серая
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            color = Color(0xFFE2E8F0), // текст: светлее
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun SideMenuPreview() {
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
    SideMenu()
}