package com.abduqodirov.notes.util

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter {

    fun formatDate(date: Date?): String {

        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.US)


        return if (date != null) {
            simpleDateFormat.format(date)
        } else {
            ""
        }
    }

}