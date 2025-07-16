package dev.sanmer.authenticator.di

import dev.sanmer.authenticator.ui.main.MainViewModel
import dev.sanmer.authenticator.ui.screens.authorize.AuthorizeViewModel
import dev.sanmer.authenticator.ui.screens.crypto.CryptoViewModel
import dev.sanmer.authenticator.ui.screens.edit.EditViewModel
import dev.sanmer.authenticator.ui.screens.encode.EncodeViewModel
import dev.sanmer.authenticator.ui.screens.home.HomeViewModel
import dev.sanmer.authenticator.ui.screens.ntp.NtpViewModel
import dev.sanmer.authenticator.ui.screens.scan.ScanViewModel
import dev.sanmer.authenticator.ui.screens.settings.SettingsViewModel
import dev.sanmer.authenticator.ui.screens.trash.TrashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ViewModels = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::AuthorizeViewModel)
    viewModelOf(::CryptoViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::EditViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ScanViewModel)
    viewModelOf(::NtpViewModel)
    viewModelOf(::EncodeViewModel)
    viewModelOf(::TrashViewModel)
}