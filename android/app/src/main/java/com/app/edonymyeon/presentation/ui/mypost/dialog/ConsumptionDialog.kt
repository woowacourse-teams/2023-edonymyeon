package com.app.edonymyeon.presentation.ui.mypost.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.edonymyeon.databinding.DialogInputConsumptionBinding
import com.app.edonymyeon.presentation.ui.mypost.ConsumptionType
import com.app.edonymyeon.presentation.ui.mypost.MyPostViewModel

class ConsumptionDialog(
    private val type: ConsumptionType,
    private val id: Long,
    private val viewModel: MyPostViewModel,
) :
    DialogFragment() {

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
        binding.type = type
        initListener()
    }

    private fun initListener() {
        binding.btnDialogCancel.setOnClickListener { dismiss() }
        binding.btnDialogOk.setOnClickListener {
            when (type) {
                ConsumptionType.PURCHASE -> viewModel.postPurchaseConfirm(
                    id,
                    binding.etConsumptionPrice.toString().toInt(),
                    binding.npYear.value,
                    binding.npMonth.value,
                )
                ConsumptionType.SAVING -> viewModel.postSavingConfirm(
                    id,
                    binding.npYear.value,
                    binding.npMonth.value,
                )
            }
        }
    }
}
