package com.csw.bluetooth.model

import com.csw.bluetooth.model.base.IDeviceInfo
import com.csw.bluetooth.model.base.IFileSave
import com.csw.bluetooth.model.base.IKeyValueSave
import com.csw.bluetooth.model.storage.ExternalFileDataCache
import javax.inject.Inject

class DataModel @Inject constructor(
) :
    IDeviceInfo,
    IFileSave,
    IKeyValueSave{

}