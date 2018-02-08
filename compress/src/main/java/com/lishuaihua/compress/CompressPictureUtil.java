package com.lishuaihua.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lishuaihua on 2018/2/8.
 */

public class CompressPictureUtil {
    private static final String TAG = "CompressPictureUtil";
    private static final String tempDir = Environment.getExternalStorageDirectory() + "/temp";
    private static final String tempName = "temp.jpg";
    private static final String tempPath = tempDir + File.separator + tempName;


    /**
     * 对比压缩出的同等质量的图片，使用哈夫曼算法的话，压缩的更小
     */
    public static void compressImageByHuffman(final String path, final int[] quality, final int totalSize, final String desPath, final CompressListener listener) {
        listener.startCompress();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File file = new File(path);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                int i = ImageCompress.nativeCompressBitmap(bitmap, quality[0], desPath, true);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                long size = getFileSize(new File(desPath)) / 1024;
                Log.i(TAG, "图片压缩后大小：" + size + "KB");
                if (size > totalSize) {
                    if (quality[0] > 1) {
                        quality[0] = quality[0] / 2;
                        Log.i(TAG, "quality=" + quality[0]);
                        compressImageByHuffman(path, quality, totalSize, desPath, listener);
                    }
                }
                listener.completedCompress();
            }
        }.execute();

    }



    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }



}