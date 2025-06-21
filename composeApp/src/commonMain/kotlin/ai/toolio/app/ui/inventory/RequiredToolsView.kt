package ai.toolio.app.ui.inventory

import ai.toolio.app.ui.wizard.model.Task
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.toolio.app.ui.wizard.model.Tool
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import toolio.composeapp.generated.resources.Res
import toolio.composeapp.generated.resources.drill
import toolio.composeapp.generated.resources.electricial_tape
import toolio.composeapp.generated.resources.hammer
import toolio.composeapp.generated.resources.level_tool
import toolio.composeapp.generated.resources.pliers
import toolio.composeapp.generated.resources.screwdriver
import toolio.composeapp.generated.resources.screws
import toolio.composeapp.generated.resources.stud_finder
import toolio.composeapp.generated.resources.tape_measuring
import toolio.composeapp.generated.resources.utility_knife
import toolio.composeapp.generated.resources.wall_plug
import toolio.composeapp.generated.resources.wire_stripper
import toolio.composeapp.generated.resources.wrench

@Composable
fun RequiredToolsView(
    title: String,
    task: Task,
    onAddToolClicked: (tool: Tool) -> Unit,
    onConfirm: () -> Unit,
    isToolAdded: (Tool) -> Boolean = { false }
) {
    val tools = task.tools
    val scope = rememberCoroutineScope()
    var addedTools by remember { mutableStateOf(mutableSetOf<Tool>()) }

    // Allow external state override for "tool added"
    LaunchedEffect(Unit) {
        addedTools = tools.filter { isToolAdded(it) }.toMutableSet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 86.dp), // space for confirm btn
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools) { tool ->
                    ToolListItem(
                        tool = tool,
                        isAdded = addedTools.contains(tool),
                        onAddClick = {
                            if (!addedTools.contains(tool)) {
                                addedTools.add(tool)
                                onAddToolClicked(tool)
                            }
                        }
                    )
                }
            }
        }

        // Confirm button at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Button(
                onClick = { onConfirm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Confirm", fontSize = 19.sp)
            }
        }
    }
}

@Composable
private fun ToolListItem(
    tool: Tool,
    isAdded: Boolean,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tool icon
        Icon(
            painter = painterResource(getToolIconRes(tool)),
            contentDescription = tool.displayName,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(36.dp)
                .background(Color.White, shape = CircleShape)
                .padding(4.dp)
        )
        Spacer(Modifier.width(16.dp))

        // Tool name
        Text(
            text = tool.displayName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        if (!isAdded) {
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Add")
            }
        } else {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2ECC40)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Added",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getToolIconRes(tool: Tool): org.jetbrains.compose.resources.DrawableResource {
    return when (tool) {
        Tool.DRILL -> Res.drawable.drill
        Tool.SCREWDRIVER -> Res.drawable.screwdriver
        Tool.HAMMER -> Res.drawable.hammer
        Tool.UTILITY_KNIFE -> Res.drawable.utility_knife
        Tool.TAPE_MEASURE -> Res.drawable.tape_measuring
        Tool.WIRE_STRIPPER -> Res.drawable.wire_stripper
        Tool.ELECTRICAL_TAPE -> Res.drawable.electricial_tape
        Tool.LEVEL -> Res.drawable.level_tool
        Tool.PLIERS -> Res.drawable.pliers
        Tool.SCREWS -> Res.drawable.screws
        Tool.STUD_FINDER -> Res.drawable.stud_finder
        Tool.WALL_PLUGS -> Res.drawable.wall_plug
        Tool.WRENCH -> Res.drawable.wrench
    }
}

@Preview
@Composable
fun PreviewRequiredToolsView() {
    RequiredToolsView(
        title = "Tools you need",
        task = Task(
            name = "Task",
            followUpQuestions = emptyList(),
            tools = listOf(Tool.DRILL, Tool.HAMMER, Tool.SCREWDRIVER)
        ),
        onAddToolClicked = {},
        onConfirm = {},
        isToolAdded = { true }
    )

}