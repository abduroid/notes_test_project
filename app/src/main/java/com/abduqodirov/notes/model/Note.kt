package com.abduqodirov.notes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "fullText")
    var fullText: String = "",

    //TODO tipi Stringmas, ArrayList<String> bo'ladi. arraydan stringga, stringdan arrayga TypeConverter
    @ColumnInfo(name = "imagePaths")
    var imagePaths: String,

    //TODO Tipi Stringmas Date bo'ladi. TypeConverter from KCal calculator
    @ColumnInfo(name = "createdDate")
    var createdDate: String,

    @ColumnInfo(name = "lastEditedDate")
    //TODO Tipi Stringmas Date bo'ladi. TypeConverter from KCal calculator
    var lastEditedDate: String = ""
)