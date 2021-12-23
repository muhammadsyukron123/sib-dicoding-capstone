package com.syukron.mymealdiary.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.syukron.mymealdiary.MainActivity
import com.syukron.mymealdiary.R
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        supportActionBar?.hide()

        mViewPager = viewPager
        mViewPager.adapter = OnBoardingViewPagerAdapter(this, this)
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()
        textSkip = findViewById(R.id.text_skip)
        textSkip.setOnClickListener {

            val intent =
                Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this)
            finish()
        }

        val btnNextStep: Button = findViewById(R.id.btn_next_step)

        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {

                val intent =
                    Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)
                finish()
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

}