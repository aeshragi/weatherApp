package com.bignerdranch.android.weather_app.ui

import androidx.compose.runtime.Composable
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.WeatherRepository

@Composable
fun App(weatherRepository: WeatherRepository, location: LocationState.LongitudeLatitude?,
        viewModelAssistedFactory: WeatherDisplayViewModel.Factory) {
    WeatherDisplayScreen(weatherRepository, viewModelAssistedFactory, location)
}