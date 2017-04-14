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

    var effectON : Boolean

    var effectProgress : Int

    var effect : Effect

    var glitchView : IGlitchView?

    fun onDraw(canvas: Canvas?)

    fun anaglyph(canvas: Canvas?,progress:Int = 20)

    fun glitch(canvas: Canvas?)

    fun saveEffect()

    fun initEffect(bitmap: Bitmap?,effect:Effect)

}

class GlitchPresenter : IGlitchPresenter{

    override var glitchView: IGlitchView? = null
        set(value) {
            field = value
        }

    override var effectON: Boolean = false

    val glitchLogic = GlitchLogic()

    val glithce =  Glitcher

    override var effectProgress = 0

    override var effect  = Effect.BASE
        set(value) {
            field = value
            glitchView?.invalidateGlitchView()
        }


    override fun anaglyph(canvas: Canvas?,progress: Int) {
        //glitchLogic.anaglyph(canvas, progress)

        glithce.anaglyphCanvas(canvas,progress)
    }

    override fun glitch(canvas: Canvas?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveEffect() {
        effectON = false
    }

    override fun initEffect(bitmap: Bitmap?,effect: Effect) {
       // glitchLogic.initEffect(bitmap?.width?:0,bitmap?.height?:0,effect)

        effectON = true
        glithce.initEffect(bitmap)

        this.effect = effect

        when (effect){
            Effect.ANAGLYPH -> effectProgress = 20
            else -> effectProgress = 0
        }
    }

    override fun onDraw(canvas: Canvas?){
        when (effect) {
            Effect.GLITCH -> Log.v("ImageView", "glitch")
            Effect.ANAGLYPH -> anaglyph(canvas, effectProgress)
            else -> Log.v("ImageView", "BASE")
        }
    }
}