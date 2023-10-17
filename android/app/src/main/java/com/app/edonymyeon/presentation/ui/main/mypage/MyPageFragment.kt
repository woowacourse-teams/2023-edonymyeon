package com.app.edonymyeon.presentation.ui.main.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import app.edonymyeon.R
import app.edonymyeon.databinding.FragmentMyPageBinding
import com.app.edonymyeon.presentation.common.fragment.BaseFragment
import com.app.edonymyeon.presentation.ui.alarmsetting.AlarmSettingActivity
import com.app.edonymyeon.presentation.ui.login.LoginActivity
import com.app.edonymyeon.presentation.ui.main.MainActivity
import com.app.edonymyeon.presentation.ui.main.mypage.chart.LineChartManager
import com.app.edonymyeon.presentation.ui.main.mypage.dialog.WithdrawDialog
import com.app.edonymyeon.presentation.ui.mypost.MyPostActivity
import com.app.edonymyeon.presentation.ui.profileupdate.ProfileUpdateActivity
import com.app.edonymyeon.presentation.uimodel.NicknameUiModel
import com.app.edonymyeon.presentation.util.makeSnackbarWithEvent
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding, MyPageViewModel>(
    { FragmentMyPageBinding.inflate(it) },
) {
    override val viewModel: MyPageViewModel by viewModels()
    override val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    private val alwaysVisibleOptions = listOf(R.id.tv_inquiry, R.id.tv_open_source_license)

    private val withdrawDialog: WithdrawDialog by lazy {
        WithdrawDialog {
            viewModel.withdraw()
            withdrawDialog.dismiss()
            logout()
        }
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

        setConsumptionChart(
            LineChartManager(
                binding.chartMyPayment,
                resources.getColor(R.color.gray_615f5f, null),
                resources.getFont(R.font.nanumsquare),
            ),
        )
        viewModel.isLogoutSuccess.observe(viewLifecycleOwner) {
            if (it) {
                (activity as MainActivity).refreshActivity()
                viewModel.clearToken()
            }
        }
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
        setVisibilityByLogin(true)
        setListenerForLogin()

        viewModel.getUserProfile()
        viewModel.setConsumptions(PERIOD_MONTH)
        binding.chartMyPayment.invalidate()
    }

    private fun setViewForNotLogin() {
        setVisibilityByLogin(false)
        setListenerForNotLogin()

        viewModel.setNoUserState(NicknameUiModel(getString(R.string.my_page_required_login)))
    }

    private fun setVisibilityByLogin(isLogin: Boolean) {
        binding.chartMyPayment.isVisible = isLogin
        binding.tvRequiredLogin.isVisible = !isLogin
        binding.btnLogin.isVisible = !isLogin
        binding.clBottom.children.forEach { view ->
            if (alwaysVisibleOptions.contains(view.id).not()) {
                view.isVisible = isLogin
            }
        }
    }

    private fun setListenerForLogin() {
        binding.tvLogout.setOnClickListener { logout() }
        binding.tvMyPost.setOnClickListener { navigateToMyPost() }
        binding.tvUpdateAlarmSetting.setOnClickListener { navigateToAlarmSetting() }
        binding.tvUpdateUserInfo.setOnClickListener { navigateToProfileUpdate() }
        binding.tvWithdraw.setOnClickListener { showDialog() }
    }

    private fun setListenerForNotLogin() {
        binding.btnLogin.setOnClickListener { navigateToLogin() }
        binding.tvMyPost.setOnClickListener { makeLoginSnackbar() }
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
        chartManager.setViewWithNoData()
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun navigateToMyPost() {
        startActivity(MyPostActivity.newIntent(requireContext()))
    }

    private fun navigateToAlarmSetting() {
        startActivity(AlarmSettingActivity.newIntent(requireContext()))
    }

    private fun navigateToProfileUpdate() {
        startActivity(ProfileUpdateActivity.newIntent(requireContext(), viewModel.profile.value!!))
    }

    private fun navigateToLogin() {
        startActivity(LoginActivity.newIntent(requireContext()))
    }

    private fun showDialog() {
        withdrawDialog.show(requireActivity().supportFragmentManager, "WithdrawDialog")
    }

    companion object {
        private const val PERIOD_MONTH = 6
    }
}
