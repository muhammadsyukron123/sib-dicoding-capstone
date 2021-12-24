package com.syukron.mymealdiary.ui.onboarding

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.syukron.mymealdiary.R

object Animatoo {

    fun animateSlideLeft(context: Context) {
        (context as AppCompatActivity).overridePendingTransition(
            R.anim.animate_slide_left_enter,
            R.anim.animate_slide_left_exit
        )
    }

}