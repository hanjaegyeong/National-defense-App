package kr.co.login

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyMessageService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("토큰", "$token")
    }
}