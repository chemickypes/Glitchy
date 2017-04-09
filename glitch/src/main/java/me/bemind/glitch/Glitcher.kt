package me.bemind.glitch

import android.graphics.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Created by angelomoroni on 27/03/17.
 */


object GlitcherUtil {
    fun byteArrayFromBitmap(bitmap: Bitmap?): ByteArray?{
        val byteOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,byteOutputStream)
        return byteOutputStream.toByteArray()
    }


    fun bitmapFromByteArray(bytes:ByteArray?): Bitmap? = BitmapFactory.decodeByteArray(bytes,0,bytes?.size?:0)
}


class Glitcher private constructor(){


    companion object Creator {
        fun  getGlitcher():Glitcher{
            val glitcher = Glitcher()
           /* glitcher.original = bitmap
            glitcher.result = glitcher.original!!.copy(glitcher.original?.config,true)*/

            return glitcher
        }
    }

   /* var original : Bitmap? = null
    var result : Bitmap? = null*/




    /*fun restore() : Bitmap?{

        result = original!!.copy(original?.config,true)
        return original
    }*/


    fun corruptBitmap(result:Bitmap?) : Bitmap{



        val RANDOM = Random()

        val h = result?.height?:0
        val w = result?.width?:0

        val arrayLen = (w) * (h)
        val stride =  w




        val intArray = IntArray(arrayLen)
        result?.getPixels(intArray,0,stride,0,0,w,h)

        val intArrayM = IntArray(intArray.size,{ i -> intArray[i].xor(RANDOM.nextInt(stride))})

        val vv = result!!.copy(result.config,true)
        vv!!.setPixels(intArrayM,0,stride,0,0,w,h)


        return vv



    }

    fun corruption(result: Bitmap?) : Bitmap?{

        val JPEG_CORRUPTION_COUNT = 5
        //val JPEG_HEADER_SIZE = 100
        val RANDOM = Random()

        val res = GlitcherUtil.byteArrayFromBitmap(result)?.clone()?:kotlin.ByteArray(0)

        if(res.isNotEmpty()) {
            for (i in 0..JPEG_CORRUPTION_COUNT - 1) {
                val idx = RANDOM.nextInt(res.size )
                res[idx] = (res[idx] + RANDOM.nextInt(3)).toByte()
            }

            return GlitcherUtil.bitmapFromByteArray(res)
        }

        return null
    }

    fun negative(result: Bitmap?) : Bitmap?{


        val h = result?.height?:0
        val w = result?.width?:0

        val negativeArray = floatArrayOf(
                -1f,  0f,  0f,  0f, 255f,
                0f, -1f,  0f,  0f, 255f,
                0f,  0f, -1f,  0f, 255f,
                0f,  0f,  0f,  1f,   0f
        )

        val bitmap = Bitmap.createBitmap(
                w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.set(negativeArray)

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(result, 0f, 0f, paint)

        return bitmap


        /*

        XOR method

        val arrayLen = (w * h)
        val stride =  w

        val intArray = IntArray(arrayLen)
        result!!.getPixels(intArray,0,stride,0,0,w,h)

        val intArrayM = IntArray(intArray.size,{ i -> intArray[i].xor(Int.MAX_VALUE)})

        val vv = result!!.copy(result!!.config,true)
        vv!!.setPixels(intArrayM,0,stride,0,0,w,h)

        result = vv
        */
    }

    fun shuffle(result: Bitmap?) :Bitmap?{
        return generateBitmap(result) { shuffleRow(it)}
    }


    fun pixelSort (result: Bitmap?) :Bitmap?{
        return generateBitmap(result){sortRow(it)}
    }

    fun anaglyph (result: Bitmap?,percentage:Int = 20) : Bitmap?{

        val leftArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
        val rightArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)


        val w = result?.width?:0
        val h = result?.height?:0

        val transX = ((w/2) * percentage).div(100)
        val transY = 15

        val anaglyphShader = BitmapShader(result, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val anaglyphPaint = Paint()
        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader

        val colorMatrix = ColorMatrix()

        val bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(0, PorterDuff.Mode.CLEAR)

        //left
        val matrix = Matrix()
        matrix.setTranslate((-transX).toFloat(), (-transY).toFloat())
        anaglyphShader.setLocalMatrix(matrix)
        colorMatrix.set(leftArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)

        //right
        val matrix2 = Matrix()
        matrix2.setTranslate((transX).toFloat(), transY.toFloat())
        anaglyphShader.setLocalMatrix(matrix2)
        colorMatrix.set(rightArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)


        c.drawBitmap(result,0f,0f,anaglyphPaint)

        return bitmap


    }


    private fun shuffleRow(row: List<Int>) : List<Int> {
        val RANDOM = Random()
        val offset = RANDOM.nextInt(row.size/2)
        return List(row.size, {ri -> row[(ri+offset)%row.size]})

    }

    private fun sortRow (row: List<Int>) : List<Int> {
        val sortedRow = List(row.size,{ri -> row[ri]})
        return sortedRow.sorted()
    }

    private fun generateBitmap (result: Bitmap?, action: (List<Int>) -> List<Int>) : Bitmap?{
        val r = List(result?.width?:0,{ row -> List(result?.height?:0,{ col -> result!!.getPixel(row,col)})})

        val  rShuffle = List(result?.height?:0,{row -> action(r[row])})

        val rr= Bitmap.createBitmap(result?.width?:0,result?.height?:0, Bitmap.Config.ARGB_8888)


        for(i in 0..(rr?.height?:1)-1){
            for (j in 0..(rr?.width?:1)-1){
                rr!!.setPixel(j,i,rShuffle[i][j])
            }
        }

        return rr
    }



}




