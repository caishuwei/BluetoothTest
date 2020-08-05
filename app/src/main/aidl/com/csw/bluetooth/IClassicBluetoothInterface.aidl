// IClassicBluetoothInterface.aidl
package com.csw.bluetooth;
import android.bluetooth.BluetoothDevice;
interface IClassicBluetoothInterface {
    /**
     * 开始扫描周围蓝牙设备
     */
    void startDiscovery();

    /**
     * 取消扫描周围蓝牙设备
     */
    void cancelDiscovery();

    /**
     * 获取蓝牙设备列表
     */
    BluetoothDevice [] getBluetoothDevices();

    /**
     * 绑定蓝牙设备
     */
    void bondBluetoothDevice(in BluetoothDevice bluetoothDevice);

    /**
     * 连接蓝牙设备
     */
    void connectBluetoothDevice(in BluetoothDevice bluetoothDevice);
}
