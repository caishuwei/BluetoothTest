package com.csw.bluetooth.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class ChatListRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //view的尺寸发生改变，滚动列表到底部
        scrollToEnd()
    }

    fun scrollToEnd() {
        adapter?.itemCount?.let {
            scrollToPosition(it - 1)
        }
    }

}