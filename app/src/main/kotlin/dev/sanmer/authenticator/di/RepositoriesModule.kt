package dev.sanmer.authenticator.di

import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.DbRepositoryImpl
import dev.sanmer.authenticator.repository.OtpRepository
import dev.sanmer.authenticator.repository.OtpRepositoryImpl
import dev.sanmer.authenticator.repository.PreferenceRepository
import dev.sanmer.authenticator.repository.PreferenceRepositoryImpl
import dev.sanmer.authenticator.repository.TimeRepository
import dev.sanmer.authenticator.repository.TimeRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val RepositoriesModule = module {
    includes(DataStoreModule, DatabaseModule)
    single<PreferenceRepositoryImpl>() bind PreferenceRepository::class
    single<DbRepositoryImpl>() bind DbRepository::class
    single<TimeRepositoryImpl>() bind TimeRepository::class
    single<OtpRepositoryImpl>() bind OtpRepository::class
}