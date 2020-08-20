package com.csw.bluetooth.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.csw.bluetooth.R
import com.csw.bluetooth.model.ExternalFileDataCache
import com.csw.bluetooth.ui.scan.ScanDeviceActivity
import com.csw.quickmvp.mvp.ui.BaseActivity
import com.csw.quickmvp.utils.LogUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun getContentViewID(): Int {
        return R.layout.activity_main
    }

    override fun initListener() {
        super.initListener()
        scan?.setOnClickListener {
            ScanDeviceActivity.openActivity(this)
        }
    }

    override fun initData() {
        super.initData()
        RxPermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe {
            if (it) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@subscribe
                }
                LogUtils.d(this, ExternalFileDataCache().getDeviceId())
            }
        }.run {
            addLifecycleTask(this)
        }

    }

}