package me.bemind.glitch

import android.graphics.Path

/**
 * Created by angelomoroni on 23/07/17.
 */

fun getPathFromShape (shape: GShape, path: Path? = null) : Path {
    val p = path?:Path()
    p.rewind()
    for(i in 0 until shape.vertices.size){
        val point = shape.vertices[i]
        if(i == 0){
            p.moveTo(point.x.toFloat(),point.y.toFloat())
        }else{
            p.lineTo(point.x.toFloat(),point.y.toFloat())
        }
    }

    p.close()

    return p
}