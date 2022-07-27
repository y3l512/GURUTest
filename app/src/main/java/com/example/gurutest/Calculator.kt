package com.example.gurutest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.calculator.*
import kotlinx.android.synthetic.main.newjob.*
import kotlinx.android.synthetic.main.newjob.btn_back

class Calculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculator)

        btn_back.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        btn_reset.setOnClickListener{
            startActivity(Intent(this, Calculator2::class.java))
        }

        //계산 결과 가져오기
        if(intent.hasExtra("sumworkhour")){
             var sumworkhour = intent.getIntExtra("sumworkhour",0)
             var holidaypay1 = intent.getIntExtra("holidaypay",0)
            var weekpay = intent.getIntExtra("weekpay",0)
            var monthpay = intent.getIntExtra("monthpay",0)
            var resulthourpay1 = intent.getStringExtra("strhourpay")
            var resultdayworkhour1 = intent.getStringExtra("strdayworkhour")
            var resultweekworkday1 = intent.getStringExtra("strweekworkday")

            sumworktime.text = sumworkhour.toString()
            holidaypay.text = holidaypay1.toString()
            sum_weekpay.text = weekpay.toString()
            sum_monthpay.text = monthpay.toString()
            tv_resultpayhour.text = resulthourpay1
            tv_resultdayworkhour.text = resultdayworkhour1
            tv_resultweekworkday.text = resultweekworkday1

        }

    }
}