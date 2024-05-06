package com.bignerdranch.android.weather_app.ui

import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.api.Main
import com.bignerdranch.android.weather_app.data.api.Weather
import com.bignerdranch.android.weather_app.data.api.WeatherApi
import com.bignerdranch.android.weather_app.data.api.WeatherInfo

class FakeWeatherApi : WeatherApi {
    var weather = Weather(id = 1, main = "cloudy", description = "more clouds", icon = "123")
    var city = City(name = "Los Angeles", lat = 123.23, lon = 12.23, country = "US", state = "CA")
    var main = Main(temp = 72.1, feelsLike = 73.3)
    val weatherInfo: WeatherInfo get() = WeatherInfo(
        weather = listOf(weather),
        main = main
    )

    override suspend fun getWeather(latitude: Double, longitude: Double): WeatherInfo {
        return WeatherInfo(weather = listOf(weather), main = main)
    }

    override suspend fun getCities(cityAndCountry: String): List<City> {
        return listOf(city)
    }
}