# 使用libjpeg进行图片压缩(哈夫曼编码)（kotlin协程）


### Step 1. Add the JitPack repository to your build file

#### Add it in your root build.gradle at the end of repositories:

    ```
    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
    ```

### Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.ewgcat:CompressImageByHuffman:1.0.6'
	}
```

```

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

```
```

    private fun compressImage() {
        vm.compressImage(
            path = path,
            quality = intArrayOf(100),
            totalSize = 100,
            desPath = desPath,
            listener = object : CompressListener {
                override fun startCompress() {
                    showLoading("Compressing")
                }

                override fun completedCompress() {
                    hideLoading()
                    Glide.with(this@MainActivity).load(File(desPath)).into(iv2)
                }
            })
    }
```
### 注意

1、支持armeabi,armeabi-v7a,arm64-v8a

2、压缩过程是耗时操作，必须在子线程中调用

3、图片压缩有最大极限，如果要指定压缩到多少KB，请结合其他压缩方式使用。


###使用效果

将3.58M的图片无损压缩到100KB




<a href="https://blog.51cto.com/13598859/2070274">CTO博客介绍</a>
