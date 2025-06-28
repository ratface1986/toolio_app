package ai.toolio.app.ui.sidemenu

import ai.toolio.app.data.toColor
import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.di.AppEnvironment
import ai.toolio.app.theme.TaskView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SideMenu(
    onAccountsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF2F403E),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Tasks Section
            TasksSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // Bottom Panel
            BottomPanel(
                onAccountsClick = onAccountsClick,
                onSettingsClick = onSettingsClick,
                onLogoutClick = onLogoutClick
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
    onAccountsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column {
            ListItem(
                headlineContent = { Text("Accounts") },
                leadingContent = { 
                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                },
                modifier = Modifier.clickable { onAccountsClick() }
            )
            
            ListItem(
                headlineContent = { Text("Settings") },
                leadingContent = { 
                    Icon(Icons.Default.Settings, contentDescription = null)
                },
                modifier = Modifier.clickable { onSettingsClick() }
            )
            
            ListItem(
                headlineContent = { Text("Log out") },
                leadingContent = { 
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                },
                modifier = Modifier.clickable { onLogoutClick() }
            )
        }
    }
}

@Preview
@Composable
fun SideMenuPreview() {
    SideMenu()
}