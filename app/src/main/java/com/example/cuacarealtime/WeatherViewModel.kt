package com.example.cuacarealtime

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuacarealtime.api.Constant
import com.example.cuacarealtime.api.NetworkResponse
import com.example.cuacarealtime.api.RetrofitInstance
import com.example.cuacarealtime.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        // Validasi input
        if (city.isBlank()) {
            _weatherResult.value = NetworkResponse.Error("Please enter a city name")
            return
        }

        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "Fetching weather for: $city with API key: ${Constant.apikey}")
                val response = weatherApi.getWeather(Constant.apikey, city)

                if (response.isSuccessful) {
                    response.body()?.let { weatherData ->
                        Log.d("WeatherViewModel", "Success: ${weatherData.location.name}, Temp: ${weatherData.current.temp_c}")
                        _weatherResult.value = NetworkResponse.Success(weatherData)
                    } ?: run {
                        Log.e("WeatherViewModel", "Response body is null")
                        _weatherResult.value = NetworkResponse.Error("No data received from server")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        400 -> "Bad request - invalid city name"
                        401 -> "Unauthorized - invalid API key"
                        403 -> "Forbidden - access denied"
                        404 -> "City not found"
                        else -> "HTTP ${response.code()}: ${response.message()}"
                    }
                    Log.e("WeatherViewModel", errorMsg)
                    _weatherResult.value = NetworkResponse.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Network error: ${e.message}"
                Log.e("WeatherViewModel", errorMsg, e)
                _weatherResult.value = NetworkResponse.Error(errorMsg)
            }
        }
    }
}