package me.bemind.glitch

import android.graphics.Point

/**
 * Created by angelomoroni on 13/04/17.
 */

enum class Effect {
    BASE,ANAGLYPH, GLITCH, WEBP, SWAP,
    NOISE,GHOST,HOOLOOVOO,WOBBLE, PIXEL,
    TPIXEL, CENSORED
}

enum class TypeEffect {
    JPEG,CANVAS,NONE
}

enum class Motion{
    NONE, LEFT, RIGHT, UP, DOWN
}

enum class MotionType {
    ZOOM,MOVE, ROTATE
}

fun Point.copy(p: Point) {
    x = p.x
    y = p.y
}
