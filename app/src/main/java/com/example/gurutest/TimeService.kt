package com.example.gurutest

import android.app.Service
import android.content.Intent
import android.os.IBinder

// 스톱워치가 어플을 닫으면 초기화되는 문제를 해결할 예정
class TimeService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}