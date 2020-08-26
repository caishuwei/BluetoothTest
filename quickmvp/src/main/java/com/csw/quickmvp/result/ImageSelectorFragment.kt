package com.csw.quickmvp.result

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.csw.quickmvp.result.base.ActivityRequestCode
import com.csw.quickmvp.result.listener.OnUriResultListener
import com.csw.quickmvp.utils.LogUtils

/**
 * 图片选择器碎片，通过文档访问框架开启图片选择器，选择后返回文件的uri
 */
class ImageSelectorFragment : Fragment() {
    private var listener: OnUriResultListener? = null

    fun execute(listener: OnUriResultListener) {
        this.listener?.onCancel()
        this.listener = listener
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, ActivityRequestCode.IMAGE_SELECTOR.code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ActivityRequestCode.IMAGE_SELECTOR.code
        ) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.run {
                    LogUtils.i(this@ImageSelectorFragment, this.toString())
                    listener?.onComplete(this)
                    return
                }
            }
            listener?.onCancel()
            listener = null
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}