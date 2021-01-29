package com.lishuaihua.test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType


open abstract class BaseActivity< U : BaseViewModel>() : AppCompatActivity() {

    @LayoutRes
    var resId: Int = 0

    protected lateinit var vm: U
        private set
    protected val activity: Activity
        get() = this
    lateinit var mContext: Context
    private val DEFAULT_STATUS_BAR_COLOR = Color.GRAY

    protected abstract fun getLayoutId(): Int


    protected//获取status_bar_height资源的ID
    //根据资源ID获取响应的尺寸值
    val statusBarHeight: Int
        get() {
            var height = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId)
            }
            return height
        }



    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initBase()
        super.onCreate(savedInstanceState)
        resId = getLayoutId()
        require(resId > 0) { "The subclass must provider a valid layout resources id." }
        setContentView(resId)
        mContext = this
        vm = createViewModel()
        initView(savedInstanceState)
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                lazzyLoad()
                window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /**
     * Initialize view model. Override this method to add your own implementation.
     *
     * @return the view model will be used.
     */
    protected fun createViewModel(): U {
        val vmClass: Class<U> = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<U>
        return ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(vmClass)
    }


    private var dialog: AppCompatDialog? = null
    protected open fun showLoading(message: String?) {
        if (dialog == null) {
            dialog = AlertDialog.Builder(this).create()
            dialog?.window?.setBackgroundDrawable(ColorDrawable())
            dialog?.window?.setDimAmount(0F)
            dialog?.setCancelable(false)
            dialog?.setOnKeyListener { _, _, _ -> false }
            dialog?.show()
            dialog?.setContentView(R.layout.layout_custom_progress_dialog_view)
            val tv_loading_message = dialog?.findViewById<TextView>(R.id.tv_loading_message)
            if (!TextUtils.isEmpty(message)){
                tv_loading_message?.setText(message)
            }else{
                tv_loading_message?.setText(R.string.loading)

            }

            dialog?.setCanceledOnTouchOutside(false)
        }
        if (!activity.isFinishing) {
            dialog?.show()
        }
    }

    protected open fun hideLoading() {
        if (!activity.isFinishing) {
            dialog?.dismiss()
        }
    }



    override fun onDestroy() {
        if (!isFinishing()) {
            dialog?.dismiss()
        }
        super.onDestroy()
    }


    protected open fun initBase() {}


    protected open fun lazzyLoad() {}


    protected open fun initView(savedInstanceState: Bundle?) {}


    /**
     * 隐藏键盘
     */
    open fun hideKeyBoard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    /**
     * 显示键盘
     */
    open fun showKeyBoard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
    }

}
