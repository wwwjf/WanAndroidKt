package com.birdsport.cphome.extension

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author:Ycl
 * @Date:2017-09-14 19:02
 * @Desc:
 */
fun SimpleDateFormat.format(oldSymbol: String, newSymbol: String, dateStr: String): String {
    try {
        if (dateStr.isNotEmpty()) {
            applyPattern(oldSymbol)
            val date = parse(dateStr)
            applyPattern(newSymbol)
            return format(date)
        } else return ""
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun SimpleDateFormat.formatPeriod(symbol: String, dateStr: String): String {
    try {
        val calendar = parse2C(symbol, dateStr)
        if (calendar != null) {
            val time = calendar.timeInMillis
            val now = System.currentTimeMillis()
            val period = now - time
            val abs = Math.abs(period)
            val minute = 60 * 1000L
            val hour = 60 * minute
            val day = 24 * hour
            val month = 30 * day
            val year = 12 * month

            return (if (abs < minute) {
                "1分钟"
            } else if (abs < hour) {//一小时内
                "${abs / minute}分钟"
            } else if (abs < day) {
                "${abs / hour}个小时"
            } else if (abs < month) {
                "${abs / day}天"
            } else if (abs < year) {
                "${abs / month}个月"
            } else {
                "${abs / year}年"
            }) + if (period > 0) "前" else "后"
        } else return ""
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }
}

fun SimpleDateFormat.format(date: Date, symbol: String): String {
    try {
        applyPattern(symbol)
        return format(date)
    } catch (e: Exception) {
        return ""
    }
}

fun SimpleDateFormat.before(symbol: String, dateStr: String): Boolean {
    try {
        applyPattern(symbol)
        val date = parse(dateStr)
        return Date().before(date)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun SimpleDateFormat.after(symbol: String, dateStr: String): Boolean {
    try {
        applyPattern(symbol)
        val date = parse(dateStr)
        return Date().after(date)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun SimpleDateFormat.parse2D(symbol: String, dateStr: String): Date? {
    try {
        applyPattern(symbol)
        return parse(dateStr)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun SimpleDateFormat.parse2C(symbol: String, dateStr: String): Calendar? {
    val date = parse2D(symbol, dateStr)
    if (date != null) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    } else return null
}

fun SimpleDateFormat.parseWeekDay(symbol: String, weekSymbol: String, dateStr: String): String {
    val calendar = parse2C(symbol, dateStr)
    if (calendar != null) {
        return "$weekSymbol${when (calendar.get(Calendar.DAY_OF_WEEK) - 1) {
            0 -> "日"
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            else -> ""
        }}"
    } else return ""
}