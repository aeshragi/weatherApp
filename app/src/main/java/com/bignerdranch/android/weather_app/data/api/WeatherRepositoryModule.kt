package com.bignerdranch.android.weather_app.data.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@InstallIn(ActivityRetainedComponent::class)
@Module
class WeatherRepositoryModule {
    @Provides
    fun bindApi(): WeatherApi {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(WeatherInterceptor())
            .build()
        val retorfit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)

            .build()
        return retorfit.create()
    }
}