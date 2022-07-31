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

    private var t_workname1 = ""
    private var t_workname2 = ""
    private var t_wage1 = 0
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

        lay_panel1.visibility = View.INVISIBLE
        lay_panel2.visibility = View.INVISIBLE

        // 근무 스톱워치 실행
        btn_gowork1.setOnClickListener {
            var intent = Intent(this, StopWatchActivity::class.java)
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


        //새 알바에서 근무지를 등록했을때 항목이 추가됨
//        if(intent.hasExtra("workname")){
            //근무지 이름 가져오기
//            val str_workname = intent.getStringExtra("workname")
//            val str_wage = intent.getStringExtra("wage")

//        }



        //val tv_paysum =0 // 총 번돈 표시


        // 메인 화면 총액 표시
        dbManager = DBManager(this, "calDB", null, 1)

        var month = "00"
        var str = ""
        var totalWage = 0

        // 월 두자리 수로 맞추기 (예.07)
        if(LocalDate.now().monthValue < 10){
            month = "0" + LocalDate.now().monthValue
        }else {
            month = LocalDate.now().monthValue.toString()
        }
        str = LocalDate.now().year.toString() + month

        // 이번 달 번 총액 표시
        sqlitedb = dbManager.readableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE yearMonth = '" + str + "';", null)

        while(cursor.moveToNext()){
            totalWage += cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
        }

        mainWageTv.text = totalWage.toString()

        cursor = sqlitedb.rawQuery("SELECT * FROM workTBL;", null)

        var i =0
        while(cursor.moveToNext()){
            if(i==0){
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


        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 삭제 버튼
        btn_delete1.setOnClickListener {

            var name = tv_workname1.text.toString()

            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM workTBL WHERE name = '" + name + "';")
            sqlitedb.execSQL("DELETE FROM calTBL WHERE name = '" + name + "';")

            sqlitedb.close()
            dbManager.close()

            lay_panel1.visibility = View.GONE
        }

        // 삭제 버튼
        btn_delete2.setOnClickListener {

            var name = tv_workname2.text.toString()

            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("DELETE FROM workTBL WHERE name = '" + name + "';")
            sqlitedb.execSQL("DELETE FROM calTBL WHERE name = '" + name + "';")

            sqlitedb.close()
            dbManager.close()

            lay_panel2.visibility = View.GONE

        }
    }


    //햄버거 메뉴의 메뉴들 클릭 시
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            //시급 계산기로
            R.id.calculator-> startActivity(Intent(this, Calculator2::class.java))
            //새알바 등록
            R.id.widgetsetting-> Toast.makeText(applicationContext,"위젯설정으로 이동", Toast.LENGTH_SHORT).show()
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