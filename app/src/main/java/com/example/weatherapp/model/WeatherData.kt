package com.example.weatherapp.model

data class WeatherData(
    var city: String,
    var time: String,
    var precipitation: String,
    var imageUrl: String,
    var currentTemp: String,
    var minTemp: String,
    var maxTemp: String,
    var lat: String,
    var lon: String,
    var windSpeed: String,
    var icon: String
)

