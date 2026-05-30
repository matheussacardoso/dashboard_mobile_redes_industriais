package com.example.dashboard_mobile_redes_industriais.iot.model

data class SensorState(
    val temperatura: Double = 0.0,
    val nivel: Double       = 0.0,
    val pressao: Double     = 0.0,
    val vazao: Double       = 0.0,

    // Histórico para os gráficos (últimos 30 pontos)
    val historicoTemperatura: List<Float> = emptyList(),
    val historicoNivel: List<Float>       = emptyList(),
    val historicoPressao: List<Float>     = emptyList(),
    val historicoVazao: List<Float>       = emptyList(),

    val brokerConectado: Boolean = false,
    val ultimoPacket: String     = "--"
) {
    // Alarmes conforme requisitos do projeto
    val alarmeTemperatura: Boolean get() = temperatura > 80.0
    val alarmeNivel: Boolean       get() = nivel < 20.0
    val alarmePressao: Boolean     get() = pressao > 8.0
}