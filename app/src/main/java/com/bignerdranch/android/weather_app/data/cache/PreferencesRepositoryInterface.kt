package com.bignerdranch.android.weather_app.data.cache

import com.bignerdranch.android.weather_app.data.api.City
import kotlinx.coroutines.flow.Flow

/**
 * A simple Preferences cache to store the last searched city and its coordinates and exposes
 * this information via [storedCity]. [StoredCity.Empty] is emitted if nothing is in the cache OR
 * if there was a corruption in the way data was saved. [StoredCity.Stored] will return the last
 * cached value.
 **/
interface PreferencesRepositoryInterface {
    val storedCity: Flow<StoredCity>

    suspend fun setSearchedCoordinates(city: City)
}