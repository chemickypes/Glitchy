package me.bemind.glitch

import android.graphics.*
import android.util.FloatMath
import android.util.Log
import java.io.ByteArrayOutputStream
import java.util.*
import android.R.attr.bitmap



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
    val redMatrix = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    val blueMatrix = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)
    val greenMatrix = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f)


    var anaglyphShader : BitmapShader? = null
    var result : Bitmap? = null
    var baseBitmap : Bitmap? = null

    var anaglyphPaint = Paint()
    private val redPaint = Paint()
    private val greenPaint = Paint()
    private val bluePaint = Paint()
    val noisePaint = Paint()
    var noiseBMPshader : BitmapShader? = null

    private var headerOfImage: Int = 417

    private val XFE_ADD = PorterDuffXfermode(PorterDuff.Mode.ADD)
    private val XFE_SRC_IN = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    val RANDOM = Random()

    private var WWIDTH: Int = 10
    private var WHEIGHT: Int = 10

    private var SMCOUNT: Int = 0
    private var matrixVertsMoved = kotlin.FloatArray(0)
    private var matrixOriginal = kotlin.FloatArray(0)

    var w = 0
    var h = 0

    val MAX_VALUE = 10

    private val malpha: Int = 150



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
                val idx = headerOfImage + RANDOM.nextInt(res.size - headerOfImage -1)
                res[idx] = (res[idx] + RANDOM.nextInt(4)).toByte()
            }

            val b = GlitcherUtil.bitmapFromByteArray(res)
            return b
        }

        return null
    }



    private fun setBitmap(result: Bitmap?, compression: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG) {
        if(baseBitmap == null) baseBitmap = result
        if(baseArray.isEmpty()){
            baseArray =  GlitcherUtil.byteArrayFromBitmap(result,compression)?.clone()?:kotlin.ByteArray(0)
            this.headerOfImage = getJpegHeaderSize(baseArray)
        }
    }

    fun webp(result: Bitmap?): Bitmap? {
        setBitmap(result,Bitmap.CompressFormat.WEBP)


        val res = baseArray.copyOf()

        val perc = (RANDOM.nextFloat()%2)
        Log.d("WEBP","perc $perc")

        if (res.size > 100) {
            val power = (res.size * perc).toInt()
            val rnd = RANDOM.nextInt(255)

            val ii = if (perc < 1f) 1 else 0
            val i = if (power <= 100) 0 else 1

            if ((i and ii) != 0) {
                Arrays.fill(res, power, power + 1, rnd.toByte())
            }
        }

        val b =  GlitcherUtil.bitmapFromByteArray(res)
        return b
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

    fun noiseCanvas(c:Canvas?,progress:Int = 170 ){
        val rndInt = RANDOM.nextInt(w)
        val rndInt2 = RANDOM.nextInt(h)
        val matrix = Matrix()
        matrix.setTranslate(rndInt.toFloat(),rndInt2.toFloat())
        noiseBMPshader?.setLocalMatrix(matrix)
        noisePaint.alpha = progress
        c?.drawRect(0f,0f,w.toFloat(),h.toFloat(), noisePaint)
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
        anaglyphShader = BitmapShader(result, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)

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



    fun ghostCanvas(c: Canvas?, x: Int, y: Int, motion: Motion) {
        c?.drawColor(0,PorterDuff.Mode.CLEAR)
        c?.drawBitmap(baseBitmap,0f,0f, redPaint)
        /*draw bitmap */ c?.drawBitmapMesh(baseBitmap, WWIDTH, WHEIGHT,smudgeGhostRGB(x,y,2,malpha,motion),0,null,0, greenPaint)

        /* draw bitmap*/c?.drawBitmapMesh(baseBitmap, WWIDTH, WHEIGHT,smudgeGhostRGB(x,y,4,malpha,motion),0,null,0, bluePaint)

    }

    fun wobbleCanvas(c: Canvas?, x: Int, y: Int, motion: Motion) {
        c?.drawColor(0,PorterDuff.Mode.CLEAR)
        c?.drawBitmap(baseBitmap,0f,0f, redPaint)
        /*draw bitmap */ c?.drawBitmapMesh(baseBitmap, WWIDTH, WHEIGHT,smudgeWobbleRGB(x,y,4,malpha,motion),0,null,0, greenPaint)

        /* draw bitmap*/c?.drawBitmapMesh(baseBitmap, WWIDTH, WHEIGHT,smudgeWobbleRGB(x,y,6,malpha,motion),0,null,0, bluePaint)
    }

    private fun smudgeWobbleRGB(x: Int, y: Int, i: Int, malpha: Int, motion: Motion): FloatArray? {
        var fArr = FloatArray(0)
        synchronized(this){
            fArr = kotlin.FloatArray(SMCOUNT*2)
            for (i5 in 0..((SMCOUNT*2)-1)  step 2 ){
                //Log.d("DEBUG","$i $i2 $i3 $i4 $i5")

                val xOriginal = matrixOriginal[i5]
                val yOriginal = matrixOriginal[i5+1]


                val distX = ((x.toFloat() - xOriginal) / w.toFloat()) * 10.0f
                val distY = ((y.toFloat() - yOriginal) / h.toFloat()) *10.0f

                val dist = Math.sqrt((distX * distX + distY * distY).toDouble())

                val coof = (i - dist) / i


                val oc = (-Math.sin(coof * 2 * Math.PI)).toFloat() * 0.15f
                if ( dist < i )
                {
                    fArr[i5] = (xOriginal + 40 * (coof+oc)).toFloat()
                    fArr[i5+1] = yOriginal
                }
                else
                {
                    fArr[i5] = xOriginal
                    fArr[i5+1] = yOriginal
                }



            }

        }
        return fArr
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

    private fun smudgeGhostRGB(i:Int,i2:Int,i3:Int,i4:Int,motion:Motion) : FloatArray?{
        var fArr = FloatArray(0)
        synchronized(this){
            fArr = kotlin.FloatArray(SMCOUNT*2)
            for (i5 in 0..((SMCOUNT*2)-1)  step 2 ){
                //Log.d("DEBUG","$i $i2 $i3 $i4 $i5")

                val xOriginal = matrixOriginal[i5]
                val yOriginal = matrixOriginal[i5+1]


                val distX = ((i.toFloat() - xOriginal) / w.toFloat()) * 10.0f
                val distY = ((i2.toFloat() - yOriginal) / h.toFloat()) * 10.0f
                val d = ((i4.toFloat() / 255.0f).toDouble() * 3.6 )+ 0.4

                val gaussX = Math.exp((-(distX * distX)).toDouble() / d).toFloat() * 0.4f
                val gaussY = Math.exp((-(distY * distY)).toDouble() / d).toFloat() * 0.4f

                //Log.d("DEBUG","$xOriginal $yOriginal $distX $distY $d $gaussX $gaussY")

                when (motion) {
                    Motion.LEFT -> {
                        fArr[i5] = xOriginal - (((w - i).toFloat() * gaussY) / i3.toFloat())
                        fArr[i5 + 1] = yOriginal
                    }
                    Motion.RIGHT -> {
                        fArr[i5] = xOriginal + ((i.toFloat() * gaussY) / i3.toFloat())
                        fArr[i5 + 1] = yOriginal
                    }
                    Motion.UP -> {
                        fArr[i5] = xOriginal
                        fArr[i5 + 1] = yOriginal - (((h - i2).toFloat() * gaussX) / i3.toFloat())
                    }
                    Motion.DOWN -> {
                        fArr[i5] = xOriginal
                        fArr[i5 + 1] = yOriginal + ((i2.toFloat() * gaussX )/ i3.toFloat())
                    }
                    else -> {
                        fArr[i5] = xOriginal
                        fArr[i5 + 1] = yOriginal
                    }
                }

            }

        }
        return fArr
    }

    private fun generateBitmap (result: Bitmap?, action: (List<Int>) -> List<Int>) : Bitmap?{
        val r = List(result?.width?:0,{ row -> List(result?.height?:0,{ col -> result!!.getPixel(row,col)})})

        val  rShuffle = List(result?.width?:0,{row -> action(r[row])})

        val rr= Bitmap.createBitmap(result?.width?:0,result?.height?:0, Bitmap.Config.ARGB_8888)


        for(i in 0 until (rr?.height?:1)-1){
            for (j in 0 until (rr?.width?:1)-1){
                rr!!.setPixel(j,i,rShuffle[j][i])
            }
        }

        return rr
    }

    fun initEffect(effect: Effect,bitmap: Bitmap?, noiseBitmap:Bitmap? = null,w: Int = -1,h: Int = -1) {

        result = bitmap
        baseBitmap = result
        baseArray = kotlin.ByteArray(0)

        if(noiseBitmap!=null){
            noiseBMPshader = BitmapShader(noiseBitmap,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT)
            noisePaint.xfermode = XFE_ADD
            noisePaint.shader = noiseBMPshader
        }


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

        if(effect == Effect.GHOST || effect == Effect.WOBBLE){
            initGhost()
        }

    }



    private fun initGhost() {
        WWIDTH = 100
        WHEIGHT = 100
        InitSmudgeMatrix()
        setGhostColor()
    }



    private fun setGhostColor() {
        redPaint.isFilterBitmap = true
        redPaint.xfermode = XFE_ADD
        greenPaint.isFilterBitmap = true
        greenPaint.xfermode = XFE_ADD
        bluePaint.isFilterBitmap = true
        bluePaint.xfermode = XFE_ADD

        val colorMatrix = ColorMatrix()

        colorMatrix.set(redMatrix)
        redPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        colorMatrix.set(greenMatrix)
        greenPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        colorMatrix.set(blueMatrix)
        bluePaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

    }


    private fun InitSmudgeMatrix() {
        SMCOUNT = (WWIDTH+1)*(WHEIGHT+1)
        matrixVertsMoved = FloatArray(SMCOUNT*2)
        matrixOriginal = FloatArray(SMCOUNT*2)

        var i = 0
        for(i2 in 0..(WHEIGHT)){
            val f = ((h*i2).div(WHEIGHT)).toFloat()
            for(i3 in 0..(WWIDTH)){
                val f2 = ((w*i3).div(WWIDTH)).toFloat()
                setXY(matrixVertsMoved,i,f2,f)
                setXY(matrixOriginal,i,f2,f)
                i+=1
            }
        }
    }

    fun initEffect(w:Int, h:Int){
        Glitcher.w = w
        Glitcher.h = h


        anaglyphShader = BitmapShader(result, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        anaglyphPaint.xfermode = XFE_ADD
        anaglyphPaint.shader = anaglyphShader
    }

    private fun setXY(fArr: FloatArray, i: Int, f: Float, f2: Float) {
        fArr[i * 2] = f
        fArr[i * 2 + 1] = f2
    }

    fun hooloovooizeCanvas(canvas: Canvas?,progress: Int = 20) {
        //nothing

        val debossPaint = Paint()

        val colorMatrix = ColorMatrix()

        colorMatrix.setSaturation(1.25f)

        val m = colorMatrix.array
        val c :Float = ((progress.toFloat()/10)+1)/* * (if(RANDOM.nextBoolean()) 1 else -1)*///10 //14
        val bright :Float = ((progress.toFloat()*2/3)/10)+1 /*RANDOM.nextInt(10)+1*/ //10 //56

        Log.d("HOOLOOVOO","c: $c bright: $bright")
        colorMatrix.set(floatArrayOf(m[ 0] * c, m[ 1] * c, m[ 2] * c, m[ 3] * c, m[ 4] * c + bright,
                m[ 5] * c, m[ 6] * c, m[ 7] * c, m[ 8] * c, m[ 9] * c + bright,
                m[10] * c, m[11] * c, m[12] * c, m[13] * c, m[14] * c + bright,
                m[15]    , m[16]    , m[17]    , m[18]    , m[19]))

        debossPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        debossPaint.xfermode = XFE_ADD
        //canvas?.drawPaint(debossPaint)
        canvas?.drawBitmap(baseBitmap, Matrix(),debossPaint)

        //try




       /* colorMatrix.set(blueMatrix)

        debossPaint.xfermode = XFE_SRC_IN
        debossPaint.maskFilter = EmbossMaskFilter(floatArrayOf(44f, 22.5f, 3.5f),2.8f,25f,4f)
        //debossPaint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        canvas?.drawPaint(debossPaint)
        canvas?.drawBitmap(result,0f,0f,debossPaint)*/



        //canvas?.drawRect(0.0f, 0.0f, w.toFloat(), h.toFloat(),debossPaint)
        //canvas?.drawFilter
    }

    fun pixelCanvas(canvas: Canvas?,density: Int = 70){

        val paint : Paint = Paint()

        val cols : Double = if(density>1)density.toDouble() else 1.0
        val blockSize : Double = w/cols
        val rows : Double = Math.ceil(h/blockSize)
        val startX = 0
        val startY = 0

        for(row in 0 until rows.toInt()){
            for (col in 0 until cols.toInt()){
                val pixelCoordX : Double = blockSize + col + startX
                val pixelCoordY : Double = blockSize + row + startY

                val midY = pixelCoordY + blockSize / 2
                val midX = pixelCoordX + blockSize / 2

                if(midX >= w || midX < 0) continue
                if(midY >= h || midY < 0) continue

                paint.color = baseBitmap?.getPixel(midX.toInt(),midY.toInt())?:0
                canvas?.drawRect(pixelCoordX.toFloat(),pixelCoordY.toFloat(),
                        (pixelCoordX+blockSize).toFloat(),(pixelCoordY+blockSize).toFloat(),paint)
            }
        }

    }

    private fun getJpegHeaderSize(byteArrayIn: ByteArray): Int {
        val byteFF = java.lang.Byte.valueOf((-1).toByte())
        val byteDA = java.lang.Byte.valueOf((-38).toByte())
        var i = 0
        while (i < byteArrayIn.size) {
            if (byteArrayIn[i] == byteFF!!.toByte() && byteArrayIn[i + 1] == byteDA!!.toByte()) {
                return i + 2
            }
            i++
        }
        return 417
    }




}




