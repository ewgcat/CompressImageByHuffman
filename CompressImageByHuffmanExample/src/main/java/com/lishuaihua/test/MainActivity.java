package com.lishuaihua.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lishuaihua.compress.ImageCompress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    //测试图片的存位置
    private String desPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "hfresult.jpg";
    private ImageView iv2;
    private ImageView iv1;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        checkPermission();


        findViewById(R.id.btn_compress)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        compressImage(path,q,50,desPath);

                    }
                });


        findViewById(R.id.btn_select_ptoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        PhotoPicker.builder()
                                .setPhotoCount(9)
                                .setShowCamera(true)
                                .setShowGif(true)
                                .setPreviewEnabled(false)
                                .start(MainActivity.this, PhotoPicker.REQUEST_CODE);
                    }
                });

    }

    int[] q = {10};

    private void compressImage( String path,  int[] quality, int totalSize, String desPath) {
         compressImageByHuffman(path, quality,totalSize, desPath);

    }

    /**
     * 对比压缩出的同等质量的图片，使用哈夫曼算法的话，压缩的更小
     */
    private void compressImageByHuffman(final String path, final int[] quality, final  int totalSize,final String desPath) {
        final float[] size = {0};
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

                Toast.makeText(MainActivity.this, "压缩完成", Toast.LENGTH_LONG).show();
                File file1 = new File(desPath);
                Glide.with(MainActivity.this).load(file1).into(iv2);
                String fileSize = Formatter.formatFileSize(MainActivity.this, getFileSize(file1));
                Log.i(TAG, "图片压缩后大小：" + fileSize);
                size[0] = (float) new File(desPath).length() / 1024;
                while (quality[0]>0&&size[0]>totalSize){
                    quality[0]=quality[0]/2;
                    Log.i(TAG,"quality="+quality[0]);
                     compressImageByHuffman(path, quality,totalSize, desPath);

                }

            }
        }.execute();

    }

    /**
     * 6.0 权限申请
     */
    private void checkPermission() {
        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                path = photos.get(0);
                File file = new File(path);
                Log.i(TAG, "原图片大小：" + Formatter.formatFileSize(MainActivity.this, getFileSize(file)));
                Glide.with(MainActivity.this).load(file).into(iv1);
            }
        }
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
