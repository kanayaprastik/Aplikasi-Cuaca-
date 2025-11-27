package com.example.cuacarealtime.api

data class Current(
    val condition: Condition,
    val humidity: Int,
    val temp_c: Double,    // Ubah dari String ke Double
    val wind_kph: Double,  // Ubah dari String ke Double
    val uv: Double,        // Ubah dari String ke Double
    val precip_mm: Double  // Ubah dari String ke Double
)