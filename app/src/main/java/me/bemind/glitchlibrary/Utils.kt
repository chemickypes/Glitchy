package me.bemind.glitchlibrary

import android.support.v4.view.ViewCompat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by angelomoroni on 11/04/17.
 */

fun animateAlpha(view: View?, runnable: Runnable, duration: Long = 350, visible: Boolean = true, value: Float = 1f){

    /*
    ViewCompat.animate(effectPanel)
                .alpha(0f)
                .setDuration(350)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    effectPanel?.visibility = GONE
                    effectPanel?.alpha = 1f
                }
                .start()
     */
    val animation = ViewCompat.animate(view)
            .alpha(value)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())

    if(visible){
        animation.withStartAction(runnable)
    }else{
        animation.withEndAction(runnable)
    }

    animation.start()





}
