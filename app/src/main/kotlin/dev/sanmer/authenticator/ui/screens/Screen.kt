package dev.sanmer.authenticator.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data class Edit(
        val id: Long = Long.MAX_VALUE,
        val uri: String = ""
    ) : Screen

    @Serializable
    data object Scan : Screen

    @Serializable
    data object Encode : Screen

    @Serializable
    data object Ntp : Screen

    @Serializable
    data object Security : Screen

    @Serializable
    data object Trash : Screen
}