package com.csw.bluetooth.ui.main

import android.Manifest
import com.csw.bluetooth.R
import com.csw.bluetooth.ui.scan.ScanDeviceActivity
import com.csw.quickmvp.mvp.ui.BaseActivity
import com.csw.quickmvp.utils.ExternalFileHelper
import com.csw.quickmvp.utils.Utils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileOutputStream

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
                val id = Utils.generateId()
                val fileName = "fileTest.txt"
                ExternalFileHelper.createFile(fileName)
                ExternalFileHelper.deleteFile(fileName)
                ExternalFileHelper.openFile(fileName)?.run {
                    FileOutputStream(this).use {
                        it.write(id.toByteArray())
                        it.flush()
                    }
                }
            }
        }.run {
            addLifecycleTask(this)
        }

    }

}