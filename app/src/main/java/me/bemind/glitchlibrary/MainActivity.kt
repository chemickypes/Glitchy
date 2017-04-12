package me.bemind.glitchlibrary

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import me.bemind.glitchappcore.*


class MainActivity : GlitchyBaseActivity(), SeekBar.OnSeekBarChangeListener, IImageView, PickPhotoBottomSheet.OnPickPhotoListener {



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
    private var anaglyphButton: Button? = null
    private var glitchButton: Button? = null
    private var seekbar: SeekBar? = null
    private var effectPanel: ViewGroup? = null

    private var toolbar : Toolbar? = null
    private var toolbarEffect : Toolbar? = null

    val imagePresenter = ImagePresenter(this)

    private val pickPhotoBS = PickPhotoBottomSheet.Creator.getPickPhotoBottomSheet(this,this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbarEffect = findViewById(R.id.toolbar_effect) as Toolbar
        toolbarEffect?.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbarEffect?.setNavigationOnClickListener {
            imagePresenter.modState = State.BASE
            imagePresenter.onBackPressed()
        }

        toolbarEffect?.inflateMenu(R.menu.ok_menu)
        toolbarEffect?.setOnMenuItemClickListener {  item ->
            when (item.itemId){
                R.id.ok_action -> {
                    applyEffect()
                    true
                }
                else -> true //do nothing
            }
        }

        setSupportActionBar(toolbar)

        mImageView = findViewById(R.id.imageView) as ImageView
        mImageView?.setOnClickListener {

            if(!imagePresenter.getIImageLogic().hasHistory()){
                pickPhotoBS.show()
            }

            /*if (imagePresenter.modState == State.BASE) {
                if (imagePresenter.getIImageLogic().hasHistory()) {
                    imagePresenter.glitchImage(Effect.GLITCH)
                } else {
                    pickPhotoBS.show()
                }
            }*/
        }

        imagePresenter.restoreInstanceState(this,savedInstanceState)

        effectPanel = findViewById(R.id.effect_panel) as ViewGroup

        anaglyphButton = findViewById(R.id.anaglyph_button) as Button
        anaglyphButton?.setOnClickListener {
            makeAnaglyphEffect(true)
        }

        glitchButton = findViewById(R.id.glitch_button) as Button
        glitchButton ?.setOnClickListener {
            makeGlitchEffect(true)
        }

    }



    private fun applyEffect() {
        imagePresenter.modState = State.BASE
        imagePresenter.saveEffect()
    }

    private fun closeCurrentEffect() {
        val runnable : Runnable = Runnable {
            effectPanel?.visibility = GONE
            effectPanel?.alpha = 1f
            effectPanel?.removeAllViews()
        }

        if(effectPanel?.visibility== VISIBLE)animateAlpha(effectPanel, runnable, 350, false, 0f)

        val runnable2 :Runnable = Runnable {
            toolbarEffect?.visibility = GONE
            toolbarEffect?.alpha = 1f
        }
        if(toolbarEffect?.visibility == VISIBLE)animateAlpha(toolbarEffect,runnable2, 350, false, 0f)

    }

    private fun openEffectPanel() {

        val runnable : Runnable = Runnable {
            effectPanel?.alpha = 0f
            effectPanel?.visibility = VISIBLE
        }

        val runnable2 :Runnable = Runnable {
            toolbarEffect?.alpha = 0f
            toolbarEffect?.visibility = VISIBLE
        }

        if(imagePresenter.getIImageLogic().hasHistory()) {
            if(effectPanel?.visibility == GONE) animateAlpha(effectPanel,runnable, 450, true, 1f)
            if(toolbarEffect?.visibility == GONE)animateAlpha(toolbarEffect,runnable2, 450, true, 1f)
        }

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

        imagePresenter.saveInstanceState(this,outState)
    }

    override fun setImagebitmap(bitmap: Bitmap) {
        runOnUiThread {
            mImageView!!.setImageBitmap(bitmap)
        }

        //mImageView?.post {  }
//        mImageView!!.setImageBitmap(bitmap)


    }

    override fun getImagePresenter(): IImagePresenter {
        return imagePresenter
    }

    override fun showGetImageError(t: Throwable) {
    }

    override fun showSaveLoader() {
    }

    override fun onSavedImage() {
    }

    override fun onSaveImageError(t: Throwable) {
    }

    override fun onResume() {
        super.onResume()
        updateState(imagePresenter.modState)
    }

    override fun updateState(state: State) {
        when (state){
            State.BASE -> closeCurrentEffect()
            State.EFFECT -> openEffectPanel()
            else -> closeCurrentEffect()
        }
    }

    override fun openCamera() {
        imagePresenter.openImageFromCamera(this,mImageView!!.width,mImageView!!.height)
    }

    override fun openGallery() {
        imagePresenter.openImageFromGallery(this,mImageView!!.width,mImageView!!.height)
    }

    override fun onBackPressed() {

        if(imagePresenter.onBackPressed()) {

            val appname = getString(R.string.app_name)
            AlertDialog.Builder(this)
                    //.setTitle("Delete entry")
                    .setMessage(getString(R.string.want_close_app,appname))
                    .setPositiveButton(android.R.string.yes, { dialog, which ->
                        // continue with delete
                        super.onBackPressed()
                    })
                    .setNegativeButton(android.R.string.no,  { dialog, which ->
                        // do nothing
                    })
                    .show()

        }
    }

    private fun makeAnaglyphEffect(init: Boolean,progress:Int = 20) {
        if(init) {
            imagePresenter.modState = State.EFFECT
            //inflate layout

            val view = LayoutInflater.from(this).inflate(R.layout.effect_anaglyph_layout,null,false)

            effectPanel?.addView(view)

            val seekbar = view.findViewById(R.id.seekbar) as SeekBar

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(arg0: SeekBar, arg1: Int, arg2: Boolean) {
                    if(arg2) makeAnaglyphEffect(false,arg1)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })
        }

        imagePresenter.glitchImage(Effect.ANAGLYPH,progress)
    }

    private fun makeGlitchEffect(init: Boolean = false){
        if(init){
            imagePresenter.modState = State.EFFECT
            //inflate layout

            val view = LayoutInflater.from(this).inflate(R.layout.effect_glitch_layout,null,false)

            effectPanel?.addView(view)

            val b = view.findViewById(R.id.tap_to_glitch_button)
            b.setOnClickListener {
                makeGlitchEffect()
            }
        }

        imagePresenter.glitchImage(Effect.GLITCH)
    }



}
