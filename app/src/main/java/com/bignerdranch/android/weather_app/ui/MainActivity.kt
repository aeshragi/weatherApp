package com.bignerdranch.android.weather_app.ui

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.WeatherRepository
import com.bignerdranch.android.weather_app.domain.LocationChecker
import com.bignerdranch.android.weather_app.ui.theme.WeatherappTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var weatherRepository: WeatherRepository
    @Inject
    lateinit var viewModelAssistedFactory: WeatherDisplayViewModel.Factory
    @Inject
    lateinit var locationCheckerFactory: LocationChecker.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationChecker =
            locationCheckerFactory.create(getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
        lifecycleScope.launch {
            // update the location when we get the latest location result
            locationChecker.location.collect {
                val location: LocationState.LongitudeLatitude? = when (it) {
                    LocationState.LoadingLocation -> null
                    is LocationState.LongitudeLatitude -> it
                    LocationState.NoPermission -> null
                }
                setContent(location)
            }
        }
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
