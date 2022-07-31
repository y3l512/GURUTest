package com.example.gurutest

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.calculator.*
import kotlinx.android.synthetic.main.newjob.*
import kotlinx.android.synthetic.main.newjob.btn_back

//새 알바 등록화면
class NewJob : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newjob)

        //뒤로가기 버튼
        btn_back.setOnClickListener{
            finish();
        }

        //테이블 가져오기




        //근무시간 계산

        btn_timecalcu.setOnClickListener {
            var hour = 0
            var min = 0

            //edittext의 시작 시간, 끝나는 시간을 변수로 바꾸는 과정
            val starthour1 = et_starthour.text.toString()
            val starthour = starthour1.toInt()
            val startmin1 = et_startmin.text.toString()
            val startmin = startmin1.toInt()

            val endhour1 = et_endhour.text.toString()
            val endhour = endhour1.toInt()
            val endmin1 = et_endmin.text.toString()
            val endmin = endmin1.toInt()

            hour = endhour - starthour
            min = endmin - startmin

            //시간 계산
            var sum = 0
            sum = hour * 60 + min
            var sum1 = sum / 60

            //총 시간
            var result = 0
            result = sum1.toInt()

          //총 시간 edittext 넣기
            tv_worktime.text = result.toString()
        }

        //등록 완료 (근무지 정보 메인으로 보냄)
        btn_fin.setOnClickListener{
            val workname = et_workname.text.toString()
            val wage = et_wage.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("workname", workname)
            intent.putExtra("wage", wage)
            startActivity(intent)

        }
    }
}


