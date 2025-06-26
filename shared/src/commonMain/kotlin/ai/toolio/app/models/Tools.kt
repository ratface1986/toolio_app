package ai.toolio.app.models

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

enum class CategoryType {
    MOUNT,
    FIX,
    INSTALL,
    DECORATE,
    MAINTAIN
}