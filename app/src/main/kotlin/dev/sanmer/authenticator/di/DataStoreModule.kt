package dev.sanmer.authenticator.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import dev.sanmer.authenticator.datastore.PreferenceSerializer
import dev.sanmer.authenticator.datastore.model.Preference
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val DataStore = module {
    factoryOf(::PreferenceSerializer) { bind<Serializer<Preference>>() }

    factory<DataStore<Preference>> {
        DataStoreFactory.create(
            serializer = get()
        ) {
            get<Context>().createDeviceProtectedStorageContext().dataStoreFile("preference.pb")
        }
    }
}