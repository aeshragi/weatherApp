package com.bignerdranch.android.weather_app.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferencesRepositoryModule {
    @Singleton
    @Provides
    fun bindDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesRepoModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: PreferencesRepository): PreferencesRepositoryInterface
}