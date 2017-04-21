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
import me.bemind.glitch.TypeEffect
import me.bemind.glitchappcore.GlitchyBaseActivity


/**
 * Created by angelomoroni on 14/04/17.
 */

interface IGlitchPresenter{

    var effectON : Boolean

    var effectProgress : Int

    var effect : Effect

    val typeEffect : TypeEffect

    var glitchView : IGlitchView?

    var restore: Boolean

    fun onDraw(canvas: Canvas?,scale:Boolean = false)

    fun anaglyph(canvas: Canvas?,progress:Int = 20)

    fun glitch(canvas: Canvas?)

    fun saveEffect()

    fun initEffect(bitmap: Bitmap?,effect:Effect)

    fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity,outState: Bundle?)

    fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity, savedInstanceState:Bundle?)

    fun initEffect(bitmap: Bitmap?, restore: Boolean)

    fun clearEffect()
    fun makeEffect(progress: Int)


}

class GlitchPresenter : IGlitchPresenter{

    private val EFFECT_PROGRESS_K: String? = "eef_pro_k"
    private val EFFECT_K: String? = "effect_k"
    private val EFFECT_ON_K: String? = "eef_on_k"
    private val  VOLATILE_BITMAP_K: String? = "volatile_bitm_k"

    var volatileBitmap: Bitmap? = null

    override var glitchView: IGlitchView? = null
        set(value) {
            field = value
        }

    override var effectON: Boolean = false

    override var restore: Boolean = false

    val glitchLogic = GlitchLogic()

    val glithce =  Glitcher

    override var effectProgress = 0

    private var scaledFactory: Float = 1f

    override var effect  = Effect.BASE
        set(value) {
            field = value
            glitchView?.invalidateGlitchView()
        }

    override val typeEffect: TypeEffect
        get() = when (effect){
            Effect.GLITCH -> TypeEffect.JPEG
            Effect.ANAGLYPH -> TypeEffect.CANVAS
            else -> TypeEffect.NONE
        }

    override fun anaglyph(canvas: Canvas?, progress: Int) {
        //glitchLogic.anaglyph(canvas, progress)

        glithce.anaglyphCanvas(canvas,progress)
    }



    override fun glitch(canvas: Canvas?) {
        Observable.fromCallable { glithce.corruption(glithce.baseBitmap) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            b : Bitmap? -> glitchView?.setImageBitmap(b,true) //volatile
                            volatileBitmap = b
                        },
                        {
                            t : Throwable -> t.printStackTrace()
                        }
                )

    }

    override fun makeEffect(progress: Int) {
        when (typeEffect){
            TypeEffect.CANVAS -> {
                this.effectProgress = progress
                glitchView?.invalidateGlitchView()
            }
            TypeEffect.JPEG -> {
                drawJPEGEffect()
            }
            else -> {/*nothing*/}
        }
    }



    override fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity,outState: Bundle?) {
        outState?.putInt(EFFECT_PROGRESS_K,effectProgress)
        outState?.putSerializable(EFFECT_K,effect)
        outState?.putBoolean(EFFECT_ON_K,effectON)


        glitchyBaseActivity.retainedFragment?.volatileBitmap = volatileBitmap

        clearEffect()
    }

    override fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity,savedInstanceState: Bundle?) {
        effectProgress = savedInstanceState?.getInt(EFFECT_PROGRESS_K,0)?:0
        effectON = savedInstanceState?.getBoolean(EFFECT_ON_K,false)?:false
        if(effectON){
            restore = true
            effect = savedInstanceState?.getSerializable(EFFECT_K) as Effect
            volatileBitmap = glitchyBaseActivity.retainedFragment?.volatileBitmap
        }
    }

    override fun saveEffect() {

        Observable.fromCallable {
            if(typeEffect == TypeEffect.CANVAS) {
                val b = Bitmap.createBitmap(glitchView?.getImageBitmap()?.width ?: 1,
                        glitchView?.getImageBitmap()?.height ?: 1,
                        Bitmap.Config.ARGB_8888)

                val canvas = Canvas(b)

                onDraw(canvas)
                return@fromCallable b
            }else{
                return@fromCallable volatileBitmap
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            b -> glitchView?.setImageBitmap(b)
                            clearEffect()

                        },
                        { t ->  t.printStackTrace()}
                )






    }

    override fun initEffect(bitmap: Bitmap?,effect: Effect) {
       // glitchLogic.initEffect(bitmap?.width?:0,bitmap?.height?:0,effect)

        effectON = true
        glithce.initEffect(effect,bitmap)

        this.effect = effect

        calculateScaleFactory(bitmap)

        when (effect){
            Effect.ANAGLYPH -> effectProgress = 20
            else -> effectProgress = 0
        }
    }

    override fun initEffect(bitmap: Bitmap?, restore: Boolean) {
        glithce.initEffect(effect,bitmap)
        this.restore = false
        calculateScaleFactory(bitmap)
    }

    override fun clearEffect() {
        effectON = false
        effect = Effect.BASE
        effectProgress = 0
        volatileBitmap = null
    }

    override fun onDraw(canvas: Canvas?,scale: Boolean){



        canvas?.save()

//       canvas?.scale(glitchView?.scaleXG?:0f, glitchView?.scaleYG?:0f)
        if(scale){
            canvas?.scale(scaledFactory,scaledFactory)
        }

        canvas?.translate(glitchView?.dispLeft?.toFloat()?.div(glitchView?.scaleXG?:1f)?:0f,
                glitchView?.dispTop?.toFloat()?.div(glitchView?.scaleYG?:1f)?:0f)

        when (effect) {
            Effect.GLITCH -> Log.v("ImageView", "glitch")
            Effect.ANAGLYPH -> anaglyph(canvas, effectProgress)
            else -> Log.v("ImageView", "BASE")
        }

        canvas?.restore()


    }

    private fun drawJPEGEffect(){
        when (effect){
            Effect.GLITCH -> glitch(null)
            else -> {}
        }
    }



    fun calculateScaleFactory(bitmap: Bitmap?){
        if(bitmap!=null){
            if(bitmap.width > bitmap.height){
                scaledFactory = (glitchView?.glitchWidth?:0f).div(bitmap.width)
            }else{
                scaledFactory = (glitchView?.glitchHeight?:0f).div(bitmap.height)
            }
        }else{
            scaledFactory = 1f
        }
    }
}