package dev.sanmer.authenticator.di

import dev.sanmer.authenticator.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DatabaseModule = module {
    single {
        AppDatabase.build(androidContext().createDeviceProtectedStorageContext())
    }

    single {
        get<AppDatabase>().auth()
    }
}