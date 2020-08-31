// LowEnergyBluetoothInterface.aidl
package com.csw.bluetooth;

// Declare any non-default types here with import statements

interface LowEnergyBluetoothInterface {
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

}
