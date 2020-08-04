package com.csw.quickmvp.utils

import java.util.*

class Utils {

    companion object{

        /**
         * 生成一个唯一ID
         */
        fun generateId(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }

    }

}