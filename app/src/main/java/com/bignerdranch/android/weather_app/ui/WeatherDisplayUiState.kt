package com.bignerdranch.android.weather_app.ui

import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.api.WeatherInfo

data class WeatherDisplayUiState(
    val cityText: String = "",
    val selectedCity: City? = null,
    val expanded: Boolean = false,
    val cities: List<City> = emptyList(),
    val weatherInfo: WeatherInfo? = null
)