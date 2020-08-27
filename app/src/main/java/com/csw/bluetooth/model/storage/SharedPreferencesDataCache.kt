package com.csw.bluetooth.model.storage

import com.csw.quickmvp.utils.SharedPreferencesUtils

class SharedPreferencesDataCache {

    /**
     * 通用信息存储
     */
    object Common : SharedPreferencesUtils.BaseOperation("Commom") {

        /**
         * 输入法键盘高度
         */
        fun setInputMethodKeyboardHeight(inputMethodKeyboardHeight: Int) {
            putInt("inputMethodKeyboardHeight", inputMethodKeyboardHeight)
        }

        /**
         * 若未设置过输入法高度，返回-1
         */
        fun getInputMethodKeyboardHeight(): Int {
            return getInt("inputMethodKeyboardHeight", -1)
        }

    }
}