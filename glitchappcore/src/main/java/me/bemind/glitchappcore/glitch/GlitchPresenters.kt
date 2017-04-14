package me.bemind.glitchappcore.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher

/**
 * Created by angelomoroni on 14/04/17.
 */

interface IGlitchPresenter{

    fun onDraw(canvas: Canvas?,effect: Effect,effectProgress: Int = 0)

    fun anaglyph(canvas: Canvas?,progress:Int = 20)

    fun glitch(canvas: Canvas?)

    fun saveEffect()

    fun initEffect(bitmap: Bitmap?,effect:Effect)

}

class GlitchPresenter : IGlitchPresenter{

    val glitchLogic = GlitchLogic()


    override fun anaglyph(canvas: Canvas?,progress: Int) {
        glitchLogic.anaglyph(canvas, progress)
    }

    override fun glitch(canvas: Canvas?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEffect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initEffect(bitmap: Bitmap?,effect: Effect) {
        glitchLogic.initEffect(bitmap?.width?:0,bitmap?.height?:0,effect)
    }

    override fun onDraw(canvas: Canvas?,effect: Effect, effectProgress:Int){
        when (effect) {
            Effect.GLITCH -> Log.v("ImageView", "glitch")
            Effect.ANAGLYPH -> anaglyph(canvas, effectProgress)
            else -> Log.v("ImageView", "BASE")
        }
    }
}