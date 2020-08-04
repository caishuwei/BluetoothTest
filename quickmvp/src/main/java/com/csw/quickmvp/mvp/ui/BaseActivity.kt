@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.mvp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.csw.quickmvp.R
import com.csw.quickmvp.mvp.base.IBaseView
import com.mingpao.epaper.ui.dialog.DialogHelper
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Activity基类
 */
abstract class BaseActivity : AppCompatActivity(), IUICreator, IBaseView {

    //对话框辅助
    protected lateinit var dialogHelper: DialogHelper

    //    private var logAdapter: LogAdapter? = null
    private var logListView: NoTouchRecyclerView? = null

    //只在生命周期中执行的任务
    private val lifecycleTasks: WeakHashMap<Disposable, Any> = WeakHashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogHelper = DialogHelper(this).apply {  }

//        //设置状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            //设置状态栏颜色
//            window.statusBarColor = Color.WHITE
//        }

        //在设置布局之前注入
        initInject()
        noticePresenterCreated()

        val activityRoot = RelativeLayout(this)
        activityRoot.fitsSystemWindows = true
        setContentView(
            activityRoot, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        activityRoot.addView(
            linearLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        //1 contentView
        val contentView: View =
            LayoutInflater.from(this).inflate(getContentViewID(), linearLayout, false)
        val contentViewLayoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        contentViewLayoutParams.weight = 1f
        linearLayout.addView(contentView, contentViewLayoutParams)

        //LogView
//        if (FirebaseAnalyticsUtils.DEBUG) {
//            FirebaseAnalyticsUtils.registerBaseActivity(this)
//            logListView = NoTouchRecyclerView(this)
//            logListView?.let {
//                it.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL, false))
//                it.addItemDecoration(SpaceLineDecoration(0, 0, 0, 1, Color.TRANSPARENT))
//                logAdapter = LogAdapter()
//                it.setAdapter(logAdapter)
//                activityRoot.addView(
//                    logListView,
//                    ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                    )
//                )
//            }
//        }

        initView(activityRoot, savedInstanceState)
        initAdapter()
        initListener()
        noticePresenterUICreated()
        initData()
    }

    /**
     * 告知切面创建
     */
    protected open fun noticePresenterCreated() {

    }

    /**
     * 告知切面UI已经创建
     */
    protected open fun noticePresenterUICreated() {

    }

    /**
     * 初始化注入
     */
    open fun initInject() {

    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
    }

    override fun initAdapter() {
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        super.startActivity(intent, options)
        overridePendingTransition(R.anim.quick_mvp_open_enter, R.anim.quick_mvp_open_exit)
    }

    override fun startActivityFromChild(
        child: Activity,
        intent: Intent?,
        requestCode: Int,
        options: Bundle?
    ) {
        super.startActivityFromChild(child, intent, requestCode, options)
        overridePendingTransition(R.anim.quick_mvp_open_enter, R.anim.quick_mvp_open_exit)
    }

    //Activity处于前台时监听下载时用户token异常，并弹出对话框提示用户重新登录
    private var showLoginDialogOnActivityForeground: Disposable? = null
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        showLoginDialogOnActivityForeground?.dispose()
        super.onPause()
    }

    override fun exit() {
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.quick_mvp_close_enter, R.anim.quick_mvp_close_exit)
    }

    override fun onDestroy() {
        dialogHelper.closeDialogs()
        clearLifecycleTask()
        super.onDestroy()
    }

    /**
     * 设置全屏
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setFullScreen(fullScreen: Boolean) {
        //ActionBar设置
        supportActionBar?.let {
            if (fullScreen) {
                it.hide()
            } else {
                it.show()
            }
        }
        //全屏设置
        if (fullScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (Build.VERSION.SDK_INT in 12..18) {
                window.decorView.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                //SYSTEM_UI_FLAG_IMMERSIVE_STICKY是沉浸模式，api19开始有的，
                // 需要搭配SYSTEM_UI_FLAG_HIDE_NAVIGATION（隐藏虚拟按钮）或SYSTEM_UI_FLAG_FULLSCREEN（隐藏状态栏）进行使用才有效
                //如果不加SYSTEM_UI_FLAG_IMMERSIVE_STICKY标签，用户手动打开会清除前两个标签，加了Sticky标签后用户打开视为暂时显示状态栏和虚拟按钮
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        } else {
            if (Build.VERSION.SDK_INT in 12..18) {
                window.decorView.systemUiVisibility = View.VISIBLE
            } else if (Build.VERSION.SDK_INT >= 19) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    /**
     * 添加只在界面生命周期中执行的任务，当界面销毁时取消任务
     */
    fun addLifecycleTask(task: Disposable) {
        if (!task.isDisposed) {
            lifecycleTasks[task] = task
        }
    }

    private fun clearLifecycleTask() {
        val keys = lifecycleTasks.keys
        for (key in keys) {
            key.dispose()
        }
    }

    private class NoTouchRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : RecyclerView(context, attrs, defStyleAttr) {

        override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
            return false
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(e: MotionEvent?): Boolean {
            return false
        }
    }

//    private inner class LogAdapter : BaseQuickAdapter<FirebaseAnalyticsUtils.LogInfo, BaseViewHolder>(R.layout.item_log) {
//
//        override fun convert(helper: BaseViewHolder, item: FirebaseAnalyticsUtils.LogInfo) {
//            helper.setText(R.id.tv_time, item.time)
//            helper.setText(R.id.tv_log, "${item.contentType}\n${item.itemName}\n${item.schoolId}")
//        }
//    }
}