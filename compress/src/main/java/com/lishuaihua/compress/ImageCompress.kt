package com.lishuaihua.compress

import android.graphics.Bitmap

object ImageCompress {
    /**
     * 使用native方法进行图片压缩。
     * Bitmap的格式必须是ARGB_8888 [android.graphics.Bitmap.Config]。
     *
     * @param bitmap   图片数据
     * @param quality  压缩质量
     * @param desPath  压缩后存放的路径
     * @param optimize 是否使用哈夫曼算法
     * @return 结果
     */
    external fun nativeCompressBitmap(
        bitmap: Bitmap,
        quality: Int,
        desPath: String?,
        optimize: Boolean
    ): Int

    init {
        System.loadLibrary("compress")
    }
}