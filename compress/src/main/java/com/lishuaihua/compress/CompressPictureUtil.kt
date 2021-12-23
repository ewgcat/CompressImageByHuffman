package com.lishuaihua.compress

import android.annotation.TargetApi
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by lishuaihua on 2018/2/8.
 */
object CompressPictureUtil {
    private var mListener: CompressListener? = null

    /**
     * 对比压缩出的同等质量的图片，使用哈夫曼算法的话，压缩的更小
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    suspend fun compressImageByHuffman(
        path: String?,
        quality: IntArray,
        totalSize: Int,
        desPath: String?,
        listener: CompressListener?
    ) {
        withContext(Dispatchers.Main) {
            if (listener != null) {
                mListener = listener
                mListener?.startCompress()
            }
            withContext(Dispatchers.IO) {
                val file = File(path)
                val fileSize =  FileUtils.getFileSize(File(path))/1024
                Log.d("compressImageByHuffman","totalSize=${totalSize}")
                Log.d("compressImageByHuffman","fileSize=${fileSize}")
                if (fileSize>totalSize){
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ImageCompress.nativeCompressBitmap(bitmap, quality[0], desPath, true)
                    val size = FileUtils.getFileSize(File(desPath)) / 1024
                    Log.d("compressImageByHuffman","size=${size}")
                    while (size > totalSize && quality[0] > 50) {
                        quality[0] = quality[0] / 2
                        compressImageByHuffman(path, quality, totalSize, desPath, null)
                    }
                }else{
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ImageCompress.nativeCompressBitmap(bitmap, quality[0], desPath, true)
                }

            }
            if (mListener != null) {
                mListener?.completedCompress()
            }
        }
    }

}
