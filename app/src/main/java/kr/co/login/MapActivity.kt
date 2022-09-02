package kr.co.login


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_map.*
import kr.co.login.databinding.ActivityMapBinding
import kotlin.math.round







class MapActivity : AppCompatActivity(), OnMapReadyCallback {
//    private var mBinding: ActivityMapBinding? = null
  //  private val binding get() = mBinding!!
    private var LOCATION_PERMISSION = 1004
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val marker =Marker()
    private val circle = CircleOverlay()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION )
    var userId:String = ""
    var userName:String = ""
    var solId:String = ""
    var solName:String = ""
    var code:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    mBinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_map)
        userId = intent.getStringExtra("userId") ?: "none"
        userName = intent.getStringExtra("userName") ?: "Anonymous"
        solId = intent.getStringExtra("solId") ?: "none"
        solName = intent.getStringExtra("solName") ?: "Anonymous"
        code = intent.getStringExtra("code") ?: "code"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSource = FusedLocationSource(this@MapActivity , LOCATION_PERMISSION)
        val fragmentManager: FragmentManager = supportFragmentManager
        var mapFragment: MapFragment? = fragmentManager.findFragmentById(R.id.map) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }
        mapFragment!!.getMapAsync(this)
        btngochat.setOnClickListener{goChatListAc()}
        btngohome.setOnClickListener{goHomeAc()}
        btngolastcheck.setOnClickListener{goLastCheckAc()}
        goTMO.setOnClickListener{goTMOAc()}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @UiThread
    override fun onMapReady(map: NaverMap) {

        naverMap = map
        naverMap.isNightModeEnabled = true

//        naverMap.maxZoom =18.0
//        naverMap.minZoom =5.0

//        카메라 설정
        val cameraPosition = CameraPosition(
            LatLng(37.536606, 126.977139), // 대상 지점
            16.0, // 줌 레벨
            0.0, // 기울임 각도
            000.0 // 베어링 각도
        )
        naverMap.cameraPosition = cameraPosition

        var setThelat : Double =37.536606
        var setThelong : Double =126.977139
        var maxx : Double =0.0
        circle.center = LatLng(setThelat,setThelong)
        circle.radius = maxx
        circle.color= 0x5099ffff.toInt()
        rsw.setOnCheckedChangeListener{buttonView,isChecked->
            if(isChecked){
                circle.map=naverMap
            }
            else{
                circle.map=null

            }
        }

        naverMap.locationSource = locationSource
        ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)
        naverMap.addOnLocationChangeListener { location ->
            setloc.setOnClickListener {

                val builder = AlertDialog.Builder(this)
                builder
                    .setMessage("현위치를 기준점으로 설정하시겠습니까?")
                    .setPositiveButton("YES",
                        DialogInterface.OnClickListener { dialog, id ->
                            setThelat = location.latitude
                            setThelong = location.longitude
                            Toast.makeText(this, "기준점 설정 완료", Toast.LENGTH_SHORT).show()
                            marker.position = LatLng(setThelat, setThelong)
                            marker.map = naverMap
                            circle.center = LatLng(setThelat,setThelong)
                            marker.captionText = "출발기준점"

                            if(maxx==0.0){
                                Toast.makeText(this,"반경을 지정해주세요",Toast.LENGTH_SHORT).show()
                            }
                        })
                builder.create()
                builder.show()
            }


            var dist : Double
            var max0 : Int =0
            var max1: Double

            //Toast.makeText(this,"${setthelat}, ${setthelong}",Toast.LENGTH_SHORT).show()
            dist=calDist(location.latitude, location.longitude,setThelat,setThelong)
            //alert.text="${dist}"
            setter.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                val inflater =layoutInflater;
                val editTitle= NumberPicker(this)
                editTitle.minValue=1
                editTitle.maxValue=10
                editTitle.wrapSelectorWheel = false


                builder
                    .setTitle("반경 입력")
                    .setMessage("이동반경을 선택해주세요(Km)")
                    .setView(editTitle)

                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            max0=editTitle.value
                            max1=max0.toDouble()
                            maxx=max1*1000
                            circle.radius = maxx

                            Toast.makeText(this, "반경 : ${max0}km", Toast.LENGTH_SHORT).show()
                            // Start 버튼 선택시 수행
                        })
                builder.show()
                builder.create()
            }?: throw IllegalStateException("Activity cannot be null")

            alerting(maxx,dist)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when {
            requestCode != LOCATION_PERMISSION -> {
                return
            }
            else -> {
                when {
                    locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults) -> {
                        if (!locationSource.isActivated){
                            naverMap.locationTrackingMode = LocationTrackingMode.None
                        }else{
                            naverMap.locationTrackingMode = LocationTrackingMode.Follow
                        }
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    /*override fun onDestroy(){
        mBinding = null
            super.onDestroy()
    }*/
    fun calDist(lat1: Double, lon1: Double, lat2:Double, lon2:Double) : Double {
        val EARTH_R = 6371000.0
        val rad = Math.PI / 180
        val radLat1 = rad * lat1
        val radLat2 = rad * lat2
        val radDist = rad * (lon1 - lon2)
        var distance = Math.sin(radLat1) * Math.sin(radLat2)
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist)
        val ret = EARTH_R * Math.acos(distance)
        return ret
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun alerting(maxrange: Double, nowdist: Double):Boolean{
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator;
        var in_out : Boolean=true
        if(maxrange==0.0){
            in_out=true
        }
        else if (maxrange>nowdist){
            //alert.text="제한구역을 벗어나지 않았습니다\n이탈까지 ${round((maxrange-nowdist)*100)/100} m 남았습니다."
            in_out=true
        }
        else{
            //alert.text="제한구역을  ${round((nowdist-maxrange)*100)/100} m 이탈하였습니다.\n 현위치를 확인하세요"
            Toast.makeText(this, "제한구역을  ${round((nowdist-maxrange)*100)/100} m 이탈하였습니다.\n 현위치를 확인하세요", Toast.LENGTH_SHORT).show()
            vibrator.vibrate(VibrationEffect.createOneShot(500,200))
            in_out=false
        }
        return in_out
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
    fun goLastCheckAc(){
        intent = Intent(this, LastCheckActivity::class.java)
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
    fun goTMOAc(){
        intent = Intent(this, TMOActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
}
