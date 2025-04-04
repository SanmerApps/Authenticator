package dev.sanmer.authenticator.ui.provider

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.authenticator.datastore.model.Preference

val LocalPreference = staticCompositionLocalOf { Preference() }