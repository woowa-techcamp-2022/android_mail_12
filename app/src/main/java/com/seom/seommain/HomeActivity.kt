package com.seom.seommain

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.seom.seommain.databinding.ActivityHomeBinding
import com.seom.seommain.databinding.DrawerHeaderBinding
import com.seom.seommain.databinding.HomeBodyVerticalBinding
import com.seom.seommain.extension.pop
import com.seom.seommain.extension.push
import com.seom.seommain.extension.replace
import com.seom.seommain.mail.MailFragment
import com.seom.seommain.model.mail.MailType
import com.seom.seommain.setting.SettingFragment
import com.seom.seommain.viewModel.HomeViewModel

class HomeActivity : AppCompatActivity() {
    //viewmodel
    val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    private lateinit var binding: ActivityHomeBinding

    // fragments
    private val mailFragment = MailFragment()
    private val settingFragment = SettingFragment()

    // drawer
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        bindViews()

//        replaceFragment(mailFragment, MailFragment.TAG)
        changeFragmentById()
    }

    private fun showAppBar() {
        binding.toolbar.isVisible = true
    }

    private fun hideAppBar() {
        binding.toolbar.isGone = true
    }

    private fun initViews() = with(binding) {
        setSupportActionBar(toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this@HomeActivity,
            root,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        root.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val nickname = intent.getStringExtra(USER_NAME) ?: "?????????"
        val email = intent.getStringExtra(USER_EMAIL) ?: "??????"

        // navigation header data binding
        val headerView = navigationView.getHeaderView(0)
        val headerBinding = DrawerHeaderBinding.bind(headerView)

        headerBinding.nicknameTextView.text = nickname
        headerBinding.emailTextView.text = email
    }

    private fun bindViews() = with(binding) {
        bottomNavigation.setOnItemSelectedListener {
            viewModel.navigationType = it.itemId
            changeFragmentById()
            true
        }

        navigationView.setNavigationItemSelectedListener {
            viewModel.changeDrawerSelectedType(
                when (it.itemId) {
                    R.id.primaryMailType -> MailType.PRIMARY
                    R.id.socialMailType -> MailType.SOCIAL
                    R.id.promotionMailType -> MailType.PROMOTION
                    else -> MailType.PRIMARY
                }
            )
            binding.root.closeDrawer(binding.navigationView)
            false
        }
    }

    private fun changeFragmentById() {
        val navigationType = viewModel.navigationType ?: R.id.mailMenuItem
        when (navigationType) {
            R.id.mailMenuItem -> {
                supportFragmentManager.replace(
                    mailFragment,
                    R.id.fragmentContainer
                )
                showAppBar()
            }
            R.id.settingMenuItem -> {
                supportFragmentManager.push(
                    settingFragment,
                    R.id.fragmentContainer,
                    R.id.mailMenuItem.toString()
                )
                hideAppBar()
            }
        }
    }

    companion object {
        const val TAG = ".HomeActivity"
        const val USER_NAME = "UserName"
        const val USER_EMAIL = "UserEmail"

        fun getIntent(context: Context, nickname: String, email: String) =
            Intent(context, HomeActivity::class.java).apply {
                putExtra(USER_NAME, nickname)
                putExtra(USER_EMAIL, email)
            }
    }

    override fun onBackPressed() {
        // 1. setting tab?????? back button ?????? ????????? mail tab?????? ??????
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.pop()?.let {
                val navigationId = it.toInt()
                binding.bottomNavigation.selectedItemId = navigationId
            }
        }
    }
}