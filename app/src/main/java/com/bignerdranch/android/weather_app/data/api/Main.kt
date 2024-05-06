package com.bignerdranch.android.weather_app.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Main(val temp: Double, @Json(name = "feels_like") val feelsLike: Double)