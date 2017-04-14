package me.bemind.glitchappcore.glitch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher

/**
 * Created by angelomoroni on 13/04/17.
 */


interface IGlitchView {


    fun getImageBitmap() : Bitmap?

    fun updateProgress(progress: Int)

    fun initEffect(effect: Effect)

    fun invalidateGlitchView()
}


/**
 * extension of ImageView in order to implement IGlitchView
 */
class ExtendedImageView : ImageView, IGlitchView {





    val glitcPresenter = GlitchPresenter()

    constructor(context: Context) : super(context){
        initView()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    override fun initEffect(effect: Effect){

        glitcPresenter.initEffect(getImageBitmap(),effect)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        glitcPresenter.onDraw(canvas)

    }


    override fun updateProgress (progress:Int){
        glitcPresenter.effectProgress = progress
        invalidateGlitchView()
    }

    override fun getImageBitmap(): Bitmap? {

        if(drawable!=null) {
           return (drawable as BitmapDrawable).bitmap
        }else{
            return null
        }


    }

    override fun invalidateGlitchView() {
        invalidate()
    }

    private fun initView() {
        glitcPresenter.glitchView = this

    }
}
