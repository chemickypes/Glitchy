package me.bemind.glitchappcore.glitch

import android.util.Log
import me.bemind.glitch.Effect
import me.bemind.glitch.Glitcher
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bemind.glitch.Motion
import me.bemind.glitch.TypeEffect
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.app.ProgressUpdate
import java.lang.Exception


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

    fun onTouchEvent(event:MotionEvent?) :Boolean

    fun saveEffect()

    fun initEffect(bitmap: Bitmap?,effect:Effect)

    fun saveInstanceState(glitchyBaseActivity: GlitchyBaseActivity,outState: Bundle?)

    fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity, savedInstanceState:Bundle?)

    fun initEffect(bitmap: Bitmap?, restore: Boolean)

    fun clearEffect()
    fun makeEffect(progress: Int)


    //effects
    fun anaglyph(canvas: Canvas?,progress:Int = 20)
    fun ghost(canvas: Canvas?,x:Int,y:Int,motion: Motion)
    fun wobble(canvas: Canvas?,x:Int,y:Int,motion: Motion)
    fun glitch(canvas: Canvas?)
    fun webp(canvas: Canvas?)
    fun swap(canvas: Canvas?)
    fun noise(canvas: Canvas?,progress: Int = 170)
    fun hooloovooize(canvas: Canvas?,progress: Int = 20)
    fun pixelize(canvas: Canvas?,progress: Int = 70,x: Int,y: Int)
    fun pixelizeTot(canvas: Canvas?,progress: Int = 70)


}

class GlitchPresenter(val context: Context) : IGlitchPresenter, GestureDetector.OnGestureListener {



    private val EFFECT_PROGRESS_K: String? = "eef_pro_k"
    private val EFFECT_K: String? = "effect_k"
    private val EFFECT_ON_K: String? = "eef_on_k"
    private val TOUCH_POINT_K: String? = "touch_point_k"
    private val MOTION_K: String? = "motion_k"

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

    var gestureDetector : GestureDetectorCompat? = null

    val viewCoords = IntArray(2)

    //touch properties

    private var touchPoint = Point(-1,-1)
    private var proviousPoint: Point? = null
    private var startTouchX = 0
    private var startTouchY = 0
    private var motion: Motion = Motion.NONE
    //end touch properties

    val setImageAction = {
        b : Bitmap? -> glitchView?.setImageBitmap(b,true) //volatile
        glitchView?.showLoader(false)
        volatileBitmap = b


    }

    override var effect  = Effect.BASE
        set(value) {
            field = value
            glitchView?.invalidateGlitchView()
        }

    override val typeEffect: TypeEffect
        get() = when (effect){
            Effect.GLITCH -> TypeEffect.JPEG
            Effect.WEBP -> TypeEffect.JPEG
            Effect.SWAP -> TypeEffect.JPEG
            Effect.NOISE -> TypeEffect.CANVAS
            Effect.ANAGLYPH -> TypeEffect.CANVAS
            Effect.GHOST -> TypeEffect.CANVAS
            Effect.WOBBLE -> TypeEffect.CANVAS
            Effect.HOOLOOVOO -> TypeEffect.CANVAS
            Effect.PIXEL -> TypeEffect.CANVAS
            Effect.TPIXEL -> TypeEffect.CANVAS
            else -> TypeEffect.NONE
        }

    init {
        gestureDetector = GestureDetectorCompat(context,this)
    }

    override fun anaglyph(canvas: Canvas?, progress: Int) {
        //glitchLogic.anaglyph(canvas, progress)

        glithce.anaglyphCanvas(canvas,progress)
    }

    override fun ghost(canvas: Canvas?, x: Int, y: Int, motion: Motion) {
        if(touchPoint.x>-1) {
            glithce.ghostCanvas(canvas, x, y, motion)
        }
    }

    override fun wobble(canvas: Canvas?, x: Int, y: Int, motion: Motion) {
        if(touchPoint.x>-1) {
            glithce.wobbleCanvas(canvas, x, y, motion)
        }
    }


    override fun hooloovooize(canvas: Canvas?,progress: Int) {
        glithce.hooloovooizeCanvas(canvas,progress)
    }

    override fun pixelize(canvas: Canvas?, progress: Int,x: Int,y: Int) {
        glithce.pixelCanvas(canvas,progress,x,y)
    }

    override fun pixelizeTot(canvas: Canvas?, progress: Int) {
        glithce.totalPixelCanvas(canvas,progress)
    }

    override fun glitch(canvas: Canvas?) {
        observeImage({
            glithce.corruption(glithce.baseBitmap)
        },setImageAction)

    }

    override fun swap(canvas: Canvas?) {
        observeImage({
            glithce.swap(glithce.baseBitmap)
        },setImageAction)
    }

    override fun webp(canvas: Canvas?) {
        observeImage(
                {glithce.webp(glithce.baseBitmap)},
                setImageAction)
    }

    override fun noise(canvas: Canvas?, progress: Int) {
        /*observeImage(
                {glithce.noise(glithce.baseBitmap)},
                setImageAction
        )*/
        glithce.noiseCanvas(canvas,progress)
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

        outState?.putParcelable(TOUCH_POINT_K,touchPoint)
        outState?.putInt(MOTION_K,motion.ordinal)


        glitchyBaseActivity.retainedFragment?.volatileBitmap = volatileBitmap

        clearEffect()
    }

    override fun restoreSavedInstanceState(glitchyBaseActivity: GlitchyBaseActivity,savedInstanceState: Bundle?) {
        touchPoint = savedInstanceState?.getParcelable(TOUCH_POINT_K)?: Point(0,0)
        motion = Motion.values()[savedInstanceState?.getInt(MOTION_K,Motion.NONE.ordinal)?:0]
        effectProgress = savedInstanceState?.getInt(EFFECT_PROGRESS_K,0)?:0
        effectON = savedInstanceState?.getBoolean(EFFECT_ON_K,false)?:false
        if(effectON){
            restore = true
            effect = savedInstanceState?.getSerializable(EFFECT_K) as Effect
            volatileBitmap = glitchyBaseActivity.retainedFragment?.volatileBitmap

            //if(effect == Effect.GHOST) glitchView?.invalidateGlitchView()
        }
    }

    override fun saveEffect() {

        Observable.fromCallable {
            if(typeEffect == TypeEffect.CANVAS) {
                val b = Bitmap.createBitmap(glitchView?.getImageBitmap()?.width ?: 1,
                        glitchView?.getImageBitmap()?.height ?: 1,
                        Bitmap.Config.ARGB_8888)

                val canvas = Canvas(b)

                if(effect == Effect.NOISE || effect == Effect.TPIXEL) {
                    canvas.drawBitmap(glitchView?.getImageBitmap(),0f,0f,null)
                }

                onDraw(canvas)
                return@fromCallable b
            }else{
                return@fromCallable volatileBitmap
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            b -> clearEffect()
                            glitchView?.setImageBitmap(b)


                        },
                        { t ->  t.printStackTrace()}
                )

    }

    override fun initEffect(bitmap: Bitmap?,effect: Effect) {
       // glitchLogic.initEffect(bitmap?.width?:0,bitmap?.height?:0,effect)


        effectON = true
        glithce.initEffect(effect,bitmap,getNoiseBitmap())

        this.effect = effect

        calculateScaleFactory(bitmap)

        touchPoint = Point(-1,-1)

        when (effect){
            Effect.ANAGLYPH -> effectProgress = 20
            Effect.NOISE -> effectProgress = 120
            Effect.HOOLOOVOO -> effectProgress = 20
            Effect.PIXEL -> effectProgress = 70
            else -> effectProgress = 0
        }
    }

    private fun getNoiseBitmap(): Bitmap? {
        try {
            return BitmapFactory.decodeStream(context.assets.open("noise.png"))
        }catch(e:Exception) {
            e.printStackTrace()
            return null
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
        touchPoint = Point(0,0)
        motion = Motion.NONE
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector?.onTouchEvent(event)

        val index = event!!.actionIndex
        val flI = floatArrayOf(event.getX(index),event.getY(index))
        touchPoint = calculateTouchPoint(flI)

        if(proviousPoint==null){
            proviousPoint = Point(((glitchView?.viewX?:0f)/2f).toInt(),
                    ((glitchView?.viewY?:0f)/2f).toInt())
        }


        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                startTouchX = touchPoint.x
                startTouchY = touchPoint.y
                motion = Motion.NONE

                proviousPoint?.copy(touchPoint)
            }
            MotionEvent.ACTION_UP -> {
                //touchX  = -1
                //touchY  = -1
                if(effect == Effect.GLITCH || effect == Effect.SWAP ){
                    makeEffect(0)
                }

            }
            MotionEvent.ACTION_MOVE -> {
               /* startTouchX = touchPoint.x
                startTouchY = touchPoint.y*/

                if(effect == Effect.ANAGLYPH || effect == Effect.NOISE || effect == Effect.PIXEL){

                    val p =  (touchPoint.x) - (proviousPoint?.x?:0)
                    ProgressUpdate.updateProgress(p.toFloat())

                    proviousPoint?.copy(touchPoint)
                }
            }
        }

        if(effect == Effect.GHOST || effect == Effect.WOBBLE || effect == Effect.TPIXEL){
            glitchView?.invalidateGlitchView()
        }

        return true
    }

    private fun calculateTouchPoint(event: FloatArray): Point {

        val im :ImageView = (glitchView as? ImageView)!!

        val dr : Drawable = glitchView?.viewDrawable!!

        val bX = (event[0] * dr.intrinsicWidth)/im.width
        val bY = (event[1] * dr.intrinsicHeight)/im.height

        return Point(bX.toInt(),bY.toInt())

    }

    override fun onShowPress(p0: MotionEvent?) {
        //nothing
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        //nothing
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {


        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        if(motion != Motion.NONE) return false

        if(Math.abs(p2)>Math.abs(p3)){
            if(Math.abs(p2)<1) return true

            motion = if(p2 < 0){ Motion.RIGHT }else{ Motion.LEFT }
            return true

        }else if(Math.abs(p3)<=1){
            return true
        }else{
            motion = if (p3 < 0) { Motion.DOWN }else{ Motion.UP }
            return true
        }
    }

    override fun onLongPress(p0: MotionEvent?) {
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
            Effect.GHOST -> ghost(canvas,touchPoint.x,touchPoint.y,motion)
            Effect.WOBBLE -> wobble(canvas,touchPoint.x,touchPoint.y,motion)
            Effect.ANAGLYPH -> anaglyph(canvas, effectProgress)
            Effect.NOISE -> noise(canvas,effectProgress)
            Effect.HOOLOOVOO -> hooloovooize(canvas,effectProgress)
            Effect.PIXEL -> pixelizeTot(canvas,effectProgress)
            Effect.TPIXEL -> pixelize(canvas,effectProgress,touchPoint.x,touchPoint.y)
            else -> Log.v("ImageView", "BASE")
        }

        //DEBUG
        /*if(effect == Effect.GHOST || effect == Effect.WOBBLE || effect == Effect.PIXEL) {
            glithce.drawPath(canvas,touchPoint.x,touchPoint.y)
        }*/

        canvas?.restore()

    }

    private fun drawJPEGEffect(){
        when (effect){
            Effect.GLITCH -> glitch(null)
            Effect.WEBP -> webp(null)
            Effect.SWAP -> swap(null)
            Effect.NOISE -> noise(null)
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

    private fun observeImage(action : () ->Bitmap?, nextAction : (Bitmap?) -> Unit){
        glitchView?.showLoader(true)
        Observable.fromCallable(action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        nextAction,
                        {
                            t : Throwable -> t.printStackTrace()
                            glitchView?.showError(t)
                        }
                )
    }
}

private fun Point.copy(p1: Point) {
    x = p1.x
    y = p1.y
}
