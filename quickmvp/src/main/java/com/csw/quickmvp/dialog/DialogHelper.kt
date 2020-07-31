package com.mingpao.epaper.ui.dialog

import android.app.Activity
import android.app.Dialog
import io.reactivex.disposables.Disposable

/**
 * 对话框辅助类
 */
class DialogHelper(val activity: Activity) {

    private val dialogMap = HashMap<String, Dialog>()

    /**
     * 存入对话框
     */
    @Suppress("unused")
    fun putDialog(id: String, dialog: Dialog) {
        dialogMap[id] = dialog
    }

    /**
     * 获取对话框
     */
    @Suppress("unused")
    fun getDialog(id: String): Dialog? {
        return dialogMap[id]
    }

    /**
     * 获取对话框并移除托管
     */
    fun getAndRemoveDialog(id: String): Dialog? {
        return dialogMap.remove(id)
    }

    /**
     * 关闭托管的对话框
     */
    fun closeDialogs() {
        for (dialog in dialogMap.values) {
            dialog.dismiss()
        }
    }

    /**
     * 创建一个加载对话框并显示
     */
    fun createLoadingDialogAndShow(id: String, disposable: Disposable): LoadingDialog {
        val loadingDialog = LoadingDialog(activity)
        loadingDialog.show()
        loadingDialog.setOnCancelListener {
            disposable.dispose()
        }
        putDialog(
            id,
            loadingDialog
        )
        return loadingDialog
    }
}