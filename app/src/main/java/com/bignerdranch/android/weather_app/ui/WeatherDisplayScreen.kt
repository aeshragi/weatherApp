package com.bignerdranch.android.weather_app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bignerdranch.android.weather_app.R
import com.bignerdranch.android.weather_app.data.LocationState
import com.bignerdranch.android.weather_app.data.api.City
import com.bignerdranch.android.weather_app.data.api.Main
import com.bignerdranch.android.weather_app.data.api.Weather
import com.bignerdranch.android.weather_app.data.api.WeatherInfo
import com.bignerdranch.android.weather_app.data.api.WeatherRepository

@Composable
fun WeatherDisplayScreen(
    weatherRepository: WeatherRepository,
    viewModelAssistedFactory: WeatherDisplayViewModel.Factory,
    location: LocationState.LongitudeLatitude?
) {
    val viewModel: WeatherDisplayViewModel = viewModel(
        key = location.toString(),
        factory = WeatherDisplayViewModel.provideFactory(
            viewModelAssistedFactory,
            location
        )
    )
    val uiState: WeatherDisplayUiState by viewModel.uiState.collectAsState()
    Column {
        SearchCityTextEdit(cityToDisplay = uiState.cityText,
            expanded = uiState.expanded,
            cities = uiState.cities,
            citySelected = { city -> viewModel.citySelected(city) },
            searchSubmit = { viewModel.citySearchSubmit() },
            onValueChange = { text -> viewModel.setCitySearchText(text) })
        DisplayWeatherInfo(weatherInfo = uiState.weatherInfo,
            cityName = uiState.selectedCity?.name,
            state = uiState.selectedCity?.state,
            urlForIcon = {
                return@DisplayWeatherInfo weatherRepository.getUrlForIcon(it)
            })
    }
}

@Composable
fun SearchCityTextEdit(
    cityToDisplay: String,
    expanded: Boolean,
    cities: List<City>,
    citySelected: (City) -> Unit,
    searchSubmit: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Box(modifier = Modifier.padding(15.dp)) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(50.dp)
            ) {
                TextField(value = cityToDisplay,
                    onValueChange = onValueChange,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "search"
                        )
                    },
                    modifier = Modifier.weight(3f),
                    placeholder = {
                        Text(
                            text = "Search for the city to get the weather for",
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp
                        )
                    })
                Spacer(modifier = Modifier.width(5.dp))
                OutlinedButton(
                    onClick = searchSubmit,
                    enabled = cityToDisplay != "",
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(1.dp))
                ) {
                    Text("Search")
                }
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { }) {
            cities.forEach {
                DropdownMenuItem(text = {
                    Text(text = "${it.name}, ${it.state}")
                }, onClick = { citySelected(it) })
            }
        }
    }
}

@Composable
fun DisplayWeatherInfo(
    weatherInfo: WeatherInfo?, cityName: String?, state: String?,
    urlForIcon: (String) -> String
) {
    val weather = weatherInfo?.weather?.get(0)
    if (weather != null) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(text = "Current weather for ")
                Text(
                    text = if (cityName != "" && state != "") "$cityName, $state" else
                        "current location",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            val iconUrl = urlForIcon(weather.icon)
            AsyncImage(
                model = iconUrl,
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp),
                contentScale = ContentScale.FillBounds,
                contentDescription = weather.description,
                placeholder = painterResource(id = R.drawable.placeholder)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = weather.description, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            if (weatherInfo.main != null) {
                Text(text = "Temperature: ${weatherInfo.main.temp}", fontSize = 20.sp)
                Text(text = "Feels like: ${weatherInfo.main.temp}", fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplayWeatherInfoPreview() {
    DisplayWeatherInfo(
        weatherInfo = WeatherInfo(
            weather = listOf(
                Weather(
                    id = 1,
                    main = "Cloudy",
                    description = "Cloudy",
                    icon = "123"
                )
            ),
            main = Main(temp = 71.2, feelsLike = 73.3),
        ),
        cityName = "Los Angeles",
        state = "California",
        urlForIcon = { "123" }
    )
}