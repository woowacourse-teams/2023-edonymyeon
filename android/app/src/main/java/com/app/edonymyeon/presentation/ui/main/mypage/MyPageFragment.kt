package com.app.edonymyeon.presentation.ui.main.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentMyPageBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class MyPageFragment : Fragment() {
    private val binding: FragmentMyPageBinding by lazy {
        FragmentMyPageBinding.inflate(layoutInflater)
    }

    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로그인이 되어있다면
        binding.tvRequiredLogin.isVisible = false

        viewModel.setConsumptions()
        setConsumptionChart()
        setMarkerView()
    }

    private fun setConsumptionChart() {
        viewModel.consumptions.observe(viewLifecycleOwner) {
            val savingEntries = it.consumptions.mapIndexed { index, consumption ->
                Entry(index + 1F, consumption.saving.toFloat())
            }
            val purchaseEntries = it.consumptions.mapIndexed { index, consumption ->
                Entry(index + 1F, consumption.purchase.toFloat())
            }
            val savingLineDataSet =
                setLineDataSet(
                    savingEntries,
                    getString(R.string.my_page_graph_saving),
                    R.color.blue_576b9e,
                )
            val purchaseLineDataSet =
                setLineDataSet(
                    purchaseEntries,
                    getString(R.string.my_page_graph_purchase),
                    R.color.red_ba3030,
                )
            setChart(LineData(savingLineDataSet, purchaseLineDataSet))
        }
    }

    private fun setLineDataSet(
        savingEntries: List<Entry>,
        label: String,
        @ColorRes colorId: Int,
    ): LineDataSet {
        return LineDataSet(savingEntries, label).apply {
            lineWidth = 2F // line width
            circleRadius = 3F // size of circles
            circleColors = listOf(resources.getColor(colorId, null)) // circle color
            color = resources.getColor(colorId, null) // line color
            setDrawCircleHole(false) // circle 안에 점
            setDrawCircles(true)
            setDrawHorizontalHighlightIndicator(true)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }
    }

    private fun setChart(lineData: LineData) {
        binding.chartMyPayment.apply {
            data = lineData
            setTouchEnabled(true)
            setExtraOffsets(10f, 0f, 15f, 15f)
            isDoubleTapToZoomEnabled = false
            setDrawGridBackground(false)
            description = null
            animateY(500)
            invalidate()
        }
        setXAxis()
        setYAxis()
        setLegend()
    }

    private fun setXAxis() {
        val xAxis: XAxis = binding.chartMyPayment.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = resources.getColor(R.color.gray_615f5f, null)
        xAxis.mAxisMaximum = 6f
        xAxis.mAxisMinimum = 1f
        xAxis.granularity = 1f
        xAxis.textSize = 8f
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return viewModel.getMonthLists()[value.toInt() - 1]
            }
        }
    }

    private fun setYAxis() {
        val leftYAxis: YAxis = binding.chartMyPayment.axisLeft
        leftYAxis.textColor = resources.getColor(R.color.gray_615f5f, null)
        leftYAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return (value / 10000).toInt().toString()
            }
        }
        val rightYAxis: YAxis = binding.chartMyPayment.axisRight
        rightYAxis.setDrawLabels(false)
    }

    private fun setLegend() {
        val legend: Legend = binding.chartMyPayment.legend
        legend.textColor = resources.getColor(R.color.gray_615f5f, null)
        legend.xEntrySpace = 20f
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
    }

    private fun setMarkerView() {
        binding.chartMyPayment.marker = object : MarkerView(context, R.layout.item_chart_marker) {
            val chartContentText = findViewById<TextView>(R.id.tv_chart_content)

            override fun refreshContent(e: Entry, highlight: Highlight?) {
                chartContentText.text =
                    resources.getString(R.string.my_page_graph_price, e.y.toInt())
                super.refreshContent(e, highlight)
            }

            override fun getOffset(): MPPointF {
                return MPPointF(-(width / 1.5).toFloat(), -height.toFloat())
            }
        }
    }
}
