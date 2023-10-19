package com.app.edonymyeon.presentation.ui.main.mypage.chart

import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.ColorInt
import app.edonymyeon.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class LineChartManager(
    private val lineChart: LineChart,
    @ColorInt private val textColor: Int,
    private val fontStyle: Typeface,
) {
    fun getLineDataSet(
        entries: List<Entry>,
        label: String,
        @ColorInt lineColor: Int,
    ): LineDataSet {
        return LineDataSet(entries, label).apply {
            lineWidth = 2F // line 두께
            circleRadius = 3F // circles 크기
            circleColors = listOf(lineColor) // circle color
            color = lineColor // line color
            setDrawCircleHole(false) // circle 안에 점
            setDrawCircles(true)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }
    }

    fun setChart(lineData: LineData, xAxisCharList: List<String>) {
        lineChart.apply {
            data = lineData
            setTouchEnabled(true) // 터치 가능하도록
            setExtraOffsets(10f, 0f, 20f, 15f) // 여백
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false) // zoom 불가능
            setDrawGridBackground(false)
            description = null
            animateY(500)
            invalidate()
        }
        setXAxis(textColor, xAxisCharList)
        setLeftYAxis(textColor)
        setRightYAxis()
        setLegend(textColor)
    }

    private fun setXAxis(@ColorInt textColor: Int, xAxisCharList: List<String>) {
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            this.textColor = textColor
            mAxisMaximum = 6f // 6개월
            mAxisMinimum = 1f
            granularity = 1f // x축 간격
            textSize = lineChart.resources.getDimension(R.dimen.my_page_graph_axis_text_size)
            typeface = fontStyle
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return xAxisCharList[value.toInt() - 1]
                }
            }
        }
    }

    private fun setLeftYAxis(@ColorInt textColor: Int) {
        lineChart.axisLeft.apply {
            this.textColor = textColor
            typeface = fontStyle
            textSize = lineChart.resources.getDimension(R.dimen.my_page_graph_axis_text_size)
            valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return (value / UNIT_MONEY + 0.1).toInt().toString()
                }
            }
        }
    }

    private fun setRightYAxis() {
        lineChart.axisRight.apply {
            setDrawLabels(false) // 우측 라벨 안 보이게
        }
    }

    private fun setLegend(@ColorInt textColor: Int) {
        lineChart.legend.apply {
            this.textColor = textColor
            typeface = fontStyle
            textSize = lineChart.resources.getDimension(R.dimen.my_page_graph_legend_text_size)
            xEntrySpace =
                lineChart.resources.getDimension(R.dimen.my_page_graph_legend_entry_size) // 라벨 간 간격 (가로)
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            xOffset = lineChart.resources.getDimension(R.dimen.my_page_graph_legend_offset_size)
            yOffset = 20f
        }
    }

    fun setMarkerView() { // 그래프 터치 시 보이는 뷰
        lineChart.marker =
            object : MarkerView(lineChart.rootView.context, R.layout.item_chart_marker) {
                val chartContentText = findViewById<TextView>(R.id.tv_chart_content)

                override fun refreshContent(e: Entry, highlight: Highlight?) {
                    chartContentText.text = resources.getString(R.string.my_page_price, e.y.toInt())
                    super.refreshContent(e, highlight)
                }

                override fun getOffset(): MPPointF {
                    return MPPointF(-(width / 1.5).toFloat(), -height.toFloat())
                }
            }
    }

    fun setViewWithNoData() {
        lineChart.apply {
            setNoDataText(resources.getString(R.string.my_page_loading_data))
            setNoDataTextColor(resources.getColor(R.color.black_434343, null))
        }
    }

    companion object {
        private const val UNIT_MONEY = 1_000
    }
}
