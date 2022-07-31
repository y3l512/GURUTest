package com.example.gurutest

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.gurutest.R

// 대화상자로 구현하려다가
class DialogActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    // 시간
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
        setContentView(R.layout.activity_dialog)

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

        // 레이아웃 초기화
        layoutInDialog2.visibility = View.GONE

        dbManager = DBManager(this, "calDB", null, 1)

        // 인텐트 정보 읽어오기
        var str_yearMonthDay = intent.getStringExtra("yearMonthDay")
        var str_yearMonth = intent.getStringExtra("yearMonth")

        // DB로 정보 읽어오기
        sqlitedb = dbManager.readableDatabase
        var cursor: Cursor = sqlitedb.rawQuery("SELECT * FROM calTBL WHERE date = '" + str_yearMonthDay + "';", null)

        var i = 0
        while(cursor.moveToNext()){
            // DB의 정보를 읽어옴(하루에 최대 알바 2개)
            if(i == 0){
                workName1 = cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()
                workWage1 = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()
                updateStartTime1 = cursor.getString(cursor.getColumnIndexOrThrow("startTime")).toString()
                updateEndTime1 = cursor.getString(cursor.getColumnIndexOrThrow("endTime")).toString()
                updateStartMin301 = cursor.getString(cursor.getColumnIndexOrThrow("startMin30")).toInt()
                updateEndMin301 = cursor.getString(cursor.getColumnIndexOrThrow("endMin30")).toInt()
                originalWage1 = cursor.getString(cursor.getColumnIndexOrThrow("dayWage")).toInt()
            }else{
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

        // 근무지명 바꾸기
        nameTv1.text = workName1
        nameTv2.text = workName2

        Toast.makeText(applicationContext, "updateStartTime1: $updateStartTime1", Toast.LENGTH_SHORT).show()
        Toast.makeText(applicationContext, "updateStartTime2: $updateStartTime2", Toast.LENGTH_SHORT).show()
        // 근무지 급여 바꾸기(하루 단위)
        if(updateStartTime1 != ""){
            dayWageTv1.text = "" + dayWage(workWage1, updateStartTime1, updateEndTime1, updateStartMin301, updateEndMin301) + "원"
        }else{ dayWageTv1.text = "0원" }
        if(updateStartTime2 != ""){
            dayWageTv2.text = "" + dayWage(workWage2, updateStartTime2, updateEndTime2, updateStartMin302, updateEndMin302) + "원"
        }else{ dayWageTv2.text = "0원" }

        // EditText 힌트 값 바꾸기 (수정 전 시간)
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

        updatebtn1.setOnClickListener {

            // 에디트 텍스트 값 읽어오기
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


        updatebtn2.setOnClickListener {

            // 에디트 텍스트 값 읽어오기
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


//        deletebtn1.setOnClickListener {
//
//            sqlitedb = dbManager.writableDatabase
//
//            sqlitedb.execSQL("DELETE FROM calTBL WHERE date = '" + str_yearMonthDay + "';")
//            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName2 + "', " + workWage2 + ", '" + str_yearMonthDay
//                    + "', '" + str_yearMonth
//                    + "', '" + updateStartTime2 + "', '" + updateEndTime2 + "', " + updateStartMin302 + ", " + updateEndMin302 + ", " + originalWage2 + ");")
//
//
//            sqlitedb.close()
//            dbManager.close()
//
//            hourEdit1_1.hint = "00"
//            hourEdit1_2.hint = "00"
//            minEdit1_1.hint = "00"
//            minEdit1_2.hint = "00"
//
//            Toast.makeText(applicationContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
//            //layoutInDialog1.visibility = View.INVISIBLE
//        }
//
//
//        deletebtn2.setOnClickListener {
//
//            sqlitedb = dbManager.writableDatabase
//
//            sqlitedb.execSQL("DELETE FROM calTBL WHERE date = '" + str_yearMonthDay + "';")
//            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + workName1 + "', " + workWage1 + ", '" + str_yearMonthDay
//                    + "', '" + str_yearMonth
//                    + "', '" + updateStartTime1 + "', '" + updateEndTime1 + "', " + updateStartMin301 + ", " + updateEndMin301 + ", " + originalWage1 + ");")
//
//            sqlitedb.close()
//            dbManager.close()
//
//            Toast.makeText(applicationContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
//            //layoutInDialog2.visibility = View.INVISIBLE
//        }


        backBtn.setOnClickListener {
            var intent = Intent(this, CalenderActivity::class.java)
            startActivity(intent)
        }
    }

    // 일바 급여 계산 (일당)
    private fun dayWage(hourWage: Int, updateStartTime: String, updateEndTime: String
                        , updateStartMin30: Int, updateEndMin30: Int): Int {
        var todayWage = 0
        var workHour = 0
        var workMin = 0

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

        if(workMin == 0){
            todayWage = workHour * hourWage
        }else if(workMin == 30) {
            todayWage = workHour * hourWage + hourWage/2
        }else{
            //workMin == 1
            todayWage = workHour*hourWage - hourWage/2
        }

        return todayWage
    }
}