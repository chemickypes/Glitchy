package me.bemind.glitchlibrary

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.iid.FirebaseInstanceId
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.NotificationCompat
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager


/**
 * Created by angelomoroni on 02/07/17.
 */
class GlitchyFirebaseMessagingService: FirebaseMessagingService(){

    private val  TAG = "GlitchyFbMsgSrv"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        remoteMessage?.let {
            Log.d(TAG, "Message Notification Body: " + it.notification?.body)
           // sendNotification(it.notification.title,it.notification.body)
        }

    }

    private fun sendNotification(title:String?,messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title?:"Glitchy")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}

class GlitchyFirebaseInstanceIDService: FirebaseInstanceIdService() {



    private val  TAG = "GlitchyFbInstanceIdSrv"

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)


        //save to SharedPReferences
        try{
            SharedPreferencesManager.getInstance().putValue(Constants.FIREBASE_TOKEN,refreshedToken)
        }catch (e:Exception){
            try {
                SharedPreferencesManager.init(this,true)
                SharedPreferencesManager.getInstance().putValue(Constants.FIREBASE_TOKEN,refreshedToken)
            }catch (e1:Exception){
                e1.printStackTrace()
            }
        }


    }
}