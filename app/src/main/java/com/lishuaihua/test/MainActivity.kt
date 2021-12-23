package com.lishuaihua.test

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lishuaihua.compress.BaseViewModel
import com.lishuaihua.compress.CompressListener
import com.lishuaihua.imageselector.utils.ImageSelector
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity<BaseViewModel>() {
    var permissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    //测试图片的存位置
    private lateinit var desPath: String
    private lateinit var iv2: ImageView
    private lateinit var iv1: ImageView
    private lateinit var tvCompressedImg: TextView
    private lateinit var tvOriginImg: TextView
    private var path: String? = null
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        iv1 = findViewById(R.id.iv_origin_img)
        tvOriginImg = findViewById(R.id.tv_origin_img)
        iv2 = findViewById(R.id.iv_compressed_img)
        tvCompressedImg = findViewById(R.id.tv_compressed_img)

        checkPermission()

        findViewById<View>(R.id.btn_compress)
            .setOnClickListener {
                compressImage()
            }

        findViewById<View>(R.id.btn_select_photo)
            .setOnClickListener {
                ImageSelector.builder()
                    .useCamera(false) // 设置是否使用拍照 需求文档v2 p38 点击上传图标则打开本机相册
                    .setSingle(true) //设置是否单选
                    .setMaxSelectCount(1) // 图片的最大选择数量，小于等于0时，不限数量。
                    .start(this@MainActivity, Constants.PICK_IMAGE) // 打开相册
            }
        iv1.setOnClickListener {
            if (!StringUtil.isEmpty(path)){
                val imgUrls = ArrayList<String>()
                imgUrls.add(path!!)
                val intent = Intent()
                val bundle = Bundle()
                bundle.putStringArrayList("URLS", imgUrls)
                bundle.putInt("POS", 0)
                intent.putExtras(bundle)
                intent.setClass(this@MainActivity, ImagePreviewActivity::class.java)
              startActivity(intent)
            }
        }
        iv2.setOnClickListener {
            if (!StringUtil.isEmpty(desPath)){
                val imgUrls = ArrayList<String>()
                imgUrls.add(desPath!!)
                val intent = Intent()
                val bundle = Bundle()
                bundle.putStringArrayList("URLS", imgUrls)
                bundle.putInt("POS", 0)
                intent.putExtras(bundle)
                intent.setClass(this@MainActivity, ImagePreviewActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun compressImage() {
        desPath = cacheDir.toString() + System.currentTimeMillis() + ".jpg"
        vm.compressImage(
            path = path,
            quality = intArrayOf(100),
            totalSize = 1024,
            desPath = desPath,
            listener = object : CompressListener {
                override fun startCompress() {
                    showLoading("Compressing")
                }

                override fun completedCompress() {
                    hideLoading()
                    var msg = "压缩后的图: " + Formatter.formatFileSize(
                        this@MainActivity,
                        getFileSize(File(desPath))
                    )
                    tvCompressedImg.text = msg.orEmpty()
                    Glide.with(this@MainActivity).load(File(desPath)).into(iv2)
                }
            })
    }


    /**
     * 6-11权限申请
     */
    private fun checkPermission() {
        XXPermissions.with(this).permission(permissions).request(object : OnPermission {
            override fun hasPermission(granted: MutableList<String>?, all: Boolean) {
                if (all) {
                    Toast.makeText(this@MainActivity, "获取相机和读写权限成功", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "获取部分权限成功，但部分权限未正常授予", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun noPermission(denied: MutableList<String>?, never: Boolean) {
                if (never) {
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    Toast.makeText(this@MainActivity, "被永久拒绝授权，请手动授予录音和日历权限", Toast.LENGTH_LONG)
                        .show()
                    XXPermissions.startPermissionActivity(
                        this@MainActivity,
                        permissions.toMutableList()
                    )
                } else {
                    Toast.makeText(this@MainActivity, "获取相机和读写权限失败", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.PICK_IMAGE) {
            if (data != null) {
                val photos = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                path = photos!![0]
                val file = File(path)
                var msg = "压缩前的图：" + Formatter.formatFileSize(this@MainActivity, getFileSize(file))
                tvOriginImg.text = msg.orEmpty()
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

    override fun getLayoutId(): Int = R.layout.activity_main
}