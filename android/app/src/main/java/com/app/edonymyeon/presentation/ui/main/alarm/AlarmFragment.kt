package com.app.edonymyeon.presentation.ui.main.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentAlarmBinding
import com.app.edonymyeon.data.datasource.notification.NotificationRemoteDataSource
import com.app.edonymyeon.data.repository.NotificationRepositoryImpl
import com.app.edonymyeon.presentation.common.fragment.BaseFragment
import com.app.edonymyeon.presentation.ui.main.alarm.adapter.AlarmAdapter
import com.app.edonymyeon.presentation.ui.mypost.MyPostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlarmFragment : BaseFragment<FragmentAlarmBinding, AlarmViewModel>(
    {
        FragmentAlarmBinding.inflate(it)
    },
) {

    override val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            NotificationRepositoryImpl(
                NotificationRemoteDataSource(),
            ),
        )
    }
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    private val adapter: AlarmAdapter by lazy {
        AlarmAdapter(onClick = {
            when (it.navigateTo) {
                "POST" -> {
                    navigateToPostDetail(it.postId)
                }

                "MYPOST" -> {
                    navigateToMyPost()
                }
            }
        })
    }

    private fun navigateToMyPost() {
        startActivity(MyPostActivity.newIntent(requireContext()))
    }

    private fun navigateToPostDetail(postId: Long) {
        startActivity(PostDetailActivity.newIntent(context = requireContext(), postId = postId))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initBinding()
        return binding.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.checkLogin()
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListener()
        binding.rvAlarmList.adapter = adapter
        viewModel.checkLogin()
        viewModel.getAlarmList()
    }

    private fun setListener() {
        setRefreshListener()
        setScrollListener()
    }

    private fun setRefreshListener() {
        binding.srlAlarmList.setOnRefreshListener {
            loadNewData()
        }
    }

    private fun setScrollListener() {
        binding.rvAlarmList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (viewModel.hasNextPage()) {
                    return
                }
                if (!binding.rvAlarmList.canScrollVertically(1)) {
                    viewModel.getAlarmList()
                }
            }
        })
    }

    private fun loadNewData() {
        viewModel.clearResult()
        viewModel.getAlarmList()
    }

    private fun setObserver() {
        with(viewModel) {
            notificationList.observe(viewLifecycleOwner) {
                binding.srlAlarmList.isRefreshing = false
                setAlarmOffIcon()
                adapter.setAllPosts(it)
            }
        }
    }

    private fun setAlarmOffIcon() {
        activity?.findViewById<BottomNavigationView>(R.id.bnv_main)?.menu?.findItem(R.id.bottom_menu_alarm)
            ?.setIcon(R.drawable.ic_bottom_nav_alarm_off)
    }
}
