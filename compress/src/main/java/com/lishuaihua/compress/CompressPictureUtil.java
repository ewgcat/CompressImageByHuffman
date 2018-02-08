package com.lishuaihua.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * Created by lishuaihua on 2018/2/8.
 */

public class CompressPictureUtil {
    private static final String TAG = "CompressPictureUtil";


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
                int size = (int) new File(desPath).length() / 1024;
                Log.i(TAG, "图片压缩后大小：" + size + "KB");
                while (quality[0] > 1 && size > totalSize) {
                    quality[0] = quality[0] / 2;
                    Log.i(TAG, "quality=" + quality[0]);
                    compressImageByHuffman(path, quality, totalSize, desPath, listener);
                }
                listener.completedCompress();
            }
        }.execute();

    }

}
