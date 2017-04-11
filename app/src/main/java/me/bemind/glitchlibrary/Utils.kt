package me.bemind.glitchlibrary

import android.support.v4.view.ViewCompat
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by angelomoroni on 11/04/17.
 */

fun animateAlpha(view: View?,value:Float = 1f,duration:Long = 350, visible:Boolean = true){

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
        animation.withStartAction {
            view?.alpha = 0f
            view?.visibility = VISIBLE
        }
    }else{
        animation.withEndAction {
            view?.visibility = GONE
            view?.alpha = 1f
        }
    }

    animation.start()



}
