package dev.sanmer.authenticator.database.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sanmer.authenticator.database.AppDatabase
import dev.sanmer.authenticator.ktx.deviceProtectedContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ) = AppDatabase.build(
        context.deviceProtectedContext
    )

    @Provides
    @Singleton
    fun providesHotpDao(db: AppDatabase) = db.hotp()

    @Provides
    @Singleton
    fun providesTotpDao(db: AppDatabase) = db.totp()
}