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

    /**
     * 获取正在连接的设备
     */
    BluetoothDevice getConnectingDevice();

    /**
     * 获取已连接的设备
     */
    BluetoothDevice getConnectedDevice();

    /**
     * 发送文本消息到设备
     */
    boolean sendTextToDevice(in BluetoothDevice bluetoothDevice, in String msg);

    /**
     * 发送图片消息到设备
     */
    boolean sendImageToDevice(in BluetoothDevice bluetoothDevice, in String uri);
}
