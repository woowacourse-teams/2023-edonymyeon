package com.app.edonymyeon.presentation.ui.mypost.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.edonymyeon.R
import app.edonymyeon.databinding.DialogInputConsumptionBinding
import com.app.edonymyeon.presentation.ui.mypost.ConsumptionType
import com.app.edonymyeon.presentation.ui.mypost.MyPostViewModel
import com.app.edonymyeon.presentation.util.makeSnackbar

class ConsumptionDialog(
    private val type: ConsumptionType,
    private val id: Long,
    private val viewModel: MyPostViewModel,
) : DialogFragment() {

    private val binding by lazy {
        DialogInputConsumptionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initBinding()
        initObserve()
        setNumberPicker()
        initListener()
    }

    private fun initBinding() {
        binding.type = type
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserve() {
        viewModel.price.observe(this) { price ->
            runCatching { if (price != BLANK) price?.toInt() ?: 0 }.onFailure {
                binding.root.makeSnackbar(this.getString(R.string.dialog_input_price_error_message))
                viewModel.setPurchasePrice(BLANK)
            }
        }
    }

    private fun initListener() {
        binding.btnDialogCancel.setOnClickListener {
            viewModel.setPurchasePrice(BLANK)
            dismiss()
        }
        binding.btnDialogOk.setOnClickListener {
            when (type) {
                ConsumptionType.PURCHASE -> viewModel.postPurchaseConfirm(
                    id,
                    binding.etConsumptionPrice.text.toString().toInt(),
                    binding.npYear.value,
                    binding.npMonth.value,
                )
                ConsumptionType.SAVING -> viewModel.postSavingConfirm(
                    id,
                    binding.npYear.value,
                    binding.npMonth.value,
                )
            }
            dismiss()
        }
        binding.npYear.setOnValueChangedListener { _, _, newVal ->
            updateMonthNumberPicker(newVal)
        }
    }

    private fun setNumberPicker() {
        viewModel.getYearMonth(id)
        val minYear = viewModel.yearMonth.value?.keys?.min() ?: 0
        val maxYear = viewModel.yearMonth.value?.keys?.max() ?: 0

        updateYearNumberPicker(minYear, maxYear)
        updateMonthNumberPicker(minYear)
    }

    private fun updateYearNumberPicker(minYear: Int, maxYear: Int) {
        binding.npYear.minValue = minYear
        binding.npYear.maxValue = maxYear
        binding.npYear.value = minYear
    }

    private fun updateMonthNumberPicker(newVal: Int) {
        val month = viewModel.yearMonth.value?.get(newVal)?.toTypedArray() ?: emptyArray()

        binding.npMonth.minValue = month.min()
        binding.npMonth.maxValue = month.max()
        binding.npMonth.value = month.min()
        binding.npMonth.wrapSelectorWheel = false
    }

    companion object {
        private const val BLANK = ""
    }
}
