package me.bemind.glitchlibrary

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.iid.FirebaseInstanceId



/**
 * Created by angelomoroni on 02/07/17.
 */
class GlitchyFirebaseMessagingService: FirebaseMessagingService(){

    private val  TAG = "GlitchyFbMsgSrv"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        remoteMessage?.let {
            Log.d(TAG, "Message Notification Body: " + it.notification?.body)
        }

    }
}

class GlitchyFirebaseInstanceIDService: FirebaseInstanceIdService() {

    private val  TAG = "GlitchyFbInstanceIdSrv"

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        //save to SharedPReferences
    }
}