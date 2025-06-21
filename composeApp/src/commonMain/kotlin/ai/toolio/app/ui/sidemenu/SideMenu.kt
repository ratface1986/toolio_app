package ai.toolio.app.ui.sidemenu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.Logout
import org.jetbrains.compose.ui.tooling.preview.Preview

data class TaskItem(
    val title: String,
    val icon: ImageVector,
    val status: TaskStatus
)

enum class TaskStatus {
    IN_PROGRESS,
    COMPLETED,
    ABORTED;

    fun toDisplayText(): String = when (this) {
        IN_PROGRESS -> "In Progress..."
        COMPLETED -> "Completed"
        ABORTED -> "Aborted"
    }
}

@Composable
fun SideMenu(
    onAccountsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
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
private fun TasksSection(modifier: Modifier = Modifier) {
    val tasks = remember {
        listOf(
            TaskItem("Fix Car", Icons.Default.DirectionsCar, TaskStatus.IN_PROGRESS),
            TaskItem("Household", Icons.Default.Home, TaskStatus.COMPLETED),
            TaskItem("Garden", Icons.Default.Yard, TaskStatus.IN_PROGRESS),
            TaskItem("Mount", Icons.Default.Landscape, TaskStatus.ABORTED)
        )
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(task)
        }
    }
}

@Composable
private fun TaskItem(task: TaskItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = task.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = task.status.toDisplayText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = when (task.status) {
                        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                        TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
                        TaskStatus.ABORTED -> MaterialTheme.colorScheme.error
                    }
                )
            }
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