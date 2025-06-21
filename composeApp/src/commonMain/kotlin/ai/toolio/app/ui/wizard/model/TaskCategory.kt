package ai.toolio.app.ui.wizard.model

import ai.toolio.app.models.Tool
import ai.toolio.app.models.WallType

object Tasks {

    val categories = listOf(
        TaskCategory(
            id = "mount",
            title = "Mount",
            prompt = "What are you going to mount today?",
            tasks = listOf(
                Task("TV",
                    listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                        FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                        FollowUpQuestion.TvSizeQuestion(TvSize.entries.toList())
                    )),
                Task("Shelf", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.ShelfTypeQuestion()
                )),
                Task("Mirror", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList())
                )),
                Task("Picture or Photo Frame", listOf(Tool.LEVEL, Tool.HAMMER, Tool.WALL_PLUGS), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.WeightClassQuestion()
                )),
                Task("Curtains or Blinds", listOf(Tool.DRILL, Tool.TAPE_MEASURE, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.WindowWidthQuestion()
                )),
                Task("Wall Hook or Rack", listOf(Tool.DRILL, Tool.SCREWS, Tool.STUD_FINDER), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries),
                    FollowUpQuestion.WeightClassQuestion()
                ))
            )
        ),
        TaskCategory(
            id = "fix_replace",
            title = "Fix or Replace",
            prompt = "What needs fixing or replacing?",
            tasks = listOf(
                Task("Electrical Outlet", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.OutletTypeQuestion()
                )),
                Task("Light Switch", listOf(Tool.SCREWDRIVER, Tool.ELECTRICAL_TAPE)),
                Task("Door Knob", listOf(Tool.SCREWDRIVER, Tool.WRENCH), listOf(
                    FollowUpQuestion.LockTypeQuestion()
                )),
                Task("Faucet", listOf(Tool.WRENCH, Tool.PLIERS)),
                Task("Shower Head", listOf(Tool.WRENCH, Tool.TAPE_MEASURE)),
                Task("Running Toilet", listOf(Tool.WRENCH, Tool.UTILITY_KNIFE)),
                Task("Squeaky Door", listOf(Tool.SCREWDRIVER, Tool.PLIERS)),
                Task("Ceiling Fan", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.LEVEL), listOf(
                    FollowUpQuestion.CeilingTypeQuestion(),
                    FollowUpQuestion.WeightClassQuestion()
                ))
            )
        ),
        TaskCategory(
            id = "install_assemble",
            title = "Install or Assemble",
            prompt = "What are you going to install or assemble?",
            tasks = listOf(
                Task("Furniture (e.g. IKEA)", listOf(Tool.SCREWDRIVER, Tool.HAMMER), listOf(
                    FollowUpQuestion.WeightClassQuestion()
                )),
                Task("Closet Shelf", listOf(Tool.DRILL, Tool.LEVEL, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList())
                )),
                Task("Wall Bracket", listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.WeightClassQuestion()
                )),
                Task("Towel Bar", listOf(Tool.DRILL, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList())
                )),
                Task("Child Safety Lock", listOf(Tool.SCREWDRIVER, Tool.UTILITY_KNIFE)),
                Task("TV Mount Hardware", listOf(Tool.DRILL, Tool.STUD_FINDER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.TvSizeQuestion(TvSize.entries.toList())
                )),
                Task("Door Lock", listOf(Tool.SCREWDRIVER, Tool.WRENCH))
            )
        ),
        TaskCategory(
            id = "light_decorate",
            title = "Light or Decorate",
            prompt = "What do you want to light or decorate?",
            tasks = listOf(
                Task("Wall Lamp", listOf(Tool.SCREWDRIVER, Tool.DRILL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList()),
                    FollowUpQuestion.LightTypeQuestion()
                )),
                Task("LED Light Strip", listOf(Tool.UTILITY_KNIFE, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.LightTypeQuestion()
                )),
                Task("Clock", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList())
                )),
                Task("Picture Frames", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion(WallType.entries.toList())
                )),
                Task("Seasonal Lights or Garland", listOf(Tool.TAPE_MEASURE, Tool.UTILITY_KNIFE)),
                Task("Wall Decals or Stickers", listOf(Tool.UTILITY_KNIFE))
            )
        ),
        TaskCategory(
            id = "maintain_clean",
            title = "Maintain or Clean",
            prompt = "What needs cleaning or maintenance?",
            tasks = listOf(
                Task("Clogged Sink or Drain", listOf(Tool.UTILITY_KNIFE), listOf(FollowUpQuestion.DrainTypeQuestion())),
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
    val tasks: List<Task>
)

data class Task(
    val name: String,
    val tools: List<Tool>,
    val followUpQuestions: List<FollowUpQuestion> = emptyList()
)

sealed class FollowUpQuestion(val question: String) {
    data class WallTypeQuestion(val options: List<WallType>) : FollowUpQuestion("What type of wall is it?")
    data class TvSizeQuestion(val options: List<TvSize>) : FollowUpQuestion("What is the size of the TV?")
    class ShelfTypeQuestion : FollowUpQuestion("What type of shelf is it?")
    class WindowWidthQuestion : FollowUpQuestion("What is the approximate window width?")
    class WeightClassQuestion : FollowUpQuestion("How heavy is the item?")
    class OutletTypeQuestion : FollowUpQuestion("What type of electrical outlet is it?")
    class LockTypeQuestion : FollowUpQuestion("What kind of lock is it?")
    class CeilingTypeQuestion : FollowUpQuestion("What type of ceiling are you installing into?")
    class LightTypeQuestion : FollowUpQuestion("What type of light are you installing?")
    class DrainTypeQuestion : FollowUpQuestion("What kind of drain is it?")
}

enum class TvSize(val label: String) {
    SMALL("Up to 32\""),
    MEDIUM("33\" to 55\""),
    LARGE("56\" and above")
}


