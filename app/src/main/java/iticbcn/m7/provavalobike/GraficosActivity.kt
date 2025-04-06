package iticbcn.m7.provavalobike

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraficosActivity : AppCompatActivity() {
    private lateinit var barChart: com.github.mikephil.charting.charts.BarChart
    private lateinit var pieChart: com.github.mikephil.charting.charts.PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficos)
        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)

        carregarDadesPerGraficar()
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
        val entries = ArrayList<BarEntry>()
        val grups = bicis.groupBy { it.matricula }
        var index = 0f

        grups.forEach { (matricula, llista) ->
            val mitjana = llista.map { it.puntuacio }.average().toFloat()
            entries.add(BarEntry(index, mitjana))
            index++
        }

        val dataSet = BarDataSet(entries, "Puntuació mitjana per matrícula").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)
        barChart.apply {
            data = barData
            description.isEnabled = false
            xAxis.setDrawLabels(false)
            axisRight.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    private fun configurarPieChart(bicis: List<Bici>) {
        val comptatge = bicis.groupingBy { it.puntuacio }.eachCount()
        val entries = comptatge.map { PieEntry(it.value.toFloat(), "${it.key} ⭐") }

        val dataSet = PieDataSet(entries, "Distribució de puntuacions").apply {
            colors = listOf(
                Color.rgb(255, 99, 132),
                Color.rgb(54, 162, 235),
                Color.rgb(255, 206, 86),
                Color.rgb(75, 192, 192),
                Color.rgb(153, 102, 255)
            )
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }

        val pieData = PieData(dataSet)
        pieChart.apply {
            data = pieData
            description.isEnabled = false
            centerText = "Puntuacions"
            animateY(1000)
            invalidate()
        }
    }
}