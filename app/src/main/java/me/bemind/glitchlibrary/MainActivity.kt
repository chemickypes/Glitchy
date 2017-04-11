package me.bemind.glitchlibrary

import android.animation.AnimatorListenerAdapter
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import me.bemind.glitchappcore.IImageView
import me.bemind.glitchappcore.ImagePresenter
import android.view.MenuItem
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AccelerateDecelerateInterpolator
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.IImagePresenter
import me.bemind.glitchappcore.State


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
    private var seekbar: SeekBar? = null
    private var effectPanel: View? = null

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
            if(imagePresenter.getIImageLogic().hasHistory()) {
                imagePresenter.glitchImage()
            }else{
                pickPhotoBS.show()
            }}

        imagePresenter.restoreInstanceState(savedInstanceState)

        effectPanel = findViewById(R.id.effect_panel)

        anaglyphButton = findViewById(R.id.anaglyph_button) as Button
        anaglyphButton?.setOnClickListener {
            imagePresenter.modState = State.EFFECT
        }

    }

    private fun applyEffect() {
        //TODO apply effect
        closeCurrentEffect()

    }

    private fun closeCurrentEffect() {
        if(effectPanel?.visibility== VISIBLE)animateAlpha(effectPanel,0f,350,false)
        if(toolbar?.visibility == VISIBLE)animateAlpha(toolbarEffect,0f,350,false)

    }

    private fun openEffectPanel() {

        if(imagePresenter.getIImageLogic().hasHistory()) {
            if(effectPanel?.visibility == GONE) animateAlpha(effectPanel, 1f, 450, true)
            if(toolbarEffect?.visibility == GONE)animateAlpha(toolbarEffect, 1f, 450, true)
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

        imagePresenter.saveInstanceState(outState)
    }

    override fun setImagebitmap(bitmap: Bitmap) {
        runOnUiThread {
            mImageView!!.setImageBitmap(bitmap)
        }

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



}
