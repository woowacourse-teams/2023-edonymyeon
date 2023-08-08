package com.app.edonymyeon.presentation.ui.postdetail.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.edonymyeon.databinding.DialogConfirmDeletePostBinding

class DeleteDialog(
    private val onPositiveButtonClick: () -> Unit,
) : DialogFragment() {
    private lateinit var binding: DialogConfirmDeletePostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogConfirmDeletePostBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDialogNo.setOnClickListener { dismiss() }
        binding.btnDialogYes.setOnClickListener { onPositiveButtonClick() }
    }
}
