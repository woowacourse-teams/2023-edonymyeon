package com.app.edonymyeon.presentation.ui.mypost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import app.edonymyeon.databinding.ActivityMyPostBinding
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

    private val dialog: ConsumptionDialog by lazy {
        ConsumptionDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initAppbar()
        setAdapter()
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

    override fun onPurchaseButtonClick() {
        dialog.show(supportFragmentManager, "")
    }

    override fun onSavingButtonClick() {
        dialog.show(supportFragmentManager, "")
    }

    override fun onCancelButtonClick() {
        TODO("Not yet implemented")
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MyPostActivity::class.java)
        }
    }
}
