package kr.co.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kr.co.login.databinding.ActivityDdaySetting2Binding
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.Button
import android.widget.TextView
import java.util.*

class DdaySeettingActivity2 : AppCompatActivity() {
    var dateEndY = 0
    var dateEndM = 0
    var dateEndD = 0
    var ddayValue = 0

    // 현재 날짜를 알기 위해 사용
    var calendar: Calendar? = null
    var currentYear = 0
    var currentMonth = 0
    var currentDay = 0

    // Millisecond 형태의 하루(24 시간)
    private val ONE_DAY = 24 * 60 * 60 * 1000
    var edit_endDateBtn: TextView? = null
    var edit_result: TextView? = null
    var datePicker: Button? = null
    lateinit var binding : ActivityDdaySetting2Binding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_setting2)
        binding = ActivityDdaySetting2Binding.inflate(layoutInflater);
        setContentView(binding.root)

        var list_of_items = arrayOf("이등병", "일병", "상병", "병장", "기타")
        var list_of_summary = arrayOf("이등병","일병","상병", "병장", "기타")

        //어답터 설정 - 안드로이드에서 제공하는 어답터를 연결
        binding.spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_of_items)

        //아이템 선택 리스너
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                MyApplication.prefs.setString("class", list_of_summary[position])
                //Log.d("hjg", MyApplication.prefs.getString("class", "no email"))
            }
        }
        //시작일, 종료일 데이터 저장
        val calendar:Calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        datePicker = findViewById<View>(R.id.datePicker) as Button
        edit_endDateBtn = findViewById<View>(R.id.edit_endDateBtn) as TextView
        edit_result = findViewById<View>(R.id.edit_result) as TextView

        //한국어 설정 (ex: date picker)
        Locale.setDefault(Locale.KOREAN)

        // 디데이 날짜 입력
        edit_endDateBtn!!.text =
            currentYear.toString() + "년 " + (currentMonth + 1) + "월 " + currentDay + "일"

        //datePicker : 디데이 날짜 입력하는 버튼, 클릭시 DatePickerDialog 띄우기
        datePicker!!.setOnClickListener {
            DatePickerDialog(
                this@DdaySeettingActivity2,
                endDateSetListener,
                currentYear,
                currentMonth,
                currentDay
            ).show()
        }
    }

    /** @brief endDateSetListener
     * @date 2016-02-18
     * @detail DatePickerDialog띄우기, 종료일 저장, 기존에 입력한 값이 있으면 해당 데이터 설정후 띄우기
     */
    private val endDateSetListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            edit_endDateBtn!!.text =
                year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일"
            ddayValue = ddayResult_int(dateEndY, dateEndM, dateEndD)
            edit_result!!.text = getDday(year, monthOfYear, dayOfMonth)
            MyApplication.prefs.setString("year", year.toString())
            if(monthOfYear + 1 < 10){
                MyApplication.prefs.setString("month", "0" + (monthOfYear + 1).toString())
            }
            else{
                MyApplication.prefs.setString("month", (monthOfYear + 1).toString())
            }
            if(dayOfMonth < 10){
                MyApplication.prefs.setString("day", "0" + dayOfMonth.toString())
            }
            else{
                MyApplication.prefs.setString("day", dayOfMonth.toString())
            }
            MyApplication.prefs.setString("date",  year.toString() + "년 " + (monthOfYear + 1).toString() + "월 " + (dayOfMonth.toString() + "일"))

        }

    /** @brief getDday
     * @date 2016-02-18
     * @param mYear : 설정한 디데이 year, mMonthOfYear : 설정한 디데이 MonthOfYear, mDayOfMonth : 설정한 디데이 DayOfMonth
     * @detail D-day 반환
     */
    private fun getDday(
        mYear: Int,
        mMonthOfYear: Int,
        mDayOfMonth: Int
    ): String {
        // D-day 설정
        val ddayCalendar = Calendar.getInstance()
        ddayCalendar[mYear, mMonthOfYear] = mDayOfMonth

        // D-day 를 구하기 위해 millisecond 으로 환산하여 d-day 에서 today 의 차를 구한다.
        val dday = ddayCalendar.timeInMillis / ONE_DAY
        val today = Calendar.getInstance().timeInMillis / ONE_DAY
        var result = dday - today

        // 출력 시 d-day 에 맞게 표시
        val strFormat: String
        if (result > 0) {
            strFormat = "D-%d"
        } else if (result == 0L) {
            strFormat = "Today"
        } else {
            result *= -1
            strFormat = "D+%d"
        }
        return String.format(strFormat, result)
    }

    /** @brief onPhotoDialog
     * @date 2016-02-18
     * @detail 디데이 값 계산
     */
    fun onCalculatorDate(dateEndY: Int, dateEndM: Int, dateEndD: Int): Int {
        return try {
            val today = Calendar.getInstance() //현재 오늘 날짜
            val dday = Calendar.getInstance()

            //시작일, 종료일 데이터 저장
            val calendar = Calendar.getInstance()
            val cyear = calendar[Calendar.YEAR]
            val cmonth = calendar[Calendar.MONTH] + 1
            val cday = calendar[Calendar.DAY_OF_MONTH]
            today[cyear, cmonth] = cday
            dday[dateEndY, dateEndM] = dateEndD // D-day의 날짜를 입력합니다.
            val day = dday.timeInMillis / 86400000
            // 각각 날의 시간 값을 얻어온 다음
            //( 1일의 값(86400000 = 24시간 * 60분 * 60초 * 1000(1초값) ) )
            val tday = today.timeInMillis / 86400000
            val count = tday - day // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
            count.toInt() // 날짜는 하루 + 시켜줘야합니다.
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            -1
        }
    }

    /** @brief ddayResult_int
     * @date 2016-02-18
     * @detail 디데이 값 계산한 값 결과값 출력
     * Todo 함수 오류 수정
     */
    fun ddayResult_int(dateEndY: Int, dateEndM: Int, dateEndD: Int): Int {
        var result = 0
        result = onCalculatorDate(dateEndY, dateEndM, dateEndD)
        return result
    }
}