package com.example.gurutest

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate

//알바 목록화면
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var mainWageTv: TextView
    lateinit var lay_panel1: LinearLayout
    lateinit var lay_panel2: LinearLayout
    lateinit var btn_delete1: ImageButton
    lateinit var btn_delete2: ImageButton
    lateinit var work_totalWage1: TextView
    lateinit var work_totalWage2: TextView

    private var t_workname1 = ""     // 근무지명
    private var t_workname2 = ""
    private var t_wage1 = 0          // 근무지 시급
    private var t_wage2 = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainWageTv = findViewById(R.id.mainWageTv)
        lay_panel1 = findViewById(R.id.lay_panel1)
        lay_panel2 = findViewById(R.id.lay_panel2)
        btn_delete1 = findViewById(R.id.btn_delete1)
        btn_delete2 = findViewById(R.id.btn_delete2)
        work_totalWage1 = findViewById(R.id.work_totalWage1)
        work_totalWage2 = findViewById(R.id.work_totalWage2)

        // 처음에는 근무지 레이아웃 안보이게
        lay_panel1.visibility = View.INVISIBLE
        lay_panel2.visibility = View.INVISIBLE

        // 근무 스톱워치 실행 -> 스톱워치로 이동
        btn_gowork1.setOnClickListener {
            var intent = Intent(this, StopWatchActivity::class.java)
            // 인텐트에 근무지명 정보 전달
            intent.putExtra("name", tv_workname1.text.toString())
            startActivity(intent)
        }
        btn_gowork2.setOnClickListener {
            var intent = Intent(this, StopWatchActivity::class.java)
            intent.putExtra("name", tv_workname2.text.toString())
            startActivity(intent)
        }


        //햄버거 메뉴 클릭
        btn_navi.setOnClickListener{
            layout_drawer.openDrawer(GravityCompat.END)
        }

        //새알바 등록(+)
        btn_newjob.setOnClickListener{
            startActivity(Intent(this, NewJob::class.java))
        }


        btn_calendar.setOnClickListener{
            startActivity(Intent(this, CalenderActivity::class.java))
        }

        naviView.setNavigationItemSelectedListener(this)

        //툴바 설정
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)

        // 메인 화면 총액 표시
        dbManager = DBManager(this, "calDB", null, 1)

        var month = "00"        // 월
        var str = ""
        var totalWage = 0       // 일당

        // 월 두자리 수로 맞추기 (예.07)
        if(LocalDate.now().monthValue < 10){
            month = "0" + LocalDate.now().monthValue
        }else {
            month = LocalDate.now().monthValue.toString()
        }
        str = LocalDate.now().year.toString() + month    // 년, 월

        // 이번 달 번 총액 표시 (년, 월을 이용해 DB 조회)
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "';", null)
        while(cursor.moveToNext()){
            // 해당 년, 월의 일당을 다 더함
            totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }
        // UI 변경
        mainWageTv.text = totalWage.toString()

        // 알바 패널에 정보 표시 (근무지명)
        cursor = sqlitedb.rawQuery("SELECT * FROM workTBL;", null)
        var i =0
        while(cursor.moveToNext()){
            // 알바를 최대 2개라고 가정해서 알바별로 다른 변수에 정보를 담음
            if(i==0){
                // 근무지명, 시급을 읽어옴
                t_workname1 = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                tv_workname1.text = t_workname1
                t_wage1 = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()

                //알바 패널 보이게
                lay_panel1.visibility = View.VISIBLE

            }else{
                t_workname2 = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                tv_workname2.text = t_workname2
                t_wage2 = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()

                lay_panel2.visibility = View.VISIBLE
            }
            i++
        }
        i=0

        // 근무지별 이번 달 총액 표시
        var work_totalWage = 0

        // 첫번째 근무지 총액
        cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "' AND name = '" + tv_workname1.text.toString() + "';", null)
        while(cursor.moveToNext()){
            // 해당하는 DB의 일당을 다 더함
            work_totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }
        // 텍스트뷰 변경
        work_totalWage1.text = "$work_totalWage" + "원"
        // 초기화
        work_totalWage = 0

        // 두번째 근무지 총액
        cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "' AND name = '" + tv_workname2.text.toString() + "';", null)
        while(cursor.moveToNext()){
            work_totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }
        // 텍스트뷰 변경
        work_totalWage2.text = "$work_totalWage" + "원"
        // 초기화
        work_totalWage = 0

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 삭제 버튼 클릭 시
        btn_delete1.setOnClickListener {

            // 근무지 이름
            var name = tv_workname1.text.toString()

            // 테이블에서 해당 근무지 정보를 삭제
            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM workTBL WHERE name = '" + name + "';")
            sqlitedb.execSQL("DELETE FROM calTBL WHERE name = '" + name + "';")

            sqlitedb.close()
            dbManager.close()

            // 해당 레이아웃을 안보이게
            lay_panel1.visibility = View.GONE
        }

        // 삭제 버튼 클릭 시
        btn_delete2.setOnClickListener {

            // 근무지 이름
            var name = tv_workname2.text.toString()

            // 테이블에서 해당 근무지 정보를 삭제
            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM workTBL WHERE name = '" + name + "';")
            sqlitedb.execSQL("DELETE FROM calTBL WHERE name = '" + name + "';")

            sqlitedb.close()
            dbManager.close()

            // 해당 레이아웃을 안보이게
            lay_panel2.visibility = View.GONE

        }
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