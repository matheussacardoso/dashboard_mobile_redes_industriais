package com.example.dashboard_mobile_redes_industriais.iot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.dashboard_mobile_redes_industriais.iot.model.SensorData
import com.example.dashboard_mobile_redes_industriais.iot.model.SensorState
import com.example.dashboard_mobile_redes_industriais.iot.mqtt.MqttService
import java.text.SimpleDateFormat
import java.util.*

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    val state = MutableLiveData(SensorState())

    private val maxHistorico = 30   // pontos exibidos no gráfico

    private val mqttService = MqttService(
        context        = application,
        brokerIp       = "192.168.15.12",  // ← substitua pelo IP real do PC
        onData         = ::onSensorData,
        onStatusChange = ::onStatus
    )

    init { mqttService.connect() }

    private fun onSensorData(data: SensorData) {
        val atual = state.value ?: SensorState()
        val hora  = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        state.postValue(
            when (data.sensor) {
                "temperatura" -> atual.copy(
                    temperatura             = data.valor,
                    historicoTemperatura    = atual.historicoTemperatura.takeLast(maxHistorico - 1) + data.valor.toFloat(),
                    ultimoPacket            = hora
                )
                "nivel" -> atual.copy(
                    nivel          = data.valor,
                    historicoNivel = atual.historicoNivel.takeLast(maxHistorico - 1) + data.valor.toFloat(),
                    ultimoPacket   = hora
                )
                "pressao" -> atual.copy(
                    pressao          = data.valor,
                    historicoPressao = atual.historicoPressao.takeLast(maxHistorico - 1) + data.valor.toFloat(),
                    ultimoPacket     = hora
                )
                "vazao" -> atual.copy(
                    vazao          = data.valor,
                    historicoVazao = atual.historicoVazao.takeLast(maxHistorico - 1) + data.valor.toFloat(),
                    ultimoPacket   = hora
                )
                else -> atual
            }
        )
    }

    private fun onStatus(connected: Boolean) {
        state.postValue(state.value?.copy(brokerConectado = connected))
    }

    override fun onCleared() {
        super.onCleared()
        mqttService.disconnect()
    }
}