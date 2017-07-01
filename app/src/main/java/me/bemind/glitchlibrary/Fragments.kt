package me.bemind.glitchlibrary

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.shamanland.fonticon.FontIconDrawable
import org.jraf.android.alibglitch.GlitchEffect
import android.content.ActivityNotFoundException
import android.widget.Toast


/**
 * Created by angelomoroni on 03/05/17.
 */

class MenuFragment: Fragment(), ShareAppBottomSheet.OnShareDialogClick {


    var rootview : View? = null

    val logoApp : ImageView? by lazy {
        rootview?.findViewById(R.id.app_logo) as ImageView
    }

    val versionApp : TextView by lazy {
        rootview?.findViewById(R.id.version_text) as TextView
    }

    val bemindLink:TextView by lazy {
        rootview?.findViewById(R.id.bemind_link) as TextView
    }

    val shareApp : FloatingActionButton by lazy {
        rootview?.findViewById(R.id.share_app_button) as FloatingActionButton
    }

    private val mShareBottomSheet: ShareAppBottomSheet by lazy {
        ShareAppBottomSheet.getShareAppDialogFragment(activity,this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mShareBottomSheet.dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootview = inflater?.inflate(R.layout.menu_fragment,container)!!

        logoApp?.setOnClickListener {
            GlitchEffect.showGlitch(activity)
        }

        val pInfo = activity.packageManager.getPackageInfo(activity.packageName,0)
        versionApp.apply {
            text = getString(R.string.v_d,pInfo.versionName)
        }

        bemindLink.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("http://www.bemind.me")
            startActivity(i)
        }

        shareApp.setImageDrawable(FontIconDrawable.inflate(activity,R.xml.ic_big_share))
        shareApp.setOnClickListener {
            //open share bottomsheet dialog
            mShareBottomSheet.show()
        }

        return rootview!!
    }

    override fun rateApp() {

        val uri = Uri.parse("market://details?id=" + activity.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
//                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            openIntent(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.packageName)), R.string.open_with)
        }

    }

    override fun instagram() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/glitchyapp/"))

        openIntent(browserIntent, R.string.open_with)
    }

    override fun facebook() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fb.me/glitchyapp"))
        openIntent(browserIntent, R.string.open_with)
    }

    override fun shareAppLink() {
        val text = getString(R.string.glitch_your_worl_using_glitchyapp,
                "http://play.google.com/store/apps/details?id=" + activity.packageName)

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"

        openIntent(sendIntent,R.string.share_with)
    }

    private fun openIntent(intent: Intent, idTitle: Int = R.string.open_with){
        try {
            startActivity(Intent.createChooser(intent,getString(idTitle)))
        }catch (e:Exception){
            Toast.makeText(activity,R.string.no_activity_to_execute_action,Toast.LENGTH_SHORT).show()
        }
    }


}
