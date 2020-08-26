@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")

package com.csw.quickmvp.utils

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns
import android.text.TextUtils
import com.csw.quickmvp.SDK
import java.io.*

/**
 * 外置存储文件辅助类
 *
 * android 开始使用分区存储，若app之前目标版本是<29，则可以存储到sd目录下，这会导致用户通过文件管理器看到大量
 * 的未知用处的文件，于是android开始强制使用分区存储。旧的app迁移到api29，用户覆盖安装后，原先创建在外置存储
 * 里面的文件仍然可以访问（需要读写权限），但如果用户卸载重装，则哪些目录就不可以直接通过file访问了。这么做目的
 * 大概是为了给开发者一个迁移数据的机会。后面均需要通过MediaStore来访问文件，或者SAF。
 * <br/>
 *
 * 外置存储文件存放于../Download/PackageName/下面
 */
object ExternalFileHelper {

    /**
     * 生成文件路径
     * @param foldersAndFileName 文件夹与文件名 device,DeviceID.txt返回device/DeviceID.txt
     */
    fun getFilePath(vararg foldersAndFileName: String): String {
        val sb = StringBuilder()
        var hasContent = false
        for (node in foldersAndFileName) {
            if (hasContent) {
                sb.append("/")
            }
            if (!TextUtils.isEmpty(node.trim())) {
                sb.append(node)
                hasContent = true
            }
        }
        return sb.toString()
    }

    /**
     * 创建一个文件，返回Uri
     * @param filePath 文件路径可包含前置路径
     * 如test.txt(../Download/PackageName/test.txt)
     * 或者device/DeviceID.txt(../Download/PackageName/device/DeviceID.txt)
     */
    fun createFile(filePath: String, mimeType: String = "text/plain"): Uri? {
        val lastPos = filePath.lastIndexOf("/")
        var prePath: String? = null
        var fileName: String = filePath
        if (lastPos != -1 && lastPos + 1 < filePath.length) {
            //拥有前置路径
            prePath = filePath.substring(0, lastPos)
            fileName = filePath.substring(lastPos + 1, filePath.length)
        }

        val contentValues = ContentValues()
        contentValues.put(FileColumns.DISPLAY_NAME, fileName)
        contentValues.put(FileColumns.MIME_TYPE, mimeType)
//        contentValues.put(FileColumns.MEDIA_TYPE, FileColumns.MEDIA_TYPE_NONE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //设置相对路径，存储于../Download/package/下面，API29设置相对路径后会插入数据会自动创建文件
            val relativePath = if (prePath == null) {
                "${Environment.DIRECTORY_DOWNLOADS}/${SDK.getApplication().packageName}/"
            } else {
                "${Environment.DIRECTORY_DOWNLOADS}/${SDK.getApplication().packageName}/$prePath/"
            }
            contentValues.put(
                FileColumns.RELATIVE_PATH,
                relativePath
            )
        } else {
            //低于API29,需要设置绝对路径
            val path =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/${SDK.getApplication().packageName}"
            val file = File("$path/$filePath")
            file.absolutePath
            try {
                //删除旧文件
                if (file.exists()) {
                    file.isDirectory
                    file.delete()
                }
                //创建文件路径
                file.parent?.run {
                    File(this).run {
                        if (!exists()) {
                            mkdirs()
                        }
                    }
                }
                //创建文件
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
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

    /**
     * 打开文件
     * @param filePath 文件路径
     */
    fun openFileDescriptor(filePath: String): FileDescriptor? {
        try {
            getUriByFilePath(filePath)?.run {
                return SDK.getApplication().contentResolver.openFileDescriptor(
                    this,
                    "rw"
                )?.fileDescriptor
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(this, "打开文件失败：$filePath")
        }
        return null
    }

    /**
     * 根据文件路径获取MediaStore Uri
     *  @param filePath 文件路径
     */
    fun getUriByFilePath(filePath: String): Uri? {
        queryFile(filePath)?.use {
            it.run {
                if (moveToFirst()) {
                    val path = getString(getColumnIndex(FileColumns.DATA))
                    LogUtils.d(this@ExternalFileHelper, "getUriByFilePath__>$path")
                    val id = getInt(getColumnIndex(FileColumns._ID))
                    return Uri.withAppendedPath(getExternalUri(), id.toString())
                }
            }
        }
        return null
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            //API29以上会自动删除文件，以下则需要自己删除，需要提供写权限
            getAbsolutePathByFilePath(filePath)?.run {
                try {
                    File(this).delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    LogUtils.e(this@ExternalFileHelper, "deleteFile:$filePath __> 删除失败")
                }
            }
        }
        getUriByFilePath(filePath)?.run {
            SDK.getApplication().contentResolver.delete(this, null, null)?.run {
                LogUtils.d(this@ExternalFileHelper, "deleteFile:$filePath __> $this")
            }
        }
    }

    /**
     * 将数据写入文件
     */
    fun writeToFile(data: ByteArray, filePath: String) {
        if (!fileExists(filePath)) {
            createFile(filePath)
        }
        try {
            openFileDescriptor(filePath)?.run {
                FileOutputStream(this).use {
                    it.write(data)
                    it.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(this@ExternalFileHelper, "writeToFile:$filePath")
        }
    }

    /**
     * 读出文件内容转为字符串
     */
    fun getFileString(filePath: String): String? {
        try {
            openFileDescriptor(filePath)?.run {
                BufferedReader(FileReader(this)).use {
                    val sb = StringBuilder()
                    var line: String?
                    var hasLine = false
                    while ((it.readLine().apply { line = this }) != null) {
                        if (hasLine) {
                            sb.append("\n")
                        }
                        sb.append(line)
                        hasLine = true
                    }
                    return sb.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(this@ExternalFileHelper, "getFileString:$filePath")
        }
        return null
    }

    /**
     * 判断文件是否存在
     */
    fun fileExists(filePath: String): Boolean {
        getAbsolutePathByFilePath(filePath)?.run {
            return File(filePath).exists()
        }
        return false
    }

    /**
     * 获取绝对文件路径，没啥用，API29以后要通过绝对路径访问外部文件，所以这个方法不用公开
     */
    private fun getAbsolutePathByFilePath(filePath: String): String? {
        queryFile(filePath)?.use {
            it.run {
                if (moveToFirst()) {
                    val path = getString(getColumnIndex(FileColumns.DATA))
                    return path
                }
            }
        }
        return null
    }

    /**
     * 通过文件路径进行查询，拼接上包名确保查询到的是我们自己应用的文件
     */
    private fun queryFile(filePath: String): Cursor? {
        val appFilePath = "%${SDK.getApplication().packageName}/$filePath"
        return SDK.getApplication().contentResolver.query(
            getExternalUri(),
            arrayOf(FileColumns._ID, FileColumns.DATA),
            "${FileColumns.DATA} like ? ",
            arrayOf(appFilePath),
            null
        )
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