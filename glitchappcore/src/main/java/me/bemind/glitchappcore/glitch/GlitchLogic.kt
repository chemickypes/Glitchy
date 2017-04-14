package me.bemind.glitchappcore.glitch

import android.graphics.Canvas
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher

/**
 * Created by angelomoroni on 14/04/17.
 */

interface IGlitchLogic{

    fun anaglyph(canvas: Canvas?, progress:Int = 20)

    fun glitch(canvas: Canvas?)

    fun saveEffect()

    fun initEffect(w:Int,h:Int,effect: Effect)
}

class GlitchLogic :IGlitchLogic {

    val glitche = Glitcher

    override fun anaglyph(canvas: Canvas?, progress: Int) {
        glitche.anaglyphCanvas(canvas,progress)
    }

    override fun glitch(canvas: Canvas?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEffect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initEffect(w: Int, h: Int, effect: Effect) {
        glitche.initEffect(w,h)
    }

}