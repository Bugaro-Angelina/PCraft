package com.example.pcraft.data.database

import androidx.room.TypeConverter
import com.example.pcraft.data.model.Component
import com.example.pcraft.data.model.ComponentType
import com.example.pcraft.data.model.CompatibilityStatus
import com.example.pcraft.data.model.StoreOffer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromComponentList(value: String?): List<Component>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Component>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toComponentList(list: List<Component>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStoreOfferList(value: String?): List<StoreOffer>? {
        if (value == null) return null
        val listType = object : TypeToken<List<StoreOffer>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toStoreOfferList(list: List<StoreOffer>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun toStringMap(map: Map<String, String>?): String? {
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromComponentType(value: String?): ComponentType? {
        if (value == null) return null
        return gson.fromJson(value, ComponentType::class.java)
    }

    @TypeConverter
    fun toComponentType(type: ComponentType?): String? {
        return gson.toJson(type)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromCompatibilityStatus(value: String?): CompatibilityStatus? {
        return value?.let { CompatibilityStatus.valueOf(it) }
    }

    @TypeConverter
    fun toCompatibilityStatus(status: CompatibilityStatus?): String? {
        return status?.name
    }
}
