package ai.toolio.app.models

enum class WallType(val label: String) {
    GYPSUM("Drywall / Gypsum"),
    CONCRETE("Concrete"),
    BRICK("Brick"),
    STONE("Stone"),
    WOOD("Wood")
}

enum class TvSize(val label: String) {
    SMALL("Up to 32\""),
    MEDIUM("33\" to 55\""),
    LARGE("56\" and above")
}

enum class ShelfType(val label: String) {
    FLOATING("Floating shelf"),
    BRACKETED("Shelf with brackets"),
    CABINET_STYLE("Cabinet-style shelf"),
    CORNER("Corner shelf")
}

enum class WindowWidth(val label: String) {
    SMALL("Less than 30 inches"),
    MEDIUM("30 to 60 inches"),
    LARGE("More than 60 inches")
}

enum class WeightClass(val label: String) {
    LIGHT("Up to 5 lbs (2.3 kg)"),
    MEDIUM("6–20 lbs (2.7–9 kg)"),
    HEAVY("21–50 lbs (9.5–23 kg)"),
    VERY_HEAVY("Over 50 lbs (23 kg)")
}

enum class OutletType(val label: String) {
    STANDARD("Standard 2-prong or 3-prong"),
    GFCI("GFCI (with reset button)"),
    USB("USB integrated"),
    SMART("Smart outlet with Wi-Fi"),
    EUROPEAN("European style")
}

enum class LockType(val label: String) {
    DEADBOLT("Deadbolt"),
    KNOB("Doorknob lock"),
    ELECTRONIC("Electronic keypad"),
    SLIDING_DOOR("Sliding door lock"),
    PADLOCK("Padlock")
}

enum class CeilingType(val label: String) {
    DRYWALL("Drywall"),
    DROP_CEILING("Drop ceiling (tiles)"),
    CONCRETE("Concrete"),
    WOOD_BEAM("Wood beam"),
    PLASTER("Plaster")
}

enum class LightType(val label: String) {
    CEILING("Ceiling light"),
    WALL_SCONCE("Wall sconce"),
    CHANDELIER("Chandelier"),
    TRACK("Track lighting"),
    PENDANT("Pendant light")
}

enum class DrainType(val label: String) {
    SINK("Sink drain"),
    FLOOR("Floor drain"),
    SHOWER("Shower drain"),
    TUB("Bathtub drain"),
    OUTDOOR("Outdoor/gutter drain")
}
