package dev.sanmer.authenticator.datastore.compose

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.authenticator.datastore.model.Preference

val LocalPreference = staticCompositionLocalOf { Preference() }