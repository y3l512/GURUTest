package com.example.gurutest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

// 스톱워치 시간을 담당하는 서비스
// 포그라운드 서비스 구현 실패로 어플이 꺼져도 제대로 동작하진 않음
class TimeService : Service() {

    var time = 0        // 시간 변수
    var isStop = false  // 서비스 종료 변수

    inner class MyBinder: Binder() {
        fun gerService(): TimeService {
            return this@TimeService
        }

        // StopWatchActivity에서 접근
        // 시간 반환
        fun getTime(): Int {
            return time
        }
        // 시간 설정
        fun setTIme(t: Int) {
            time = t
        }

        // 서비스 종료 변수 설정
        fun setStop(stop: Boolean){
            isStop = stop
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 서비스 생성 시 스레드 시작
        var counter = Thread(Counter())
        counter.start()
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isStop = true
        return super.onUnbind(intent)
    }



    override fun onDestroy() {
        super.onDestroy()
        isStop = true
    }

    // 시간을 증가시키는 스레드
    inner class Counter : Runnable {

        var handler = Handler()

        override fun run() {
            while(true){
                if(isStop){
                    break;
                }

                time++

                try {
                    Thread.sleep(1000)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }
            }

        }

    }

}