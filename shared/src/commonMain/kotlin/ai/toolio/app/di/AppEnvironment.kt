package ai.toolio.app.di

import ai.toolio.app.misc.MeasureType
import ai.toolio.app.models.Task
import ai.toolio.app.models.TaskCategory
import ai.toolio.app.models.TaskStatus
import ai.toolio.app.models.UserProfile
import ai.toolio.app.repo.ToolioRepo
import ai.toolio.app.utils.NativeFeatures

object AppEnvironment {
    private var _userProfile: UserProfile? = null
    val userProfile: UserProfile get() = _userProfile ?: error("AppEnvironment:UserProfile not initialized")

    private var _nativeFeatures: NativeFeatures? = null
    val nativeFeatures: NativeFeatures get() = _nativeFeatures ?: error("AppEnvironment:NativeFeatures not initialized")

    private var _repo: ToolioRepo? = null
    val repo: ToolioRepo get() = _repo ?: error("AppEnvironment:Repo not initialized")


    fun init(userProfile: UserProfile, nativeFeatures: NativeFeatures, repo: ToolioRepo) {
        _userProfile = userProfile
        _nativeFeatures = nativeFeatures
        _repo = repo
    }

    fun setUserProfile(profile: UserProfile) {
        _userProfile = profile
    }

    fun getSessionId() =
        userProfile.sessions.find { it.task.status == TaskStatus.IN_PROGRESS }?.sessionId.orEmpty()

    fun updateSession(
        sessionId: String? = null,
        title: String? = null,
        category: TaskCategory? = null,
        task: Task? = null,
        answers: Map<String, String>? = null,
        isSaved: Boolean? = null
    ) {
        val last = userProfile.sessions.last()
        userProfile.sessions[userProfile.sessions.lastIndex] = last.copy(
            sessionId = sessionId ?: last.sessionId,
            title = title ?: last.title,
            category = category ?: last.category,
            task = task ?: last.task,
            answers = answers ?: last.answers,
            isSaved = isSaved ?: last.isSaved
        )
    }

    fun updateUserSettings(
        nickname: String = userProfile.settings.nickname,
        email: String = userProfile.settings.email,
        language: String = userProfile.settings.language,
        measure: MeasureType = userProfile.settings.measure
    ) {
        userProfile.settings = userProfile.settings.copy(
            nickname = nickname,
            email = email,
            language = language,
            measure = measure
        )
    }

    fun reset() {
        _userProfile = null
        _nativeFeatures = null
        _repo = null
    }
}