@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.utils

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.json.JSONArray

class GSONUtils {

    companion object {
        private val gson = GsonBuilder().serializeNulls().create()

        fun <T> parseArray(jsonStr: String?, clazz: Class<T>): ArrayList<T>? {
            if (TextUtils.isEmpty(jsonStr)) {
                return null
            }
            var result: ArrayList<T>? = null
            try {
                val objectType = object : TypeToken<ArrayList<JsonObject>>() {}.type
                val elements = gson.fromJson<ArrayList<JsonObject>>(jsonStr, objectType)
                result = ArrayList(elements.size)
                for (e in elements) {
                    result.add(gson.fromJson(e, clazz))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun parseStringArray(databaseValue: String): ArrayList<String>? {
            var result: ArrayList<String>? = null
            try {
                val jsonArray = JSONArray(databaseValue)
                result = ArrayList(jsonArray.length())
                for (i in 0 until jsonArray.length()) {
                    result.add(jsonArray.optString(i))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        fun <T> parseObject(jsonStr: String?, clazz: Class<T>): T? {
            if (TextUtils.isEmpty(jsonStr)) {
                return null
            }
            try {
                return gson.fromJson(jsonStr, clazz)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun toJSONString(o: Any?): String? {
            return if (o == null) {
                null
            } else {
                gson.toJson(o)
            }
        }


    }

}