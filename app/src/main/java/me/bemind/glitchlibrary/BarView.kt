package me.bemind.customcanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import me.bemind.glitchlibrary.R


/**
 * Created by angelomoroni on 18/07/17.
 */

class BarView : View {

    private var mPaint: Paint? = null

    var progress : Int = 0
    set(value) {
        field = value
        invalidate()
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        init(attrs)
    }



    private fun init(attrs: AttributeSet?) {

        val arr = context.theme.obtainStyledAttributes(attrs, R.styleable.BarView,0,0)

        val backColor = arr.getColor(R.styleable.BarView_barViewBackgroundColor,android.R.color.white)
        val barColor = arr.getColor(R.styleable.BarView_barViewColor,android.R.color.holo_orange_dark)
        val stroke = arr.getDimensionPixelSize(R.styleable.BarView_barViewThickness,16)

        val cap = arr.getInt(R.styleable.BarView_barViewCap,0)

        progress = arr.getInt(R.styleable.BarView_barViewProgress,0)

        mPaint = Paint(Paint.DITHER_FLAG)
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.color = barColor
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeJoin = Paint.Join.MITER
        mPaint!!.strokeCap = when(cap) {
            0 -> Paint.Cap.SQUARE
            else -> Paint.Cap.ROUND
        }
        mPaint!!.strokeWidth = stroke.toFloat()

        arr.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val calculatedX = (progress * width)/100f
        canvas?.drawLine(0f,0f,calculatedX,0f,mPaint)
    }

    fun addProgressOnSwipe(distx:Float){
        val p = (Math.abs(distx)*100)/width

        if(distx<0){
            addProgress(p.toInt() *- 1)
        }else{
            addProgress(p.toInt())
        }
    }


    fun addProgress(toAdd:Int){

        if(toAdd in -100..100) {
            val p = progress + toAdd

            if (p < 0) {
                progress = 0
            } else if (p > 100) {
                progress = 100
            } else {
                progress = p
            }
        }
    }

}