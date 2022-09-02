package kr.co.login

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kr.co.login.databinding.ActivityAlarmMidBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AlarmMidActivity : AppCompatActivity() {
    val binding by lazy {ActivityAlarmMidBinding.inflate(layoutInflater)}
    private var alarmManager: AlarmManager? = null
    private var mCalender: GregorianCalendar? = null
    private var notificationManager: NotificationManager? = null
    var builder: NotificationCompat.Builder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        mCalender = GregorianCalendar()
        Log.v("HelloAlarmActivity", mCalender!!.time.toString())
        setContentView(binding.root)


        //접수일 알람 버튼
        val button = findViewById<View>(R.id.btnNoti) as Button
        button.setOnClickListener { setAlarm() }
    }

    private fun setAlarm() {
        //AlarmReceiver에 값 전달
        val receiverIntent = Intent(this@AlarmMidActivity, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@AlarmMidActivity, 0, receiverIntent, 0)
        val from = "2022-08-20 07:04:00" //임의로 날짜와 시간을 지정

        //날짜 포맷을 바꿔주는 소스코드
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var datetime: Date? = null
        try {
            datetime = dateFormat.parse(from)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val calendar = Calendar.getInstance()
        calendar.time = datetime
        alarmManager!![AlarmManager.RTC, calendar.timeInMillis] = pendingIntent
    }
}