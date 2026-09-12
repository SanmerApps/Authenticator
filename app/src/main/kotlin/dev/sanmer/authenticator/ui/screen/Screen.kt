package dev.sanmer.authenticator.ui.screen

import androidx.navigation3.runtime.NavKey
import dev.sanmer.authenticator.model.OtpUri
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data class Edit(
        val authId: Long = -1,
        val otpUri: OtpUri? = null
    ) : Screen

    @Serializable
    data object Scan : Screen

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