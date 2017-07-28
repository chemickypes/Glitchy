package me.bemind.glitchlibrary

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import android.support.v4.content.ContextCompat
import android.support.v7.widget.*
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.shamanland.fonticon.FontIconDrawable
import com.shamanland.fonticon.FontIconTypefaceHolder
import com.shamanland.fonticon.FontIconView
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.Disposable
import me.bemind.customcanvas.BarView
import me.bemind.glitchappcore.app.*
import me.bemind.sidemenu.SideMenu
import me.bemind.sidemenu.SideMenuToggle
import net.idik.lib.slimadapter.SlimAdapter
import org.jraf.android.alibglitch.GlitchEffect
import permissions.dispatcher.*
import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager


@RuntimePermissions
class MainActivity : GlitchyBaseActivity(),IAppView, PickPhotoBottomSheet.OnPickPhotoListener,
SaveImageBottomSheet.OnSaveImageListener{
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    private var disposable: Disposable? = null

    //private var mFirebaseAnalytics : FirebaseAnalytics? = null

    private var mImageView : ExtendedImageView? = null

    private var effectPanel: ViewGroup? = null

    private val effectList by lazy {
        (findViewById(R.id.effect_list) as RecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private val loaderView by lazy {
        findViewById(R.id.loaderView)
    }

    private var toolbar : Toolbar? = null

    //private var toolbarEffect : Toolbar? = null
    private var actionBar : View? = null

    private val saveAction : ImageView by lazy {
        findViewById(R.id.save_effect) as ImageView
    }

    private val cleaAction : ImageView by lazy {
        findViewById(R.id.clear_effect) as ImageView
    }

    private val saveSAction by lazy {
        findViewById(R.id.save_s_button)
    }

    private val clearSAction by lazy {
        findViewById(R.id.clear_s_button)
    }

    private var appPresenter : IAppPresenter = AppPresenter()

    private var ioPresenter: IIOPresenter = IOPresenter()

    private val pickPhotoBS = PickPhotoBottomSheet.Creator.getPickPhotoBottomSheet(this,this)

    private val saveImageBS = SaveImageBottomSheet.Creator.getSaveImageBottomSheet(this,this)

    private var optionMenu: Menu? = null

    val sidemenu : SideMenu by lazy {
        findViewById(R.id.side_menu) as SideMenu
    }

    val bar : BarView by lazy {
        findViewById(R.id.bar) as BarView
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
                            .visibility(R.id.badge_textView,
                                    when(data.effectNew){
                                        EFFECT_NEWS.NONE -> GONE
                                        else -> VISIBLE
                                    })

                }
                .attachTo(effectList)
    }

    private val densityRv by lazy {
        findViewById(R.id.rv) as RecyclerView
    }

    private val densityAdapter by lazy {
        SlimAdapter.create()

    }

    private val densityPanel by lazy {
        findViewById(R.id.density_panel)
    }

    private val plusInfoPanel by lazy {
        findViewById(R.id.plus_info_panel) as ViewGroup
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        FontIconTypefaceHolder.init(assets, "material_icons.ttf")
        setContentView(R.layout.activity_main)

        GlitchyEventTracker.init(this)

        toolbar = findViewById(R.id.toolbar) as Toolbar

        actionBar = findViewById(R.id.action_panel)

        cleaAction.setImageDrawable(FontIconDrawable.inflate(this,R.xml.ic_close))
        cleaAction.setOnClickListener {
            if(appPresenter.modState == State.EFFECT){
                appPresenter.modState = State.BASE
                mImageView?.clearEffect()
            }
        }
        clearSAction.setOnClickListener {
            if(appPresenter.modState == State.EFFECT){
                appPresenter.modState = State.BASE
                mImageView?.clearEffect()
            }
        }

        saveAction.setImageDrawable(FontIconDrawable.inflate(this,R.xml.ic_done))
        saveAction.setOnClickListener { applyEffect() }
        saveSAction.setOnClickListener { applyEffect() }


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


        //listener
        bar.onProgressChangeListener = {
            progress -> mImageView?.makeEffect(progress)
        }
        

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
            bar.visibility = GONE
            actionBar?.visibility = GONE
            actionBar?.alpha = 1f
            plusInfoPanel.removeAllViews()
        }
        if(actionBar?.visibility == VISIBLE)animateAlpha(actionBar,runnable2, 350, false, 0f)

        if(densityPanel.visibility == VISIBLE){
            densityPanel.animate().alpha(0f)
                    .setDuration(200)
                    .setListener(object: AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            densityPanel.visibility = GONE
                        }
                    })
        }

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
            actionBar?.alpha = 0f
            actionBar?.visibility = VISIBLE
        }

        if(mImageView?.hasHistory?:false) {
            if(effectPanel?.visibility == GONE) animateAlpha(effectPanel,runnable, 450, true, 1f)
            if(actionBar?.visibility == GONE)animateAlpha(actionBar,runnable2, 450, true, 1f)
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

        disposable?.dispose()
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
        t.printStackTrace()
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

        if(checkPlayServices()) {
            appPresenter.onResume()

            //val intent = intent

            intent?.let {
                val action = intent.action
                val type = intent.type

                if ((Intent.ACTION_SEND == action || Intent.ACTION_EDIT == action) && type != null && type.startsWith("image/")) {
                    try {
                        //MainActivityPermissionsDispatcher.showCameraWithCheck(this)
                        MainActivityPermissionsDispatcher.handleIntentWithCheck(this)
                        //handleIntent()
                    } catch (e: Exception) {
                        Toast.makeText(this, R.string.error_open_image, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
        
        disposable = ProgressUpdate.progressSubject.subscribe(
                {progress -> bar.addProgressOnSwipe(progress)}
        )

    }
    
    


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults)
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
        ioPresenter.saveImage(mImageView?.getImageBitmap(),this)
        saveImageBS.dismiss()
    }

    override fun shareImage() {
        ioPresenter.shareImage(mImageView?.getImageBitmap(),this)
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

        try {
            GlitchEffect.showGlitch(this)
        }catch (e:Exception){
            e.printStackTrace()
        }

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
            optionMenu?.let {
                menuInflater.inflate(R.menu.main_menu_ex, optionMenu)
            }
            optionMenu?.getItem(0)?.icon = FontIconDrawable.inflate(this, R.xml.open_menu)
            optionMenu?.getItem(1)?.icon = FontIconDrawable.inflate(this, R.xml.ic_save)

        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun handleIntent() {
        ioPresenter.openImage(this, intent)
        intent = null
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionCameraDenied(){
        showErrorGetImage(RuntimeException("need permission"))
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onNeverAskAgainStoragePermission(){
        Toast.makeText(this,R.string.fundamental_permissions,Toast.LENGTH_LONG).show()
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showRationaleForCamera(request: PermissionRequest) {
        AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow,{ dialogInterface: DialogInterface, i: Int -> request.proceed()})
                .setNegativeButton(R.string.button_deny, { dialogInterface: DialogInterface, i: Int -> request.cancel() })
                .show()
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
                Effect.PIXEL -> makePixelEffect(true)
                Effect.TPIXEL -> makeTPixelEffect(true)
                Effect.CENSORED -> makeCensoderEffect(true)
                else -> {
                }
            }
        }

    }

    private fun makeHooloovooEffect(init: Boolean = false,progress:Int = 20) {
        val effect = HooloovooEffectState(R.layout.effect_anaglyph_layout,progress)
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.HOOLOOVOO)
            inflateEffectLayout(effect)
        }else{
            appPresenter.effectState = effect
            mImageView?.makeEffect(progress)
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

    private fun makePixelEffect(init: Boolean = false,progress: Int = 70) {
        val effect = PixelEffectState(R.layout.effect_anaglyph_layout,progress)

        if(init) {
            appPresenter.modState = State.EFFECT
            mImageView?.initEffect(Effect.PIXEL)
            inflateEffectLayout(effect)
        }else{
            appPresenter.effectState = effect
            mImageView?.makeEffect(progress)
        }
    }

    private fun makeTPixelEffect(init: Boolean = false,progress: Int = 70) {
        val effect = TPixelEffectState(R.layout.effect_anaglyph_layout,progress)

        if(init) {
            appPresenter.modState = State.EFFECT
            mImageView?.initEffect(Effect.TPIXEL)
            inflateEffectLayout(effect)
        }else{
            appPresenter.effectState = effect
            mImageView?.makeEffect(progress)
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

    private fun makeCensoderEffect(init:Boolean = false){
        if(init){
            appPresenter.modState = State.EFFECT

            mImageView?.initEffect(Effect.CENSORED)
            inflateEffectLayout(CensoredEffectState(R.layout.effect_glitch_layout))
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

                bar.visibility = VISIBLE


            }
            is WebpEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.tap_here_to_glitch_webp)

            }
            is SwapEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.tap_here_to_swap)

            }
            is GlitchEffectState -> {
                val b = view.findViewById(R.id.tap_to_glitch_button)

            }
            is GhostEffectState -> {
                //nothing
            }
            is WobbleEffectState -> {
                //nothing
            }
            is HooloovooEffectState ->{

               /* val bar = view.findViewById(R.id.bar) as BarView?
                bar?.progress = effectState.progress*/
                bar.visibility = VISIBLE

            }
            is AnaglyphEffectState -> {
              /*  val bar = view.findViewById(R.id.bar) as BarView?
                bar?.progress = effectState.progress*/

               // bar.visibility = VISIBLE
            }
            is PixelEffectState ->{
                /*val bar = view.findViewById(R.id.bar) as BarView?
                bar?.progress = effectState.progress*/

                bar.visibility = VISIBLE

            }

            is CensoredEffectState ->{
                val b = view.findViewById(R.id.tap_to_glitch_button) as TextView
                b.setText(R.string.double_tap_to_censored)
            }

            is TPixelEffectState ->{

                var sel = 25

                val densP = layoutInflater.inflate(R.layout.density_panel,plusInfoPanel,false)
                plusInfoPanel.addView(densP)

                val denstext = densP.findViewById(R.id.text_density) as TextView

                densP.setOnClickListener {
                    densityPanel.animate().alpha(1f)
                            .setDuration(200)
                            .setListener(object: AnimatorListenerAdapter(){
                                override fun onAnimationStart(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    densityPanel.alpha = 0f
                                    densityPanel.visibility = VISIBLE

                                    densityAdapter.updateData(intArrayOf(20,40,50,60,75,80,100).asList())

                                    densityAdapter.attachTo(densityRv)

                                    densityRv.smoothScrollBy(10,0) //need
                                }
                            })
                }

                val pickerLayoutManager = PickerLayoutManager(this, PickerLayoutManager.HORIZONTAL, false)
                pickerLayoutManager.isChangeAlpha = true
                pickerLayoutManager.scaleDownBy = 0.99f
                pickerLayoutManager.scaleDownDistance = 0.8f

                try{
                    val snapHelper =  LinearSnapHelper()
                    snapHelper.attachToRecyclerView(densityRv)
                }catch (e:Exception ){
                    e.printStackTrace()
                }

                densityRv.layoutManager = pickerLayoutManager

                densityAdapter
                        .register<Int>(R.layout.density_row){
                            data, injector ->
                            injector.text(R.id.text, data.toString())
                            injector.clicked(R.id.text,{
                                v ->
                                v?.let {
                                    sel = (v as TextView).text.toString().toInt()

                                    denstext.text = sel.toString()
                                    densityPanel.animate().alpha(0f)
                                            .setDuration(200)
                                            .setListener(object: AnimatorListenerAdapter(){
                                                override fun onAnimationEnd(animation: Animator?) {
                                                    super.onAnimationEnd(animation)
                                                    densityPanel.visibility = GONE

                                                    mImageView?.makeEffect(sel)
                                                }
                                            })
                                }
                            })
                        }






               /* val save = findViewById(R.id.save_selection) as ImageView
                save.setImageDrawable(FontIconDrawable.inflate(this,R.xml.ic_done))
                save.setOnClickListener {
                    //set sel

                    denstext.text = sel.toString()
                    densityPanel.animate().alpha(0f)
                            .setDuration(200)
                            .setListener(object: AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    densityPanel.visibility = GONE

                                    mImageView?.makeEffect(sel)
                                }
                            })
                }

                pickerLayoutManager.setOnScrollStopListener {
                    v ->
                    v?.let {
                        sel = (v as TextView).text.toString().toInt()
                    }

                }*/


            }
            else -> /*nothing*/ Log.i("Glitchy","base layout")
        }


        effectPanel?.addView(view)
    }


    private fun checkPlayServices() : Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                Log.i("GlitchyApp", "This device is not supported.")
                finish()
            }
            return false
        }
        return true
    }


}
