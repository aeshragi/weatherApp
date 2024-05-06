package com.bignerdranch.android.weather_app.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherInfo(val weather: List<Weather>, val main: Main? = null)

