package com.example.gurutest

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.sqlite.SQLiteDatabase
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gurutest.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.min

class StopWatchActivity : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    var totalTime = 0                       // 총 근무시간(급여에 반영)
    private var isRunning = false           // 실행 여부
    private var time = 0                 // 근무 시간
    private var timerTask: Timer? = null
    private var startTime = ""              // 근무 시작 시간
    private var endTime = ""                // 근무 종료 시간
    private var startMin30 = 0              // 근무 시작 시간이 정각이 아니라 30분일때를 고려한 변수(CalenderActivity에서 시간 UI변경 시 사용)
    private var endMin30 = 0                // 근무 종료 시간이 정각이 아니라 30분일때를 고려한 변수(CalenderActivity에서 시간 UI변경 시 사용)

    lateinit var commentTv: TextView
    lateinit var imageView: ImageView
    lateinit var hourTv: TextView
    lateinit var minTv: TextView
    lateinit var secTv: TextView
    lateinit var breakBtn: Button
    lateinit var endBtn: Button
    lateinit var backBtn: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        title = "근무 기록"

        commentTv = findViewById(R.id.comment)
        imageView = findViewById(R.id.imageView)
        hourTv = findViewById(R.id.hour)
        minTv = findViewById(R.id.minute)
        secTv = findViewById(R.id.sec)
        breakBtn = findViewById(R.id.breakBtn)
        endBtn = findViewById(R.id.endBtn)
        backBtn = findViewById(R.id.backBtn)

        dbManager = DBManager(this, "calDB", null, 1)

        // 이 액티비티 실행 시 자동으로 근무 시작
        start()

        // 버튼 클릭 이벤트
        // 휴식
        breakBtn.setOnClickListener {
            pause()
        }

        // 퇴근
        endBtn.setOnClickListener {
            reset()
        }

        // 뒤로 가기
        backBtn.setOnClickListener {
            // 메인액티비티로 이동
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    // 서비스
    var binder: TimeService.MyBinder? = null
    var timeService: TimeService? = null
    var isService = false
    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 서비스와 액티비티 연결
            isService = true
            binder = service as TimeService.MyBinder
            // 시간을 세팅 (휴식했을때를 대비)
            binder!!.setTIme(time)
            timeService = binder!!.gerService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // 비정상적으로 연결 끊기면 변수 초기화
            isService = false
        }
    }

    // 시간에 따라 UI를 변경하는 스레드
    inner class GetCountThread : Runnable {

        private var handler = Handler()

        override fun run() {
            while(isRunning){
                if(binder == null){
                    continue;
                }

                // 서비스의 시간 변수를 얻어와 UI를 변경
                handler.post(Runnable {
                    run {
                        try{
                            time = binder!!.getTime()

                            val sec = time % 60
                            var min = time / 60
                            val hour = min / 60
                            min -= hour * 60

                            hourTv.text = "%02d".format(hour)
                            minTv.text = "%02d".format(min)
                            secTv.text = "%02d".format(sec)

                        } catch(e: RemoteException) {
                            e.printStackTrace()
                        }
                    }
                })

                try{
                    Thread.sleep(500)
                } catch(e: InterruptedException){
                    e.printStackTrace()
                }
            }
        }

    }

    // 근무 시작
    @RequiresApi(Build.VERSION_CODES.O)
    fun start() {

        // 서비스 시작, 바인드 서비스
        var intent = Intent(this, TimeService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)

        // UI 변경 스레드 시작
        Thread(GetCountThread()).start()

        // 실행 중
        isRunning = true

        // 뒤로가기 버튼 안보이게
        backBtn.visibility = View.INVISIBLE

        // 이미지와 텍스트뷰 변경
        imageView.setImageResource(R.drawable.working)
        commentTv.text = "열심히 일하는 중"

        // 시작 시간 구하기, 45분 이상 -> 1시간, 25~44분 -> 30분, 그 이하 -> 0분
        // 정확히 정시에 누르지 않을 때 위해 일정 오차는 알아서 수정 -> 30분 단위
        var startTimeHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"))
        var startTimeMin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm"))

        if (startTimeMin.toInt() >= 45) {
            startTime = (startTimeHour.toInt() + 1).toString()
        } else if (startTimeMin.toInt() >= 25) {
            startTime = startTimeHour
            startMin30 = 1
        } else {
            startTime = startTimeHour
        }
    }


    // 휴식
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pause(){

        // isRunning != isRunning 으로 하니 자꾸 오류가 나서 수정
        if(isRunning) isRunning = false
        else isRunning = true

        // 휴식 버튼을 눌렀을 때 근무 중인지 휴식 중인지에 따라 다른 동작
        if(isRunning){
            // 휴식 끝
            // 다시 start() 함수 호출, 이때 저장했던 시간을 넘겨줌
            start()
            breakBtn.text = "휴식"
        }
        else{
            // 휴식
            // 서비스의 time을 저장하고 서비스 종료
            time = binder!!.getTime()
            unbindService(connection)

            // UI 변경
            imageView.setImageResource(R.drawable.working_break)
            commentTv.text = "잠깐 숨 돌리는 시간!"
            breakBtn.text = "휴식 시간 끝"
        }
    }

    // 퇴근
    @RequiresApi(Build.VERSION_CODES.O)
    private fun reset(){
        // 서비스 중지
        binder!!.setStop(true)

        // 뒤로가기 버튼 보이게
        backBtn.visibility = View.VISIBLE

        // 총 근무시간, 45분 이상 -> 1시간, 25~44분 -> 30분, 그 이하 -> 0분
        totalTime = when(minTv.text.toString().toInt()){
            in 45..60 -> hourTv.text.toString().toInt()*60 + 60
            in 25..44 -> hourTv.text.toString().toInt()*60 + 30
            else -> hourTv.text.toString().toInt()*60
        }

        // UI 변경
        imageView.setImageResource(R.drawable.working_end)
        commentTv.text = "퇴근! 수고 많았어요:)"

        // 총 근무시간을 알려줌
        Toast.makeText(this, "총 근무시간 : $totalTime 분", Toast.LENGTH_SHORT).show()

        // 끝나는 시간 구하기, 45분 이상 -> 1시간, 25~44분 -> 30분, 그 이하 -> 0분
        var endTimeHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"))
        var endTimeMin = LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm"))

        if(endTimeMin.toInt() >= 45){
            endTime = (endTimeHour.toInt() + 1).toString()
        }else if(endTimeMin.toInt() >= 25) {
            endTime = endTimeHour
            endMin30 = 1
        }else{
            endTime = endTimeHour
        }

        // 오늘 임금을 계산
        todayWage()
    }


    // 오늘 임금을 계산하고 DB에 정보를 넣음
    @RequiresApi(Build.VERSION_CODES.O)
    fun todayWage(){

        // 근무지 이름 얻어오기
        var name = intent.getStringExtra("name")
        var hourWage = 0   // 시급

        // DB에서 시급 가져오기
        sqlitedb = dbManager.writableDatabase
        var cursor = sqlitedb.rawQuery("SELECT * FROM workTBL WHERE name = '" + name + "';", null)

        if(cursor.moveToNext()){
             hourWage = cursor.getString(cursor.getColumnIndexOrThrow("wage")).toInt()
        }

        var todayWage = 0  // 일당

        // 총 시간이 0.5시간(30분)으로 떨어지는 경우와 아닌 경우를 나눠서 일당 계산
        if(totalTime%60 == 0){
            todayWage = totalTime/60 * hourWage
        }else{
            todayWage = totalTime/60 * hourWage + hourWage/2
        }

        // 일당이 0보다 크다면 DB에 입력
        if(todayWage > 0){
            // 오늘 날짜
            var todayDate: LocalDate = LocalDate.now()
            // DB에 정보 입력
            sqlitedb.execSQL("INSERT INTO calTBL VALUES ('" + name + "', " + hourWage + ", '"
                    + todayDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
                    + "', '" + todayDate.format(DateTimeFormatter.ofPattern("yyyyMM")).toString()
                    + "', '" + startTime + "', '" + endTime + "', " + startMin30 + ", " + endMin30 + ", " + todayWage + ");")

            sqlitedb.close()
            dbManager.close()
        }

        // 얼마 벌었는지 알려줌
        Toast.makeText(this, todayWage.toString() + "원", Toast.LENGTH_SHORT).show()

        // 30분단위 여부 초기화
        startMin30 = 0
        endMin30 = 0

    }

    // 퇴근 시 뜨는 뒤로가기 버튼으로만 나갈 수 있게 시스템 뒤로가기 막기
    override fun onBackPressed() {
        // super.onBackPressed()
    }
}