package me.bemind.glitchappcore.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher
import android.R.attr.scaleY
import android.R.attr.scaleX
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by angelomoroni on 14/04/17.
 */

interface IGlitchPresenter{

    var effectON : Boolean

    var effectProgress : Int

    var effect : Effect

    var glitchView : IGlitchView?

    var restore: Boolean

    fun onDraw(canvas: Canvas?)

    fun anaglyph(canvas: Canvas?,progress:Int = 20)

    fun glitch(canvas: Canvas?)

    fun saveEffect()

    fun initEffect(bitmap: Bitmap?,effect:Effect)

    fun saveInstanceState(outState: Bundle?)

    fun restoreSavedInstanceState(savedInstanceState:Bundle?)

    fun initEffect(bitmap: Bitmap?, restore: Boolean)

    fun clearEffect()


}

class GlitchPresenter : IGlitchPresenter{

    private val EFFECT_PROGRESS_K: String? = "eef_pro_k"

    private val EFFECT_K: String? = "effect_k"

    private val EFFECT_ON_K: String? = "eef_on_k"

    override var glitchView: IGlitchView? = null
        set(value) {
            field = value
        }

    override var effectON: Boolean = false

    override var restore: Boolean = false

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


    override fun saveInstanceState(outState: Bundle?) {
        outState?.putInt(EFFECT_PROGRESS_K,effectProgress)
        outState?.putSerializable(EFFECT_K,effect)
        outState?.putBoolean(EFFECT_ON_K,effectON)
    }

    override fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        effectProgress = savedInstanceState?.getInt(EFFECT_PROGRESS_K,0)?:0
        effectON = savedInstanceState?.getBoolean(EFFECT_ON_K,false)?:false
        if(effectON){
            restore = true
            effect = savedInstanceState?.getSerializable(EFFECT_K) as Effect
        }
    }

    override fun saveEffect() {

        Observable.fromCallable {
            val b = Bitmap.createBitmap(glitchView?.getImageBitmap()?.width?:1,
                glitchView?.getImageBitmap()?.height?:1,
                Bitmap.Config.ARGB_8888)

            val canvas = Canvas(b)

            onDraw(canvas)
            return@fromCallable b
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            b -> glitchView?.setImageBitmap(b)
                            effectON = false
                            effectProgress = 0
                            effect = Effect.BASE
                        },
                        { t ->  t.printStackTrace()}
                )






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

    override fun initEffect(bitmap: Bitmap?, restore: Boolean) {
        glithce.initEffect(bitmap)
        this.restore = false
    }

    override fun clearEffect() {
        effectON = false
        effect = Effect.BASE
        effectProgress = 0
    }

    override fun onDraw(canvas: Canvas?){
        canvas?.save()

        //canvas?.scale(glitchView?.scaleXG?:0f, glitchView?.scaleYG?:0f)
        /*canvas?.translate(glitchView?.dispLeft?.toFloat()?.div(glitchView?.scaleXG?:1f)?:0f,
                glitchView?.dispTop?.toFloat()?.div(glitchView?.scaleYG?:1f)?:0f)*/

        when (effect) {
            Effect.GLITCH -> Log.v("ImageView", "glitch")
            Effect.ANAGLYPH -> anaglyph(canvas, effectProgress)
            else -> Log.v("ImageView", "BASE")
        }

        canvas?.restore()
    }
}