package com.example.gurutest

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        // 날짜(년월일, 예.20220728), 날짜(년월, CalenderActivity 총액 계산용, 예. 202207)
        // 근무 시작시간, 근무 종료시간, 시작시간이 30분 단위인지 확인, 종료시간이 30분 단위인지 확인, 일당
        db!!.execSQL("CREATE TABLE calTBL (date String, yearMonth String, startTime String, endTime String, startMin30 INTEGER, endMin30 INTEGER, dayWage INTEGER)")

        // 근무지 이름, 시급
        db!!.execSQL("CREATE TABLE workTBL (name String, wage INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS calTBL")
        db!!.execSQL("DROP TABLE IF EXISTS workTBL")
    }
}