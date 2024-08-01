package com.example.newsapplication.util

import android.icu.util.Calendar
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
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

suspend fun hideBottomNavigationView(view: BottomNavigationView) {
    view.clearAnimation()
    view.animate()
        .translationY(view.height.toFloat())
        .scaleX(0.8f)
        .alpha(0.0f)
        .setDuration(350)
        .start()
    delay(350L)
    view.visibility = View.GONE
}
fun showBottomNavigationView(view: BottomNavigationView) {
    view.clearAnimation()
    view.animate()
        .translationY(0f)
        .scaleX(1f)
        .alpha(1f)
        .setDuration(350)
        .start()
}
