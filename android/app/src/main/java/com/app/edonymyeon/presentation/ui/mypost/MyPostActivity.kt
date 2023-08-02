package com.app.edonymyeon.presentation.ui.mypost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityMyPostBinding
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl
import com.app.edonymyeon.presentation.ui.mypost.adapter.MyPostAdapter
import com.app.edonymyeon.presentation.ui.mypost.dialog.ConsumptionDialog
import com.app.edonymyeon.presentation.ui.mypost.listener.MyPostClickListener

class MyPostActivity : AppCompatActivity(), MyPostClickListener {

    private val binding: ActivityMyPostBinding by lazy {
        ActivityMyPostBinding.inflate(layoutInflater)
    }

    private val adapter: MyPostAdapter by lazy {
        MyPostAdapter(this)
    }

    private lateinit var dialog: ConsumptionDialog

    private val viewModel: MyPostViewModel by viewModels {
        MyPostViewModelFactory(ProfileRepositoryImpl(ProfileRemoteDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.getMyPosts(20, 0)
        initAppbar()
        setAdapter()
        observeMyPosts()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAppbar() {
        setSupportActionBar(binding.tbMypost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setAdapter() {
        binding.rvMyPost.adapter = adapter
    }

    private fun observeMyPosts() {
        viewModel.posts.observe(this) {
            adapter.setMyPosts(it)
        }
    }

    override fun onPurchaseButtonClick(id: Long) {
        onConfirmButtonClicked(
            ConsumptionType.PURCHASE,
            id,
        )
    }

    override fun onSavingButtonClick(id: Long) {
        onConfirmButtonClicked(
            ConsumptionType.SAVING,
            id,
        )
    }

    override fun onCancelButtonClick(id: Long) {
        viewModel.deleteConfirm(id)
    }

    private fun onConfirmButtonClicked(consumptionType: ConsumptionType, id: Long) {
        dialog = ConsumptionDialog(
            consumptionType,
            id,
            viewModel,
        )
        dialog.show(
            supportFragmentManager,
            when (consumptionType) {
                ConsumptionType.PURCHASE -> "PurchaseConfirmDialog"
                ConsumptionType.SAVING -> "SavingConfirmDialog"
            },
        )
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MyPostActivity::class.java)
        }
    }
}
