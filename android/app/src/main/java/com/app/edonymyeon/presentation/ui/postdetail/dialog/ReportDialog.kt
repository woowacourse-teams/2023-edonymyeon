package com.app.edonymyeon.presentation.ui.postdetail.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.edonymyeon.R
import app.edonymyeon.databinding.DialogReportBinding
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailViewModel

class ReportDialog(
    private val id: Long,
    private val viewModel: PostDetailViewModel,
) : DialogFragment() {

    private lateinit var binding: DialogReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogReportBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initListener()
    }

    private fun initListener() {
        binding.rgReport.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_report_etc -> binding.etReportContent.visibility = View.VISIBLE
                else -> binding.etReportContent.visibility = View.GONE
            }
        }
        binding.tvReport.setOnClickListener {
            viewModel.postReport(id, getReportNum(), binding.etReportContent.text.toString())
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun getReportNum() = when (binding.rgReport.checkedRadioButtonId) {
        R.id.rb_report_profit -> 1
        R.id.rb_report_privacy -> 2
        R.id.rb_report_obscene -> 3
        R.id.rb_report_abuse -> 4
        R.id.rb_report_repeat -> 5
        R.id.rb_report_etc -> 6
        else -> 0
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        isCancelable = false
    }
}
