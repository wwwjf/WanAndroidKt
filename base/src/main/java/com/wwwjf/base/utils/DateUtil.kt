package com.wwwjf.base.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val DEFAULT_SIMPLEDATE_FORMAT = SimpleDateFormat("yyyyMMddHHmmss")

    fun format(timeMillis:Long):String{
        return millsToString(timeMillis, DEFAULT_SIMPLEDATE_FORMAT)
    }

    private fun millsToString(timeMillis: Long, format: SimpleDateFormat) :String{
        return format.format(Date(timeMillis))
    }
}