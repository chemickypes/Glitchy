package me.bemind.glitch

import android.graphics.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Created by angelomoroni on 27/03/17.
 */


object GlitcherUtil {
    fun byteArrayFromBitmap(bitmap: Bitmap?,
                            compression:Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): ByteArray?{
        val byteOutputStream = ByteArrayOutputStream()
        bitmap?.compress(compression,100,byteOutputStream)
        return byteOutputStream.toByteArray()
    }


    fun bitmapFromByteArray(bytes:ByteArray?): Bitmap? = BitmapFactory.decodeByteArray(bytes,0,bytes?.size?:0)
}


object Glitcher {




    private var baseArray: ByteArray = kotlin.ByteArray(0)

    val leftArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val rightArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)

    var anaglyphShader : BitmapShader? = null
    var result : Bitmap? = null
    var baseBitmap : Bitmap? = null
    var anaglyphPaint = Paint()

    val RANDOM = Random()

    var w = 0
    var h = 0

    val MAX_VALUE = 10



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

        setBitmap(result)

        val JPEG_CORRUPTION_COUNT = 35
        //val JPEG_HEADER_SIZE = 100

        val res = baseArray.copyOf()

        if(res.isNotEmpty()) {
            for (i in 0..JPEG_CORRUPTION_COUNT - 1) {
                val idx = RANDOM.nextInt(res.size )
                res[idx] = (res[idx] + RANDOM.nextInt(3)).toByte()
            }

            return GlitcherUtil.bitmapFromByteArray(res)
        }

        return null
    }

    private fun setBitmap(result: Bitmap?, compression: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
        if(baseBitmap == null) baseBitmap = result
        if(baseArray.isEmpty()) baseArray =  GlitcherUtil.byteArrayFromBitmap(result,compression)?.clone()?:kotlin.ByteArray(0)
    }

    fun webp(result: Bitmap?): Bitmap? {
        setBitmap(result,Bitmap.CompressFormat.WEBP)


        val res = baseArray.copyOf()

        val perc = (RANDOM.nextFloat()%2)
        for (f in 0..165) {

        }

        if (res.size > 100) {
            val power = (res.size * perc).toInt()
            val rnd = RANDOM.nextInt(255)

            val ii = if (perc < 1f) 1 else 0
            val i = if (power <= 100) 0 else 1

            if ((i and ii) != 0) {
                Arrays.fill(res, power, power + 1, rnd.toByte())
            }
        }

        return GlitcherUtil.bitmapFromByteArray(res)
    }

    fun swap(result: Bitmap?) :Bitmap? {

        setBitmap(result)

        val x = RANDOM.nextInt(w)
        //val y = RANDOM.nextInt(h)

        val a = MAX_VALUE.toFloat().div((w * x).toFloat())
        //val b = MAX_VALUE.toFloat().div((h * (h-y)).toFloat())

        val res = baseArray.copyOf()
        val blockSize = res.size * 10 / 100
        val header = if(res.size <1000) 100 else 417

        val c = a.div(2)

        for(i in 0..c.toInt()){
            val rnd1 = RANDOM.nextInt((res.size ) - blockSize)
            val rnd2 = RANDOM.nextInt((res.size ) - blockSize)

            for( j in 0..blockSize){
                val tmp = res[(header+rnd1)+j]
                res[(header+rnd1)+j] = res[(header+rnd2)+j]
                res[(header+rnd2)+j] = tmp
            }
        }
        return GlitcherUtil.bitmapFromByteArray(res)
    }

    fun noise(result: Bitmap?):Bitmap? {

        val arrayLen = (w * h)
        val stride =  w

        val intArray = IntArray(arrayLen)
        result!!.getPixels(intArray,0,stride,0,0,w,h)

        val intArrayM = IntArray(intArray.size,{ i -> intArray[i].or(Color.rgb(
                RANDOM.nextInt(255),
                RANDOM.nextInt(255),
                RANDOM.nextInt(255)
        ))})

        val vv = result!!.copy(result!!.config,true)
        vv!!.setPixels(intArrayM,0,stride,0,0,w,h)

        return vv
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

    fun initAnaglyph ( result: Bitmap?){
        this.result = result
        anaglyphShader = BitmapShader(result, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader

    }

    fun anaglyph (percentage:Int = 20) : Bitmap?{

        val w = result?.width?:0
        val h = result?.height?:0

        //val transX = (w * percentage).div(200)
        val transX = ( percentage)
        val transY = 0



        val colorMatrix = ColorMatrix()

        val bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(0, PorterDuff.Mode.CLEAR)

        //left
        val matrix = Matrix()
        matrix.setTranslate((-transX).toFloat(), (transY).toFloat())
        anaglyphShader?.setLocalMatrix(matrix)
        colorMatrix.set(leftArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)

        //right
        val matrix2 = Matrix()
        matrix2.setTranslate((transX).toFloat(), transY.toFloat())
        anaglyphShader?.setLocalMatrix(matrix2)
        colorMatrix.set(rightArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)


        c.drawBitmap(result,0f,0f,anaglyphPaint)

        return bitmap


    }

    fun anaglyphCanvas(c:Canvas?,process: Int = 20){

        val colorMatrix = ColorMatrix()
        val transX = ( process)
        val transY = 0
        c?.drawColor(0, PorterDuff.Mode.CLEAR)

        //left
        val matrix = Matrix()
        matrix.setTranslate((-transX).toFloat(), (transY).toFloat())
        anaglyphShader?.setLocalMatrix(matrix)
        colorMatrix.set(leftArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c?.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)

        //right
        val matrix2 = Matrix()
        matrix2.setTranslate((transX).toFloat(), transY.toFloat())
        anaglyphShader?.setLocalMatrix(matrix2)
        colorMatrix.set(rightArray)
        anaglyphPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        c?.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(), anaglyphPaint)




        //c?.drawBitmap(result,0f,0f,anaglyphPaint)
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

    fun initEffect(effect: Effect,bitmap: Bitmap?,w: Int = -1,h: Int = -1) {

        result = bitmap
        baseBitmap = result
        baseArray = kotlin.ByteArray(0)


        val we = if((w == -1) || (w > bitmap?.width?: Int.MAX_VALUE)){
            bitmap?.width?:0
        }else{
            w
        }

        val he = if((h == -1) || (h > bitmap?.height?: Int.MAX_VALUE)){
            bitmap?.height?:0
        }else{
            h
        }

        initEffect(we,he)

    }

    fun initEffect(w:Int, h:Int){
        Glitcher.w = w
        Glitcher.h = h


        anaglyphShader = BitmapShader(result, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        anaglyphPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
        anaglyphPaint.shader = anaglyphShader
    }

    fun ghost(canvas: Canvas?, x: Int, y: Int, motion: Motion) {

    }


}




