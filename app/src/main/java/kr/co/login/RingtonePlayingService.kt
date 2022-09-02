package kr.co.login

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat


class RingtonePlayingService : Service() {
    var mediaPlayer: MediaPlayer? = null
    var startId = 0
    var isRunning = false

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "default"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("알람시작")
                .setContentText("알람음이 재생됩니다.")
                .setSmallIcon(R.drawable.ic_btn_speak_now)
                .build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var startId = startId
        val getState = intent.extras!!.getString("state")!!
        startId = when (getState) {
            "alarm on" -> 1
            "alarm off" -> 0
            else -> 0
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if (!isRunning && startId == 1) {
            mediaPlayer = MediaPlayer.create(this, R.drawable.alert_dark_frame)
            mediaPlayer!!.start()
            isRunning = true
            this.startId = 0
        } else if (isRunning && startId == 0) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            isRunning = false
            this.startId = 0
        } else if (!isRunning && startId == 0) {
            isRunning = false
            this.startId = 0
        } else if (isRunning && startId == 1) {
            isRunning = true
            this.startId = 1
        } else {
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestory() 실행", "서비스 파괴")
    }
}