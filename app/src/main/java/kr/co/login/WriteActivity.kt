package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.login.ChatListActivity.Companion.userId
import kr.co.login.databinding.ActivityChatRoomBinding
import kr.co.login.databinding.ActivityWriteBinding
import kr.co.login.model.Room
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class WriteActivity : AppCompatActivity() {
    val binding by lazy { ActivityWriteBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://kotlin-7be75-default-rtdb.asia-southeast1.firebasedatabase.app/")
    var roomsRef = database.getReference("rooms")
    var userId:String = ""
    var userName:String = ""
    var solId:String = ""
    var solName:String = ""
    var writeName:String = ""
    //var writedate:String = ""
    var code:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        userId = intent.getStringExtra("userId")?: "none"
        userName = intent.getStringExtra("userName")?: "Anonymous"
        solId = intent.getStringExtra("solId")?: "none"
        solName = intent.getStringExtra("solName")?: "Anonymous"
        code = intent.getStringExtra("code")?: "code"
      //  writedate = intent.getStringExtra("writedate")?: "date"
        if(ChatListActivity.code == "1111"){
            writeName = userName
        }
        else{
            writeName = solName
        }
        with(binding){
            binding.btnWrite.setOnClickListener{endWrite()}
        }
        /*with(binding){
            binding.btnBack.setOnClickListener{goChatAc()}
        }*/
    }
    /*
    fun goChatAc(){
        intent = Intent(this, ChatListActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        startActivity(intent)
    }*/

    fun endWrite(){
        with(binding) {
            val roomTitle = editTitle.text.toString()
            val roomWrite = editWrite.text.toString()
            if(roomTitle.isNotEmpty() && roomWrite.isNotEmpty()) {
                val room: Room
                if(ChatListActivity.code == "1111") {
                    room = Room(editTitle.text.toString(), userName, editWrite.text.toString())
                }
                else{
                    room = Room(editTitle.text.toString(), solName, editWrite.text.toString())
                }
                val roomId = roomsRef.push().key!!
                room.id = roomId
                roomsRef.child(roomId).setValue(room)
                var date = Date(room.timestamp)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR"))
                val writedate = dateFormat.format(date)

                goChatRoom(
                    userId,
                    userName,
                    solId,
                    solName,
                    roomId,
                    roomTitle,
                    roomWrite,
                    writeName
                )
            }
            else{
                Toast.makeText(
                    baseContext, "제목과 글을 작성해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    fun goChatRoom(userId:String, userName:String, solId:String, solName:String, roomId:String, roomTitle:String, roomWrite:String, writeName:String){
        intent = Intent(this, ChatListActivity::class.java)
        intent.putExtra("roomId", roomId)
        intent.putExtra("userId", userId)
        intent.putExtra("solId", solId)
        intent.putExtra("userName", userName)
        intent.putExtra("solName", solName)
        intent.putExtra("roomTitle", roomTitle)
        intent.putExtra("roomWrite", roomWrite)
        intent.putExtra("writeName", writeName)
        if(ChatListActivity.code == "1111"){
            intent.putExtra("code", "1111")
        }
        else{
            intent.putExtra("code", "1234")
        }
        startActivity(intent)
        //onDestroy()
    }
    /*fun createRoom(title:String){
        val room = Room(title, userName)
        val roomId = roomRef.push().key!!
        room.title = room
        roomRef.child(roomId).setValue(room)
     }
     */
}