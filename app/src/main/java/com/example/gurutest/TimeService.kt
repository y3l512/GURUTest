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

// 스톱워치가 어플을 닫으면 초기화되는 문제를 해결할 예정
class TimeService : Service() {

    var time = 0

    inner class MyBinder: Binder() {
        fun gerService(): TimeService {
            return this@TimeService
        }

        fun getTime(): Int {
            return time
        }

        fun setTIme(t: Int) {
            time = t
        }

        fun setStop(stop: Boolean){
            isStop = stop
        }
    }

    override fun onCreate() {
        super.onCreate()

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

    var isStop = false

    override fun onDestroy() {
        super.onDestroy()
        isStop = true
    }

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

            handler.post(Runnable {
                run {
                    Toast.makeText(applicationContext, "서비스 종료", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    // 포그라운드 서비스
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun startForegroundService(service: Intent?): ComponentName? {
//        var builder = NotificationCompat.Builder(this, "timeService")
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//        builder.setContentTitle("출근")
//        builder.setContentText("아르바이트 시간 계산 중")
//
//        var notificationIntent = Intent(this, StopWatchActivity::class.java)
//        var pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
//        builder.setContentIntent(pendingIntent)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            var manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(NotificationChannel("timeService", "기본 채널"
//                , NotificationManager.IMPORTANCE_DEFAULT))
//        }
//        startForeground(1, builder.build())
//
//        return super.startForegroundService(service)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        if (intent != null) {
//            if("startForeground".equals(intent.action)){
//                startForegroundService(intent)
//            }
//        }
//        return super.onStartCommand(intent, flags, startId)
//    }

}