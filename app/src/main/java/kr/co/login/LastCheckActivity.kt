package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.co.login.databinding.ActivityLastCheckBinding
import kotlin.system.exitProcess
import android.app.DatePickerDialog
import android.widget.DatePicker

import java.util.*

class LastCheckActivity : AppCompatActivity() {
    val binding by lazy { ActivityLastCheckBinding.inflate(layoutInflater)}
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
            binding.btngolastcheck.setOnClickListener{goLastCheckAc()}
            binding.btngohome.setOnClickListener{goHomeAc()}
            binding.btngochat.setOnClickListener{goChatListAc()}
            binding.btngotmo.setOnClickListener{goMapAc()}
            binding.btngochecklist.setOnClickListener{goChecklistAc()}
            binding.btndateclick.setOnClickListener{//goSettingAc()}
                goAlarm()}

        }
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
    fun goAlarm(){
        intent = Intent(this, AlarmMidActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goLastCheckAc(){
        intent = Intent(this, LastCheckActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goHomeAc(){
        intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goChatListAc(){
        intent = Intent(this, ChatListActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goMapAc(){
        intent = Intent(this, MapActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goSettingAc(){
        intent = Intent(this, DdaySeettingActivity2::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goChecklistAc(){
        intent = Intent(this, CheckBoxActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
}