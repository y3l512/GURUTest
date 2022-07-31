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
//    lateinit var timeTv: TextView
//    lateinit var timeTv2: TextView
    lateinit var dayWageTv: TextView
    lateinit var updateBtn: Button

    private var startTime = "0"       // 근무 시작 시간(hour)
    private var endTime = "0"         // 근무 종료 시간(hour)
    private var startMin30 = 0        // 근무 시작 시간(이 30분 단위인지
    private var endMin30 = 0          // 근무 종료 시간이 30분 단위인지
    private var startTime2 = "0"      // 하루에 알바를 두 번 했을 경우를 대비한 변수들
    private var endTime2 = "0"
    private var startMin302 = 0
    private var endMin302 = 0
    private var dayWage = 0           // 하루에 번 돈
    private var totalWage = 0         // 이번달에 번 돈

    // 시간 수정 대화상자에 보낼 문자열
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
//        timeTv = findViewById(R.id.timeTv)
//        timeTv2 = findViewById(R.id.timeTv2)
        dayWageTv = findViewById(R.id.dayWageTv)
        updateBtn = findViewById(R.id.updateBtn)

        layout.visibility = View.GONE

        dbManager = DBManager(this, "calDB", null, 1)

        var month = ""   // 월
        var str = ""     // DB 조회 시 사용할 문자열(코드 62줄)

        // 월 두자리 수로 맞추기 (예.07)
        if(LocalDate.now().monthValue < 10){
            month = "0" + LocalDate.now().monthValue
        }else {
            month = LocalDate.now().monthValue.toString()
        }
        str = LocalDate.now().year.toString() + month
        str_yearMonth = str

        // 이번 달 번 총액 표시
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "';", null)

        while(cursor.moveToNext()){
            totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }

        totalWageTv.text = totalWage.toString()

//        cursor = sqlitedb.rawQuery("SELECT * FROM workTBL;", null)
//
//        while(cursor.moveToNext()){
//            Toast.makeText(applicationContext, cursor.getString(cursor.getColumnIndexOrThrow("name")).toString(),Toast.LENGTH_SHORT).show()
//            Toast.makeText(applicationContext, cursor.getString(cursor.getColumnIndexOrThrow("wage")).toString(),Toast.LENGTH_SHORT).show()
//        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 캘린더 뷰 날짜들이 눌렸을 때
        calenderView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            // 알바를 하루에 두 개 했을 경우를 대비한 두번째 시간 변수를 일단 안보이게 함
//            timeTv2.visibility = View.GONE

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

            str_yearMonthDay = year.toString() + monthMM + daydd

            // DB에서 정보 조회
            sqlitedb = dbManager.readableDatabase
            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE date = '" + year.toString() + monthMM + daydd + "';", null)

            var i = 0
            while(cursor.moveToNext()){
                // 처음 시간과 두번째 시간을 나눠서 띄움(하루에 알바를 두 개 했을 경우를 대비)
                if(i == 0){
                    startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                    endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                    startMin30 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                    endMin30 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                }else{
//                    timeTv2.visibility = View.VISIBLE
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
            layout.visibility = View.VISIBLE
            yearMonDayTv.text = "$year 년 ${month+1}월 $dayOfMonth 일"
            dayWageTv.text = dayWage.toString() + "원"

            // 일한 시간 텍스트 초기화
//            if(startMin30 == 1){
//                if(endMin30 == 1){
//                    timeTv.text = startTime + "시 30분 - " + endTime + "시 30분"
//                }else {
//                    timeTv.text = startTime + "시 30분 - " + endTime + "시"
//                }
//            }else {
//                if(endMin30 == 1){
//                    timeTv.text = startTime + "시 - " + endTime + "시 30분"
//                }else {
//                    timeTv.text = startTime + "시 - " + endTime + "시"
//                }
//            }
//
//            if(startMin302 == 1){
//                if(endMin302 == 1){
//                    timeTv2.text = startTime2 + "시 30분 - " + endTime2 + "시 30분"
//                }else {
//                    timeTv2.text = startTime2 + "시 30분 - " + endTime2 + "시"
//                }
//            }else {
//                if(endMin302 == 1){
//                    timeTv2.text = startTime2 + "시 - " + endTime2 + "시 30분"
//                }else {
//                    timeTv2.text = startTime2 + "시 - " + endTime2 + "시"
//                }
//            }

            // 변수 초기화
            startTime = "0"
            endTime = "0"
            startMin30 = 0
            endMin30 = 0
            dayWage = 0
        }

        updateBtn.setOnClickListener {
            if(layout.visibility == View.VISIBLE){

                var intent = Intent(this, DialogActivity::class.java)
                intent.putExtra("yearMonthDay", str_yearMonthDay)
                intent.putExtra("yearMonth", str_yearMonth)
                startActivity(intent)
            }
        }

        btn_navi.setOnClickListener{
            layout_drawer.openDrawer(GravityCompat.END)
        }

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