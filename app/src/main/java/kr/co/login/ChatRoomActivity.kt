package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.login.databinding.ActivityChatRoomBinding
import kr.co.login.databinding.ItemMsgListBinding
import kr.co.login.model.Message
import kr.co.login.model.Room
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class ChatRoomActivity : AppCompatActivity() {
    val binding by lazy { ActivityChatRoomBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://kotlin-7be75-default-rtdb.asia-southeast1.firebasedatabase.app/")
    lateinit var msgRef:DatabaseReference
    var roomId:String = ""
    var roomTitle:String = ""
    var roomWrite:String = ""
    var roomWriteDate:String = ""
    var userId:String = ""
    var userName:String = ""
    var solId:String = ""
    var solName:String = ""
    var code:String = ""
    var writeName:String = ""
    var writedate:String = ""
    //  val test = Room().write
    val msgList = mutableListOf<Message>()
    lateinit var adapter: MsgListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            userId = intent.getStringExtra("userId") ?: "none"
            userName = intent.getStringExtra("userName") ?: "Anonymous"
            solId = intent.getStringExtra("solId") ?: "none"
            solName = intent.getStringExtra("solName") ?: "Anonymous"
            roomId = intent.getStringExtra("roomId") ?: "none"
            roomTitle = intent.getStringExtra("roomTitle") ?: "anonymous"
            roomWrite = intent.getStringExtra("roomWrite") ?: "내용없음"
            roomWriteDate = intent.getStringExtra("roomWriteDate") ?:"date"
            code = intent.getStringExtra("code") ?: "code"
            writeName = intent.getStringExtra("writeName") ?: "작성자 없음"
            writedate = intent.getStringExtra("writedate") ?: "1111"
        }
        msgRef = database.getReference("rooms").child(roomId).child("messages")

        adapter = MsgListAdapter(msgList)
        with(binding){
            recyclerMsgs.adapter = adapter
            recyclerMsgs.layoutManager = LinearLayoutManager(baseContext)
            if(ChatListActivity.code == "1111"){
                textWriteName.setText("익명")
            }
            else {
                textWriteName.setText(writeName)
            }
            textWriteDate.setText(writedate)
            textTitle.setText(roomTitle)
            textWrite.setText(roomWrite)
            btnSend.setOnClickListener {sendMsg()}
        }
        loadMsgs()
    }/*
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
    fun goChatList(){
        intent = Intent(this, ChatListActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        startActivity(intent)
    }*/
    fun loadMsgs(){
        msgRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                msgList.clear()
                for(item in snapshot.children){
                    item.getValue(Message::class.java)?.let { msg ->
                        msgList.add(msg)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                print(error.message)
            }
        })
    }
    fun sendMsg(){
        with(binding){
            if(editMsg.text.isNotEmpty()){
                val message: Message
                if(ChatListActivity.userName == "Anonymous" && ChatListActivity.userId == "none") {
                    message = Message(editMsg.text.toString(), ChatListActivity.solName)
                }
                else{
                    message = Message(editMsg.text.toString(), ChatListActivity.userName)
                }
                val msgId = msgRef.push().key!!
                message.id = msgId
                msgRef.child(msgId).setValue(message)
                editMsg.setText("")
            }
        }
    }
}

class MsgListAdapter(val msgList:MutableList<Message>): RecyclerView.Adapter<MsgListAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMsgListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val msg = msgList.get(position)
        holder.setMsg(msg)
    }
    override fun getItemCount(): Int{
        return msgList.size
    }

    class Holder(val binding: ItemMsgListBinding): RecyclerView.ViewHolder(binding.root){
        fun setMsg(msg:Message){
            var date = Date(msg.timestamp)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR"))
            val str_date = dateFormat.format(date)
            if(ChatListActivity.code == "1111"){
                binding.textName.setText("익명")
            }
            else{
                binding.textName.setText(msg.userName)
            }
            binding.textMsg.setText(msg.msg)
            binding.textDate.setText(str_date)
        }
    }
}