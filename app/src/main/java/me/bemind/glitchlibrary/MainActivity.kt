package me.bemind.glitchlibrary

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.bemind.glitch.Glitcher
import org.jraf.android.alibglitch.GlitchEffect

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        glitcher.restore()
        Observable.fromCallable { glitcher.anaglyph(p1).result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{bitmap:Bitmap? -> mImageView?.setImageBitmap(bitmap)}
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mTextMessage: TextView? = null
    private var mImageView :ImageView? = null
    private var glitchButton: Button? = null
    private var seekbar: SeekBar? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                mTextMessage!!.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                mTextMessage!!.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                mTextMessage!!.setText(R.string.title_notifications)
                GlitchEffect.showGlitch(this)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextMessage = findViewById(R.id.message) as TextView
        val navigation = findViewById(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mImageView = findViewById(R.id.imageView) as ImageView

        glitchButton = findViewById(R.id.glitch_button) as Button
        glitchButton?.setOnClickListener { glitchImage() }

        seekbar = findViewById(R.id.seekBar1) as SeekBar

        seekbar!!.setOnSeekBarChangeListener(this )


    }



    lateinit var glitcher : Glitcher

    private fun glitchImage() {
        val bitmap = mImageView?.drawable as BitmapDrawable
        glitcher = Glitcher.Creator.getGlitcher(bitmap.bitmap)

        glitcher.restore()
        //val co = glitcher.corruptBitmap()

        Observable.fromCallable { glitcher.negative().result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{bitmap:Bitmap? -> mImageView?.setImageBitmap(bitmap)}


    }

}
