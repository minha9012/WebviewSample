package com.example.webviewsample.database

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimestampConverter {
    @SuppressLint("SimpleDateFormat")
    var df: DateFormat = SimpleDateFormat(Constants.TIME_STAMP_FORMAT_MY)
    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return if (value != null) {
            try {
                return df.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            null
        } else {
            null
        }
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun dateToTimestamp(value: Date?): String? {
        return if (value == null) null else df.format(value)
    }
}