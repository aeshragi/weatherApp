package com.bignerdranch.android.weather_app.data

sealed class LocationState {
    data object LoadingLocation : LocationState()
    data object NoPermission : LocationState()
    data class LongitudeLatitude(val lon: Double, val lat: Double) : LocationState()
}