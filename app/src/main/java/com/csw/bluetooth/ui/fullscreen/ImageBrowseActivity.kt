package com.csw.bluetooth.ui.fullscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.csw.bluetooth.R
import com.csw.bluetooth.database.table.ImageMessageData
import com.csw.bluetooth.entities.MessageItem
import com.csw.quickmvp.mvp.ui.BaseActivity
import com.csw.quickmvp.utils.ImageLoader
import kotlinx.android.synthetic.main.activity_image_borwse.*

class ImageBrowseActivity : BaseActivity() {

    companion object {
        fun openActivity(
            context: Context,
            imageMessageItemList: ArrayList<MessageItem>,
            destMessageId: String
        ) {
            val intent = Intent(context, ImageBrowseActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("imageMessageItemList", imageMessageItemList)
            intent.putExtra("destMessageId", destMessageId)
            context.startActivity(intent)
        }
    }

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setFullScreen(true)
        super.onCreate(savedInstanceState)
    }

    override fun getContentViewID(): Int {
        return R.layout.activity_image_borwse
    }

    override fun initAdapter() {
        super.initAdapter()
        imageAdapter = ImageAdapter().apply {
            viewPager?.adapter = this
        }
    }

    override fun initData() {
        super.initData()
        intent.getSerializableExtra("imageMessageItemList")?.let { imageMessageItemListObj ->
            imageAdapter?.setNewData(imageMessageItemListObj as ArrayList<MessageItem>)
        }
        intent.getStringExtra("destMessageId")?.let { id ->
            imageAdapter?.data?.run {
                for ((index, item) in this.withIndex()) {
                    if (item.message.messageId == id) {
                        viewPager?.setCurrentItem(index, false)
                        break
                    }
                }
            }
        }
    }

    private inner class ImageAdapter : PagerAdapter() {

        val data = ArrayList<MessageItem>()

        fun setNewData(newData: List<MessageItem>?) {
            data.clear()
            newData?.run {
                data.addAll(this)
            }
            notifyDataSetChanged()
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return ImageView(container.context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                val item = data[position]
                item.getData()?.run {
                    if (this is ImageMessageData) {
                        ImageLoader.loadImage(this@ImageBrowseActivity, this@apply, imageUrl)
                    }
                }
                container.addView(
                    this,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            if (obj is View) {
                container.removeView(obj)
            }
        }

        override fun isViewFromObject(view: View, item: Any): Boolean {
            return view == item
        }

        override fun getCount(): Int {
            return data.size
        }
    }
}