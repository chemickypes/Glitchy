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


class Glitcher {


    companion object Creator {
        fun  getGlitcher(bitmap: Bitmap):Glitcher{
            val glitcher = Glitcher()
            glitcher.original = bitmap
            glitcher.result = glitcher.original!!.copy(glitcher.original?.config,true)

            return glitcher
        }
    }

    var original : Bitmap? = null
    var result : Bitmap? = null




    fun restore() : Bitmap?{

        result = original!!.copy(original?.config,true)
        return original
    }


    fun corruptBitmap() : Glitcher{

        /*val RANDOM = Random()
        //val r = List(result!!.width,{ row -> List(result!!.height,{ col -> result!!.getPixel(row,col)})})

        var vv = result!!.copy(result!!.config,true)

        for(i in 0..50000){
            var indH = RANDOM.nextInt(vv!!.height) %vv!!.height
            var indW = RANDOM.nextInt(vv!!.width) %vv!!.width
            val to = RANDOM.nextInt()
            var pixel = vv!!.getPixel(indW,indH)
            Log.d("TAG", "pixel $pixel to $to")
            pixel = (pixel+to)
            Log.d("TAG", "mod pixel $pixel")
            vv!!.setPixel(indW,indH,pixel)


        }

        result = vv.copy(vv!!.config,false)
        return result*/

        val RANDOM = Random()

        val h = result!!.height
        val w = result!!.width

        val arrayLen = (w * h)
        val stride =  w

        val y = h/(RANDOM.nextInt(4)+1)
        val x = w/(RANDOM.nextInt(4)+1)



        val intArray = IntArray(arrayLen)
        result!!.getPixels(intArray,0,stride,0,0,w,h)

        val intArrayM = IntArray(intArray.size,{ i -> intArray[i].xor(RANDOM.nextInt(stride))})

        val vv = result!!.copy(result!!.config,true)
        vv!!.setPixels(intArrayM,0,stride,0,0,w,h)

        result = vv
        return this



    }

    fun corruption() : Glitcher{

        val JPEG_CORRUPTION_COUNT = 5
        val JPEG_HEADER_SIZE = 100
        val RANDOM = Random()

        val res = GlitcherUtil.byteArrayFromBitmap(result)?.clone()?:kotlin.ByteArray(0)

        if(res.isNotEmpty()) {
            for (i in 0..JPEG_CORRUPTION_COUNT - 1) {
                val idx = RANDOM.nextInt(res.size )
                res[idx] = (res[idx] + RANDOM.nextInt(3)).toByte()
            }

            result = GlitcherUtil.bitmapFromByteArray(res)
        }

        return this
    }

    fun negative() : Glitcher{


        val h = result!!.height
        val w = result!!.width

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

        result = bitmap

        return this

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

    fun shuffle() :Glitcher{
        return generateBitmap { shuffleRow(it)}
    }


    fun pixelSort () : Glitcher{
        return generateBitmap{sortRow(it)}
    }

    fun anaglyph (percentage:Int = 20) : Glitcher{

        val leftArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
        val rightArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)


        val transX = ((result!!.width/2) * percentage).div(100)
        val transY = 15

        val anaglyphShader = BitmapShader(result, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val anaglyphPaint = Paint()
        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader

        val colorMatrix = ColorMatrix()

        val bitmap = Bitmap.createBitmap(result!!.width,result!!.height,Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(0, PorterDuff.Mode.CLEAR)

        //left
        val matrix = Matrix()
        matrix.setTranslate((-transX).toFloat(), (-transY).toFloat())
        anaglyphShader.setLocalMatrix(matrix)
        colorMatrix.set(leftArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, result!!.width.toFloat(), result!!.height.toFloat(), anaglyphPaint)

        //right
        val matrix2 = Matrix()
        matrix2.setTranslate((transX).toFloat(), transY.toFloat())
        anaglyphShader.setLocalMatrix(matrix2)
        colorMatrix.set(rightArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, result!!.width.toFloat(), result!!.height.toFloat(), anaglyphPaint)


        c.drawBitmap(result,0f,0f,anaglyphPaint)

        result = bitmap


        return this

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

    private fun generateBitmap ( action: (List<Int>) -> List<Int>) : Glitcher{
        val r = List(result!!.width,{ row -> List(result!!.height,{ col -> result!!.getPixel(row,col)})})

        val  rShuffle = List(result?.height?:0,{row -> action(r[row])})

        result = Bitmap.createBitmap(result!!.width,result!!.height, Bitmap.Config.ARGB_8888)


        for(i in 0..(result!!.height)-1){
            for (j in 0..(result!!.width)-1){
                result!!.setPixel(j,i,rShuffle[i][j])
            }
        }

        return this
    }



}




