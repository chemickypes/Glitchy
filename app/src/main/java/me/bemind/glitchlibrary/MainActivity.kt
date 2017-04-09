package me.bemind.glitchlibrary

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import me.bemind.glitch.Glitcher
import me.bemind.glitchappcore.IImageView
import me.bemind.glitchappcore.ImagePresenter
import android.view.MenuItem


class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, IImageView, PickPhotoBottomSheet.OnPickPhotoListener {


    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        //glitcher.restore()
       /* Observable.fromCallable { glitcher.anaglyph(p1).result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{bitmap:Bitmap? -> mImageView?.setImageBitmap(bitmap)}*/
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mImageView :ImageView? = null
    private var glitchButton: Button? = null
    private var seekbar: SeekBar? = null

    private var toolbar : Toolbar? = null

    private val imagePresenter = ImagePresenter(this)

    private val pickPhotoBS = PickPhotoBottomSheet.Creator.getPickPhotoBottomSheet(this,this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)

        mImageView = findViewById(R.id.imageView) as ImageView
        mImageView?.setOnClickListener { imagePresenter.glitchImage() }

        glitchButton = findViewById(R.id.glitch_button) as Button
        glitchButton?.setOnClickListener { glitchImage() }

        seekbar = findViewById(R.id.seekBar1) as SeekBar

        seekbar!!.setOnSeekBarChangeListener(this )

        imagePresenter.restoreInstanceState(savedInstanceState)




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.open_action -> {
                //open action
                pickPhotoBS.show()
            }

            R.id.save_action -> {
                imagePresenter.saveImage()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        pickPhotoBS.dismiss()
    }

    override fun onStart() {
        super.onStart()
        imagePresenter.subscribe(this)
    }

    override fun onStop() {
        super.onStop()
        imagePresenter.unsubscribe()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        imagePresenter.saveInstanceState(outState)
    }

    override fun setImagebitmap(bitmap: Bitmap) {
        mImageView!!.setImageBitmap(bitmap)
    }

    override fun showGetImageError(t: Throwable) {
    }

    override fun showSaveLoader() {
    }

    override fun onSavedImage() {
    }

    override fun onSaveImageError(t: Throwable) {
    }

    override fun openCamera() {
        imagePresenter.openImageFromCamera(mImageView!!.width,mImageView!!.height)
    }

    override fun openGallery() {
        imagePresenter.openImageFromGallery(mImageView!!.width,mImageView!!.height)
    }

    lateinit var glitcher : Glitcher

    private fun glitchImage() {
       /* val bitmap = mImageView?.drawable as BitmapDrawable
        glitcher = Glitcher.Creator.getGlitcher(bitmap.bitmap)

        glitcher.restore()
        //val co = glitcher.corruptBitmap()

        Observable.fromCallable { glitcher.negative().result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{bitmap:Bitmap? -> mImageView?.setImageBitmap(bitmap)}*/


        imagePresenter.openImageFromCamera(mImageView!!.width,mImageView!!.height)


    }

}
