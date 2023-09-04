package com.app.edonymyeon.presentation.ui.mypost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.databinding.ActivityMyPostBinding
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl
import com.app.edonymyeon.presentation.ui.mypost.adapter.MyPostAdapter
import com.app.edonymyeon.presentation.ui.mypost.dialog.ConsumptionDialog
import com.app.edonymyeon.presentation.ui.mypost.listener.MyPostClickListener
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity

class MyPostActivity : AppCompatActivity(), MyPostClickListener {
    private val notificationId by lazy {
        intent.getLongExtra(KEY_NOTIFICATION_ID, -1)
    }

    private val binding: ActivityMyPostBinding by lazy {
        ActivityMyPostBinding.inflate(layoutInflater)
    }

    private val adapter: MyPostAdapter by lazy {
        MyPostAdapter(this)
    }

    private lateinit var dialog: ConsumptionDialog

    private val viewModel: MyPostViewModel by viewModels {
        MyPostViewModelFactory(
            ProfileRepositoryImpl(ProfileRemoteDataSource()),
            PostRepositoryImpl(PostRemoteDataSource()),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAppbar()
        setListener()
        setAdapter()
        setMyPostsObserver()
    }

    override fun onResume() {
        super.onResume()
        loadNewData()
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

    private fun setAppbar() {
        setSupportActionBar(binding.tbMypost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun setListener() {
        binding.rvMyPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (viewModel.hasNextPage()) {
                    return
                }
                if (!binding.rvMyPost.canScrollVertically(1)) {
                    viewModel.getMyPosts(notificationId)
                }
            }
        })

        binding.srlRefresh.setOnRefreshListener {
            binding.srlRefresh.isRefreshing = false
            loadNewData()
        }
    }

    private fun setAdapter() {
        binding.rvMyPost.adapter = adapter
    }

    private fun setMyPostsObserver() {
        viewModel.posts.observe(this) {
            adapter.setMyPosts(it)
        }
    }

    private fun loadNewData() {
        viewModel.clearResult()
        viewModel.getMyPosts(notificationId)
    }

    override fun onMyPostClick(id: Long) {
        startActivity(PostDetailActivity.newIntent(this, id))
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
        private const val KEY_NOTIFICATION_ID = "key_notification_id"
        fun newIntent(context: Context): Intent {
            return Intent(context, MyPostActivity::class.java)
        }

        fun newIntent(context: Context, notificationId: Long): Intent {
            return Intent(context, MyPostActivity::class.java).apply {
                putExtra(KEY_NOTIFICATION_ID, notificationId)
            }
        }
    }
}
