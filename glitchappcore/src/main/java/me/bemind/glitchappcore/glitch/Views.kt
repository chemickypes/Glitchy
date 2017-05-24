package me.bemind.glitchappcore.glitch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import me.bemind.glitch.Effect
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import me.bemind.glitch.TypeEffect
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.history.HistoryPresenter
import me.bemind.glitchappcore.history.IHistoryView
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils




/**
 * Created by angelomoroni on 13/04/17.
 */


interface IGlitchView {


    fun getImageBitmap() : Bitmap?

    fun setImageBitmap(bitmap: Bitmap?,volatile:Boolean = false)

    fun makeEffect(progress: Int = -1)

    fun initEffect(effect: Effect)

    fun invalidateGlitchView()

    fun save()

    var dispTop: Int

    var dispLeft: Int

    var scaleXG: Float
    var scaleYG: Float

    val glitchWidth : Float
    val glitchHeight : Float

    val canInitEffect : Boolean

    fun clearEffect()

    fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity,outState: Bundle?)

    fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity,savedInstanceState: Bundle?)

    fun onResume()

    fun showLoader(show:Boolean)


}


/**
 * extension of ImageView in order to implement IGlitchView
 */
class ExtendedImageView : ImageView, IGlitchView,IHistoryView, View.OnLayoutChangeListener {


    override var scaleXG: Float = 0.0f
        get()  = this.scaleX

    override var scaleYG: Float = 0f
        get() = this.scaleY

    override val glitchWidth: Float
        get() = this.width.toFloat()
    override val glitchHeight: Float
        get() = this.height.toFloat()

    override var dispTop: Int = 0

    override var dispLeft: Int = 0

    var loaderView : View? =  null

    var loaderAnimation : ViewPropertyAnimator? = null


    override val canInitEffect: Boolean
        get() = hasHistory && !glitcPresenter.effectON




    val glitcPresenter = GlitchPresenter(context)
    val historyPresenter = HistoryPresenter(context)

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

        glitcPresenter.onDraw(canvas,true)

    }

    override fun clearEffect() {
        if(glitcPresenter.typeEffect == TypeEffect.CANVAS) {
            glitcPresenter.clearEffect()
            setImageBitmap(getImageBitmap(), false, false)
        }else{
            glitcPresenter.clearEffect()
            historyPresenter.lastImage()
        }
    }

    fun updateProgress (progress:Int){
        glitcPresenter.effectProgress = progress
        invalidateGlitchView()
    }

    override fun makeEffect(progress: Int) {
        glitcPresenter.makeEffect(progress)
    }

    override fun getImageBitmap(): Bitmap? {

        if(drawable!=null) {
           /* val b = (drawable as BitmapDrawable).bitmap
           return b*/
            //return historyPresenter.image
            return (drawable as BitmapDrawable).bitmap
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

    override fun setImageBitmap(bitmap: Bitmap?, volatile: Boolean) {
        setImageBitmap(bitmap,false,!volatile)
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        setImageBitmap(bitmap,false,true)
    }

    fun setImageBitmap(bm: Bitmap?,newphoto:Boolean = false,toAdd:Boolean = false, animation:Boolean = false){
        if (animation) {
               ImageViewAnimatedChange(context,this,bm)
        } else {
            super.setImageBitmap(bm)
        }
        if(toAdd) historyPresenter.addImage(bm!!,newphoto)

        loadMesure()
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

    override fun showLoader(show: Boolean) {
        if(show){
            loaderAnimation = loaderView?.animate()
                    ?.alpha(1f)
                    ?.setListener(
                            object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator?) {
                                    super.onAnimationStart(animation)
                                    loaderView?.visibility = View.VISIBLE
                                }
                            }
                    )
                    ?.setDuration(300)
                    ?.setStartDelay(100)

            loaderAnimation?.start()
        }else{
            loaderAnimation?.cancel()
            loaderView?.visibility = View.GONE
            loaderView?.alpha = 0f
        }
    }

    override fun setPreviousImage(back: Bitmap?,restore:Boolean ) {
        if(glitcPresenter.typeEffect == TypeEffect.JPEG){
           setImageBitmap(glitcPresenter.volatileBitmap,false,false)
        }else {
            setImageBitmap(back, false, false)
        }
    }

    override fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity, outState: Bundle?) {
        glitchyBaseActivity.retainedFragment?.history = historyPresenter.getHistoryToSave()
        glitcPresenter.saveInstanceState(glitchyBaseActivity,outState)
    }

    override fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity, savedInstanceState: Bundle?) {
        glitcPresenter.restoreSavedInstanceState(glitchyBaseActivity, savedInstanceState)
        historyPresenter.restoreHistory(savedInstanceState,glitchyBaseActivity.retainedFragment?.history)

    }

    override fun onResume() {
        if(glitcPresenter.restore ){
            glitcPresenter.initEffect(getImageBitmap(),glitcPresenter.restore)
            invalidateGlitchView()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        loadMesure()
    }

    override fun onLayoutChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
        if(p1 == 0 && p2 == 0 && p3 == 0 && p4 == 0) return

        if(p0?.width?:0 > 0 ){
            onResume()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
       return glitcPresenter.onTouchEvent(event)
    }

    private fun loadMesure() {
        val fArr = FloatArray(9)
        imageMatrix.getValues(fArr)
        getWindowVisibleDisplayFrame(Rect())
        this.scaleX = fArr[0]
        this.scaleY = fArr[4]
        this.dispTop = fArr[5].toInt()
        this.dispLeft = fArr[2].toInt()

        getLocationOnScreen(glitcPresenter.viewCoords)

    }

    private fun initView() {
        glitcPresenter.glitchView = this
        historyPresenter.historyView = this

        addOnLayoutChangeListener(this)


    }

    private fun ImageViewAnimatedChange(c: Context, v: ImageView, new_image: Bitmap?) {
        val anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out)
        val anim_in = AnimationUtils.loadAnimation(c, android.R.anim.fade_in)
        anim_out.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                v.setImageBitmap(new_image)
                anim_in.setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {}
                })
                v.startAnimation(anim_in)
            }
        })
        v.startAnimation(anim_out)
    }
}
