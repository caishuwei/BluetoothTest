@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")

package com.csw.quickmvp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ImageLoader {
    companion object {

        fun loadImageWithBgIfSuccess(
            with: Any?,
            imageView: ImageView,
            url: String?,
            placeHolderImgResId: Int = 0
        ) {
            imageView.setBackgroundColor(Color.TRANSPARENT)
            loadImage(with, imageView, url, placeHolderImgResId,
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView.setBackgroundColor(Color.parseColor("#ff999999"))
                        return false
                    }
                }
            )
        }


        /**
         * 根据url加载图片
         * @param with 在什么页面加载(如Application,Activity,Fragment，Context),glide可以根据加载环境控制
         * 图片加载任务
         */
        @SuppressLint("ObsoleteSdkInt")
        fun loadImage(
            with: Any?,
            imageView: ImageView,
            url: String?,
            placeHolderImgResId: Int = 0,
            requestListener: RequestListener<Drawable>? = null
        ) {
            //上下文检查
            val context = with ?: imageView.context
            if (context is Activity
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && context.isDestroyed
            ) {
                //上下文是Activity，既然在这个页面加载图片，而页面已销毁，那就不用加载了
                return
            }
            try {
                var requestBuilder = getRequestManager(context, imageView.context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                if (placeHolderImgResId != 0) {
                    requestBuilder = requestBuilder.placeholder(placeHolderImgResId)//占位图
                }
                if (requestListener != null) {
                    requestBuilder = requestBuilder.listener(requestListener)
                }
                requestBuilder
//                        .error(R.drawable.bg_image_error)//图片加载失败显示的图片
//                        .fallback(R.drawable.bg_image_error)//地址为空时显示的图片
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 根据上下文类型，获取请求管理器
         */
        private fun getRequestManager(with: Any, context: Context): RequestManager {
            return when (with) {
                is FragmentActivity -> {
                    Glide.with(with)
                }
                is Activity -> {
                    Glide.with(with)
                }
                is Fragment -> {
                    Glide.with(with)
                }
                is Context -> {
                    Glide.with(with)
                }
                else -> {
                    Glide.with(context)
                }
            }
        }

    }
}

