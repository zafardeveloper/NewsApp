package com.example.newsapplication.util

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale


fun formatDate(inputDate: String, inputFormat: String, outputFormat: String): String {
    val inputSdf = SimpleDateFormat(inputFormat, Locale("ru"))
    val outputSdf = SimpleDateFormat(outputFormat, Locale("ru"))

    return try {
        val date = inputSdf.parse(inputDate)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val parsedYear = Calendar.getInstance()
        parsedYear.time = date

        val finalFormat = if (currentYear != parsedYear.get(Calendar.YEAR)) {
            "dd MMM, yyyy"
        } else {
            outputFormat
        }

        SimpleDateFormat(finalFormat, Locale("ru")).format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        inputDate
    }
}
