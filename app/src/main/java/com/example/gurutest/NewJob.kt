package com.example.gurutest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.calculator.*
import kotlinx.android.synthetic.main.newjob.*
import kotlinx.android.synthetic.main.newjob.btn_back

class NewJob : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newjob)

        //뒤로가기 버튼
        btn_back.setOnClickListener{
            finish();
        }


        //근무시간 계산

        btn_timecalcu.setOnClickListener {
            var hour = 0
            var min = 0
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

            var sum = 0
            sum = hour * 60 + min
            var sum1 = sum / 60

            var result = 0
            result = sum1.toInt()


            tv_worktime.text = result.toString()
        }

        //등록 완료 (근무지 정보 메인으로 보냄)
        btn_fin.setOnClickListener{
            val workname = et_workname.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("workname", workname)
            startActivity(intent)

        }
    }
}


