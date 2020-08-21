package com.csw.bluetooth.ui.main

import com.csw.bluetooth.R
import com.csw.bluetooth.service.bluetooth.classic.ClassicBluetoothService
import com.csw.bluetooth.ui.scan.ScanDeviceActivity
import com.csw.quickmvp.mvp.ui.BaseActivity
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
        ClassicBluetoothService.waitForClientConnect(this)
    }

}