package com.app.edonymyeon.presentation.ui.main.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentMyPageBinding
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsRemoteDataSource
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.repository.ConsumptionsRepositoryImpl
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl
import com.app.edonymyeon.data.service.client.RetrofitClient
import com.app.edonymyeon.data.util.PreferenceUtil
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.ui.main.MainActivity
import com.app.edonymyeon.presentation.ui.main.mypage.chart.LineChartManager
import com.app.edonymyeon.presentation.ui.mypost.MyPostActivity
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData

class MyPageFragment : Fragment() {
    private val binding: FragmentMyPageBinding by lazy {
        FragmentMyPageBinding.inflate(layoutInflater)
    }

    private val viewModel: MyPageViewModel by viewModels {
        MyPageViewModelFactory(
            ProfileRepositoryImpl(ProfileRemoteDataSource()),
            ConsumptionsRepositoryImpl(ConsumptionsRemoteDataSource()),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setViewByLogin()
    }

    private fun setViewByLogin() {
        if (viewModel.isLogin) {
            setViewForLogin()
        } else {
            setViewForNotLogin()
        }
    }

    private fun setViewForLogin() {
        binding.tvRequiredLogin.isVisible = false
        binding.btnLogin.isVisible = false
        binding.tvLogout.isVisible = true
        binding.tvLogout.setOnClickListener { logout() }
        binding.tvMyPost.setOnClickListener { navigateToMyPost() }
        binding.tvUpdateUserInfo.setOnClickListener { }

        viewModel.getUserProfile()
        viewModel.setConsumptions(PERIOD_MONTH)

        setConsumptionChart(
            LineChartManager(
                binding.chartMyPayment,
                resources.getColor(R.color.gray_615f5f, null),
            ),
        )
    }

    private fun setViewForNotLogin() {
        binding.chartMyPayment.isVisible = false
        binding.tvLogout.isVisible = false
        binding.btnLogin.setOnClickListener { navigateToLogin() }
        binding.tvMyPost.setOnClickListener { makeLoginSnackbar() }
        binding.tvUpdateUserInfo.setOnClickListener { makeLoginSnackbar() }

        viewModel.setNoUserState(getString(R.string.my_page_required_login))
    }

    private fun makeLoginSnackbar() {
        binding.root.makeSnackbarWithEvent(
            message = getString(R.string.all_required_login),
            eventTitle = getString(R.string.login_title),
        ) { navigateToLogin() }
    }

    private fun setConsumptionChart(chartManager: LineChartManager) {
        viewModel.consumptions.observe(viewLifecycleOwner) {
            val savingLineDataSet =
                chartManager.getLineDataSet(
                    entries = it.consumptionAmounts.mapIndexed { index, consumption ->
                        Entry(index + 1F, consumption.saving.toFloat())
                    },
                    label = getString(R.string.my_page_graph_saving),
                    lineColor = resources.getColor(R.color.blue_576b9e, null),
                )
            val purchaseLineDataSet =
                chartManager.getLineDataSet(
                    entries = it.consumptionAmounts.mapIndexed { index, consumption ->
                        Entry(index + 1F, consumption.purchase.toFloat())
                    },
                    label = getString(R.string.my_page_graph_purchase),
                    lineColor = resources.getColor(R.color.red_ba3030, null),
                )

            chartManager.apply {
                setChart(
                    lineData = LineData(savingLineDataSet, purchaseLineDataSet),
                    xAxisCharList = viewModel.getMonthLists(),
                )
                setMarkerView()
            }
        }
    }

    private fun logout() {
        PreferenceUtil.setValue(AuthLocalDataSource.USER_ACCESS_TOKEN, null)
        RetrofitClient.getInstance().clearAccessToken()
        (activity as MainActivity).refreshActivity()
    }

    private fun navigateToMyPost() {
        startActivity(MyPostActivity.newIntent(requireContext()))
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.newIntent(requireContext()))
    }

    companion object {
        private const val PERIOD_MONTH = 6
    }
}
