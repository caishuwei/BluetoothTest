package com.csw.bluetooth.ui.scan

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.csw.bluetooth.R
import com.csw.bluetooth.app.MyApplication
import com.csw.quickmvp.mvp.ui.BaseMVPActivity
import com.csw.quickmvp.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_scan.*
import java.util.*

/**
 * 扫描周围的蓝牙设备
 */
class ScanDeviceActivity : BaseMVPActivity<ScanDeviceContract.Presenter>(),
    ScanDeviceContract.View {

    companion object {
        fun openActivity(context: Context) {
            val intent = Intent(context, ScanDeviceActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private var bluetoothDeviceAdapter: BluetoothDeviceAdapter? = null

    override fun initInject() {
        super.initInject()
        MyApplication.instance.appComponent.getScanDeviceComponentBuilder()
            .setView(this)
            .build()
            .inject(this)
    }

    override fun getContentViewID(): Int {
        return R.layout.activity_scan
    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        super.initView(rootView, savedInstanceState)
        setSupportActionBar(toolbar)
        recyclerView?.run {
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        bluetoothDeviceAdapter = BluetoothDeviceAdapter().apply {
            recyclerView?.adapter = this
        }
    }

    override fun initListener() {
        super.initListener()
        bluetoothDeviceAdapter?.setOnItemClickListener { _, _, p ->
            bluetoothDeviceAdapter?.getItem(p)?.run {
                presenter.connectDevice(this)
            }
        }
    }

    override fun initData() {
        super.initData()
        presenter.initUIData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.run {
            menuInflater.inflate(
                R.menu.toolbar_menu_scan_device
                , this
            )
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_scan -> {
                RxPermissions(this).request(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).subscribe {
                    if (it) {
                        presenter.beginScan()
                    } else {
                        ToastUtils.showShort(R.string.toast_must_provide_location_permission_for_use_bluetooth)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //View------------------------------------------------------------------------------------------

    override fun onDeviceNoSupportBluetooth() {
        ToastUtils.showShort(R.string.toast_device_unsupport_bluetooth)
        finish()
        return
    }

    override fun setScanButtonEnable(enable: Boolean) {
        toolbar?.run {
            menu.findItem(R.id.menu_scan)?.isEnabled = enable
        }
    }

    override fun updateDeviceList(bluetoothDevices: ArrayList<BluetoothDevice>) {
        bluetoothDeviceAdapter?.setNewData(bluetoothDevices)
    }

    override fun onScanFinish() {
        toolbar?.run {
            menu.findItem(R.id.menu_scan)?.actionView?.clearAnimation()
        }
    }

    override fun onScanStarted() {
        toolbar?.run {
            val item = menu.findItem(R.id.menu_scan)
            item.actionView?.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.scan_menu_rotate_animation
                )
            )
        }
    }
}