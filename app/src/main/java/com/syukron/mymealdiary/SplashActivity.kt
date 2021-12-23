package com.syukron.mymealdiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.syukron.mymealdiary.ui.onboarding.OnBoardingActivity
import com.syukron.mymealdiary.util.ThemeProvider

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val theme = ThemeProvider(this).getThemeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(theme)

        supportActionBar?.hide()

        Handler(this.mainLooper).postDelayed({

            startActivity(Intent(this, OnBoardingActivity::class.java))

            finish()

        }, ANIMATION_TIME)
    }

    companion object {
        const val ANIMATION_TIME: Long = 3000
    }
}