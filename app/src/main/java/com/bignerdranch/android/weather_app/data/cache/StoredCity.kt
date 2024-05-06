package com.bignerdranch.android.weather_app.data.cache

import com.bignerdranch.android.weather_app.data.api.City

sealed class StoredCity {
    data object Empty : StoredCity()
    data class Stored(val city: City) : StoredCity()
}
