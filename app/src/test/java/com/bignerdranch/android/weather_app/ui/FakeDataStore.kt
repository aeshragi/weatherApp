package com.bignerdranch.android.weather_app.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//class FakeDataStore : DataStore<Preferences> {
//    var fakeData: Preferences = Preferences()
//
//    override val data: Flow<Preferences> = flow { emit(fakeData) }
//
//    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
//        TODO("Not yet implemented")
//    }
//
//}