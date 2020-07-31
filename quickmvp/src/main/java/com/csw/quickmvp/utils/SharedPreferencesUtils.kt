@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.utils

import android.content.Context
import android.content.SharedPreferences
import com.csw.quickmvp.SDK

class SharedPreferencesUtils {

    companion object {
        private val sharedPreferencesMap = HashMap<String, SharedPreferences>()

        fun getSharedPreferencesEditor(
            name: String,
            mode: Int = Context.MODE_PRIVATE
        ): SharedPreferences.Editor {
            return getSharedPreferences(name, mode).edit()
        }

        fun getSharedPreferences(
            name: String,
            mode: Int = Context.MODE_PRIVATE
        ): SharedPreferences {
            val sp = sharedPreferencesMap[name]
            return if (sp == null) {
                val newSharedPreferences = SDK.getApplication().getSharedPreferences(name, mode)
                sharedPreferencesMap[name] = newSharedPreferences
                newSharedPreferences
            } else {
                sp
            }
        }
    }

    /**
     * SharedPreferences基本的存储操作，提交采用异步提交apply，但它缓存在内存里的记录应该是实时的
     */
    open class BaseOperation(private val fileName: String) {

        fun putString(key: String, value: String?) {
            getSharedPreferencesEditor(fileName).putString(key, value).apply()
        }

        fun getString(key: String, def: String? = null): String? {
            return getSharedPreferences(fileName).getString(key, def)
        }

        fun putBoolean(key: String, value: Boolean) {
            getSharedPreferencesEditor(fileName).putBoolean(key, value).apply()
        }

        fun getBoolean(key: String, def: Boolean = false): Boolean {
            return getSharedPreferences(fileName).getBoolean(key, def)
        }

        fun putInt(key: String, value: Int) {
            getSharedPreferencesEditor(fileName).putInt(key, value).apply()
        }

        fun getInt(key: String, def: Int = 0): Int {
            return getSharedPreferences(fileName).getInt(key, def)
        }

        fun putLong(key: String, value: Long) {
            getSharedPreferencesEditor(fileName).putLong(key, value).apply()
        }

        fun getLong(key: String, def: Long = 0L): Long {
            return getSharedPreferences(fileName).getLong(key, def)
        }

        fun putFloat(key: String, value: Float) {
            getSharedPreferencesEditor(fileName).putFloat(key, value).apply()
        }

        fun getFloat(key: String, def: Float = 0F): Float {
            return getSharedPreferences(fileName).getFloat(key, def)
        }

        fun printAll() {
            val all = getSharedPreferences(fileName).all.entries
            val sb = StringBuilder()
            sb.append("$fileName:\n")
            for (entry in all) {
                sb.append("\t\t${entry.key}=${entry.value}\n")
            }
            LogUtils.i("SharedPreferences", sb.toString())
        }
    }
}