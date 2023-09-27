package com.app.edonymyeon.presentation.ui.postdetail.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.edonymyeon.databinding.DialogPostDeletedBinding

class PostDeletedDialog(
    private val onPositiveButtonClick: () -> Unit,
) : DialogFragment() {

    private lateinit var binding: DialogPostDeletedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogPostDeletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvDialogOk.setOnClickListener { onPositiveButtonClick() }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        isCancelable = false
    }
}
