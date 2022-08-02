package com.example.gurutest

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.gurutest.R

// 근무 시간 수정 액티비티
class UpdateActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 수정 된 시간을 담을 변수들
    var updateStartTime1 = ""
    var updateEndTime1 = ""
    var updateStartMin301 = 0
    var updateEndMin301 = 0
    var updateStartTime2 = ""
    var updateEndTime2 = ""
    var updateStartMin302 = 0
    var updateEndMin302 = 0
    var workName1 = ""
    var workName2 = ""
    var workWage1 = 0
    var workWage2 = 0

    // 수정 전 일당을 담을 변수
    var originalWage1 = 0
    var originalWage2 = 0

    lateinit var layoutInDialog1: LinearLayout
    lateinit var layoutInDialog2: LinearLayout
    lateinit var hourEdit1_1: EditText
    lateinit var hourEdit1_2: EditText
    lateinit var hourEdit2_1: EditText
    lateinit var hourEdit2_2: EditText
    lateinit var minEdit1_1 : EditText
    lateinit var minEdit1_2 : EditText
    lateinit var minEdit2_1 : EditText
    lateinit var minEdit2_2 : EditText
    lateinit var updatebtn1 : Button
    lateinit var updatebtn2 : Button
    lateinit var deletebtn1 : Button
    lateinit var deletebtn2 : Button
    lateinit var backBtn: Button
    lateinit var nameTv1 : TextView
    lateinit var nameTv2 : TextView
    lateinit var dayWageTv1 : TextView
    lateinit var dayWageTv2 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        layoutInDialog1 = findViewById(R.id.layoutInDialog1)
        layoutInDialog2 = findViewById(R.id.layoutInDialog2)
        hourEdit1_1= findViewById(R.id.hourEdt1_1)
        hourEdit1_2 = findViewById(R.id.hourEdt1_2)
        hourEdit2_1 = findViewById(R.id.hourEdt2_1)
        hourEdit2_2 = findViewById(R.id.hourEdt2_2)
        minEdit1_1 = findViewById(R.id.minEdt1_1)
        minEdit1_2 = findViewById(R.id.minEdt1_2)
        minEdit2_1 = findViewById(R.id.minEdt2_1)
        minEdit2_2 = findViewById(R.id.minEdt2_2)
        updatebtn1 = findViewById(R.id.updatebtn1)
        updatebtn2 = findViewById(R.id.updatebtn2)
        deletebtn1 = findViewById(R.id.deletebtn1)
        deletebtn2 = findViewById(R.id.deletebtn2)
        backBtn = findViewById(R.id.dlgbackBtn)
        nameTv1 = findViewById(R.id.nameTv1)
        nameTv2 = findViewById(R.id.nameTv2)
        dayWageTv1 = findViewById(R.id.dayWage1)
        dayWageTv2 = findViewById(R.id.dayWage2)

        // 두번째 레이아웃은 처음에 보이지 않게 함
        layoutInDialog2.visibility = View.GONE

        dbManager = DBManager(this, "calDB", null, 1)

        // 인텐트 정보 읽어오기(DB 검색, 입력 시 사용)
        var str_yearMonthDay = intent.getStringExtra("yearMonthDay")
        var str_yearMonth = intent.getStringExtra("yearMonth")

        // DB로 정보 읽어오기(년, 월, 일)
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE date = '" + str_yearMonthDay + "';", null)
        var i = 0
        // DB의 정보를 읽어옴(하루에 최대 알바 2개)
        while(cursor.moveToNext()){
            if(i == 0){
                workName1 = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                workWage1 = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()
                updateStartTime1 = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                updateEndTime1 = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                updateStartMin301 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                updateEndMin301 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                originalWage1 = cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
            }else{
                // 알바가 두 개일 시 보이지 않게 했던 레이아웃 보이게 함
                layoutInDialog2.visibility = View.VISIBLE
                workName2 = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                workWage2 = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()
                updateStartTime2 = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                updateEndTime2 = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                updateStartMin302 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                updateEndMin302 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                originalWage2 = cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
            }
            i++
        }
        i = 0  //초기화

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        // 근무지명 텍스트 변경
        nameTv1.text = workName1
        nameTv2.text = workName2

        // 금액 텍스트 바꾸기 (일당)
        // 데이터가 있는 경우엔 일당 계산, 데이터가 없는 경우엔 0원으로 표시
        if(updateStartTime1 != ""){
            dayWageTv1.text = "" + dayWage(workWage1, updateStartTime1, updateEndTime1, updateStartMin301, updateEndMin301) + "원"
        }else{ dayWageTv1.text = "0원" }
        if(updateStartTime2 != ""){
            dayWageTv2.text = "" + dayWage(workWage2, updateStartTime2, updateEndTime2, updateStartMin302, updateEndMin302) + "원"
        }else{ dayWageTv2.text = "0원" }

        // EditText 힌트 값 바꾸기 (수정 전 DB에 들어있는 근무 시간)
        // 첫번째 아르바이트 변수들
        if(updateStartTime1 == ""){
            hourEdit1_1.hint = "0"
        }else { hourEdit1_1.hint = updateStartTime1 }
        if(updateEndTime1 == ""){
            hourEdit1_2.hint = "0"
        }else { hourEdit1_2.hint = updateEndTime1 }
        if(updateStartMin301 == 1){
            minEdit1_1.hint = "30"
        }else { minEdit1_1.hint = "0" }
        if(updateEndMin301 == 1){
            minEdit1_2.hint = "30"
        }else { minEdit1_2.hint = "0" }

        // 두번째 아르바이트 변수들
        if(updateStartTime2 == ""){
            hourEdit2_1.hint = "0"
        }else { hourEdit2_1.hint = updateStartTime2 }
        if(updateEndTime2 == ""){
            hourEdit2_2.hint = "0"
        }else { hourEdit2_2.hint = updateEndTime2 }
        if(updateStartMin302 == 1){
            minEdit2_1.hint = "30"
        }else { minEdit2_1.hint = "0" }
        if(updateEndMin302 == 1){
            minEdit2_2.hint = "30"
        }else { minEdit2_2.hint = "0" }


        // 수정버튼이 눌렸을 시
        updatebtn1.setOnClickListener {

            // 업데이트 될 시간 읽어오기
            updateStartTime1 = hourEdit1_1.text.toString()
            updateEndTime1 = hourEdit1_2.text.toString()
            if(minEdit1_1.text.toString() == "30"){
                updateStartMin301 = 1
            }else if(minEdit1_1.text.toString() == "0" || minEdit1_1.text.toString() == "00"){
                updateStartMin301 = 0
            }else{
                Toast.makeText(this, "분에는 0 또는 30만 들어갈 수 있습니다.", Toast.LENGTH_LONG).show()
            }
            if(minEdit1_2.text.toString() == "30"){
                updateEndMin301 = 1
            }else if(minEdit1_2.text.toString() == "0" || minEdit1_2.text.toString() == "00"){
                updateEndMin301 = 0
            }else{
                Toast.makeText(this, "분에는 0 또는 30만 들어갈 수 있습니다.", Toast.LENGTH_LONG).show()
            }

            // 일당 계산
            var todayWage = dayWage(workWage1, updateStartTime1, updateEndTime1, updateStartMin301, updateEndMin301)

            // 금액 텍스트 변경
            dayWageTv1.text = "$todayWage" + "원"

            // DB 수정
            sqlitedb = dbManager.writableDatabase
            // 키 값이 없어서 같은 날 알바가 두개면 둘 다 삭제될 것이므로 다시 두개 다 입력
            sqlitedb.execSQL("DELETE FROM calTBL WHERE date = '" + str_yearMonthDay + "';")

            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName1 + "', " + workWage1 + ", '" + str_yearMonthDay
                    + "', '" + str_yearMonth
                    + "', '" + updateStartTime1 + "', '" + updateEndTime1 + "', " + updateStartMin301 + ", " + updateEndMin301 + ", " + todayWage + ");")
            originalWage1 = todayWage
            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName2 + "', " + workWage2 + ", '" + str_yearMonthDay
                    + "', '" + str_yearMonth
                    + "', '" + updateStartTime2 + "', '" + updateEndTime2 + "', " + updateStartMin302 + ", " + updateEndMin302 + ", " + originalWage2 + ");")


            sqlitedb.close()
            dbManager.close()

            Toast.makeText(applicationContext, "수정되었습니다.", Toast.LENGTH_SHORT).show()
        }


        // 수정버튼이 눌렸을 시(updatebtn1과 동작은 똑같고 변수만 다름)
        updatebtn2.setOnClickListener {

            // 업데이트 될 시간 읽어오기
            updateStartTime2 = hourEdit2_1.text.toString()
            updateEndTime2 = hourEdit2_2.text.toString()
            if(minEdit2_1.text.toString() == "30"){
                updateStartMin302 = 1
            }else if(minEdit2_1.text.toString() == "0" || minEdit2_1.text.toString() == "00"){
                updateStartMin302 = 0
            }else{
                Toast.makeText(this, "분에는 0 또는 30만 들어갈 수 있습니다.", Toast.LENGTH_LONG).show()
            }
            if(minEdit2_2.text.toString() == "30"){
                updateEndMin302 = 1
            }else if(minEdit2_2.text.toString() == "0" || minEdit2_2.text.toString() == "00"){
                updateEndMin302 = 0
            }else{
                Toast.makeText(this, "분에는 0 또는 30만 들어갈 수 있습니다.", Toast.LENGTH_LONG).show()
            }

            // 일당 계산
            var todayWage = dayWage(workWage2, updateStartTime2, updateEndTime2, updateStartMin302, updateEndMin302)

            // 금액 텍스트 변경
            dayWageTv2.text = "$todayWage" + "원"

            // DB 수정
            sqlitedb = dbManager.writableDatabase
            // 키 값이 없어서 같은 날 알바가 두개면 둘 다 삭제될 것이므로 다시 두개 다 입력
            sqlitedb.execSQL("DELETE FROM calTBL WHERE date = '" + str_yearMonthDay + "';")

            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName1 + "', " + workWage1 + ", '" + str_yearMonthDay
                    + "', '" + str_yearMonth
                    + "', '" + updateStartTime1 + "', '" + updateEndTime1 + "', " + updateStartMin301 + ", " + updateEndMin301 + ", " + originalWage1 + ");")

            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName2 + "', " + workWage2 + ", '" + str_yearMonthDay
                    + "', '" + str_yearMonth
                    + "', '" + updateStartTime2 + "', '" + updateEndTime2 + "', " + updateStartMin302 + ", " + updateEndMin302 + ", " + todayWage + ");")
            originalWage2 = todayWage

            sqlitedb.close()
            dbManager.close()

            Toast.makeText(applicationContext, "수정되었습니다.", Toast.LENGTH_SHORT).show()

        }

        // 뒤로가기 버튼 클릭 시
        backBtn.setOnClickListener {
            // 캘린더 액티비티로 이동
            var intent = Intent(this, CalenderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 일바 급여 계산 (일당)
    private fun dayWage(hourWage: Int, updateStartTime: String, updateEndTime: String
                        , updateStartMin30: Int, updateEndMin30: Int): Int {

        var todayWage = 0     // 일당
        var workHour = 0      // 일한 시간
        var workMin = 0       // 일한 시간(분)

        // 시간 계산
        if((updateEndTime.toInt() - updateStartTime.toInt()) >= 0){
            workHour = updateEndTime.toInt() - updateStartTime.toInt()
        }else {
            workHour = (24 - updateStartTime.toInt()) + updateEndTime.toInt()
        }

        // 분 계산
        if((updateStartMin30 == 1 && updateEndMin30 == 1) || (updateStartMin30 == 0 && updateEndMin30 == 0)){
            workMin = 0
        }else if(updateStartMin30 == 0 && updateEndMin30 == 1){
            workMin = 30
        }else {
            // updateStartMin301 == 1 && updateEndMin301 == 0
            workMin = 1
        }

        // 일당 계산
        if(workMin == 0){
            todayWage = workHour * hourWage
        }else if(workMin == 30) {
            todayWage = workHour * hourWage + hourWage/2
        }else{
            //workMin == 1
            todayWage = workHour*hourWage - hourWage/2
        }

        // 일당 반환
        return todayWage
    }
}