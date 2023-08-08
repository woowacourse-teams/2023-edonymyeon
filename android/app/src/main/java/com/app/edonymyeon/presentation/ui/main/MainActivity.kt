package com.app.edonymyeon.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.app.edonymyeon.R
import com.app.edonymyeon.databinding.ActivityMainBinding
import com.app.edonymyeon.presentation.ui.main.alarm.AlarmFragment
import com.app.edonymyeon.presentation.ui.main.home.HomeFragment
import com.app.edonymyeon.presentation.ui.main.mypage.MyPageFragment
import com.app.edonymyeon.presentation.ui.main.search.SearchFragment

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initFragmentContainerView()
        setBottomNavigationView()
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

    fun refreshActivity() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun getTag(itemId: Int): String = when (itemId) {
        R.id.bottom_menu_search -> FRAGMENT_SEARCH
        R.id.bottom_menu_home -> FRAGMENT_HOME
        R.id.bottom_menu_alarm -> FRAGMENT_ALARM
        R.id.bottom_menu_my_page -> FRAGMENT_MY_PAGE
        else -> throw IllegalArgumentException()
    }

    companion object {
        private const val FRAGMENT_SEARCH = "search"
        private const val FRAGMENT_HOME = "home"
        private const val FRAGMENT_ALARM = "alarm"
        private const val FRAGMENT_MY_PAGE = "myPage"

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
