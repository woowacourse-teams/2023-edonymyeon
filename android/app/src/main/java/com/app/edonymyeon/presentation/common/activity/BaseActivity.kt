package com.app.edonymyeon.presentation.common.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.util.makeSnackbar

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(private val inflate: (LayoutInflater) -> B) :
    AppCompatActivity() {
    lateinit var binding: B

    abstract val viewModel: VM
    abstract val inflater: LayoutInflater
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(inflater)
        viewModel.fetchState.observe(this) {
            binding.root.makeSnackbar("네트워크 연결 상태를 확인해주세요.")
        }
    }
}
