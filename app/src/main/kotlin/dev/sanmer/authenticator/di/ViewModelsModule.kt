package dev.sanmer.authenticator.di

import dev.sanmer.authenticator.ui.screen.edit.EditViewModel
import dev.sanmer.authenticator.ui.screen.export.ExportViewModel
import dev.sanmer.authenticator.ui.screen.home.HomeViewModel
import dev.sanmer.authenticator.ui.screen.main.MainViewModel
import dev.sanmer.authenticator.ui.screen.ntp.NtpViewModel
import dev.sanmer.authenticator.ui.screen.scan.ScanViewModel
import dev.sanmer.authenticator.ui.screen.setting.SettingViewModel
import dev.sanmer.authenticator.ui.screen.trash.TrashViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val ViewModelsModule = module {
    includes(RepositoriesModule)
    viewModel<MainViewModel>()
    viewModel<HomeViewModel>()
    viewModel<EditViewModel>()
    viewModel<ScanViewModel>()
    viewModel<SettingViewModel>()
    viewModel<TrashViewModel>()
    viewModel<NtpViewModel>()
    viewModel<ExportViewModel>()
}