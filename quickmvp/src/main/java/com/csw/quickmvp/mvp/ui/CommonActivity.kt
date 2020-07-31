@file:Suppress("unused", "MemberVisibilityCanBePrivate", "RedundantOverride")
package com.csw.quickmvp.mvp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.csw.quickmvp.R

open class CommonActivity : BaseActivity() {

    companion object {

        const val FRAGMENT_TAG = "common_activity_fragment_tag"
        fun <T : Fragment> openActivity(context: Context, clazz: Class<T>, data: Bundle?) {
            val intent = Intent(context, CommonActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("clazz", clazz)
            data?.let {
                intent.putExtra("data", it)
            }
            context.startActivity(intent)
        }

    }

    override fun getContentViewID(): Int {
        return R.layout.quick_mvp_activity_common
    }

    override fun initData() {
        super.initData()

        val clazz = intent.getSerializableExtra("clazz")
        val data = intent.getBundleExtra("data")
        if (clazz != null && clazz is Class<*>) {
            var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            if (fragment == null) {
                val newInstance = clazz.newInstance()
                if (newInstance is Fragment) {
                    fragment = newInstance
                    data?.let {
                        fragment.arguments = it
                    }
                }
            }
            if (fragment != null) {
                if (!fragment.isAdded) {
                    supportFragmentManager.beginTransaction().add(R.id.quick_mvp_fl_fragment_container, fragment, FRAGMENT_TAG).commitAllowingStateLoss()
                }
                if (fragment.isDetached) {
                    supportFragmentManager.beginTransaction().attach(fragment).commitAllowingStateLoss()
                }
                return
            }
        }
        finish()
    }
}