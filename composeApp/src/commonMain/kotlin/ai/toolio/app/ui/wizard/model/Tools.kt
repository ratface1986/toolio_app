package ai.toolio.app.ui.wizard.model

enum class Tool(
    val displayName: String,
    val toolInfo: String = "",
    val wallTypes: List<WallType> = emptyList()
) {
    DRILL("Drill"),
    SCREWDRIVER("Screwdriver"),
    LEVEL("Level"),
    HAMMER("Hammer"),
    STUD_FINDER("Stud Finder"),
    WALL_PLUGS("Wall Plugs", wallTypes = listOf(WallType.CONCRETE, WallType.BRICK, WallType.GYPSUM)),
    SCREWS("Screws"),
    TAPE_MEASURE("Tape Measure"),
    WRENCH("Wrench"),
    PLIERS("Pliers"),
    UTILITY_KNIFE("Utility Knife"),
    WIRE_STRIPPER("Wire Stripper"),
    ELECTRICAL_TAPE("Electrical Tape")
}

enum class WallType {
    GYPSUM,
    CONCRETE,
    BRICK,
    STONE
}