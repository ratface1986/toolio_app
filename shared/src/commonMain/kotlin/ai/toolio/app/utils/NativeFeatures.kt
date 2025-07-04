package ai.toolio.app.utils

import ai.toolio.app.di.AuthService

data class NativeFeatures(
    val photoPicker: PhotoPicker,
    val authService: AuthService
)