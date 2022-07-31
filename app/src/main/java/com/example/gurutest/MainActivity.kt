package com.example.gurutest

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

//알바 목록화면
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 근무 스톱워치 실행
        btn_gowork1.setOnClickListener {
            var intent = Intent(this, StopWatchActivity::class.java)
            startActivity(intent)
        }
        btn_gowork2.setOnClickListener {
            var intent = Intent(this, StopWatchActivity::class.java)
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

        dbManager = DBManager(this, "workDB", null, 1)
        sqlitedb = dbManager.readableDatabase


        //새 알바에서 근무지를 등록했을때 항목이 추가됨
        if(intent.hasExtra("workname")){
            //근무지 이름 가져오기
            val str_workname = intent.getStringExtra("workname")
            val str_wage = intent.getStringExtra("wage")

            var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM workTBL WHERE name = '" + str_workname + "';", null)

            var i =0

            while(cursor.moveToNext()){
                if(i==0){
                   var t_workname1 = cursor.getString(cursor.getColumnIndexOrThrow("str_workname")).toString()
                    tv_workname1.text = t_workname1
                    var t_wage1 = cursor.getString(cursor.getColumnIndexOrThrow("str_wage")).toString()
                    //알바 패널 보이게
                    lay_panel1.visibility=View.VISIBLE


                }else{
                    var t_workname2 = cursor.getString(cursor.getColumnIndexOrThrow("str_workname")).toString()
                    tv_workname2.text = t_workname2
                    var t_wage2 = cursor.getString(cursor.getColumnIndexOrThrow("str_wage")).toString()
                    lay_panel2.visibility=View.VISIBLE
                }
                i++

            }
            i=0


            cursor.close()
            sqlitedb.close()
            dbManager.close()
        }





        val tv_paysum =0 // 총 번돈 표시
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