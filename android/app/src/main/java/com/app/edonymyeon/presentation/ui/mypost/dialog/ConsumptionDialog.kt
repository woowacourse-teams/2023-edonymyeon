package com.app.edonymyeon.presentation.ui.mypost.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.edonymyeon.databinding.DialogInputConsumptionBinding

class ConsumptionDialog : DialogFragment() {

    private val binding by lazy {
        DialogInputConsumptionBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnDialogCancel.setOnClickListener { dismiss() }
        binding.btnDialogOk.setOnClickListener {}
    }
}
