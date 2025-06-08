package ai.toolio.app.ui.wizard

object Tasks {

    val categories = listOf(
        TaskCategory(
            id = "mount",
            title = "Mount",
            prompt = "What are you going to mount today?",
            tasks = listOf(
                "TV",
                "Shelf",
                "Mirror",
                "Picture or Photo Frame",
                "Curtains or Blinds",
                "Wall Hook or Rack"
            )
        ),
        TaskCategory(
            id = "fix_replace",
            title = "Fix or Replace",
            prompt = "What needs fixing or replacing?",
            tasks = listOf(
                "Electrical Outlet",
                "Light Switch",
                "Door Knob",
                "Faucet",
                "Shower Head",
                "Running Toilet",
                "Squeaky Door",
                "Ceiling Fan"
            )
        ),
        TaskCategory(
            id = "install_assemble",
            title = "Install or Assemble",
            prompt = "What are you going to install or assemble?",
            tasks = listOf(
                "Furniture (e.g. IKEA)",
                "Closet Shelf",
                "Wall Bracket",
                "Towel Bar",
                "Child Safety Lock",
                "TV Mount Hardware",
                "Door Lock"
            )
        ),
        TaskCategory(
            id = "light_decorate",
            title = "Light or Decorate",
            prompt = "What do you want to light or decorate?",
            tasks = listOf(
                "Wall Lamp",
                "LED Light Strip",
                "Clock",
                "Picture Frames",
                "Seasonal Lights or Garland",
                "Wall Decals or Stickers"
            )
        ),
        TaskCategory(
            id = "maintain_clean",
            title = "Maintain or Clean",
            prompt = "What needs cleaning or maintenance?",
            tasks = listOf(
                "Clogged Sink or Drain",
                "Stove or Range Hood Filter",
                "Washing Machine Smell",
                "Smoke Detector Battery",
                "Air Filter",
                "Behind Refrigerator",
                "Sticky Drawer or Cabinet"
            )
        )
    )

}

data class TaskCategory(
    val id: String,
    val title: String,
    val prompt: String,
    val tasks: List<String>
)