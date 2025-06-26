package ai.toolio.app.ui.wizard

import ai.toolio.app.data.toDrawableResource
import ai.toolio.app.models.CategoryType
import ai.toolio.app.models.Task
import ai.toolio.app.models.TaskCategory
import ai.toolio.app.models.Tasks
import ai.toolio.app.theme.BackButton
import ai.toolio.app.theme.HeadlineMediumText
import ai.toolio.app.theme.ListItemView
import ai.toolio.app.theme.TitleMediumText
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.decorate
import toolio.composeapp.generated.resources.drill
import toolio.composeapp.generated.resources.fix
import toolio.composeapp.generated.resources.install
import toolio.composeapp.generated.resources.maintain
import toolio.composeapp.generated.resources.mount

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TaskChooserWizardScreen(
    categories: List<TaskCategory>,
    onCategoryChosen: (task: Task) -> Unit,
    onBack: () -> Unit = {}
) {
    // null = main screen; else = selected category
    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }
    //var selectedCategory by remember { mutableStateOf<TaskCategory?>(Tasks.categories.first()) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x282F32).copy(alpha = 1.0f))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
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
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BackButton(onClick = onBack)
                                    Spacer(Modifier.width(16.dp))
                                    HeadlineMediumText("What are you going to do?")
                                }
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
        }
    }
}

@Composable
private fun CategoryGridView(
    categories: List<TaskCategory>,
    onCategoryClick: (TaskCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = 2
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = true,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Padding grid edges only
    ) {
        items(categories) { category ->
            CategoryButton(
                title = category.title,
                categoryDrawableResource = category.type.toDrawableResource(),
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryButton(
    title: String,
    categoryDrawableResource: DrawableResource,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 159.dp, height = 180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF616161))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                    start = Offset.Zero,
                    end = Offset.Infinite
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(categoryDrawableResource),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            TitleMediumText(
                text = title,
                color = Color.White,
                alignment = TextAlign.Center
            )
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
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton { onBack() }
            Spacer(Modifier.width(16.dp))
            HeadlineMediumText(category.prompt)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            category.tasks.forEach { task ->
                ListItemView(
                    text = task.name,
                    textColor = Color.White,
                    alignment = Alignment.Center,
                    isLarge = true,
                    onClick = { onClick(task) }
                )
            }
        }
    }
}

@Preview
@Composable
fun TaskChooserWizardScreenPreview() {
    TaskChooserWizardScreen(categories = Tasks.categories, {})
    //SubcategoryListView(category = Tasks.categories.first(), {}, {})
}