package kr.co.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kr.co.login.databinding.ActivityChatListBinding
import kr.co.login.model.Room
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class ChatListActivity : AppCompatActivity() {
    val binding by lazy{ActivityChatListBinding.inflate(layoutInflater)}
    val database = Firebase.database("https://kotlin-7be75-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val roomsRef = database.getReference("rooms")
    //var writeCode:Int = 0
    companion object{
        var userId:String = ""
        var userName:String = ""
        var solId:String = ""
        var solName:String = ""
        var code:String = ""
        //var writedate:String = ""
    }
    val roomList = mutableListOf<Room>()
    lateinit var adapter: ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: "none"
        userName = intent.getStringExtra("userName") ?: "Anonymous"
        solId = intent.getStringExtra("solId") ?: "none"
        solName = intent.getStringExtra("solName") ?: "Anonymous"
        code = intent.getStringExtra("code") ?: "code"
        //writedate = intent.getStringExtra("writedate") ?: "date"
        with(binding){
            binding.btngolastcheck.setOnClickListener{goLastCheckAc()}
            binding.btngohome.setOnClickListener{goHomeAc()}
            binding.btngotmo.setOnClickListener{goMapAc()}
            btnCreate.setOnClickListener {goWriteActivity()}
        }
        adapter = ChatRoomListAdapter(roomList)
        with(binding){
            recyclerRooms.adapter = adapter
            recyclerRooms.layoutManager = LinearLayoutManager(baseContext)
        }
        loadRooms()

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
    fun goMapAc(){
        intent = Intent(this, MapActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    fun goAlarm(){
        intent = Intent(this, AlarmActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }

    fun loadRooms(){
        roomsRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomList.clear()
                for(item in snapshot.children){
                    item.getValue(Room::class.java)?.let { room ->
                        roomList.add(room)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError){
                print(error.message)
            }
        })
    }

    fun goWriteActivity(){

        val intent = Intent(this, WriteActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("solId", solId)
        intent.putExtra("solName", solName)
        startActivity(intent)
    }
    /* fun openCreateRoom() {
         val editTitle = EditText(this)
         val dialog = AlertDialog.Builder(this)
             .setTitle("방 이름")
             .setView(editTitle)
             .setPositiveButton("만들기") {dlg, id ->
                 createRoom(editTitle.text.toString())
             }
         dialog.show()
     }*/

    /*fun createRoom(title:String){
        val room = Room(title, userName)
        val roomId = roomsRef.push().key!!
        room.id = roomId
        roomsRef.child(roomId).setValue(room)
    }*/
}

class ChatRoomListAdapter(val roomList:MutableList<Room>) : RecyclerView.Adapter<ChatRoomListAdapter.Holder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return Holder(view)
    }
    override fun onBindViewHolder(holder: Holder, position: Int){
        val room = roomList.get(position)
        holder.setRoom(room)
    }
    override fun getItemCount(): Int{
        return roomList.size
    }
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        lateinit var mRoom:Room
        init{
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatRoomActivity::class.java)
                intent.putExtra("writeCode", 0)
                intent.putExtra("roomId", mRoom.id)
                intent.putExtra("roomTitle", mRoom.title)
                intent.putExtra("roomWrite", mRoom.write)
                intent.putExtra("code", ChatListActivity.code)
                intent.putExtra("writeName", mRoom.users)
                var Wdate = Date(mRoom.timestamp)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR"))
                val writedate = dateFormat.format(Wdate)
                intent.putExtra("writedate", writedate)
                itemView.context.startActivity(intent)
            }
        }
        fun setRoom(room:Room){
            this.mRoom = room
            itemView.findViewById<TextView>(android.R.id.text1).setText(room.title)
        }
    }
}