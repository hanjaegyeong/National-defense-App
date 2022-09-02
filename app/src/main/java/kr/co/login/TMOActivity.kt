package kr.co.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.EdgeEffectCompat.getDistance
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Transformations.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import data.Row
import data.TMO
import kakaodata.KakaoData
import kotlinx.android.synthetic.main.activity_tmoactivity.*
import kr.co.login.databinding.ActivityDdaySetting2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.*
import kotlin.math.pow

class TMOActivity : AppCompatActivity(), OnMapReadyCallback {
    private var LOCATION_PERMISSION = 1004
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val r = 6372.8 * 1000

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION ,android.Manifest.permission.ACCESS_COARSE_LOCATION )

    //lateinit var binding : ActivityDdaySetting2Binding;
    var userId:String = ""
    var userName:String = ""
    var solId:String = ""
    var solName:String = ""
    var code:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmoactivity)
        userId = intent.getStringExtra("userId") ?: "none"
        userName = intent.getStringExtra("userName") ?: "Anonymous"
        solId = intent.getStringExtra("solId") ?: "none"
        solName = intent.getStringExtra("solName") ?: "Anonymous"
        code = intent.getStringExtra("code") ?: "code"
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationSource = FusedLocationSource(this@TMOActivity , LOCATION_PERMISSION)
        
        val fragmentManager: FragmentManager = supportFragmentManager
        var mapFragment: MapFragment? = fragmentManager.findFragmentById(R.id.map) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }
        mapFragment!!.getMapAsync(this)
        loadTMO()
        goMap.setOnClickListener {goMap()}
    }
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK ca0bee0d7a7eab4ff56d596d787ba4fe"  // REST API 키
    }
    fun loadTMO() {
        val retrofit = Retrofit.Builder()
            .baseUrl(TMOOpenApi.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val seoulOpenService = retrofit.create(TMOOpenService::class.java)

        seoulOpenService.getTMO(TMOOpenApi.API_KEY).enqueue(object : Callback<TMO> {
            override fun onResponse(call: Call<TMO>, response: Response<TMO>) {
                //Log.d("hjg", response.body().toString())
                showTMOs(response.body() as TMO)
            }

            override fun onFailure(call: Call<TMO>, t: Throwable) {
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }
    fun goMap(){
        intent = Intent(this, MapActivity::class.java)
        intent.putExtra("userId", HomeActivity.userId)
        intent.putExtra("userName", HomeActivity.userName)
        intent.putExtra("solId", HomeActivity.solId)
        intent.putExtra("solName", HomeActivity.solName)
        intent.putExtra("code", HomeActivity.code)
        startActivity(intent)
    }
    //TMO 역이름 모두 추출해서 좌표반환 함수에 인자로 넣기
    private fun showTMOs(tmos: TMO) {
        for (tmo in tmos.DS_TB_MND_TMO_INFO.row) {
            //Log.d("hjgtmo", tmo.tmo_nm)
            searchKeyword(tmo.tmo_nm + "역", tmo)
        }
    }

    // 역 검색 함수
    private fun searchKeyword(keyword: String, tmo: Row) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, 1)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<KakaoData> {
            override fun onResponse(call: Call<KakaoData>, response: Response<KakaoData>) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                //Log.d("hjg", "Body: ${response.body()}")
                showXY(response.body() as KakaoData, tmo)
            }
            override fun onFailure(call: Call<KakaoData>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }
    private fun showXY(xys: KakaoData, tmo: Row) {
        for (xy in xys.documents) {

            naverMap.locationSource = locationSource
            ActivityCompat.requestPermissions(this, PERMISSION, LOCATION_PERMISSION)
            naverMap.addOnLocationChangeListener { location ->
                Log.d("hjg", "${location.latitude}, ${location.longitude}")

                val dist = getDistance(location.latitude, location.longitude, xy.y.toDouble(), xy.x.toDouble())
                //Log.d("hjgdist", dist.toString())
                val marker = Marker()
                if (dist < 15000){
                    naverMap.addOnCameraChangeListener { reason, animated ->

                        marker.position = LatLng(xy.y.toDouble(), xy.x.toDouble())
                        marker.map = naverMap
                        marker.icon = MarkerIcons.BLUE
                    }
                }
                else {
                    naverMap.addOnCameraChangeListener { reason, animated ->
                        marker.position = LatLng(xy.y.toDouble(), xy.x.toDouble())
                        marker.map = naverMap
                        marker.icon = MarkerIcons.BLUE
                        marker.width = 70
                        marker.height = 80
                    }
                }
                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                    override fun getText(infoWindow: InfoWindow): CharSequence {
                        return tmo.pstnexpln
                    }
                }
                val listener = Overlay.OnClickListener { overlay ->
                    val marker = overlay as Marker

                    if (marker.infoWindow == null) {
                        // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                        infoWindow.open(marker)
                    } else {
                        // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                        infoWindow.close()
                    }
                    true
                }
                marker.onClickListener = listener
            }
        }
    }
    //거리 계산 함수
    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
        val c = 2 * kotlin.math.asin(sqrt(a))
        return (r * c).toInt()
    }
    @UiThread
    override fun onMapReady(map: NaverMap) {

        naverMap = map
//        naverMap.maxZoom =18.0
//        naverMap.minZoom =5.0


//        카메라 설정
        val cameraPosition = CameraPosition(
            LatLng(37.7245691321456, 126.717609009683), // 대상 지점
            16.0, // 줌 레벨
            0.0, // 기울임 각도
            0.0 // 베어링 각도
        )
        naverMap.cameraPosition = cameraPosition
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

}