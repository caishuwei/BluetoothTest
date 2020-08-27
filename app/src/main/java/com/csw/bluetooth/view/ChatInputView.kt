package com.csw.bluetooth.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csw.bluetooth.R
import com.csw.quickmvp.mvp.ui.IUICreator
import com.csw.quickmvp.result.listener.OnUriResultListener
import com.csw.quickmvp.utils.FileSelectUtils
import com.csw.quickmvp.utils.ScreenInfo
import kotlinx.android.synthetic.main.view_chat_input.view.*

class ChatInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IUICreator {

    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private val functionList = ArrayList<Function>()
    var onRequestSendMessageListener: OnRequestSendMessageListener? = null

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
        //设置ViewPager的高度，为两行item的高度
        viewPager?.run {
            LayoutInflater.from(context).inflate(R.layout.item_chat_funtion, this, false)
                ?.let { functionItem ->
                    functionItem.measure(
                        MeasureSpec.makeMeasureSpec(ScreenInfo.WIDTH / 3, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(ScreenInfo.HEIGHT / 2, MeasureSpec.AT_MOST)
                    )
                    val functionItemH = functionItem.measuredHeight
                    layoutParams.height = functionItemH * 2
                    requestLayout()
                }
        }
    }

    override fun initAdapter() {

        myViewPagerAdapter = MyViewPagerAdapter(functionList).apply {
            viewPager?.adapter = this
        }
    }

    private var extrasFunctionPanelAnimator: ValueAnimator? = null
        set(value) {
            field?.cancel()
            field = value
        }
    private val extrasFunctionPanelUpdateListener = ValueAnimator.AnimatorUpdateListener {
        if (it == extrasFunctionPanelAnimator) {
            it.animatedValue?.let { v ->
                if (v is Int) {
                    changeExtrasFunctionPanelHeight(v)
                }
            }
        }
    }

    private fun changeExtrasFunctionPanelHeight(height: Int) {
        extrasFunctionPanel?.run {
            layoutParams.height = height
            requestLayout()
        }
    }

    private fun openFunctionPanel() {
        //关闭输入法
        context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(
            windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        //执行动画，展开额外功能面板
        extrasFunctionPanel?.run {
            viewPager?.layoutParams?.height?.let { displayHeight ->
                extrasFunctionPanelAnimator = ObjectAnimator.ofInt(height, displayHeight).apply {
                    addUpdateListener(extrasFunctionPanelUpdateListener)
                    duration = 200
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
        }
    }

    private fun closeFunctionPanel() {
        extrasFunctionPanel?.run {
            extrasFunctionPanelAnimator = ObjectAnimator.ofInt(height, 0).apply {
                addUpdateListener(extrasFunctionPanelUpdateListener)
                duration = 200
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }

    override fun initListener() {
        extrasFunction?.setOnClickListener {
            if (extrasFunctionPanel?.height ?: 0 == 0) {
                openFunctionPanel()
            } else {
                closeFunctionPanel()
            }
        }
        msg.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                //输入法弹出
                changeExtrasFunctionPanelHeight(0)
//                closeFunctionPanel()
            }
        }
        msg?.setOnClickListener {
            //输入法弹出
            changeExtrasFunctionPanelHeight(0)
//            closeFunctionPanel()
        }
    }

    override fun initData() {
        functionList.add(ImagePickerFunction())
        functionList.add(ImagePickerFunction())
        functionList.add(ImagePickerFunction())
        functionList.add(ImagePickerFunction())
        functionList.add(ImagePickerFunction())
        functionList.add(ImagePickerFunction())
        myViewPagerAdapter.notifyDataSetChanged()
    }

    //inner class-----------------------------------------------------------------------------------

    private inner class MyViewPagerAdapter(val functions: List<Function>) : PagerAdapter() {

        private val pageItemCount = 6

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return RecyclerView(container.context).apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = FunctionAdapter(
                    functions.subList(
                        0 + position * pageItemCount,
                        (6 + position * pageItemCount).coerceAtMost(functions.size)
                    )
                )
                container.addView(
                    this,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
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
            return (functions.size + pageItemCount - 1) / pageItemCount
        }

    }

    private inner class FunctionAdapter(val functions: List<Function>) :
        BaseQuickAdapter<Function, BaseViewHolder>(R.layout.item_chat_funtion, functions) {

        init {
            setOnItemClickListener { _, _, position ->
                getItem(position)?.execute()
            }
        }

        override fun convert(helper: BaseViewHolder?, item: Function?) {
            if (helper == null || item == null) {
                return
            }
            helper.setImageResource(R.id.image, item.imageResId)
            helper.setText(R.id.name, item.nameResId)
        }

    }

    private abstract inner class Function(
        @DrawableRes val imageResId: Int,
        @StringRes val nameResId: Int
    ) {
        abstract fun execute()
    }

    /**
     * 图片选择
     */
    private inner class ImagePickerFunction() :
        Function(R.drawable.ic_image, R.string.function_pick_image) {
        override fun execute() {
            onRequestSendMessageListener?.run {
                val c = context
                if (c is FragmentActivity) {
                    FileSelectUtils.selectImage(c.supportFragmentManager,
                        object : OnUriResultListener {
                            override fun onComplete(uri: Uri) {
                                sendImage(uri.toString())
                            }

                            override fun onCancel() {

                            }
                        })
                }
            }
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