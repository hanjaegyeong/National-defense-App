package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.co.login.databinding.ActivityCheckBoxBinding
import kotlin.system.exitProcess

class CheckBoxActivity : AppCompatActivity() {
    val binding by lazy { ActivityCheckBoxBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            binding.btnback.setOnClickListener{goCheckBoxAc()}
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
    fun goCheckBoxAc(){
        intent = Intent(this, LastCheckActivity::class.java)
        startActivity(intent)
    }}