package com.csw.quickmvp.utils

import androidx.fragment.app.FragmentManager
import com.csw.quickmvp.result.ImageSelectorFragment
import com.csw.quickmvp.result.listener.OnUriResultListener

object FileSelectUtils {

    /**
     * 选择一张图片
     */
    fun selectImage(fragmentManager: FragmentManager, listener: OnUriResultListener) {
        FragmentHelper.getFragmentInstance(
            fragmentManager,
            ImageSelectorFragment::class.java.name,
            ImageSelectorFragment::class.java
        ).execute(listener)
    }

}