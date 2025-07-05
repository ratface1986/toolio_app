package ai.toolio.app.models

import kotlinx.serialization.Serializable

object Tasks {

    val categories = listOf(
        TaskCategory(
            id = "591cb661-8cdd-47f8-9d69-927093a934e7",
            title = "Mount",
            prompt = "What are you going to mount today?",
            type = CategoryType.MOUNT,
            tasks = listOf(
                Task("2700f39a-f383-47ae-b942-877223b048c3", "TV",
                    listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                        FollowUpQuestion.WallTypeQuestion,
                        FollowUpQuestion.TvSizeQuestion
                    )),
                Task("a5eb34b3-f672-4029-a716-fcc68dea2e1d","Shelf", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.ShelfTypeQuestion
                )),
                Task("36d1d969-2df5-4dd7-bb65-573e1db0b077","Mirror", listOf(Tool.DRILL, Tool.WALL_PLUGS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("5599d9f0-290c-4460-844b-a4f13149e42b","Picture or Photo Frame", listOf(Tool.LEVEL, Tool.HAMMER, Tool.WALL_PLUGS), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("2af3e7e8-0311-4db8-a90b-b263a39cf6e4","Curtains or Blinds", listOf(Tool.DRILL, Tool.TAPE_MEASURE, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WindowWidthQuestion
                )),
                Task("562cb296-e8b0-45e8-882b-4f75d9727fb2","Wall Hook or Rack", listOf(Tool.DRILL, Tool.SCREWS, Tool.STUD_FINDER), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                ))
            )
        ),
        TaskCategory(
            id = "bdcefe4f-6379-4584-825d-c0af6ff2ef46",
            title = "Fix or Replace",
            prompt = "What needs fixing or replacing?",
            type = CategoryType.FIX,
            tasks = listOf(
                Task("38eb4373-4f04-4252-97f8-efdc0fb62631", "Electrical Outlet", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.OutletTypeQuestion
                )),
                Task("3201d5d3-1df2-49b6-8ffc-b4f10fca1287","Light Switch", listOf(Tool.SCREWDRIVER, Tool.ELECTRICAL_TAPE)),
                Task("8a57fe97-d4a3-43f7-83d9-2b5cccf32545","Door Knob", listOf(Tool.SCREWDRIVER, Tool.WRENCH), listOf(
                    FollowUpQuestion.LockTypeQuestion
                )),
                Task("60fb12ac-388e-426c-ad39-e8d2cc8e9960","Faucet", listOf(Tool.WRENCH, Tool.PLIERS)),
                Task("ee036748-6534-4f2d-aff1-01c44effcfd9","Shower Head", listOf(Tool.WRENCH, Tool.TAPE_MEASURE)),
                Task("184d5f55-9aa0-4022-9f3b-7f3ffbf10416","Running Toilet", listOf(Tool.WRENCH, Tool.UTILITY_KNIFE)),
                Task("6da04e3e-39ce-4e71-a2b6-3b567b70f99b","Squeaky Door", listOf(Tool.SCREWDRIVER, Tool.PLIERS)),
                Task("1dd9fa43-8511-4793-a59a-4d9c3ec48cb3","Ceiling Fan", listOf(Tool.SCREWDRIVER, Tool.WIRE_STRIPPER, Tool.LEVEL), listOf(
                    FollowUpQuestion.CeilingTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                ))
            )
        ),
        TaskCategory(
            id = "0893d855-e6ef-4a5e-8f37-9a3d027d3940",
            title = "Install or Assemble",
            prompt = "What are you going to install or assemble?",
            type = CategoryType.INSTALL,
            tasks = listOf(
                Task("fc3928f2-8c03-4ce6-a28a-9ed76e55421a","Furniture (e.g. IKEA)", listOf(Tool.SCREWDRIVER, Tool.HAMMER), listOf(
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("295e4ae8-e655-48ba-8964-92a4924692b7","Closet Shelf", listOf(Tool.DRILL, Tool.LEVEL, Tool.SCREWS), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("70fae98b-1d5b-430a-8323-a08ee67e44da","Wall Bracket", listOf(Tool.DRILL, Tool.SCREWS, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.WeightClassQuestion
                )),
                Task("986140f5-ba2e-4865-8db6-e4b9a2bf633b","Towel Bar", listOf(Tool.DRILL, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("3c909e5e-1c5a-4ced-bd2d-cf7ef7d4099c","Child Safety Lock", listOf(Tool.SCREWDRIVER, Tool.UTILITY_KNIFE)),
                Task("0e95dbaf-80a7-41f4-a665-7b4268b40ea0","TV Mount Hardware", listOf(Tool.DRILL, Tool.STUD_FINDER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.TvSizeQuestion
                )),
                Task("2c31c9d8-32cf-4ec8-b768-3bb3eaaa90f1","Door Lock", listOf(Tool.SCREWDRIVER, Tool.WRENCH))
            )
        ),
        TaskCategory(
            id = "aedaa50e-e2ac-4991-919a-0b8e45455138",
            title = "Light or Decorate",
            prompt = "What do you want to light or decorate?",
            type = CategoryType.DECORATE,
            tasks = listOf(
                Task("fcf13786-35bb-4fa5-be2b-6a6871484ed2","Wall Lamp", listOf(Tool.SCREWDRIVER, Tool.DRILL), listOf(
                    FollowUpQuestion.WallTypeQuestion,
                    FollowUpQuestion.LightTypeQuestion
                )),
                Task("1e56bdfc-bd7c-4b35-a563-ebfad0aad075","LED Light Strip", listOf(Tool.UTILITY_KNIFE, Tool.ELECTRICAL_TAPE), listOf(
                    FollowUpQuestion.LightTypeQuestion
                )),
                Task("c4d01b2c-08af-44ea-9c71-23f68d5a2e28","Clock", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("c7ffcc8d-02e1-4075-aa54-d494df8bceaa","Picture Frames", listOf(Tool.HAMMER, Tool.LEVEL), listOf(
                    FollowUpQuestion.WallTypeQuestion
                )),
                Task("b98ce1a2-aa1c-4acf-9df8-0ffe33f34590","Seasonal Lights or Garland", listOf(Tool.TAPE_MEASURE, Tool.UTILITY_KNIFE)),
                Task("19d30764-e014-44c7-9e71-37904724a3f0","Wall Decals or Stickers", listOf(Tool.UTILITY_KNIFE))
            )
        ),
        TaskCategory(
            id = "212af4eb-4659-4500-9347-09c33429acdc",
            title = "Maintain or Clean",
            prompt = "What needs cleaning or maintenance?",
            type = CategoryType.MAINTAIN,
            tasks = listOf(
                Task("a52c644a-e564-43ce-bbaf-0fb42be2a841","Clogged Sink or Drain", listOf(Tool.UTILITY_KNIFE), listOf(FollowUpQuestion.DrainTypeQuestion)),
                Task("993e4ba5-7f88-4879-b74e-f7d974e0d436","Stove or Range Hood Filter", listOf(Tool.UTILITY_KNIFE)),
                Task("3e24c532-7dbd-4de0-b822-d4cbedd37052","Washing Machine Smell", listOf(Tool.UTILITY_KNIFE)),
                Task("8ed43eb9-532e-4a70-802d-84290551bee4","Smoke Detector Battery", listOf(Tool.SCREWDRIVER)),
                Task("326aa7b6-5543-4c60-88f0-285fb5e03ad3","Air Filter", listOf(Tool.UTILITY_KNIFE)),
                Task("0f82e18b-f70a-4223-836c-3351544bfba6","Behind Refrigerator", listOf(Tool.UTILITY_KNIFE)),
                Task("088ec7c4-ef85-4042-9be3-6473622edfe7","Sticky Drawer or Cabinet", listOf(Tool.SCREWDRIVER, Tool.UTILITY_KNIFE))
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

@Serializable
data class TaskCategory(
    val id: String,
    val title: String,
    val prompt: String,
    val type: CategoryType,
    val tasks: List<Task>
)

@Serializable
data class Task(
    val id: String,
    val name: String,
    val tools: List<Tool>,
    val followUpQuestions: List<FollowUpQuestion> = emptyList(),
    val status: TaskStatus = TaskStatus.IDLE
)

@Serializable
sealed class FollowUpQuestion(val question: String, open val options: List<String> = emptyList()) {
    @Serializable
    object WallTypeQuestion : FollowUpQuestion(
        "What type of wall is it?",
        WallType.entries.map { it.label }
    )
    @Serializable
    object TvSizeQuestion : FollowUpQuestion(
        "What is the size of the TV?",
        TvSize.entries.map { it.label }
    )
    @Serializable
    object ShelfTypeQuestion : FollowUpQuestion(
        "What type of shelf is it?",
        ShelfType.entries.map { it.label }
    )
    @Serializable
    object WindowWidthQuestion : FollowUpQuestion(
        "What is the approximate window width?",
        WindowWidth.entries.map { it.label }
    )
    @Serializable
    object WeightClassQuestion : FollowUpQuestion(
        "How heavy is the item?",
        WeightClass.entries.map { it.label }
    )
    @Serializable
    object OutletTypeQuestion : FollowUpQuestion(
        "What type of electrical outlet is it?",
        OutletType.entries.map { it.label }
    )
    @Serializable
    object LockTypeQuestion : FollowUpQuestion(
        "What kind of lock is it?",
        LockType.entries.map { it.label }
    )
    @Serializable
    object CeilingTypeQuestion : FollowUpQuestion(
        "What type of ceiling are you installing into?",
        CeilingType.entries.map { it.label }
    )
    @Serializable
    object LightTypeQuestion : FollowUpQuestion(
        "What type of light are you installing?",
        LightType.entries.map { it.label }
    )
    @Serializable
    object DrainTypeQuestion : FollowUpQuestion(
        "What kind of drain is it?",
        DrainType.entries.map { it.label }
    )
}

/*data class TaskItem(
    val title: String,
    val icon: ImageVector,
    val status: TaskStatus
)*/

enum class TaskStatus {
    IDLE,
    IN_PROGRESS,
    COMPLETED,
    ABORTED;
}


