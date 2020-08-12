package com.csw.quickmvp.log

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.csw.quickmvp.R
import com.csw.quickmvp.utils.RxBus
import com.csw.quickmvp.utils.ScreenInfo
import com.csw.quickmvp.utils.SpaceLineDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class LogViewFragment : Fragment() {

    private val logInfoList = ArrayList<LogInfo>()
    private var logAdapter: LogAdapter? = null
    private var logListView: NoTouchRecyclerView? = null
    private var disposable: Disposable? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logInfoList.clear()
        disposable?.dispose()
        disposable = RxBus.getDefault().toObservable(OnNewLogInfo::class.java)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                logInfoList.add(0, it.logInfo)
                logAdapter?.notifyDataSetChanged()
            }
        container?.run {
            logListView = NoTouchRecyclerView(context).apply {
                setPadding(0, ScreenInfo.dp2Px(20f), 0, 0)
                setLayoutManager(LinearLayoutManager(context, RecyclerView.VERTICAL, false))
                addItemDecoration(SpaceLineDecoration(0, 0, 0, 1, Color.TRANSPARENT))
                logAdapter = LogAdapter(logInfoList)
                setAdapter(logAdapter)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
        return logListView
    }

    override fun onResume() {
        super.onResume()
        LogDisplayController.instance.getLogList().apply {
            logInfoList.clear()
            logInfoList.addAll(this)
            logAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        disposable?.dispose()
        disposable = null
        logAdapter = null
        logListView = null
        super.onDestroyView()
    }

    private class NoTouchRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : RecyclerView(context, attrs, defStyleAttr) {

        override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
            return false
        }

        override fun onTouchEvent(e: MotionEvent?): Boolean {
            return false
        }
    }

    private inner class LogAdapter(data: ArrayList<LogInfo>) :
        BaseQuickAdapter<LogInfo, BaseViewHolder>(
            R.layout.quick_mvp_item_log, data
        ) {

        override fun convert(helper: BaseViewHolder, item: LogInfo) {
            val color = when (item.level) {
                Log.VERBOSE -> Color.WHITE
                Log.DEBUG -> Color.GREEN
                Log.INFO -> Color.YELLOW
                Log.WARN -> Color.BLUE
                Log.ERROR -> Color.RED
                else -> {
                    Color.WHITE
                }
            }
            helper.setTextColor(R.id.tv_time,color)
            helper.setTextColor(R.id.tv_log,color)
            helper.setText(R.id.tv_time, item.getTime())
            helper.setText(R.id.tv_log, item.getContent())
        }
    }


}