package me.bemind.glitchappcore.app

import android.content.res.Configuration
import android.os.Bundle
import me.bemind.glitchappcore.EffectState
import me.bemind.glitchappcore.GlitchyBaseActivity
import me.bemind.glitchappcore.State

/**
 * Created by angelomoroni on 18/04/17.
 */

class AppPresenter : IAppPresenter {

    private val STATE_K = "state_k"
    private val EFFECT_LAYOUT_K = "effect_l_k"
    private val EMPTY_IMAGE_IEW_K = "empty_image_view_k"

    private var savedInstanceState : Bundle? = null

    override var modState: State = State.BASE
        set(value) {
            field = value
            appView?.updateState(field)
            if(value == State.BASE) effectState = null
        }


    override var effectState: EffectState? = null

    override var appView: IAppView? = null

    override var emptyImageView = true

    override fun saveInstanceState(activity: GlitchyBaseActivity, outState: Bundle?) {
        outState?.putSerializable(STATE_K,modState)
        if(effectState!=null) outState?.putParcelable(EFFECT_LAYOUT_K,effectState)
        outState?.putBoolean(EMPTY_IMAGE_IEW_K,emptyImageView)
    }

    override fun restoreInstanceState(activity: GlitchyBaseActivity, savedInstanceState: Bundle?) {
        this.savedInstanceState = savedInstanceState
        if(savedInstanceState?.containsKey(STATE_K)?:false) modState = savedInstanceState?.getSerializable(STATE_K) as State
        if(savedInstanceState?.containsKey(EFFECT_LAYOUT_K)?:false) effectState = savedInstanceState?.getParcelable(EFFECT_LAYOUT_K)
        savedInstanceState?.containsKey(EMPTY_IMAGE_IEW_K)?.let {
            emptyImageView = savedInstanceState.getBoolean(EMPTY_IMAGE_IEW_K,false)
        }
    }

    override fun onBackPressed(): Boolean {
        if(modState!=State.BASE){
            modState = State.BASE
            return true
        }else{
            return false
        }

    }

    override fun onResume() {
        appView?.restoreView(effectState,emptyImageView)
        appView?.updateState(modState)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
    }

}
