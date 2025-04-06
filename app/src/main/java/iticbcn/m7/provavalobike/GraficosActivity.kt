package iticbcn.m7.provavalobike

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraficosActivity : AppCompatActivity() {
    private lateinit var barChart: com.github.mikephil.charting.charts.BarChart
    private lateinit var pieChart: com.github.mikephil.charting.charts.PieChart
    private var matriculas = mutableListOf<String>()

    private val customColors = listOf(
        Color.rgb(63, 81, 181),
        Color.rgb(233, 30, 99),
        Color.rgb(255, 152, 0),
        Color.rgb(76, 175, 80),
        Color.rgb(156, 39, 176),
        Color.rgb(244, 67, 54)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficos)
        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)

        configurarEstilosBasicos()
        carregarDadesPerGraficar()
    }

    private fun configurarEstilosBasicos() {
        listOf(barChart, pieChart).forEach {
            it.setBackgroundColor(Color.WHITE)
            it.description.isEnabled = false
            it.setTouchEnabled(true)
        }
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
    }

    private fun carregarDadesPerGraficar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bicis = RetrofitClient.instance.getBicisValorades()
                withContext(Dispatchers.Main) {
                    configurarBarChart(bicis)
                    configurarPieChart(bicis)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        this@GraficosActivity,
                        "Error carregant dades: ${e.message}",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun configurarBarChart(bicis: List<Bici>) {
        val grups = bicis.groupBy { it.matricula }
        matriculas = grups.keys.toMutableList()
        val entries = grups.values.mapIndexed { index, lista ->
            BarEntry(index.toFloat(), lista.map { it.puntuacio }.average().toFloat())
        }

        val dataSet = BarDataSet(entries, "Puntuació mitjana per matrícula").apply {
            colors = customColors
            valueTextColor = Color.DKGRAY
            valueTextSize = 12f
            setDrawValues(true)
        }

        barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.4f }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?) =
                        matriculas.getOrNull(value.toInt())?.takeLast(4) ?: ""
                }
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.DKGRAY
                labelRotationAngle = -90f
                setLabelCount(matriculas.size, true)
                setAvoidFirstLastClipping(true)
            }
            axisLeft.apply {
                textColor = Color.DKGRAY
                axisMinimum = 0f
                granularity = 1f
            }
            axisRight.isEnabled = false
            legend.isEnabled = false
            setExtraOffsets(10f, 0f, 10f, 30f)
            setPinchZoom(false)
            setVisibleXRangeMaximum(matriculas.size.toFloat())
            animateY(800)
            invalidate()
        }
    }

    private fun configurarPieChart(bicis: List<Bici>) {
        val comptatge = bicis.groupingBy { it.puntuacio }.eachCount()
        val total = comptatge.values.sum().toFloat()
        val entries = comptatge.map {
            val percent = (it.value / total) * 100
            PieEntry(it.value.toFloat(), "${it.key} ⭐ (${"%.1f".format(percent)}%)")
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = customColors
            valueTextColor = Color.WHITE
            valueTextSize = 14f
            sliceSpace = 3f
        }

        pieChart.apply {
            data = PieData(dataSet)
            setUsePercentValues(false)
            centerText = "Distribució\nPuntuacions"
            setCenterTextSize(14f)
            setCenterTextColor(Color.DKGRAY)
            setHoleColor(Color.TRANSPARENT)
            legend.isEnabled = true
            legend.textSize = 12f
            legend.textColor = Color.DKGRAY
            animateY(800)
            invalidate()
        }
    }
}