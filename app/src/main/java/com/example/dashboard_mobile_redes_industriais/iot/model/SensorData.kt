package com.example.dashboard_mobile_redes_industriais.iot.model

data class SensorData(
    val sensor: String,
    val valor: Double,
    val unidade: String,
    val timestamp: String,
    val topico: String
)