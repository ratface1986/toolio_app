package ai.toolio.app.ui.wizard

import ai.toolio.app.ui.wizard.model.Task
import ai.toolio.app.ui.wizard.model.TaskCategory
import ai.toolio.app.ui.wizard.model.Tasks
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TaskChooserWizardScreen(
    categories: List<TaskCategory>,
    onCategoryChosen: (task: Task) -> Unit
) {
    // null = main screen; else = selected category
    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Background spans all
    ) {
        AnimatedContent(
            targetState = selectedCategory,
            transitionSpec = {
                if (targetState != null && initialState == null) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width } + fadeOut()
                } else if (targetState == null && initialState != null) {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                        slideOutHorizontally { width -> width } + fadeOut()
                } else {
                    fadeIn() togetherWith fadeOut()
                }
            }
        ) { category ->
            if (category == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "What are we going do?",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    CategoryGridView(
                        categories = categories,
                        onCategoryClick = { selected ->
                            selectedCategory = selected
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                SubcategoryListView(
                    category = category,
                    onClick = onCategoryChosen,
                    onBack = { selectedCategory = null }
                )
            }
        }
    }
}

@Composable
private fun CategoryGridView(
    categories: List<TaskCategory>,
    onCategoryClick: (TaskCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 3
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 16.dp) // Padding grid edges only
    ) {
        items(categories) { category ->
            CategoryButton(
                title = category.title,
                icon = Icons.Default.Category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // ensures square in grid cell
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            Modifier
                .padding(12.dp) // Reduced padding to maximize text space
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 3,
                    minLines = 1,
                    softWrap = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SubcategoryListView(
    category: TaskCategory,
    onClick: (task: Task) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Category, // Substitute with back arrow if desired
                    contentDescription = "Back"
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = category.prompt,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(category.tasks) { task ->
                Surface(
                    onClick = { onClick(task) },
                    shape = RoundedCornerShape(14.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 1.dp
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = task.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TaskChooserWizardScreenPreview() {
    //TaskChooserWizardScreen(categories = Tasks.categories, {})
    SubcategoryListView(category = Tasks.categories.first(), {}, {})
}