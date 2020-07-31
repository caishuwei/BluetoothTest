package com.mingpao.epaper.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.csw.quickmvp.R
import com.csw.quickmvp.dialog.LoadingView
import com.csw.quickmvp.utils.ScreenInfo
import com.csw.quickmvp.utils.ToastUtils

/**
 * 加载对话框
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class LoadingDialog : Dialog {

    constructor(context: Context) : super(context, R.style.quick_mvp_SwipeBackStyle)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    private var rotateloading: LoadingView
    private val cancelListenerSet = HashSet<DialogInterface.OnCancelListener>()
    private var toastMsg = "任務已取消"
    private val toastCancelListener = DialogInterface.OnCancelListener {
        ToastUtils.showShort(toastMsg)
    }

    init {
        setContentView(R.layout.quick_mvp_dialog_loading)

        window?.run {
            //采用与App一致的状态栏颜色设置
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            statusBarColor = Color.WHITE

            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val lp = attributes
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            lp.dimAmount = 0.0f  //修改背景半透明程度
            attributes = lp
        }

        rotateloading = findViewById(R.id.quick_mvp_rotate_loading)
        rotateloading.setLineColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
        rotateloading.setLineWidth(ScreenInfo.dp2Px(6f))
        super.setOnDismissListener { rotateloading.stop() }
        super.setOnCancelListener {
            cancelListenerSet.iterator().let {
                while (it.hasNext()) {
                    it.next().run {
                        onCancel(this@LoadingDialog)
                    }
                }
            }
        }
//        setCancelToast()
    }

    override fun show() {
        super.show()
        rotateloading.start()
    }

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        addOnCancelListener(listener)
    }


    /**
     * 添加取消监听
     */
    fun addOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        listener?.let {
            cancelListenerSet.add(it)
        }
    }

    /**
     * 移除取消监听
     */
    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        listener?.let {
            cancelListenerSet.remove(it)
        }
    }


    /**
     * 任务取消时显示的Toasts
     */
    fun setCancelToast(msg: String = "任務已取消"): LoadingDialog {
        toastMsg = msg
        addOnCancelListener(toastCancelListener)
        return this
    }
}