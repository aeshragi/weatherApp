package com.bignerdranch.android.weather_app.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bignerdranch.android.weather_app.data.api.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) :
    PreferencesRepositoryInterface {
    override val storedCity: Flow<StoredCity> = dataStore.data.map {
        val name = it[SEARCHED_CITY]
        val lon = it[SEARCHED_LON]
        val lat = it[SEARCHED_LAT]
        val state = it[SEARCHED_STATE]
        if (name == null || lon == null || lat == null || state == null) {
            // if any of the fields are null, data is corrupted so we map this to an Empty state
            StoredCity.Empty
        } else {
            StoredCity.Stored(
                City(
                    lon = lon,
                    lat = lat,
                    name = name,
                    country = "US",
                    state = state
                )
            )
        }
    }.distinctUntilChanged()

    override suspend fun setSearchedCoordinates(city: City) {
        dataStore.edit {
            it[SEARCHED_LON] = city.lon
            it[SEARCHED_LAT] = city.lat
            it[SEARCHED_CITY] = city.name
            it[SEARCHED_STATE] = city.state
        }
    }

    companion object {
        private val SEARCHED_LON = doublePreferencesKey("lon")
        private val SEARCHED_LAT = doublePreferencesKey("lat")
        private val SEARCHED_CITY = stringPreferencesKey("city")
        private val SEARCHED_STATE = stringPreferencesKey("state")
    }
}