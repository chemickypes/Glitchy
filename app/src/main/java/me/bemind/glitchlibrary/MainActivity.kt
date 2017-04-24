package me.bemind.glitchlibrary

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.SeekBar
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import me.bemind.glitch.Effect
import me.bemind.glitchappcore.glitch.ExtendedImageView
import me.bemind.glitchappcore.*
import me.bemind.glitchappcore.io.IIOPresenter
import me.bemind.glitchappcore.io.IOPresenter
import android.content.Intent
import android.util.Log
import me.bemind.glitchappcore.app.*


class MainActivity : GlitchyBaseActivity(),IAppView, PickPhotoBottomSheet.OnPickPhotoListener,
SaveImageBottomSheet.OnSaveImageListener{



    private var mImageView : ExtendedImageView? = null
    private var anaglyphButton: Button? = null
    private var glitchButton: Button? = null
    private var webpButton: Button? = null
    private var effectPanel: ViewGroup? = null


    private var toolbar : Toolbar? = null
    private var toolbarEffect : Toolbar? = null


    private var appPresenter : IAppPresenter = AppPresenter()
    private var ioPresenter: IIOPresenter = IOPresenter()

    private val pickPhotoBS = PickPhotoBottomSheet.Creator.getPickPhotoBottomSheet(this,this)

    private val saveImageBS = SaveImageBottomSheet.Creator.getSaveImageBottomSheet(this,this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbarEffect = findViewById(R.id.toolbar_effect) as Toolbar
        toolbarEffect?.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbarEffect?.setNavigationOnClickListener {
            if(appPresenter.modState == State.EFFECT){
                appPresenter.modState = State.BASE
                mImageView?.clearEffect()
            }
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

        mImageView = findViewById(R.id.imageView) as ExtendedImageView
        mImageView?.setOnClickListener {

            if(!(mImageView?.hasHistory?:false)){
                pickPhotoBS.show()
            }

        }

        mImageView?.restoreSavedInstanceState(this,savedInstanceState)

        appPresenter.restoreInstanceState(this,savedInstanceState)

        effectPanel = findViewById(R.id.effect_panel) as ViewGroup

        anaglyphButton = findViewById(R.id.anaglyph_button) as Button
        anaglyphButton?.setOnClickListener {

            if(mImageView?.getImageBitmap()!=null) {
                makeAnaglyphEffect(true)
            }
        }

        glitchButton = findViewById(R.id.glitch_button) as Button
        glitchButton ?.setOnClickListener {
            makeGlitchEffect(true)
        }

        webpButton = findViewById(R.id.webp_button) as Button
        webpButton?.setOnClickListener {
            makeWebpEffect(true)
        }



    }



    private fun applyEffect() {
        appPresenter.modState = State.BASE
        //imagePresenter.saveEffect()
        mImageView?.save()
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

        if(mImageView?.hasHistory?:false) {
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
                //imagePresenter.saveImage()
                //open bottom sheet
//                ioPresenter.saveImage(mImageView?.getImageBitmap())
                saveImageBS.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        pickPhotoBS.dismiss()
        saveImageBS.dismiss()
    }

    override fun onStart() {
        super.onStart()
        appPresenter.appView = this
        ioPresenter.ioView = this
    }

    override fun onStop() {
        super.onStop()
        appPresenter.appView = null
        ioPresenter.ioView = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        mImageView?.saveInstanceState(this,outState)

        appPresenter.saveInstanceState(this,outState)

    }

    /*fun setImagebitmap(bitmap: Bitmap) {
        runOnUiThread {
            mImageView!!.setImageBitmap(bitmap,true,true)
            *//*mImageView?.layoutParams?.width = bitmap.width
            mImageView?.layoutParams?.height = bitmap.height
            mImageView?.requestLayout()*//*
        }
    }*/

    override fun getIOPresenter(): IIOPresenter  = ioPresenter

    override fun setImage(bitmap: Bitmap) {
        runOnUiThread {
            mImageView!!.setImageBitmap(bitmap,true,true)
        }
    }

    override fun showErrorGetImage(t: Throwable) {
        Toast.makeText(this,R.string.get_image_error,Toast.LENGTH_SHORT).show()
    }

    override fun showSuccessSaveImage(fileName: String) {
        Toast.makeText(this,getString(R.string.image_saved,fileName),Toast.LENGTH_SHORT).show()
    }

    override fun showErrorSaveImage(t: Throwable) {
       Toast.makeText(this,R.string.image_saving_error,Toast.LENGTH_SHORT).show()
        t.printStackTrace()
    }

    override fun showErrorImageShare(t: Throwable) {
        Toast.makeText(this,R.string.error_share,Toast.LENGTH_SHORT).show()
        t.printStackTrace()
    }

    override fun canShareImage(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND)
        share.setType("image/jpg");
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, getString(R.string.share)))
    }

    override fun onResume() {
        super.onResume()
        appPresenter.onResume()

    }

    override fun onPostResume() {
        super.onPostResume()
       // mImageView?.onResume()
    }

    override fun updateState(state: State) {
        when (state){
            State.BASE -> closeCurrentEffect()
            State.EFFECT -> openEffectPanel()
            else -> closeCurrentEffect()
        }
    }

    override fun openCamera() {
        //imagePresenter.openImageFromCamera(this,mImageView!!.width,mImageView!!.height)
        ioPresenter.openImage(this,IIOPresenter.TypePick.CAMERA)
    }

    override fun openGallery() {
        //imagePresenter.openImageFromGallery(this,mImageView!!.width,mImageView!!.height)
        ioPresenter.openImage(this,IIOPresenter.TypePick.GALLERY)
    }

    override fun saveImage() {
        ioPresenter.saveImage(mImageView?.getImageBitmap())
        saveImageBS.dismiss()
    }

    override fun shareImage() {
        ioPresenter.shareImage(mImageView?.getImageBitmap())
        saveImageBS.dismiss()
    }

    override fun restoreView(effectState: EffectState?) {
        /*updateState(appPresenter.modState)
        if(appPresenter.effectLayout!=0)inflateEffectLayout(appPresenter.effectLayout)*/
        if(effectState!=null){
            inflateEffectLayout(effectState)
        }
    }

    override fun onBackPressed() {

        if(appPresenter.onBackPressed()){
            mImageView?.clearEffect()
        }else {

            if (!(mImageView?.back()?:true)) {

                val appname = getString(R.string.app_name)
                AlertDialog.Builder(this)
                        //.setTitle("Delete entry")
                        .setMessage(getString(R.string.want_close_app, appname))
                        .setPositiveButton(android.R.string.yes, { dialog, which ->
                            // continue with delete
                            super.onBackPressed()
                        })
                        .setNegativeButton(android.R.string.no, { dialog, which ->
                            // do nothing
                        })
                        .show()

            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        appPresenter.onConfigurationChanged(newConfig)
    }

    private fun makeAnaglyphEffect(init: Boolean, progress:Int = 20) {
        val effect = AnaglyphEffectState(R.layout.effect_anaglyph_layout,progress)
        if(init) {
            appPresenter.modState = State.EFFECT
            //inflate layout
            mImageView?.initEffect(Effect.ANAGLYPH)


            inflateEffectLayout(effect)
        }else {

            appPresenter.effectState = effect
            mImageView?.makeEffect(progress)
        // imagePresenter.glitchImage(Effect.ANAGLYPH, progress, init)
        }
    }

    private fun makeGlitchEffect(init: Boolean = false){
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.GLITCH)
            inflateEffectLayout(GlitchEffectState(R.layout.effect_glitch_layout))
        }

        //imagePresenter.glitchImage(Effect.GLITCH)
        mImageView?.makeEffect()

    }

    private fun makeWebpEffect(init: Boolean = false){
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.WEBP)
            inflateEffectLayout(WebpEffectState(R.layout.effect_glitch_layout))
        }

        //imagePresenter.glitchImage(Effect.GLITCH)
        mImageView?.makeEffect()
    }


    private fun inflateEffectLayout(effectState: EffectState){
        appPresenter.effectState = effectState
        val view = LayoutInflater.from(this).inflate(effectState.layout,null,false)
        when (effectState){
            is WebpEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button)
                b.setOnClickListener {
                    makeWebpEffect()
                }
            }
            is GlitchEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button)
                b.setOnClickListener {
                    makeGlitchEffect()
                }
            }
            is AnaglyphEffectState -> {
               val seekbar = view.findViewById(R.id.seekbar) as SeekBar?
                seekbar?.progress = effectState.progress

                seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(arg0: SeekBar, arg1: Int, arg2: Boolean) {
                        if(arg2) makeAnaglyphEffect(false,arg1)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                })
            }
            else -> /*nothing*/ Log.i("Glitchy","base layout")
        }


        effectPanel?.addView(view)
    }


}
