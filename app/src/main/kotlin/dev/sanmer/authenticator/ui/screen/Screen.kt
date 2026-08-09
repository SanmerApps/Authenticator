package dev.sanmer.authenticator.ui.screen

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import dev.sanmer.authenticator.model.serializable.UriSerializer
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Edit(
        val authId: Long = -1,
        @Serializable(UriSerializer::class)
        val otpUri: Uri = Uri.EMPTY
    ) : Screen

    @Serializable
    data object Setting : Screen

    @Serializable
    data object Trash : Screen

    @Serializable
    data object Ntp : Screen

    @Serializable
    data object Export : Screen

    @Serializable
    data object Brand : Screen
}