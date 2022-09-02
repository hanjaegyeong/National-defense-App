package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.login.databinding.ActivityMainBinding
import kr.co.login.model.Solicitor
import kr.co.login.model.User
import kotlin.system.exitProcess

//윤환오빠 import
/*import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.telecom.Call
import android.widget.SeekBar
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup1.**/


class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://kotlin-7be75-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val usersRef = database.getReference("users")
    val SolRef = database.getReference("solic")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) //binding root
        with(binding){
            binding.btnSignin.setOnClickListener{signin()}
            binding.btnSignup.setOnClickListener{signup()}
        }

        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSource = FusedLocationSource(this@MainActivity , LOCATION_PERMISSION)
        val fragmentManager: FragmentManager = supportFragmentManager
        var mapFragment: MapFragment? = fragmentManager.findFragmentById(R.id.map) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }
        mapFragment!!.getMapAsync(this)*/
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


    fun signup(){
        with(binding){
            val id = editId.text.toString()
            val password = editPassword.text.toString()
            val name = editName.text.toString()
            val code = editCode.text.toString()

            if(id.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()){
                if(code == "1111") {
                    usersRef.child(id).get().addOnSuccessListener {
                        if (it.exists()) {
                            Toast.makeText(
                                baseContext,
                                "아이디가 존재합니다.",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else {
                            val user = User(id, password, name)
                            usersRef.child(id).setValue(user)
                            signin()
                        }
                    }
                }
                else if(code == "1234"){
                    SolRef.child(id).get().addOnSuccessListener {
                        if (it.exists()) {
                            Toast.makeText(
                                baseContext,
                                "아이디가 존재합니다.",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else {
                            val solic = Solicitor(id, password, name)
                            SolRef.child(id).setValue(solic)
                            signin()
                        }
                    }
                }
                else{
                    Toast.makeText(
                        baseContext,
                        "잘못된 코드입니다.",
                        Toast.LENGTH_LONG
                    )
                }

            }
            else{
                Toast.makeText(baseContext,
                    "아이디, 비밀번호, 별명을 모두 입력해야 합니다.",
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun signin(){
        with(binding){
            val id = editId.text.toString()
            val password = editPassword.text.toString()
            val code = editCode.text.toString()

            if(id.isNotEmpty() && password.isNotEmpty() && code.isNotEmpty()) {
                if(code == "1111") {
                    usersRef.child(id).get().addOnSuccessListener {
                        if (it.exists()) {
                            it.getValue(User::class.java)?.let { user ->
                                if (user.password == password) {
                                    goUserChatroomList(user.id, user.name, code)
                                } else {
                                    Toast.makeText(
                                        baseContext, "비밀번호가 다릅니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(baseContext, "아이디가 없습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else if(code == "1234") {
                    SolRef.child(id).get().addOnSuccessListener {
                        if (it.exists()) {
                            it.getValue(Solicitor::class.java)?.let { solicitor ->
                                if (solicitor.password == password) {
                                    goSolChatroomList(solicitor.id, solicitor.name, code)
                                } else {
                                    Toast.makeText(
                                        baseContext, "비밀번호가 다릅니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else{
                            Toast.makeText(baseContext, "아이디가 없습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Toast.makeText(
                        baseContext, "잘못된 코드입니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else{
                Toast.makeText(baseContext, "아이디, 비밀번호를 입력해야 합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun goUserChatroomList(userId: String, userName: String, code: String){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("code", code)
        startActivity(intent)
    }

    fun goSolChatroomList(solId: String, solName: String, code: String){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        intent.putExtra("code", code)
        startActivity(intent)
    }
}