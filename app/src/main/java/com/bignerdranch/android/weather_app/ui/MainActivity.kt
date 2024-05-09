package com.bignerdranch.android.weather_app.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.WeatherRepository
import com.bignerdranch.android.weather_app.ui.theme.WeatherappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var weatherRepository: WeatherRepository
    @Inject
    lateinit var viewModelAssistedFactory: WeatherDisplayViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // update the location when we get the latest location result
                getLocation(getSystemService(Context.LOCATION_SERVICE) as? LocationManager).collectLatest {
                    val location: LocationState.LongitudeLatitude? = when (it) {
                        LocationState.LoadingLocation -> null
                        is LocationState.LongitudeLatitude -> it
                        LocationState.NoPermission -> null
                    }
                    setContent(location)
                }
            }
        }
    }

    private fun getLocation(locationManager: LocationManager?): Flow<LocationState> = callbackFlow {
        trySend(LocationState.LoadingLocation)
        val listener = LocationListener { location ->
            trySend(
                LocationState.LongitudeLatitude(
                    lon = location.longitude,
                    lat = location.latitude
                )
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            trySend(LocationState.NoPermission)
        } else {

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                listener
            )
        }
        awaitClose { locationManager?.removeUpdates(listener) }
    }

    private fun setContent(location: LocationState.LongitudeLatitude?) = setContent {
        WeatherappTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                App(weatherRepository, location, viewModelAssistedFactory)
            }
        }
    }
}
