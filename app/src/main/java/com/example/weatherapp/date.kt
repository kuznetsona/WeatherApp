package com.example.weatherapp

fun date(int: Int): String {
    val month : ArrayList<String> = arrayListOf("Jan", "Feb", "Mar", "Apr", "May",
        "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return month[int]
}