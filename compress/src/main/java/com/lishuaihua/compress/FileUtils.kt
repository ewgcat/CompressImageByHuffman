package com.lishuaihua.compress

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object FileUtils {

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                size = fis.available().toLong()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fis != null) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            Log.e("获取文件大小", "文件不存在!")
        }
        return size
    }


}