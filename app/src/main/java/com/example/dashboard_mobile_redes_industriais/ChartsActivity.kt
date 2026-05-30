package com.example.dashboard_mobile_redes_industriais

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.dashboard_mobile_redes_industriais.iot.viewmodel.SensorViewModel

class ChartsActivity : AppCompatActivity() {

    private val viewModel: SensorViewModel by viewModels()

    private lateinit var chartTemperatura: LineChart
    private lateinit var chartNivel: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charts)

        chartTemperatura = findViewById(R.id.chartTemperatura)
        chartNivel       = findViewById(R.id.chartNivel)

        configurarGrafico(chartTemperatura, "Temperatura (°C)", 20f, 90f)
        configurarGrafico(chartNivel,       "Nível (%)",        0f,  100f)

        viewModel.state.observe(this) { state ->
            atualizarGrafico(chartTemperatura, state.historicoTemperatura, Color.parseColor("#E53935"))
            atualizarGrafico(chartNivel,       state.historicoNivel,       Color.parseColor("#1565C0"))
        }
    }

    private fun configurarGrafico(chart: LineChart, descricao: String, yMin: Float, yMax: Float) {
        chart.description.text      = descricao
        chart.description.textSize  = 12f
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.axisRight.isEnabled   = false
        chart.axisLeft.axisMinimum  = yMin
        chart.axisLeft.axisMaximum  = yMax
        chart.xAxis.setDrawLabels(false)
        chart.legend.isEnabled      = false
        chart.setNoDataText("Aguardando dados do broker...")
    }

    private fun atualizarGrafico(chart: LineChart, historico: List<Float>, color: Int) {
        if (historico.isEmpty()) return

        val entries = historico.mapIndexed { i, v -> Entry(i.toFloat(), v) }

        val dataSet = LineDataSet(entries, "").apply {
            this.color       = color
            setDrawValues(false)
            setDrawCircles(false)
            lineWidth        = 2.5f
            mode             = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillAlpha        = 30
            fillColor        = color
        }

        chart.data = LineData(dataSet)
        chart.invalidate()   // redesenha
    }
}