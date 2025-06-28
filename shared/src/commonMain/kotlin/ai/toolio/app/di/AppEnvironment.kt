package ai.toolio.app.di

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

    fun reset() {
        _userProfile = null
        _nativeFeatures = null
        _repo = null
    }
}