package com.example.hrmanagement.component

import android.util.Log
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import kotlinx.datetime.Month
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun startOfTheDayInMillis(timestamp: Long): Long{
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}


fun formatTimestampLegacy(timestamp: Long): String {
    if (timestamp > 0) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    } else {
        return "-"
    }
}

fun isWeekend(timestamp: Long): Boolean {
    Log.d("ApplyCompOffScreen","timestamp $timestamp")
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    Log.d("ApplyCompOffScreen","timestamp $dayOfWeek")
    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
}


fun timestampToLocalDateTime(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return Instant.ofEpochMilli(timestamp)
        .atZone(zoneId)
        .toLocalDateTime()
}

@OptIn(ExperimentalMaterial3Api::class)
fun getDateTimeMills(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long? = datePickerState.selectedDateMillis?.let { dateMillis ->
    val localDate = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate()
    val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
    LocalDateTime.of(localDate, localTime)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
}

fun convertToStartOfDayInGMT(selectedDateMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val localDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(zoneId)
        .toLocalDate() // Extract just the date (no time)
    return localDate
        .atStartOfDay(zoneId) // Set time to 00:00 in local timezone
        .toInstant()          // Convert to Instant (GMT)
        .toEpochMilli()       // Get millis
}

//fun localDateTimeToMillis(dateTime: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Long {
//    val zonedDateTime: ZonedDateTime = dateTime.atZone(zoneId)
//    return zonedDateTime.toInstant().toEpochMilli()
//}

fun dayOfDate(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val date = calendar.time
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return dayFormat.format(date)
}

fun monthNumberToShortName(month: Int): String {
    return Month.of(month)
        .getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH) // "Jan", "Feb", "Mar", ...
}

fun completeFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun dateFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + "..."
    } else {
        this
    }
}