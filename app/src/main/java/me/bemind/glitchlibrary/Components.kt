package me.bemind.glitchlibrary

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by angelomoroni on 04/05/17.
 */


enum class TYPEFONT {
    BOLD,REGULAR,LIGHT
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
