package dev.sanmer.authenticator.di

import dev.sanmer.authenticator.repository.DbRepository
import dev.sanmer.authenticator.repository.DbRepositoryImpl
import dev.sanmer.authenticator.repository.PreferenceRepository
import dev.sanmer.authenticator.repository.PreferenceRepositoryImpl
import dev.sanmer.authenticator.repository.TimeRepository
import dev.sanmer.authenticator.repository.TimeRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val Repositories = module {
    singleOf(::PreferenceRepositoryImpl) { bind<PreferenceRepository>() }
    singleOf(::DbRepositoryImpl) { bind<DbRepository>() }
    singleOf(::TimeRepositoryImpl) { bind<TimeRepository>() }
}