package com.bignerdranch.android.weather_app.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherInfo

    @GET("geo/1.0/direct?")
    suspend fun getCities(@Query("q") cityAndCountry: String): List<City>
}