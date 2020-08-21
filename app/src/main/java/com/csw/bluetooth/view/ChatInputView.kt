package com.csw.bluetooth.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.csw.bluetooth.R
import com.csw.quickmvp.mvp.ui.IUICreator
import kotlinx.android.synthetic.main.view_chat_input.view.*

class ChatInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IUICreator {

    init {
        LayoutInflater.from(context).inflate(getContentViewID(), this, true)
        initView(this, null)
        initAdapter()
        initListener()
        initData()
    }

    override fun getContentViewID(): Int {
        return R.layout.view_chat_input
    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
    }

    override fun initAdapter() {
        extrasFunction?.setOnClickListener {
            if (extrasFunctionPanel.visibility == GONE) {
                openFunctionPanel()
            } else {
                closeFunctionPanel()
            }
        }

        viewPager?.adapter
    }

    private fun openFunctionPanel() {
        extrasFunctionPanel?.run {
            visibility = View.VISIBLE
            translationY
            startAnimation(
                TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f
                )
            )
        }
    }

    private fun closeFunctionPanel() {
        extrasFunctionPanel?.run {
            visibility = View.GONE
            translationY
            startAnimation(
                TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f
                )
            )
        }
    }

    override fun initListener() {
    }

    override fun initData() {

    }

    //inner class-----------------------------------------------------------------------------------

    private inner class MyViewPagerAdapter(val functions: List<Function>) : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return functions[position].getView().apply {
                parent?.let {
                    if (it is ViewGroup) {
                        it.removeView(this)
                    }
                }
                container.addView(this)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
            if (item is View) {
                item.apply {
                    parent?.let {
                        if (it is ViewGroup) {
                            it.removeView(this)
                        }
                    }
                }
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return functions.size
        }

    }

    private inner class Function {
        private val view :View

        init {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_funtion,this@ChatInputView,false)
        }

        fun getView(): View {
            return view
        }

    }

    //interface-------------------------------------------------------------------------------------
    interface OnRequestSendMessageListener {

        /**
         * 发送一张图片
         */
        fun sendImage(uri: String)

    }
}