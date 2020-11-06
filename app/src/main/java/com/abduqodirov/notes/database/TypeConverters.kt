package com.abduqodirov.notes.database

import android.util.Log
import androidx.room.TypeConverter
import java.util.*
import kotlin.collections.ArrayList

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }


    @TypeConverter
    fun fromString(value: String): ArrayList<String> {

        Log.d("jkms", "Converterda raw: $value")
        val paths = value.split(",").toTypedArray()

        val workerArrayList = ArrayList<String>()

        for (path in paths) {
            Log.d("jkms", "bittalab $path")
            workerArrayList.add(path)
        }

        return workerArrayList
    }

    @TypeConverter
    fun arrayToString(images: ArrayList<String>): String {

        var value = ""

        for (image in images) {

            value += image

            if (images.indexOf(image) != images.size - 1) {

                value += ","
            }
        }

        return value
    }

}