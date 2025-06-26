package ai.toolio.app.models

import androidx.compose.ui.graphics.vector.ImageVector

object Tasks {

    val categories = listOf(
        TaskCategory(
            id = "mount",
            title = "Mount",
            prompt = "What are you going to mount today?",
            type = CategoryType.MOUNT,
            tasks = listOf(
                Task("TV",
                    listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                        FollowUpQuestion.WallTypeQuestion,
                        FollowUpQuestion.TvSizeQuestion
                    )),
                Task("Shelf", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.ShelfTypeQuestion
                )),
                Task("Mirror", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("Picture or Photo Frame", listOf(Tool.LEVEL, Tool.HAMMER, Tool.WALL_PLUGS), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("Curtains or Blinds", listOf(Tool.DRILL, Tool.TAPE_MEASURE, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WindowWidthQuestion
                )),
                Task("Wall Hook or Rack", listOf(Tool.DRILL, Tool.SCREWS, Tool.STUD_FINDER), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                ))
            )
        ),
        TaskCategory(
            id = "fix_replace",
            title = "Fix or Replace",
            prompt = "What needs fixing or replacing?",
            type = CategoryType.FIX,
            tasks = listOf(
                Task("Electrical Outlet", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.OutletTypeQuestion
                )),
                Task("Light Switch", listOf(Tool.SCREWDRIVER, Tool.ELECTRICAL_TAPE)),
                Task("Door Knob", listOf(Tool.SCREWDRIVER, Tool.WRENCH), listOf(
                    FollowUpQuestion.LockTypeQuestion
                )),
                Task("Faucet", listOf(Tool.WRENCH, Tool.PLIERS)),
                Task("Shower Head", listOf(Tool.WRENCH, Tool.TAPE_MEASURE)),
                Task("Running Toilet", listOf(Tool.WRENCH, Tool.UTILITY_KNIFE)),
                Task("Squeaky Door", listOf(Tool.SCREWDRIVER, Tool.PLIERS)),
                Task("Ceiling Fan", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.LEVEL), listOf(
                    FollowUpQuestion.CeilingTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                ))
            )
        ),
        TaskCategory(
            id = "install_assemble",
            title = "Install or Assemble",
            prompt = "What are you going to install or assemble?",
            type = CategoryType.INSTALL,
            tasks = listOf(
                Task("Furniture (e.g. IKEA)", listOf(Tool.SCREWDRIVER, Tool.HAMMER), listOf(
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("Closet Shelf", listOf(Tool.DRILL, Tool.LEVEL, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("Wall Bracket", listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("Towel Bar", listOf(Tool.DRILL, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("Child Safety Lock", listOf(Tool.SCREWDRIVER, Tool.UTILITY_KNIFE)),
                Task("TV Mount Hardware", listOf(Tool.DRILL, Tool.STUD_FINDER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.TvSizeQuestion
                )),
                Task("Door Lock", listOf(Tool.SCREWDRIVER, Tool.WRENCH))
            )
        ),
        TaskCategory(
            id = "light_decorate",
            title = "Light or Decorate",
            prompt = "What do you want to light or decorate?",
            type = CategoryType.DECORATE,
            tasks = listOf(
                Task("Wall Lamp", listOf(Tool.SCREWDRIVER, Tool.DRILL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.LightTypeQuestion
                )),
                Task("LED Light Strip", listOf(Tool.UTILITY_KNIFE, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.LightTypeQuestion
                )),
                Task("Clock", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("Picture Frames", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("Seasonal Lights or Garland", listOf(Tool.TAPE_MEASURE, Tool.UTILITY_KNIFE)),
                Task("Wall Decals or Stickers", listOf(Tool.UTILITY_KNIFE))
            )
        ),
        TaskCategory(
            id = "maintain_clean",
            title = "Maintain or Clean",
            prompt = "What needs cleaning or maintenance?",
            type = CategoryType.MAINTAIN,
            tasks = listOf(
                Task("Clogged Sink or Drain", listOf(Tool.UTILITY_KNIFE), listOf(FollowUpQuestion.DrainTypeQuestion)),
                Task("Stove or Range Hood Filter", listOf(Tool.UTILITY_KNIFE)),
                Task("Washing Machine Smell", listOf(Tool.UTILITY_KNIFE)),
                Task("Smoke Detector Battery", listOf(Tool.SCREWDRIVER)),
                Task("Air Filter", listOf(Tool.UTILITY_KNIFE)),
                Task("Behind Refrigerator", listOf(Tool.UTILITY_KNIFE)),
                Task("Sticky Drawer or Cabinet", listOf(Tool.SCREWDRIVER, Tool.UTILITY_KNIFE))
            )
        )
    )

    fun getToolsFor(task: Task, answers: Map<String, Any>): List<Tool> {
        val tools = mutableSetOf<Tool>()

        when (task.name) {
            "TV" -> {
                tools.add(Tool.DRILL)
                tools.add(Tool.LEVEL)

                val wall = answers["WallType"] as? WallType
                val size = answers["TvSize"] as? TvSize

                if (wall != null) {
                    when (wall) {
                        WallType.GYPSUM -> tools.addAll(listOf(Tool.WALL_PLUGS, Tool.STUD_FINDER))
                        WallType.BRICK, WallType.CONCRETE, WallType.STONE -> tools.addAll(listOf(Tool.HAMMER, Tool.WALL_PLUGS))
                        WallType.WOOD -> TODO()
                    }
                }

                if (size == TvSize.LARGE) {
                    tools.add(Tool.SCREWS)
                    tools.add(Tool.STUD_FINDER)
                }
            }

            "Shelf" -> {
                tools.add(Tool.LEVEL)
                val wall = answers["WallType"] as? WallType
                if (wall == WallType.GYPSUM) tools.add(Tool.WALL_PLUGS)
                else tools.addAll(listOf(Tool.SCREWS, Tool.DRILL))
            }

            // и так далее
        }

        return tools.toList()
    }
}

data class TaskCategory(
    val id: String,
    val title: String,
    val prompt: String,
    val type: CategoryType,
    val tasks: List<Task>
)

data class Task(
    val name: String,
    val tools: List<Tool>,
    val followUpQuestions: List<FollowUpQuestion> = emptyList()
)

sealed class FollowUpQuestion(val question: String, open val options: List<String> = emptyList()) {
    object WallTypeQuestion : FollowUpQuestion(
        "What type of wall is it?",
        WallType.entries.map { it.label }
    )

    object TvSizeQuestion : FollowUpQuestion(
        "What is the size of the TV?",
        TvSize.entries.map { it.label }
    )

    object ShelfTypeQuestion : FollowUpQuestion(
        "What type of shelf is it?",
        ShelfType.entries.map { it.label }
    )

    object WindowWidthQuestion : FollowUpQuestion(
        "What is the approximate window width?",
        WindowWidth.entries.map { it.label }
    )

    object WeightClassQuestion : FollowUpQuestion(
        "How heavy is the item?",
        WeightClass.entries.map { it.label }
    )

    object OutletTypeQuestion : FollowUpQuestion(
        "What type of electrical outlet is it?",
        OutletType.entries.map { it.label }
    )

    object LockTypeQuestion : FollowUpQuestion(
        "What kind of lock is it?",
        LockType.entries.map { it.label }
    )

    object CeilingTypeQuestion : FollowUpQuestion(
        "What type of ceiling are you installing into?",
        CeilingType.entries.map { it.label }
    )

    object LightTypeQuestion : FollowUpQuestion(
        "What type of light are you installing?",
        LightType.entries.map { it.label }
    )

    object DrainTypeQuestion : FollowUpQuestion(
        "What kind of drain is it?",
        DrainType.entries.map { it.label }
    )
}

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


