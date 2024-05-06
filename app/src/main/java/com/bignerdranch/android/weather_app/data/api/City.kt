package com.bignerdranch.android.weather_app.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class City(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String
)


