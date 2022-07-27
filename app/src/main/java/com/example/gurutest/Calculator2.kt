package com.example.gurutest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.calculator2.*
import kotlinx.android.synthetic.main.newjob.*
import kotlinx.android.synthetic.main.newjob.btn_back

class Calculator2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculator2)

        btn_calcu.setOnClickListener{

            //시급, 하루 시간, 한주 근무 일수를 변수로 가져옴
            var strhourpay = et_hourpay.text.toString()
            var strdayworkhour = et_dayworkhour.text.toString()
            var strweekworkday = et_weekworkday.text.toString()

            var hourpay = strhourpay.toInt()
            var dayworkhour = strdayworkhour.toInt()
            var weekworkday = strweekworkday.toInt()

            //총 근무 시간
            var sumworkhour = dayworkhour*weekworkday

            //예상 주휴수당
            var holidaypay :Int

            if(sumworkhour>=40) {
                holidaypay = hourpay*8
            }
            else if(sumworkhour>=15){
                var holidaypay2 = sumworkhour*8*hourpay
                holidaypay = holidaypay2/40
            }
            else{
                holidaypay=0
            }

            //주휴 시간은 아무리 찾아봐도 뭔지 잘모르겠어서 일단 안넣었어요

            //예상 주급
            var weekpay = hourpay*dayworkhour*weekworkday + holidaypay

            //예상 월급
            var monthpay = weekpay*4


            if(strhourpay.isNotEmpty()&&strdayworkhour.isNotEmpty()&&strweekworkday.isNotEmpty()) {
                var intent = Intent(this, Calculator::class.java)
                intent.putExtra("sumworkhour", sumworkhour)
                intent.putExtra("holidaypay", holidaypay)
                intent.putExtra("weekpay", weekpay)
                intent.putExtra("monthpay", monthpay)
                intent.putExtra("strhourpay",strhourpay)
                intent.putExtra("strdayworkhour",strdayworkhour)
                intent.putExtra("strweekworkday",strweekworkday)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext,"항목을 전부 입력해주세요!", Toast.LENGTH_SHORT).show()
                //항목을 입력하지 않았을 때 경고창을 띄우고 싶은데 오류남
            }

        }

        //뒤로가기 버튼
        btn_back.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}