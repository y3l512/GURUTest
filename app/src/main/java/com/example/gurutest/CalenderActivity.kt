package com.example.gurutest

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.example.gurutest.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_calender.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btn_navi
import kotlinx.android.synthetic.main.activity_main.layout_drawer
import java.time.LocalDate

class CalenderActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var calenderView: CalendarView
    lateinit var totalWageTv: TextView
    lateinit var layout: LinearLayout
    lateinit var yearMonDayTv: TextView
    lateinit var dayWageTv: TextView
    lateinit var updateBtn: Button

    private var startTime = "0"       // 근무 시작 시간(hour)
    private var endTime = "0"         // 근무 종료 시간(hour)
    private var startMin30 = 0        // 근무 시작 시간(이 30분 단위인지
    private var endMin30 = 0          // 근무 종료 시간이 30분 단위인지
    private var startTime2 = "0"      // 하루에 알바를 두 번 했을 경우를 대비한 변수들(하루에 알바 최대 두개로 가정하고 구현)
    private var endTime2 = "0"
    private var startMin302 = 0
    private var endMin302 = 0
    private var dayWage = 0           // 하루에 번 돈
    private var totalWage = 0         // 이번달에 번 돈

    // UpdateActivity에 인텐트로 보낼 문자열
    private var str_yearMonthDay = ""
    private var str_yearMonth = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        calenderView = findViewById(R.id.calendarView)
        totalWageTv = findViewById(R.id.totalWageTv)
        layout = findViewById(R.id.layout)
        yearMonDayTv = findViewById(R.id.yearMonDayTv)
        dayWageTv = findViewById(R.id.dayWageTv)
        updateBtn = findViewById(R.id.updateBtn)

        // 아래 날짜별 돈을 표시해주는 레이아웃을 처음에는 보이지 않게함
        layout.visibility = View.GONE

        dbManager = DBManager(this, "calDB", null, 1)

        // ↓↓↓이번 달 총액 표시
        var month = ""   // 월
        var str = ""
        // 오늘 월 두자리 수로 맞추기 (예.07)
        if(LocalDate.now().monthValue < 10){
            month = "0" + LocalDate.now().monthValue
        }else {
            month = LocalDate.now().monthValue.toString()
        }
        // DB에서 오늘 년,월을 이용해 정보를 검색하기 위한 년,월 문자열
        str = LocalDate.now().year.toString() + month
        str_yearMonth = str

        // DB에서 해당 년, 월 정보 가져오기
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "';", null)
        while(cursor.moveToNext()){
            // 해당 년, 월의 일당을 다 합침
            totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }
        // UI  변경
        totalWageTv.text = totalWage.toString()

        cursor.close()
        sqlitedb.close()
        dbManager.close()



        // 캘린더 뷰 날짜들이 눌렸을 때, 해당 년, 월, 일에 해당하는 정보를 가져옴
        calenderView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            // 월, 일 두자리수로 맞추기(예. 7 -> 07)
            var monthMM = ""
            var daydd = ""

            // 월
            if(month < 10){
                monthMM = "0" + (month + 1)
            }else {
                monthMM = (month + 1).toString()
            }

            // 일
            if(dayOfMonth < 10){
                daydd = "0" + dayOfMonth
            }else {
                daydd = dayOfMonth.toString()
            }

            // 년, 월, 일 문자열
            str_yearMonthDay = year.toString() + monthMM + daydd

            // DB에서 정보 조회 (해당 년, 월, 일)
            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE date = '" + str_yearMonthDay + "';", null)

            var i = 0
            while(cursor.moveToNext()){
                // 처음 시간과 두번째 시간을 나눠서 띄움(하루에 알바를 두 개 했을 경우를 대비)
                if(i == 0){
                    startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                    endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                    startMin30 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                    endMin30 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                }else{
                    startTime2 = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                    endTime2 = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                    startMin302 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                    endMin302 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                }

                // 오늘 번 돈 총액
                dayWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
                i++
            }

            cursor.close()
            sqlitedb.close()
            dbManager.close()

            // UI 업데이트
            layout.visibility = View.VISIBLE   // 액티비티 실행 시 안보이게 했던 레이아웃을 이제는 보이게 함
            yearMonDayTv.text = "$year 년 ${month+1}월 $dayOfMonth 일"
            dayWageTv.text = dayWage.toString() + "원"

            // 변수 초기화
            startTime = "0"
            endTime = "0"
            startMin30 = 0
            endMin30 = 0
            dayWage = 0
        }

        // 수정 버튼 클릭 시
        updateBtn.setOnClickListener {
            if(layout.visibility == View.VISIBLE){

                // UpdateActivity에 인텐트를 보냄
                var intent = Intent(this, UpdateActivity::class.java)
                intent.putExtra("yearMonthDay", str_yearMonthDay)
                intent.putExtra("yearMonth", str_yearMonth)
                startActivity(intent)
            }
        }

        // 메뉴 버튼을 눌렀을 시
        btn_navi.setOnClickListener{
            layout_drawer.openDrawer(GravityCompat.END)
        }

        // 메인 화면(근무 목록)으로 이동 버튼 눌렀을 시
        btn_main.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        naviView1.setNavigationItemSelectedListener(this)

    }


    //햄버거 메뉴의 메뉴들 클릭 시
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            //시급 계산기로
            R.id.calculator-> startActivity(Intent(this, Calculator2::class.java))

        }
        layout_drawer.closeDrawers()
        return false
    }

    //햄버거 메뉴를 클릭한 후 하단 뒤로가기를 눌렀을 때 앱이 종료되지 않고 메인으로 돌아가게함
    override fun onBackPressed() {
        if (layout_drawer.isDrawerOpen(GravityCompat.END)) {
            layout_drawer.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }


}