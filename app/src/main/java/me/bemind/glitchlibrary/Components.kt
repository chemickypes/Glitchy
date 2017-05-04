package me.bemind.glitchlibrary

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by angelomoroni on 04/05/17.
 */


enum class TYPEFONT {
    BOLD,REGULAR,LIGHT
}

class GlitcyTypefaceSpan(family:String,val typeface:Typeface) : TypefaceSpan(family){
    override fun updateDrawState(ds: TextPaint?) {
        applyCustomTypeFace(ds,typeface)
    }

    override fun updateMeasureState(paint: TextPaint?) {
        applyCustomTypeFace(paint,typeface)
    }

    private fun applyCustomTypeFace(ds: TextPaint?, typeface: Typeface) {
        val oldstyle = when(ds?.typeface){
            null -> 0
            else -> ds.typeface.style
        }

        val fake = oldstyle and typeface.style.inv()

        if((fake and Typeface.BOLD)!=0){
            ds?.isFakeBoldText = true
        }

        if((fake and Typeface.ITALIC)!=0){
            ds?.textSkewX = (-0.25f)
        }

        ds?.typeface = typeface
    }
}

class RegularTypeFaceSpan : TypefaceSpan {

    private val newType: Typeface?

    constructor(family: String, type: Typeface) : super(family) {
        newType = type
    }

    constructor(source: Parcel) : super(source) {
        newType = null
    }

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {

        private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
            val oldStyle: Int
            val old = paint.typeface
            if (old == null) {
                oldStyle = 0
            } else {
                oldStyle = old.style
            }

            val fake = oldStyle and tf!!.style.inv()
            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }

            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = tf
        }


    }
}

object GlitchyTypeFaceGetter {
    val BOLD = "Montserrat-Bold.otf"
    val REGULAR = "Montserrat-Regular.otf"
    val LIGHT = "Montserrat-Light.otf"

    fun getTypeFace(context:Context, typefont: TYPEFONT = TYPEFONT.REGULAR) : Typeface{
        return Typeface.createFromAsset(context.assets,
                when(typefont){
                    TYPEFONT.LIGHT -> LIGHT
                    TYPEFONT.BOLD -> BOLD
                    else -> REGULAR
                })
    }
}

interface CustomFontText {
    fun customizeTypeFace(context: Context,typefont: TYPEFONT,action : (Typeface) -> Unit){
        action(GlitchyTypeFaceGetter.getTypeFace(context,typefont))
    }
}

class GlithcyTextView : TextView,CustomFontText{

    constructor(context: Context) : super(context){
        initView()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet? = null) {

        if(isInEditMode || attrs == null) return

        //attrs is not null
        val a = context.obtainStyledAttributes(attrs,R.styleable.GlithcyTextView)
        val tyEnum = a.getInteger(R.styleable.GlithcyTextView_glitchyTypeface,0)
        val ty = when(tyEnum){
            0 -> TYPEFONT.REGULAR
            1 -> TYPEFONT.BOLD
            2 -> TYPEFONT.LIGHT
            else -> TYPEFONT.REGULAR
        }

        customizeTypeFace(context,ty, this::setTypeface)
    }

}
