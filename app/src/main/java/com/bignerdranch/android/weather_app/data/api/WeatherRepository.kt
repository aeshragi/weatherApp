package com.bignerdranch.android.weather_app.data.api

import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun getUSCities(city: String): List<City> =
        weatherApi.getCities("$city,US")

    suspend fun getWeather(city: City) =
        weatherApi.getWeather(latitude = city.lat, longitude = city.lon)

    fun getUrlForIcon(icon: String) = "https://openweathermap.org/img/wn/${icon}@2x.png"
}