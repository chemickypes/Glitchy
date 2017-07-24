package me.bemind.glitchappcore.glitch

import android.util.Log
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ScaleGestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector
import com.yalantis.ucrop.util.RotationGestureDetector
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bemind.glitch.*
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.app.ProgressUpdate
import kotlin.Exception


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
    fun anaglyphPoint(canvas: Canvas?, absX: Float, absY: Float)
    fun censored(canvas: Canvas?, absDeltaX: Float, absDeltaY: Float,angleToRotate:Int,
                 mXScaledFactor :Float = 1f,mYScaleFactor:Float = 1f,motionType: MotionType = MotionType.MOVE)
}

class GlitchPresenter(val context: Context) : IGlitchPresenter, GestureDetector.OnGestureListener
        /*ScaleGestureDetector.OnScaleGestureListener, MoveGestureDetector.OnMoveGestureListener */{



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
    var scaleDetector :  ScaleGestureDetector? = null
    var mRotateDetector :  RotateGestureDetector? = null
    var mMoveDetector : MoveGestureDetector? = null

    val viewCoords = IntArray(2)

    var absDeltaX = 0f
    var absDeltaY = 0f

    private var mLastAngle: Int = 0
    private var angleToRotate: Int = 0

    private var motionType: MotionType = MotionType.MOVE

    //touch properties

    private var touchPoint = Point(-1,-1)
    private var previousPoint: Point? = null
    private var startTouchX = 0
    private var startTouchY = 0
    private var motion: Motion = Motion.NONE
    private var mScaleFactor: Float = 1f
    private var mXScaleFactor: Float = 1f
    private var mYScaleFactor: Float = 1f

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
            Effect.CENSORED -> TypeEffect.CANVAS
            else -> TypeEffect.NONE
        }


    init {
        gestureDetector = GestureDetectorCompat(context,this)
        scaleDetector = ScaleGestureDetector(context,ScaleListener())
        mMoveDetector = MoveGestureDetector(context,MoveListener())
        mRotateDetector = RotateGestureDetector(context,RotateListener())
    }

    override fun anaglyph(canvas: Canvas?, progress: Int) {
        //glitchLogic.anaglyph(canvas, progress)

        glithce.anaglyphCanvas(canvas,progress)
    }

    override fun anaglyphPoint(canvas: Canvas?, absX:Float, absY:Float) {
        glithce.anaglyphCanvas(canvas,absX,absY)
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

    override fun censored(canvas: Canvas?, absDeltaX: Float, absDeltaY: Float,angleToRotate:Int,mXScaleFactor:Float,mYScaleFactor:Float,motionType: MotionType){
        glithce.censoredCanvas(canvas,absDeltaX,absDeltaY,angleToRotate.toFloat(),mXScaleFactor,mYScaleFactor,motionType)
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

                if(effect == Effect.NOISE || effect == Effect.TPIXEL || effect == Effect.CENSORED) {
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
            Effect.TPIXEL -> effectProgress = 25
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

        val index = event!!.actionIndex
        val flI = floatArrayOf(event.getX(index),event.getY(index))
        touchPoint = calculateTouchPoint(flI)

        if(previousPoint==null){
            previousPoint = Point(((glitchView?.viewX?:0f)/2f).toInt(),
                    ((glitchView?.viewY?:0f)/2f).toInt())
        }

        scaleDetector?.onTouchEvent(event)
        mRotateDetector?.onTouchEvent(event)
        mMoveDetector?.onTouchEvent(event)
        gestureDetector?.onTouchEvent(event)


        if(effect == Effect.GHOST || effect == Effect.WOBBLE || effect == Effect.TPIXEL
                || effect == Effect.ANAGLYPH || effect == Effect.CENSORED) {
            glitchView?.invalidateGlitchView()
        }




        return true
    }

    /*override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(p0: ScaleGestureDetector?) {
    }



    override fun onScale(p0: ScaleGestureDetector?): Boolean {
        //mScaleFactor *= (p0?.scaleFactor?.toFloat() ?: 1f)
        mScaleFactor = p0?.scaleFactor?:1f
        return true
    }*/

     //#######################
    private fun doubleTouchEvent(event: MotionEvent?) {
        event?.let {
            if(effect == Effect.CENSORED){
                val deltaX = it.getX(0) - it.getX(1)
                val deltaY = it.getY(0) - it.getY(1)
                val radians = Math.atan((deltaY / deltaX).toDouble())
                //Convert to degrees
                val degrees = (radians * 180 / Math.PI).toInt()

                when(it.actionMasked){
                    MotionEvent.ACTION_MOVE -> {

                        motionType = MotionType.ROTATE
                        angleToRotate = degrees - mLastAngle
                    }
                    /*MotionEvent.ACTION_POINTER_UP -> {
                    }*/
                    else ->{
                        mLastAngle = degrees
                    }
                }


                glitchView?.invalidateGlitchView()
            }
        }

    }

    private fun singleTouchEvent(event: MotionEvent?){
        val index = event!!.actionIndex
        val flI = floatArrayOf(event.getX(index),event.getY(index))
        touchPoint = calculateTouchPoint(flI)

        if(previousPoint==null){
            previousPoint = Point(((glitchView?.viewX?:0f)/2f).toInt(),
                    ((glitchView?.viewY?:0f)/2f).toInt())
        }


        when (event.action){
            MotionEvent.ACTION_DOWN -> {
                startTouchX = touchPoint.x
                startTouchY = touchPoint.y
                motion = Motion.NONE

                previousPoint?.copy(touchPoint)
            }
            MotionEvent.ACTION_UP -> {
                //touchX  = -1
                //touchY  = -1
                if(effect == Effect.GLITCH || effect == Effect.SWAP ){
                    makeEffect(0)
                }

                motionType = MotionType.NONE

            }
            MotionEvent.ACTION_MOVE -> {
                /* startTouchX = touchPoint.x
                 startTouchY = touchPoint.y*/

                if(motionType != MotionType.ROTATE && motionType != MotionType.ZOOM) {

                    motionType = MotionType.MOVE

                    val p = (touchPoint.x) - (previousPoint?.x ?: 0)

                    absDeltaX = p.toFloat()
                    absDeltaY = (touchPoint.y - (previousPoint?.y ?: 0)).toFloat()

                    previousPoint?.copy(touchPoint)


                    if (/*effect == Effect.ANAGLYPH ||*/ effect == Effect.NOISE || effect == Effect.PIXEL) {

                        ProgressUpdate.updateProgress(p.toFloat())

                    }
                }
            }
        }

        if(effect == Effect.GHOST || effect == Effect.WOBBLE || effect == Effect.TPIXEL
                || effect == Effect.ANAGLYPH || effect == Effect.CENSORED) {
            glitchView?.invalidateGlitchView()
        }
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
        if(effect == Effect.GLITCH || effect == Effect.SWAP ){
            makeEffect(0)
        }
        return true
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



    override fun onDraw(canvas: Canvas?, scale: Boolean){


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
            Effect.ANAGLYPH -> anaglyphPoint(canvas, absDeltaX,absDeltaY)
            Effect.NOISE -> noise(canvas,effectProgress)
            Effect.HOOLOOVOO -> hooloovooize(canvas,effectProgress)
            Effect.PIXEL -> pixelizeTot(canvas,effectProgress)
            Effect.TPIXEL -> pixelize(canvas,effectProgress,touchPoint.x,touchPoint.y)
            Effect.CENSORED -> censored(canvas,absDeltaX,absDeltaY,angleToRotate,mXScaleFactor,mYScaleFactor,motionType)
            else -> Log.v("ImageView", "BASE")
        }

        //DEBUG
        /*if(effect == Effect.GHOST || effect == Effect.WOBBLE || effect == Effect.PIXEL) {
            glithce.drawPath(canvas,touchPoint.x,touchPoint.y)
        }*/

        try{
            canvas?.restore()
        }catch (e: Exception){
            e.printStackTrace()
        }

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

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor // scale change since previous event

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))

            //mPrevSpan > 0 ? mCurrSpan / mPrevSpan : 1

            if(detector.currentSpanX>detector.currentSpanY) {
                mXScaleFactor *= if (detector.previousSpanX > 0) {
                    detector.currentSpanX / detector.previousSpanX
                } else {
                    1f
                }

                mXScaleFactor = Math.max(0.1f, Math.min(mXScaleFactor, 10.0f))
            }else {

                mYScaleFactor *= if (detector.previousSpanY > 0) {
                    detector.currentSpanY / detector.previousSpanY
                } else {
                    1f
                }

                mYScaleFactor = Math.max(0.1f, Math.min(mYScaleFactor, 10.0f))
            }

            return true
        }
    }

    private inner class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener() {
        override fun onRotate(detector: RotateGestureDetector): Boolean {
            angleToRotate -= (detector.rotationDegreesDelta.toInt() )
            return true
        }
    }

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            val d = detector.focusDelta
           /* mFocusX += d.x
            mFocusY += d.y*/

            absDeltaY = d.y
            absDeltaX = d.x

            if (/*effect == Effect.ANAGLYPH ||*/ effect == Effect.NOISE || effect == Effect.PIXEL) {

                ProgressUpdate.updateProgress(absDeltaX)

            }
            // mFocusX = detector.getFocusX();
            // mFocusY = detector.getFocusY();
            return true
        }
    }


}

private fun Point.copy(p1: Point) {
    x = p1.x
    y = p1.y
}
