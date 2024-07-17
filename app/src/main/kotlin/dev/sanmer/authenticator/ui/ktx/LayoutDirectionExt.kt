package dev.sanmer.authenticator.ui.ktx

import androidx.compose.ui.unit.LayoutDirection

fun LayoutDirection.asReversed() = when (this) {
    LayoutDirection.Rtl -> LayoutDirection.Ltr
    LayoutDirection.Ltr -> LayoutDirection.Rtl
}