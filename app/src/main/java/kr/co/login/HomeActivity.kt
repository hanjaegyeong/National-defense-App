package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.login.ChatListActivity.Companion.userId
import kr.co.login.ChatListActivity.Companion.userName
import kr.co.login.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {
    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://kotlin-7be75-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.getReference("users")
    val SolRef = database.getReference("solic")
    companion object{
        var userId:String = ""
        var userName:String = ""
        var solId:String = ""
        var solName:String = ""
        var code:String = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userId = intent.getStringExtra("userId") ?: "none"
        userName = intent.getStringExtra("userName") ?: "Anonymous"
        solId = intent.getStringExtra("solId") ?: "none"
        solName = intent.getStringExtra("solName") ?: "Anonymous"
        code = intent.getStringExtra("code") ?: "code"
        with(binding){
            if(code == "1111") {
                textperid.setText(userName)
            }
            else{
                textperid.setText(solName)
            }
            binding.btngocheckbox.setOnClickListener{goLastCheckAc()}
            binding.btngochat.setOnClickListener{goChatListAc()}
            binding.btngotmo.setOnClickListener{goMapAc()}
            binding.btnddaysetting.setOnClickListener{goDdaySettingAc()}
        }
        binding.savedClass.text = MyApplication.prefs.getString("class", "-")
        //Log.d("hjg", MyApplication.prefs.getString("class", "no email"))

        binding.btnddaysetting.setOnClickListener(){
            val intent = Intent(this, DdaySeettingActivity2::class.java)
            startActivity(intent)
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd")
        val eDate = MyApplication.prefs.getString("year", "0000") + MyApplication.prefs.getString("month", "00") + MyApplication.prefs.getString("day", "00")

        val endDate = dateFormat.parse(eDate).time
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        binding.outDday.text = ((endDate - today) / (24 * 60 * 60 * 1000)).toString()
        binding.outDate.text = MyApplication.prefs.getString("date", "미설정")
    }

    override fun onPause() {
        super.onPause()
   }
    override fun onResume() {
        super.onResume()
        binding.savedClass.text = MyApplication.prefs.getString("class", "-")

        val dateFormat = SimpleDateFormat("yyyyMMdd")
        val eDate = MyApplication.prefs.getString("year", "0000") + MyApplication.prefs.getString("month", "00") + MyApplication.prefs.getString("day", "00")

        val endDate = dateFormat.parse(eDate).time
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        binding.outDday.text = ((endDate - today) / (24 * 60 * 60 * 1000)).toString()
        binding.outDate.text = MyApplication.prefs.getString("date", "미설정")


    }

    private var backKeyPressedTime: Long = 0

    override fun onBackPressed(){
        if(System.currentTimeMillis() - backKeyPressedTime >= 1500) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        else{
            this.finishAffinity()
            System.runFinalization()
            exitProcess(0)
        }
    }
    fun goDdaySettingAc() {
        intent = Intent(this, DdaySettingActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        intent.putExtra("code", code)
        startActivity(intent)
    }
    fun goLastCheckAc(){
        intent = Intent(this, LastCheckActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        intent.putExtra("code", code)
        startActivity(intent)
    }
    fun goChatListAc(){
        intent = Intent(this, ChatListActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        intent.putExtra("code", code)
        startActivity(intent)
    }
    fun goMapAc() {
        intent = Intent(this, MapActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        intent.putExtra("code", code)
        startActivity(intent)
    }

}
