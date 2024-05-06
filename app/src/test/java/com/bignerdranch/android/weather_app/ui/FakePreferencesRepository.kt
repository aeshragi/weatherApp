package com.bignerdranch.android.weather_app.ui

import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.cache.PreferencesRepositoryInterface
import com.bignerdranch.android.weather_app.data.cache.StoredCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository : PreferencesRepositoryInterface {
    var _storedCity: Flow<StoredCity> = flow { emit(StoredCity.Empty) }

    override val storedCity: Flow<StoredCity> get() = _storedCity

    override suspend fun setSearchedCoordinates(city: City) { }
}