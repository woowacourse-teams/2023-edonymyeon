package com.app.edonymyeon.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import app.edonymyeon.R
import app.edonymyeon.databinding.ActivityMainBinding
import com.app.edonymyeon.data.service.fcm.AlarmService
import com.app.edonymyeon.presentation.ui.main.alarm.AlarmFragment
import com.app.edonymyeon.presentation.ui.main.home.HomeFragment
import com.app.edonymyeon.presentation.ui.main.mypage.MyPageFragment
import com.app.edonymyeon.presentation.ui.main.search.SearchFragment
import com.app.edonymyeon.presentation.ui.mypost.MyPostActivity
import com.app.edonymyeon.presentation.ui.postdetail.PostDetailActivity
import com.app.edonymyeon.presentation.util.makeSnackbar

class MainActivity : AppCompatActivity() {
    private val fragments = mapOf(
        FRAGMENT_SEARCH to SearchFragment(),
        FRAGMENT_HOME to HomeFragment(),
        FRAGMENT_ALARM to AlarmFragment(),
        FRAGMENT_MY_PAGE to MyPageFragment(),
    )

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var backClickedTime = 0L

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() - backClickedTime >= 1500) {
                backClickedTime = System.currentTimeMillis()
                binding.root.makeSnackbar(getString(R.string.main_ask_back_click))
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setOnBackCallback()
        initFragmentContainerView()
        setBottomNavigationView()
        setAlarmObserver()
        navigateByIntent()
    }

    private fun setOnBackCallback() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun initFragmentContainerView() {
        supportFragmentManager.commit {
            add(R.id.fcv_main, HomeFragment(), FRAGMENT_HOME)
            setReorderingAllowed(true)
        }
    }

    private fun setBottomNavigationView() {
        binding.bnvMain.selectedItemId = R.id.bottom_menu_home
        binding.bnvMain.setOnItemSelectedListener { selectedIcon ->
            changeFragment(getTag(selectedIcon.itemId))
            true
        }
    }

    private fun changeFragment(tag: String) {
        hideShowingFragment()

        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.commit {
            if (findFragment != null) {
                show(findFragment)
            } else {
                val fragment: Fragment = fragments[tag] ?: throw IllegalArgumentException()
                add(R.id.fcv_main, fragment, tag)
            }
        }
    }

    private fun hideShowingFragment() {
        val currentTag = getTag(binding.bnvMain.selectedItemId)
        supportFragmentManager.commit {
            supportFragmentManager.findFragmentByTag(currentTag)?.let { hide(it) }
        }
    }

    private fun getTag(itemId: Int): String = when (itemId) {
        R.id.bottom_menu_search -> FRAGMENT_SEARCH
        R.id.bottom_menu_home -> FRAGMENT_HOME
        R.id.bottom_menu_alarm -> FRAGMENT_ALARM
        R.id.bottom_menu_my_page -> FRAGMENT_MY_PAGE
        else -> throw IllegalArgumentException()
    }

    private fun setAlarmObserver() {
        AlarmService.isAlarmOn.observe(this) {
            if (it) {
                binding.bnvMain.menu.findItem(R.id.bottom_menu_alarm)
                    .setIcon(R.drawable.ic_bottom_nav_alarm_on)
            }
        }
    }

    fun refreshActivity() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun navigateByIntent() {
        when (intent.extras?.getString(NOTIFICATION_CLICK_EVENT)) {
            NOTIFICATION_CLICK_EVENT_POST -> {
                val postId = (intent.extras?.getString(NOTIFICATION_POST_ID) ?: "0").toLong()
                val notificationId = (intent.extras?.getString(NOTIFICATION_ID) ?: "0").toLong()
                navigateToPostDetail(postId, notificationId)
            }

            NOTIFICATION_CLICK_EVENT_MYPOST -> {
                navigateToMyPost()
            }
        }
    }

    private fun navigateToPostDetail(postId: Long?, notificationId: Long?) {
        startActivity(PostDetailActivity.newIntent(this, postId ?: 0, notificationId ?: -1))
    }

    private fun navigateToMyPost() {
        startActivity(MyPostActivity.newIntent(this))
    }

    companion object {
        private const val FRAGMENT_SEARCH = "search"
        private const val FRAGMENT_HOME = "home"
        private const val FRAGMENT_ALARM = "alarm"
        private const val FRAGMENT_MY_PAGE = "myPage"

        private const val NOTIFICATION_CLICK_EVENT = "navigateTo"
        private const val NOTIFICATION_CLICK_EVENT_POST = "POST"
        private const val NOTIFICATION_CLICK_EVENT_MYPOST = "MYPOST"
        private const val NOTIFICATION_POST_ID = "postId"
        private const val NOTIFICATION_ID = "notificationId"

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
