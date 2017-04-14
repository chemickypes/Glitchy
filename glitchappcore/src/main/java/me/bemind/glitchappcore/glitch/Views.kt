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
import android.graphics.drawable.Drawable
import android.R.attr.scaleY
import android.R.attr.scaleX
import android.graphics.Rect


/**
 * Created by angelomoroni on 13/04/17.
 */


interface IGlitchView {


    fun getImageBitmap() : Bitmap?

    fun updateProgress(progress: Int)

    fun initEffect(effect: Effect)

    fun invalidateGlitchView()

    var dispTop: Int

    var dispLeft: Int

    var scaleXG: Float
    var scaleYG: Float

}


/**
 * extension of ImageView in order to implement IGlitchView
 */
class ExtendedImageView : ImageView, IGlitchView {

    override var scaleXG: Float = 0f

    override var scaleYG: Float = 0f

    override var dispTop: Int = 0

    override var dispLeft: Int = 0

    var newPhoto = false

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

        if(!newPhoto)glitcPresenter.onDraw(canvas)

        newPhoto = false
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

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        newPhoto = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        loadMesure()
    }





    private fun loadMesure() {
        val fArr = FloatArray(9)
        imageMatrix.getValues(fArr)
        getWindowVisibleDisplayFrame(Rect())
        this.scaleXG = fArr[0]
        this.scaleYG = fArr[4]
        this.dispTop = fArr[5].toInt()
        this.dispLeft = fArr[2].toInt()
        /*val drawable = drawable
        if (drawable != null) {
            val intrinsicWidth = drawable.intrinsicWidth
            this.effects.SetlRect(Math.max(drawable.intrinsicHeight, intrinsicWidth) / 100 * 3)
        }*/
    }

    private fun initView() {
        glitcPresenter.glitchView = this

    }
}
