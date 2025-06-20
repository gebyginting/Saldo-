package com.geby.saldo.utils

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

object PieChartManager {

    fun setupChart(pieChart: PieChart, income: Float, expense: Float) {
        val entries = mutableListOf<PieEntry>().apply {
            if (income > 0) add(PieEntry(income, "Pemasukan"))
            if (expense > 0) add(PieEntry(expense, "Pengeluaran"))
        }
        val isDark = isDarkMode(pieChart)

        val dataSet = PieDataSet(entries, "").apply {
            colors = entries.map {
                when (it.label) {
                    "Pemasukan" -> Color.parseColor("#4CAF50")
                    "Pengeluaran" -> Color.parseColor("#F44336")
                    else -> Color.GRAY
                }
            }
            valueTextSize = 12f
            valueTextColor = Color.WHITE
            valueTypeface = Typeface.DEFAULT_BOLD
            sliceSpace = 2f
            setDrawValues(true)
        }

        pieChart.apply {
            setUsePercentValues(true)
            data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(pieChart))
            }
            configureAppearance("Keuangan", isDark)
            configureLegend(isDark)
            animateY(1000)
            invalidate()
        }
    }

    fun setupEmptyChart(pieChart: PieChart) {
        val isDark = isDarkMode(pieChart)

        val dataSet = PieDataSet(listOf(PieEntry(1f, "Belum Ada Data")), "").apply {
            colors = listOf(Color.LTGRAY)
            valueTextColor = Color.TRANSPARENT
            setDrawValues(false)
        }

        pieChart.apply {
            setUsePercentValues(false)
            data = PieData(dataSet)
            configureAppearance("Belum Ada Data", isDark)
            legend.isEnabled = false
            animateY(500)
            invalidate()
        }

    }

    private fun PieChart.configureAppearance(centerText: String, isDark: Boolean) {
        description.isEnabled = false
        this.centerText = centerText
        setCenterTextSize(14f)
        setDrawEntryLabels(false)
        isDrawHoleEnabled = true
        setCenterTextColor(if (isDark) Color.WHITE else Color.BLACK)
        setHoleColor(if (isDark) Color.BLACK else Color.WHITE)
        setTransparentCircleAlpha(0)
    }

    private fun PieChart.configureLegend(isDark: Boolean) {
        legend.apply {
            isEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(false)
            textSize = 12f
            textColor = if (isDark) Color.WHITE else Color.BLACK
            form = Legend.LegendForm.CIRCLE
            formSize = 12f
            xEntrySpace = 20f
            yOffset = 10f
        }
    }


    private fun isDarkMode(pieChart: PieChart): Boolean {
        val uiMode = pieChart.context.resources.configuration.uiMode
        return (uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}