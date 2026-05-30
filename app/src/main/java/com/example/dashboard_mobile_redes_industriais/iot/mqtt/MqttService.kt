package com.example.dashboard_mobile_redes_industriais.iot.mqtt

import android.content.Context
import com.example.dashboard_mobile_redes_industriais.iot.model.SensorData
import com.google.gson.Gson
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttService(
    private val context: Context,
    private val brokerIp: String,      // IP local do PC com o Docker
    private val port: Int = 1883,
    private val onData: (SensorData) -> Unit,
    private val onStatusChange: (Boolean) -> Unit
) {
    // Tópico wildcard — assina todos os sensores do tanque de uma vez
    companion object {
        const val TOPIC_WILDCARD = "industria/tanque/+"
    }

    private val serverUri = "tcp://$brokerIp:$port"
    private val clientId  = "android_dashboard_${System.currentTimeMillis()}"
    private val gson      = Gson()

    private lateinit var client: MqttAndroidClient

    fun connect() {
        client = MqttAndroidClient(context, serverUri, clientId)

        client.setCallback(object : MqttCallback {

            override fun connectionLost(cause: Throwable?) {
                onStatusChange(false)
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                try {
                    val json    = message.toString()
                    val data    = gson.fromJson(json, SensorData::class.java)
                    onData(data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })

        val options = MqttConnectOptions().apply {
            isCleanSession  = true
            connectionTimeout = 10
            keepAliveInterval = 30
            isAutomaticReconnect = true   // reconecta automaticamente se cair
        }

        try {
            client.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    onStatusChange(true)
                    // Assina o wildcard após conectar
                    try {
                        client.subscribe(TOPIC_WILDCARD, 1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    onStatusChange(false)
                    exception?.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        if (::client.isInitialized && client.isConnected) {
            client.disconnect()
        }
    }
}