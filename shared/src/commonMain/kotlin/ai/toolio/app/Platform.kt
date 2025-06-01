package ai.toolio.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform