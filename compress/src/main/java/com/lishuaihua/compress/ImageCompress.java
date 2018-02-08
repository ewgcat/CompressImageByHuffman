package com.lishuaihua.compress;


import android.graphics.Bitmap;



public class ImageCompress {

    static {
        System.loadLibrary("compress");
    }

    private ImageCompress() {
    }

    /**
     * 使用native方法进行图片压缩。
     * Bitmap的格式必须是ARGB_8888 {@link android.graphics.Bitmap.Config}。
     *
     * @param bitmap   图片数据
     * @param quality  压缩质量
     * @param dstFile  压缩后存放的路径
     * @param optimize 是否使用哈夫曼算法
     * @return 结果
     */
    public static native int nativeCompressBitmap(Bitmap bitmap, int quality, String dstFile, boolean optimize);
}
