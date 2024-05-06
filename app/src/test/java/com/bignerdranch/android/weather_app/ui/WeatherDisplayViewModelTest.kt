package com.bignerdranch.android.weather_app.ui

import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.api.Main
import com.bignerdranch.android.weather_app.data.api.Weather
import com.bignerdranch.android.weather_app.data.api.WeatherInfo
import com.bignerdranch.android.weather_app.data.api.WeatherRepository
import com.bignerdranch.android.weather_app.data.cache.StoredCity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class WeatherDisplayViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var longitudeLatitude: LocationState.LongitudeLatitude? = null

    var weatherRepository: WeatherRepository = WeatherRepository(FakeWeatherApi())

    var preferencesRepository = FakePreferencesRepository()

    @Test
    fun atStartup_whenCacheIsEmpty_uiStateWillNotContainACity() {
        // When
        preferencesRepository._storedCity = flowOf(StoredCity.Empty)
        val viewModel =
            WeatherDisplayViewModel(longitudeLatitude, preferencesRepository, weatherRepository)

        // Then
        runTest {
            assert(
                viewModel.uiState.first() == WeatherDisplayUiState(
                    cityText = "",
                    selectedCity = null,
                    expanded = false,
                    cities = emptyList(),
                    weatherInfo = null
                )
            )
        }
    }

    @Test
    fun atStartup_whenCacheIsEmpty_AndWeHaveLocationPermission_uiStateWillContainWeatherForThatLocation() {
        // Given
        preferencesRepository._storedCity = flowOf(StoredCity.Empty)

        // When
        longitudeLatitude = LocationState.LongitudeLatitude(lon = 12.22, lat = 23.23)
        val viewModel =
            WeatherDisplayViewModel(longitudeLatitude, preferencesRepository, weatherRepository)

        // Then
        runTest {

            assert(
                viewModel.uiState.first() == WeatherDisplayUiState(
                    cityText = "current Location",
                    selectedCity = City(
                        name = "", lat = 23.23,
                        lon = 12.22,
                        state = "",
                        country = "US"
                    ),
                    expanded = false,
                    cities = emptyList(),
                    weatherInfo = WeatherInfo(
                        weather = listOf(
                            Weather(
                                id = 1,
                                main = "cloudy",
                                description = "more clouds",
                                icon = "123"
                            )
                        ),
                        main = Main(temp = 72.1, feelsLike = 73.3)
                    )
                )
            )
        }
    }

    @Test
    fun atStartup_whenCacheIsNotEmpty_uiStateWillContainTheCorrespondingCity() {
        // Given
        val city =
            City(name = "New York", state = "New York", country = "US", lat = -12.0, lon = 13.0)

        // When
        preferencesRepository._storedCity = flow { emit(StoredCity.Stored(city)) }
        val viewModel =
            WeatherDisplayViewModel(longitudeLatitude, preferencesRepository, weatherRepository)

        // Then
        runTest {
            assert(
                viewModel.uiState.first() == WeatherDisplayUiState(
                    cityText = city.name,
                    selectedCity = City(
                        name = "New York",
                        state = "New York",
                        country = "US",
                        lat = -12.0,
                        lon = 13.0
                    ),
                    expanded = false,
                    cities = emptyList(),
                    weatherInfo = WeatherInfo(
                        weather = listOf(
                            Weather(
                                id = 1,
                                main = "cloudy",
                                description = "more clouds",
                                icon = "123"
                            )
                        ),
                        main = Main(temp = 72.1, feelsLike = 73.3)
                    )
                )
            )
        }
    }

    @Test
    fun whenUserSearchesForACity_AListOfCitiesWillBeReturned() {
        // Given
        val city =
            City(name = "New York", state = "New York", country = "US", lat = -12.0, lon = 13.0)
        val weatherApi = FakeWeatherApi()
        weatherApi.city = city
        val weatherRepository = WeatherRepository(weatherApi)
        val viewModel =
            WeatherDisplayViewModel(longitudeLatitude, preferencesRepository, weatherRepository)

        runTest {
            // When
            viewModel.setCitySearchText("new york")
            viewModel.citySearchSubmit()

            //Then
            assert(
                viewModel.uiState.first() == WeatherDisplayUiState(
                    cityText = "new york",
                    cities = listOf(city),
                    expanded = true,
                    weatherInfo = null,
                    selectedCity = null
                )
            )
        }
    }

    @Test
    fun whenUserSelectsACityFromMenu_WeatherDataIsRetrieved() {
        // Given
        val city =
            City(name = "New York", state = "New York", country = "US", lat = -12.0, lon = 13.0)
        val weatherApi = FakeWeatherApi()
        weatherApi.city = city
        val weatherRepository = WeatherRepository(weatherApi)
        val viewModel =
            WeatherDisplayViewModel(longitudeLatitude, preferencesRepository, weatherRepository)

        runTest {
            // When
            viewModel.citySelected(city)

            //Then
            assert(
                viewModel.uiState.first() == WeatherDisplayUiState(
                    cityText = "",
                    cities = emptyList(),
                    expanded = false,
                    weatherInfo = weatherApi.weatherInfo,
                    selectedCity = city
                )
            )
        }
    }
}

