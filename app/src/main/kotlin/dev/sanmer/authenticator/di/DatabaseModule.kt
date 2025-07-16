package dev.sanmer.authenticator.di

import android.content.Context
import dev.sanmer.authenticator.database.AppDatabase
import dev.sanmer.authenticator.ktx.deviceProtectedContext
import org.koin.dsl.module

val Database = module {
    single {
        AppDatabase.build(get<Context>().deviceProtectedContext)
    }

    single {
        get<AppDatabase>().totp()
    }
}