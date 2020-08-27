package com.csw.bluetooth.model.base

import com.csw.bluetooth.model.storage.SharedPreferencesDataCache

/**
 * 键值对存储
 */
interface IKeyValueSave {

    /**
     * 输入法键盘高度
     */
    fun setInputMethodKeyboardHeight(inputMethodKeyboardHeight: Int) {
        SharedPreferencesDataCache.Common.setInputMethodKeyboardHeight(inputMethodKeyboardHeight)
    }

    /**
     * 获取输入法高度
     * @return 若未设置过输入法高度，返回-1
     */
    fun getInputMethodKeyboardHeight(): Int {
        return SharedPreferencesDataCache.Common.getInputMethodKeyboardHeight()
    }

}