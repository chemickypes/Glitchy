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
import android.os.Bundle
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.history.HistoryPresenter
import me.bemind.glitchappcore.history.IHistoryView


/**
 * Created by angelomoroni on 13/04/17.
 */


interface IGlitchView {


    fun getImageBitmap() : Bitmap?

    fun setImageBitmap(bitmap: Bitmap?)

    fun updateProgress(progress: Int)

    fun initEffect(effect: Effect)

    fun invalidateGlitchView()

    fun save()

    var dispTop: Int

    var dispLeft: Int

    var scaleXG: Float
    var scaleYG: Float
    fun clearEffect()

    fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity,outState: Bundle?)
    fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity,savedInstanceState: Bundle?)

    fun onResume()

}


/**
 * extension of ImageView in order to implement IGlitchView
 */
class ExtendedImageView : ImageView, IGlitchView,IHistoryView {


    override var scaleXG: Float = 0.0f
        get()  = this.scaleX

    override var scaleYG: Float = 0f
        get() = this.scaleY


    override var dispTop: Int = 0

    override var dispLeft: Int = 0

    var newPhoto = false

    val glitcPresenter = GlitchPresenter()
    val historyPresenter = HistoryPresenter()

    override var hasHistory: Boolean
        get() = historyPresenter.hasHistory
        set(value) {/*nothing*/}



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

        //newPhoto = false
    }

    override fun clearEffect() {
        setImageBitmap(getImageBitmap(),false,false)
    }

    override fun updateProgress (progress:Int){
        glitcPresenter.effectProgress = progress
        invalidateGlitchView()
    }

    override fun getImageBitmap(): Bitmap? {

        if(drawable!=null) {
            val b = (drawable as BitmapDrawable).bitmap
           return b
        }else{
            return null
        }


    }

    override fun invalidateGlitchView() {
        invalidate()
    }

    override fun save() {
        glitcPresenter.saveEffect()
        //save to history
    }

    override fun setImageBitmap(bm: Bitmap?) {
        //super.setImageBitmap(bm)
        //newPhoto = true
        setImageBitmap(bm,false,true)
    }

    fun setImageBitmap(bm: Bitmap?,newphoto:Boolean = false,toAdd:Boolean = false){
        super.setImageBitmap(bm)
        if(toAdd) historyPresenter.addImage(bm!!,newphoto)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        loadMesure()
    }

    override fun back(): Boolean {
        if(historyPresenter.canBack){
            historyPresenter.back()
            return true
        }else{
            return false
        }
    }

    override fun setPreviousImage(back: Bitmap?,restore:Boolean ) {
        setImageBitmap(back,false,false)
    }

    override fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity, outState: Bundle?) {
        glitchyBaseActivity.retainedFragment?.history = historyPresenter.getHistoryToSave()
        glitcPresenter.saveInstanceState(outState)
    }

    override fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity, savedInstanceState: Bundle?) {
        glitcPresenter.restoreSavedInstanceState(savedInstanceState)
        historyPresenter.restoreHistory(glitchyBaseActivity.retainedFragment?.history)

    }

    override fun onResume() {
        if(glitcPresenter.restore ){
            glitcPresenter.initEffect(getImageBitmap(),glitcPresenter.restore)
            invalidateGlitchView()
        }
    }

    private fun loadMesure() {
        val fArr = FloatArray(9)
        imageMatrix.getValues(fArr)
        getWindowVisibleDisplayFrame(Rect())
        this.scaleX = fArr[0]
        this.scaleY = fArr[4]
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
        historyPresenter.historyView = this

    }
}
