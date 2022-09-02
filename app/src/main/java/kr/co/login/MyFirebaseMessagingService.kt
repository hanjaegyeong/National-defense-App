package kr.co.login

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import kotlin.system.exitProcess

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String){
        Log.d("토큰", "$token")
    }
    private var backKeyPressedTime: Long = 0

}