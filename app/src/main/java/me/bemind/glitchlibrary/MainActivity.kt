package me.bemind.glitchlibrary

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
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
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.shamanland.fonticon.FontIconDrawable
import com.shamanland.fonticon.FontIconTypefaceHolder
import io.fabric.sdk.android.Fabric
import me.bemind.glitchappcore.app.*
import me.bemind.sidemenu.SideMenu
import me.bemind.sidemenu.SideMenuToggle
import net.idik.lib.slimadapter.SlimAdapter
import org.jraf.android.alibglitch.GlitchEffect
import java.util.*


class MainActivity : GlitchyBaseActivity(),IAppView, PickPhotoBottomSheet.OnPickPhotoListener,
SaveImageBottomSheet.OnSaveImageListener{


    private var mImageView : ExtendedImageView? = null

    private var effectPanel: ViewGroup? = null

    private val effectList by lazy<RecyclerView> {
        (findViewById(R.id.effect_list) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private val loaderView by lazy {
        findViewById(R.id.loaderView)
    }

    private var toolbar : Toolbar? = null

    private var toolbarEffect : Toolbar? = null

    private var appPresenter : IAppPresenter = AppPresenter()

    private var ioPresenter: IIOPresenter = IOPresenter()

    private val pickPhotoBS = PickPhotoBottomSheet.Creator.getPickPhotoBottomSheet(this,this)

    private val saveImageBS = SaveImageBottomSheet.Creator.getSaveImageBottomSheet(this,this)

    private var optionMenu: Menu? = null

    val sidemenu : SideMenu by lazy {
        findViewById(R.id.side_menu) as SideMenu
    }

    val sideMenuToggle : SideMenuToggle by lazy {
        SideMenuToggle(this,sidemenu,toolbar,R.string.open,R.string.close)
    }

    private val effectAdapter by lazy {
        SlimAdapter.create()
                .register<EffectView>(R.layout.effect_view_row) {
                    data,injector ->
                    injector.text(R.id.effect_name,data.name)
                            .clicked(R.id.effect_view_id){
                                initEffect(data.effect)
                            }
                            .text(R.id.badge_textView,
                            when(data.effectNew){
                                EFFECT_NEWS.PRO -> getString(R.string.pro_)
                                EFFECT_NEWS.NEW -> getString(R.string.new_)
                                else -> getString(R.string.new_)
                            })
                            .textColor(R.id.badge_textView,
                                    when(data.effectNew){
                                        EFFECT_NEWS.PRO -> ContextCompat.getColor(this,R.color.turquoise)
                                        EFFECT_NEWS.NEW -> ContextCompat.getColor(this,R.color.amaranth)
                                        else -> ContextCompat.getColor(this,R.color.amaranth)
                                    })
                    when(data.effectNew){
                        EFFECT_NEWS.NONE -> injector.gone(R.id.badge_textView)
                        else -> {}//nothing
                    }
                }
                .attachTo(effectList)
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        FontIconTypefaceHolder.init(assets, "material_icons.ttf")
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbarEffect = findViewById(R.id.toolbar_effect) as Toolbar
        toolbarEffect?.navigationIcon = FontIconDrawable.inflate(this,R.xml.ic_close)
        toolbarEffect?.setNavigationOnClickListener {
            if(appPresenter.modState == State.EFFECT){
                appPresenter.modState = State.BASE
                mImageView?.clearEffect()
            }
        }

        toolbarEffect?.inflateMenu(R.menu.ok_menu)
        toolbarEffect?.menu?.getItem(0)?.icon = FontIconDrawable.inflate(this,R.xml.ic_done)
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

        val spS = SpannableString(toolbar?.title?.toString()/*?.toUpperCase(Locale.ITALIAN)*/)
        spS.setSpan(GlitcyTypefaceSpan("",
                GlitchyTypeFaceGetter.getTypeFace(this,TYPEFONT.MONO)),0,spS.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        supportActionBar?.title = spS

        sidemenu.sideMenuToggle = sideMenuToggle
        sidemenu.post { sideMenuToggle.syncState() }

        mImageView = findViewById(R.id.imageView) as ExtendedImageView
        mImageView?.setOnClickListener {

            if(!(mImageView?.hasHistory?:false)){
                pickPhotoBS.show()
            }

        }
        mImageView?.loaderView = loaderView

        mImageView?.restoreSavedInstanceState(this,savedInstanceState)

        appPresenter.restoreInstanceState(this,savedInstanceState)

        effectPanel = findViewById(R.id.effect_panel) as ViewGroup

        findViewById(R.id.void_text_view).setOnClickListener {
            pickPhotoBS.show()
        }

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            effectList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
            }
        }else{
            effectList.apply {
                layoutManager = GridLayoutManager(this@MainActivity,3)
            }
        }
        effectAdapter.updateData(EffectList.data).notifyDataSetChanged()



    }

    private fun applyEffect() {
        appPresenter.modState = State.BASE
        //imagePresenter.saveEffect()
        mImageView?.save()
    }

    private fun closeCurrentEffect() {


        sidemenu.apply {
            openable = true
        }

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

        sidemenu.apply {
            openable = false
        }

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
        this.optionMenu = menu
        inflateActivityMenu()
        //applyFont(menu)
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

    override fun getIOPresenter(): IIOPresenter  = ioPresenter

    override fun setImage(bitmap: Bitmap) {
        runOnUiThread {
            openListEffectPanel()
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

        val clipboard =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText(getString(R.string.app_name),getString(R.string.hashtag_text_to_share))
        Toast.makeText(this,R.string.text_copied_on_the_clipboard,Toast.LENGTH_LONG).show()

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

    override fun restoreView(effectState: EffectState?,emptyImageView:Boolean) {
        /*updateState(appPresenter.modState)
        if(appPresenter.effectLayout!=0)inflateEffectLayout(appPresenter.effectLayout)*/
        if(effectState!=null){
            inflateEffectLayout(effectState)
        }

        if(!emptyImageView){
            showListEffect()
        }

    }

    override fun onBackPressed() {

        if(sidemenu.isOpen){
            sidemenu.closePane()
        }else if(appPresenter.onBackPressed()){
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

    private fun openListEffectPanel() {
        appPresenter.emptyImageView = false

        inflateActivityMenu()

        showListEffect()

        GlitchEffect.showGlitch(this)

        //listEffectPanel.visibility = VISIBLE
    }

    private fun showListEffect() {
        /*animateAlpha(listEffectPanel, Runnable {
            listEffectPanel.alpha = 0f
            listEffectPanel.visibility = VISIBLE
        }, 10050, true, 1f)*/

        effectList.animate()
                .alpha(1f)
                .setDuration(1000)
                //.setInterpolator(AccelerateDecelerateInterpolator())
                .withStartAction {
                    effectList.alpha = 0f
                    effectList.visibility = VISIBLE
                }
                .start()

    }

    private fun inflateActivityMenu(){

        if(appPresenter.emptyImageView) {
            menuInflater.inflate(R.menu.main_menu, optionMenu)
            optionMenu?.getItem(0)?.icon = FontIconDrawable.inflate(this, R.xml.open_menu)
        }else {
            optionMenu?.clear()
            menuInflater.inflate(R.menu.main_menu_ex, optionMenu)
            optionMenu?.getItem(0)?.icon = FontIconDrawable.inflate(this, R.xml.open_menu)
            optionMenu?.getItem(1)?.icon = FontIconDrawable.inflate(this, R.xml.ic_save)

        }
    }

    private fun initEffect(effect:Effect) {

        if(mImageView?.canInitEffect?:false) {
            when (effect) {
                Effect.ANAGLYPH -> makeAnaglyphEffect(true)
                Effect.GHOST -> makeGhostEffect(true)
                Effect.WOBBLE -> makeWobbleEffect(true)
                Effect.GLITCH -> makeGlitchEffect(true)
                Effect.WEBP -> makeWebpEffect(true)
                Effect.SWAP -> makeSwapEffect(true)
                Effect.NOISE -> makeNoiseEffect(true)
                Effect.HOOLOOVOO -> makeHooloovooEffect(true)
                else -> {
                }
            }
        }

    }

    private fun makeHooloovooEffect(init: Boolean = false) {
        val effect =HooloovooEffectState(R.layout.effect_hooloovoo_layout);
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.HOOLOOVOO)
            inflateEffectLayout(effect)
        }else{
            appPresenter.effectState = effect
            mImageView?.makeEffect(0)
        }
    }

    private fun makeGhostEffect(init: Boolean = false) {
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.GHOST)
            inflateEffectLayout(GhostEffectState(R.layout.effect_ghost_layout))
        }
    }

    private fun makeWobbleEffect(init: Boolean = false) {
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.WOBBLE)
            inflateEffectLayout(WobbleEffectState(R.layout.effect_ghost_layout))
        }
    }

    private fun makeNoiseEffect(init: Boolean = false,progress: Int = 70) {
        val effect = NoiseEffectState(R.layout.effect_anaglyph_layout,progress)
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.NOISE)
            inflateEffectLayout(effect)
        }else{

            appPresenter.effectState = effect
            mImageView?.makeEffect(progress+50)
        }

        //imagePresenter.glitchImage(Effect.GLITCH)

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

    private fun makeSwapEffect(init: Boolean = false){
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.SWAP)
            inflateEffectLayout(SwapEffectState(R.layout.effect_glitch_layout))
        }

        //imagePresenter.glitchImage(Effect.GLITCH)
        mImageView?.makeEffect()
    }

    private fun inflateEffectLayout(effectState: EffectState){
        appPresenter.effectState = effectState
        val view = LayoutInflater.from(this).inflate(effectState.layout,null,false)
        when (effectState){
            is NoiseEffectState ->{
                /*val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.tap_here_to_create_noise)
                b.setOnClickListener {
                    makeNoiseEffect()
                }*/

                val seekbar = view.findViewById(R.id.seekbar) as SeekBar?
                seekbar?.progress = effectState.progress

                seekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(arg0: SeekBar, arg1: Int, arg2: Boolean) {
                        if(arg2) makeNoiseEffect(false,arg1)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {

                    }
                })
            }
            is WebpEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.tap_here_to_glitch_webp)
                b.setOnClickListener {
                    makeWebpEffect()
                }
            }
            is SwapEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.tap_here_to_swap)
                b.setOnClickListener {
                    makeSwapEffect()
                }
            }
            is GlitchEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button)
                b.setOnClickListener {
                    makeGlitchEffect()
                }
            }
            is GhostEffectState -> {
                //nothing
            }
            is WobbleEffectState -> {
                //nothing
            }
            is HooloovooEffectState ->{
                val b = view.findViewById(R.id.text_effect) as TextView
                b.setOnClickListener {
                    makeHooloovooEffect()
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

    private fun applyFont(menu: Menu?) {
        for(i in 0 until menu?.size()!!){
            val item = menu.getItem(i)
            val tv = LayoutInflater.from(this).inflate(R.layout.menu_text_item,toolbar,false) as TextView
            tv.text = item.title
            item.actionView = tv

            tv.setOnClickListener {
                val onOptionsItemSelected = onOptionsItemSelected(item)
            }

        }
    }


}
