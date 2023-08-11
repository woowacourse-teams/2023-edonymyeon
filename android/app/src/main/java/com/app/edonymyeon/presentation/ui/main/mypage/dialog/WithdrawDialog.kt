package com.app.edonymyeon.presentation.ui.main.mypage.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.edonymyeon.databinding.DialogWithdrawBinding

class WithdrawDialog(
    private val onWithdrawClick: () -> Unit,
) : DialogFragment() {

    private lateinit var binding: DialogWithdrawBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initListener()
    }

    private fun initListener() {
        binding.tvWithdrawOk.setOnClickListener { onWithdrawClick() }
        binding.tvWithdrawCancel.setOnClickListener { dismiss() }
    }
}
