package com.example.dashboard_mobile_redes_industriais

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dashboard_mobile_redes_industriais.iot.model.SensorState
import com.example.dashboard_mobile_redes_industriais.iot.viewmodel.SensorViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Botão → tela de gráficos
        findViewById<View>(R.id.btnVerGraficos).setOnClickListener {
            startActivity(Intent(this, ChartsActivity::class.java))
        }

        // Observa mudanças de estado
        viewModel.state.observe(this) { state -> atualizar(state) }
    }

    private fun atualizar(s: SensorState) {
        // --- Status do broker ---
        val dot    = findViewById<View>(R.id.viewStatusDot)
        val tvStatus = findViewById<TextView>(R.id.tvBrokerStatus)
        val tvPacket = findViewById<TextView>(R.id.tvUltimoPacket)

        if (s.brokerConectado) {
            dot.setBackgroundResource(R.drawable.circle_green)
            tvStatus.text      = "Broker: Conectado"
            tvStatus.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            dot.setBackgroundResource(R.drawable.circle_red)
            tvStatus.text      = "Broker: Desconectado"
            tvStatus.setTextColor(Color.parseColor("#C62828"))
        }
        tvPacket.text = "Último: ${s.ultimoPacket}"

        // --- Cards ---
        atualizarCard(
            cardId    = R.id.cardTemperatura,
            nome      = "Temperatura",
            valor     = s.temperatura,
            unidade   = "°C",
            topico    = "industria/tanque/temperatura",
            alarme    = s.alarmeTemperatura
        )
        atualizarCard(
            cardId    = R.id.cardNivel,
            nome      = "Nível",
            valor     = s.nivel,
            unidade   = "%",
            topico    = "industria/tanque/nivel",
            alarme    = s.alarmeNivel
        )
        atualizarCard(
            cardId    = R.id.cardPressao,
            nome      = "Pressão",
            valor     = s.pressao,
            unidade   = "bar",
            topico    = "industria/tanque/pressao",
            alarme    = s.alarmePressao
        )
        atualizarCard(
            cardId    = R.id.cardVazao,
            nome      = "Vazão",
            valor     = s.vazao,
            unidade   = "L/min",
            topico    = "industria/tanque/vazao",
            alarme    = false
        )

        // --- Painel de alarmes ---
        val temAlarme = s.alarmeTemperatura || s.alarmeNivel || s.alarmePressao
        val layoutAlarmes = findViewById<View>(R.id.layoutAlarmes)
        layoutAlarmes.visibility = if (temAlarme) View.VISIBLE else View.GONE

        setAlarm(R.id.tvAlarmeTemp,    s.alarmeTemperatura)
        setAlarm(R.id.tvAlarmeNivel,   s.alarmeNivel)
        setAlarm(R.id.tvAlarmePressao, s.alarmePressao)
    }

    private fun atualizarCard(
        cardId: Int, nome: String, valor: Double,
        unidade: String, topico: String, alarme: Boolean
    ) {
        val card = findViewById<View>(cardId)
        card.findViewById<TextView>(R.id.tvSensorNome).text   = nome
        card.findViewById<TextView>(R.id.tvSensorValor).text  = "%.1f".format(valor)
        card.findViewById<TextView>(R.id.tvSensorUnidade).text = unidade
        card.findViewById<TextView>(R.id.tvTopico).text        = topico

        // Fundo vermelho claro quando em alarme
        val container = card.findViewById<View>(R.id.cardContainer)
        container.setBackgroundColor(
            if (alarme) Color.parseColor("#FFEBEE") else Color.WHITE
        )
    }

    private fun setAlarm(viewId: Int, ativo: Boolean) {
        val tv = findViewById<TextView>(viewId)
        tv.visibility = if (ativo) View.VISIBLE else View.GONE
    }
}