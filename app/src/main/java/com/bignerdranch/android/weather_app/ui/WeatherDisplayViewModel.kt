package com.bignerdranch.android.weather_app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.WeatherRepository
import com.bignerdranch.android.weather_app.data.cache.PreferencesRepositoryInterface
import com.bignerdranch.android.weather_app.data.cache.StoredCity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for WeatherDisplayScreen.
 *  it has the following responsibilities:
 *    1) on initialization, it will check to see if we have a cached city/coordinates,
 *    and use that to retrieve the weather data from the backend.
 *   2) On initialization, If cache is empty, it will check the location information
 *   (via [longitudeLatitude]) and use that to retrieve the weather data for the current location.
 *  PLEASE NOTE: once the cache is full, because the user has searched
 *  for a city, we will never use the location to retrieve the weather info.
 *  TODO we could add a button to clear cache.
 *
 *   3) if neither cache nor location exist, we do nothing and wait for user input, which will be
 *   received via [setCitySearchText] and [citySearchSubmit]. Once we receive the city name,
 *   we will fetch a list of cities corresponding to the text via [WeatherRepository.getUSCities]
 *   because there could be more than one city with a certain name in the US.
 *
 *   4. [citySelected] will relay the user's selection and use the longitude and latitude information
 *   to retrieve the weather data for that city and serve it to the UI.
 *   It will also cache the city, lon and lat for future use.
 */
class WeatherDisplayViewModel @AssistedInject constructor(
    @Assisted private val longitudeLatitude: LocationState.LongitudeLatitude?,
    private val preferencesRepository: PreferencesRepositoryInterface,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<WeatherDisplayUiState> =
        MutableStateFlow(WeatherDisplayUiState())

    init {
        viewModelScope.launch {
            val storedCity = preferencesRepository.storedCity.first()
            if (storedCity is StoredCity.Stored) {
                val weather = weatherRepository.getWeather(storedCity.city)
                _uiState.update { old ->
                    old.copy(
                        weatherInfo = weather,
                        expanded = false,
                        selectedCity = storedCity.city,
                        cityText = storedCity.city.name
                    )
                }
            }
            // if the Location information is exposed via "longitudeLatitude", we will use that
            // to retrieve the
            if (longitudeLatitude != null) {
                val city = City(
                    name = "", lat = longitudeLatitude.lat,
                    lon = longitudeLatitude.lon,
                    state = "",
                    country = "US"
                )
                val weather = weatherRepository.getWeather(city)
                _uiState.update { old ->
                    old.copy(
                        weatherInfo = weather,
                        expanded = false,
                        selectedCity = city,
                        cityText = "current Location"
                    )
                }
            }
        }
    }

    val uiState: StateFlow<WeatherDisplayUiState> = _uiState

    fun setCitySearchText(city: String) = _uiState.update { old -> old.copy(cityText = city) }

    fun citySearchSubmit() {
        viewModelScope.launch {
            val cities = weatherRepository.getUSCities(_uiState.value.cityText)
            _uiState.update { old ->
                old.copy(cities = cities, expanded = cities.isNotEmpty())
            }
        }
    }

    fun citySelected(city: City) {
        viewModelScope.launch {
            val weather = weatherRepository.getWeather(city)
            _uiState.update { old ->
                old.copy(
                    weatherInfo = weather,
                    expanded = false,
                    selectedCity = city
                )
            }
            preferencesRepository.setSearchedCoordinates(city)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(location: LocationState.LongitudeLatitude?): WeatherDisplayViewModel
    }


    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            location: LocationState.LongitudeLatitude?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(location) as T
            }
        }
    }
}

