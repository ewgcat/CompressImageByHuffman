package com.lishuaihua.test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lishuaihua.compress.CompressListener
import com.lishuaihua.imageselector.utils.ImageSelector
import com.lishuaihua.permissions.JackPermissions
import com.lishuaihua.permissions.OnPermission
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity<BaseViewModel>() {
    var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    //测试图片的存位置
    private lateinit var desPath: String
    private lateinit var iv2: ImageView
    private lateinit var iv1: ImageView
    private var path: String? = null
    private val TAG="MainActivity"
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        iv1 = findViewById(R.id.iv1)
        iv2 = findViewById(R.id.iv2)
        desPath = cacheDir.toString() + System.currentTimeMillis() + ".jpg"
        checkPermission()

        findViewById<View>(R.id.btn_compress)
            .setOnClickListener {
                  vm.compressImage(
                        path = path,
                        quality =  intArrayOf(100),
                        totalSize = 100,
                        desPath = desPath,
                        object : CompressListener {
                            override fun startCompress() {
                                Log.d(TAG, "startCompress")
                                showLoading("Compressing")
                            }
                            override fun completedCompress() {
                                Log.d(TAG, "completedCompress")
                                hideLoading()
                                val file1 = File(desPath)
                                Log.i(
                                    TAG,
                                    "压缩后：" + Formatter.formatFileSize(
                                        this@MainActivity,
                                        getFileSize(file1)
                                    )
                                )
                                Glide.with(this@MainActivity).load(file1).into(iv2)
                            }
                        })
                }

        findViewById<View>(R.id.btn_select_ptoto)
            .setOnClickListener {
                ImageSelector.builder()
                    .useCamera(false) // 设置是否使用拍照 需求文档v2 p38 点击上传图标则打开本机相册
                    .setSingle(true) //设置是否单选
                    .setMaxSelectCount(1) // 图片的最大选择数量，小于等于0时，不限数量。
                    .start(this@MainActivity, Constants.PICK_IMAGE) // 打开相册
            }
    }



    /**
     * 6-11权限申请
     */
    private fun checkPermission() {
        JackPermissions.with(this).permission(permissions).request(object : OnPermission {
            override fun onGranted(permissions: List<String>, all: Boolean) {}
            override fun onDenied(permissions: List<String>, never: Boolean) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE) {
            if (data != null) {
                val photos = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                path = photos!![0]
                val file = File(path)
                Log.i(
                    TAG,
                    "原图片大小：" + Formatter.formatFileSize(this@MainActivity, getFileSize(file))
                )
                Glide.with(this@MainActivity).load(file).into(iv1!!)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"

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
                Log.e(TAG, file.absolutePath)
                var fis = FileInputStream(file)
                try {
                    size = fis.available().toLong()
                } catch (e: IOException) {
                    Log.e(TAG, e.message!!)
                }
            } else {
                Log.e("获取文件大小", "文件不存在!")
            }
            return size
        }
    }

    override fun getLayoutId(): Int =R.layout.activity_main
}