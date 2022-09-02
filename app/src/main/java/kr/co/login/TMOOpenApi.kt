package kr.co.login

import data.TMO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

class TMOOpenApi {
    companion object {
        val DOMAIN = "https://openapi.mnd.go.kr"
        val API_KEY = "3436313634313131383832323632303938"
    }
}
interface TMOOpenService {

    @GET("{api_key}/json/DS_TB_MND_TMO_INFO/1/200/")
    fun getTMO(@Path("api_key") key: String): Call<TMO>
}