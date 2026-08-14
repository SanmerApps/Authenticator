package dev.sanmer.authenticator.di

import android.content.Context
import dev.sanmer.authenticator.database.AppDatabase
import org.koin.dsl.module

val Database = module {
    single {
        AppDatabase.build(get<Context>().createDeviceProtectedStorageContext())
    }

    single {
        get<AppDatabase>().auth()
    }
}