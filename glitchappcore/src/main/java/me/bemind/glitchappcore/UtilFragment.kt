package me.bemind.glitchappcore

import android.app.Fragment
import android.os.Bundle
import me.bemind.glitchappcore.Image
import java.util.ArrayList

/**
 * Created by angelomoroni on 12/04/17.
 */

class RetainedFragment : Fragment(){



    object TAG {
        val TAG = "RetainedFragment"
    }

    var history : ArrayList<ImageDescriptor>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }
}
