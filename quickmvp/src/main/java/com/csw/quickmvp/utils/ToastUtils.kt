@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")
package com.csw.quickmvp.utils

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import com.csw.quickmvp.SDK

/**
 * Toast工具类，默认提供了一个ApplicationToast,即使应用处于后台，也可以在其它app上显示提示
 */
class ToastUtils {

    companion object {
        /**
         * 默认的应用Toast
         */
        val DEFAULT_TOAST = newToast()
            get() {
                //每次取这个对象将其重置为默认配置
                field.setText("")
                field.duration = Toast.LENGTH_SHORT
                return field
            }

        /**
         * 生成一个Toast
         * @context 上下文，默认值是Application
         */
        private fun newToast(context: Context = SDK.getApplication(), text: String = "", duration: Int = Toast.LENGTH_SHORT): Toast {
            return Toast.makeText(context, text, duration)
        }

        /**
         * 显示一个短提示
         * @text 提示文本
         * @toast toast对象，默认通过[newToast]新建一个Toast用于提示，会导致多个Toast排着队显示，这样不会错失中间要显示的消息。
         *  若使用同一个Toast对象连续多次setText只会更新文本和显示时间，本类提供一个通用的Toast[DEFAULT_TOAST]
         */
        fun showShort(text: String, toast: Toast = newToast()) {
            if(TextUtils.isEmpty(text)){
                return
            }
            toast.setText(text)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }

        fun showShort(@StringRes textResId: Int, toast: Toast = newToast()) {
            toast.setText(textResId)
            toast.duration = Toast.LENGTH_SHORT
            toast.show()
        }

        fun showLong(text: String, toast: Toast = newToast()) {
            if(TextUtils.isEmpty(text)){
                return
            }
            toast.setText(text)
            toast.duration = Toast.LENGTH_LONG
            toast.show()
        }

        fun showLong(@StringRes textResId: Int, toast: Toast = newToast()) {
            toast.setText(textResId)
            toast.duration = Toast.LENGTH_LONG
            toast.show()
        }

    }

}