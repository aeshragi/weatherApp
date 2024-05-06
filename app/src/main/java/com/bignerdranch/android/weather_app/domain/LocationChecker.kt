package com.bignerdranch.android.weather_app.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.LocationState.LoadingLocation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Location Checker will expose [location] StateFlow of type [LocationState] that will provide the
 * current location.
 *  [LoadingLocation] is returned upon initialization. If the app doesn't have the location permission
 *  it will return [LocationState.NoPermission]. Otherwise, once the Location Manager provides
 *  the location, it will be wrapped in [LocationState.LongitudeLatitude] and emitted from [location].
 */
class LocationChecker @AssistedInject constructor(
    @ActivityContext private val context: Context,
    @Assisted private val locationManager: LocationManager?
) {
    private val _location: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.LoadingLocation)
    val location: StateFlow<LocationState> = _location

    private val locationListener: LocationListener = LocationListener {
        // do something
        _location.value = LocationState.LongitudeLatitude(lon = it.longitude, lat = it.latitude)
    }
    init {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _location.value = LocationState.NoPermission
        } else {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locationListener)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(locationManager: LocationManager?): LocationChecker
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory
        ): Factory = object : Factory {
            override fun create(locationManager: LocationManager?): LocationChecker {
                return assistedFactory.create(locationManager)
            }
        }
    }
}

