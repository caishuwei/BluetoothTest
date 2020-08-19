@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")

package com.csw.quickmvp.utils

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import com.csw.quickmvp.SDK
import java.io.File
import java.io.FileDescriptor

/**
 * 外置存储文件辅助类
 *
 * android 开始使用分区存储，若app之前目标版本是<29，则可以存储到sd目录下，这会导致用户通过文件管理器看到大量
 * 的未知用处的文件，于是android开始强制使用分区存储。旧的app迁移到api29，用户覆盖安装后，原先创建在外置存储
 * 里面的文件仍然可以访问（需要读写权限），但如果用户卸载重装，则哪些目录就不可以直接通过file访问了。这么做目的
 * 大概是为了给开发者一个迁移数据的机会。后面均需要通过MediaStore来访问文件，或者SAF。
 */
object ExternalFileHelper {

    /**
     * 创建一个文件，返回Uri
     */
    fun createFile(fileName: String): Uri? {
        val contentValues = ContentValues()
        contentValues.put(FileColumns.DISPLAY_NAME, fileName)
        contentValues.put(FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_NONE)
        contentValues.put(FileColumns.MIME_TYPE, "text/plain")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //设置相对路径，存储于download/package/下面，API29设置相对路径后会插入数据会自动创建文件
            contentValues.put(
                FileColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/${SDK.getApplication().packageName}"
            )
        } else {
            //设置绝对路径
            val path =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/${SDK.getApplication().packageName}"
            val file = File("$path/$fileName")
            if (!file.exists()) {
                //路径
                File(path).run {
                    if (!exists()) {
                        mkdirs()
                    }
                }
                //文件
                file.createNewFile()
            }
            contentValues.put(
                FileColumns.DATA,
                file.path
            )
        }
        return SDK.getApplication().contentResolver.insert(
            getExternalUri(),
            contentValues
        )
    }

    fun openFile(fileName: String): FileDescriptor? {
        try {
            getUriByFileName(fileName)?.run {
                return SDK.getApplication().contentResolver.openFileDescriptor(
                    this,
                    "rw"
                )?.fileDescriptor
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(this, "打开文件失败：$fileName")
        }
        return null
    }

    fun getUriByFileName(fileName: String): Uri? {
        SDK.getApplication().contentResolver.query(
            getExternalUri(),
            arrayOf(FileColumns.DATA, FileColumns.DISPLAY_NAME, FileColumns._ID),
            "${FileColumns.DATA} like ? and ${FileColumns.DISPLAY_NAME} = ?",
            arrayOf("%${SDK.getApplication().packageName}%", fileName),
            null
        ).use {
            it?.run {
                if (moveToFirst()) {
                    val path = getString(getColumnIndex(FileColumns.DATA))
                    val name = getString(getColumnIndex(FileColumns.DISPLAY_NAME))
                    LogUtils.d(this@ExternalFileHelper, path + "___" + name)
                    val id = getInt(getColumnIndex(FileColumns._ID))
                    return Uri.withAppendedPath(getExternalUri(), id.toString())
                }
            }
        }
        return null
    }

    fun getFilePathByName(fileName: String): String? {
        SDK.getApplication().contentResolver.query(
            getExternalUri(),
            arrayOf(FileColumns.DATA, FileColumns.DISPLAY_NAME),
            "${FileColumns.DATA} like ? and ${FileColumns.DISPLAY_NAME} = ?",
            arrayOf("%${SDK.getApplication().packageName}%", fileName),
            null
        ).use {
            it?.run {
                if (moveToFirst()) {
                    val path = getString(getColumnIndex(FileColumns.DATA))
                    val name = getString(getColumnIndex(FileColumns.DISPLAY_NAME))
                    LogUtils.d(this@ExternalFileHelper, path + "___" + name)
                    return path
                }
            }
        }
        return null
    }

    fun deleteFile(fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //API29以上会自动删除文件，以下则需要自己删除
            getFilePathByName(fileName)?.run {
                File(this).delete()
            }
        }
        getUriByFileName(fileName)?.run {
            SDK.getApplication().contentResolver.delete(this, null, null)?.run {
                LogUtils.d(this@ExternalFileHelper, "deleteFile:$fileName __> $this")
            }
        }
    }

    private fun getExternalUri(): Uri {
        return MediaStore.Files.getContentUri(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.VOLUME_EXTERNAL
            else
                "external"
        )
    }
}