package com.lishuaihua.compress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BaseViewModel : ViewModel() {
    /**
     * 没法在主线程完成的繁重操作
     */
    fun compressImage( path: String?,
                        quality: IntArray,
                        totalSize: Int,
                        desPath: String?,
                        listener: CompressListener?) {
        viewModelScope.launch {
            CompressPictureUtil.compressImageByHuffman(path, quality, totalSize, desPath, listener)
        }
    }
}
