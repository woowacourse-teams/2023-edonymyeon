package com.app.edonymyeon.presentation.ui.main.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentMyPageBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

        setConsumptionChart()
    }

    private fun setConsumptionChart() {
        val savingEntries: List<Entry> = listOf(
            Entry(1F, 10000f),
            Entry(2F, 20000f),
            Entry(3F, 40000f),
            Entry(4F, 30000f),
            Entry(5F, 30000f),
            Entry(6F, 60000f),
        )
        val purchaseEntries: List<Entry> = listOf(
            Entry(1F, 260000f),
            Entry(2F, 54000f),
            Entry(3F, 42000f),
            Entry(4F, 35000f),
            Entry(5F, 20000f),
            Entry(6F, 10000f),
        )

        val savingLineDataSet =
            setLineDataSet(savingEntries, "절약금액 (단위 10,000원)", R.color.blue_576b9e)
        val purchaseLineDataSet =
            setLineDataSet(purchaseEntries, "소비금액 (단위 10,000원)", R.color.red_ba3030)
        setChart(LineData(savingLineDataSet, purchaseLineDataSet))
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
            setExtraOffsets(3f, 0f, 3f, 15f)
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
                return when (value) {
                    1f -> "2022.12"
                    2f -> "2023.01"
                    3f -> "2023.02"
                    4f -> "2023.03"
                    5f -> "2023.04"
                    6f -> "2023.05"
                    else -> ""
                }
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
}
