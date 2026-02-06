package com.example.tacticalcommandandcontrol.core.database.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>): String =
        value.joinToString(separator = ",")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(",")
}
