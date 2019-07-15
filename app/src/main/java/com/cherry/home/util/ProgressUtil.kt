package com.cherry.home.util

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.WindowManager
import android.widget.TextView
import com.cherry.home.R

object ProgressUtil {
    private var progressDialog: Dialog? = null

    fun showLoading(context: Context, message: String) {
        if (progressDialog == null) {
            progressDialog = getSimpleProgressDialog(context, "", DialogInterface.OnCancelListener { progressDialog = null })
        }
        if (!TextUtils.isEmpty(message)) {
            (progressDialog!!.findViewById(R.id.progress_dialog_message) as TextView).text = message
        }
        if (!isShowLoading()) {
            progressDialog!!.show()
        }
    }

    fun setAlpha(dimAmount: Float, alpha: Float) {
        val lp = progressDialog!!.window!!.attributes
        lp.dimAmount = dimAmount
        lp.alpha = alpha
        progressDialog!!.window!!.attributes = lp
        progressDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }


    fun showLoading(context: Context, title: CharSequence, message: CharSequence) {
        showLoading(context, title, message, false)
    }

    fun showLoading(context: Context, title: CharSequence, resId: Int) {
        showLoading(context, title, resId, false)

    }

    fun showLoading(context: Context, title: CharSequence, message: CharSequence, isCancelable: Boolean) {
        showLoading(context, title, message, isCancelable, false, null)
    }

    fun showLoading(context: Context, title: CharSequence, resId: Int, isCancelable: Boolean) {
        showLoading(context, title, resId, isCancelable, false, null)
    }

    fun showLoading(context: Context, title: CharSequence, message: CharSequence, isCancelable: Boolean, listener: DialogInterface.OnCancelListener) {
        showLoading(context, title, message, isCancelable, false, listener)
    }

    fun showLoading(context: Context, title: CharSequence, resId: Int, isCancelable: Boolean, listener: DialogInterface.OnCancelListener) {
        showLoading(context, title, resId, isCancelable, false, listener)
    }

    fun showLoading(context: Context?, title: CharSequence, resId: Int, isCancelable: Boolean, isCancelOnTouchOutside: Boolean, listener: DialogInterface.OnCancelListener?) {
        if (context == null) return
        val message = context.resources.getString(resId)
        showLoading(context, title, message, isCancelable, isCancelOnTouchOutside, listener)
    }

    fun showLoading(context: Context?, title: CharSequence, message: CharSequence, isCancelable: Boolean, isCancelOnTouchOutside: Boolean, listener: DialogInterface.OnCancelListener?) {
        if (context == null) return
        if (progressDialog != null) hideLoading()
        progressDialog = ProgressDialog.show(context, title, message, false, isCancelable, listener)
        progressDialog!!.setCanceledOnTouchOutside(isCancelOnTouchOutside)
    }

    fun showLoading(context: Context, resId: Int) {
        showLoading(context, context.getString(resId))
    }

    fun isShowLoading(): Boolean {
        return if (progressDialog == null) {
            false
        } else progressDialog!!.isShowing
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog!!.context != null) {
            progressDialog!!.hide()
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        progressDialog = null
    }

    /**
     * 获取进度条对话框
     *
     * @param mContext
     * @param title
     * @param msg
     * @return
     */
    fun getProgressDialog(mContext: Context, title: String, msg: String): ProgressDialog {
        val progressDialog = ProgressDialog(mContext)
        if (!TextUtils.isEmpty(title)) {
            progressDialog.setTitle(title)
        }
        if (!TextUtils.isEmpty(msg)) {
            progressDialog.setMessage(msg)
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        val drawable = mContext.resources.getDrawable(R.drawable.progress_normal)
        progressDialog.setProgressDrawable(drawable)
        progressDialog.isIndeterminate = false
        return progressDialog
    }

    /**
     * 简单菊花进度
     *
     * @param mContext
     * @param msg
     * @return
     */
    fun getSimpleProgressDialog(mContext: Context, msg: String): Dialog {
        return getSimpleProgressDialog(mContext, msg, null)
    }

    fun getSimpleProgressDialog(mContext: Context, msg: String, listener: DialogInterface.OnCancelListener?): Dialog {
        val dialog = Dialog(mContext, R.style.TY_Progress_Dialog)
        dialog.setContentView(R.layout.ty_progress_dialog_h)
        if (!TextUtils.isEmpty(msg)) {
            (dialog.findViewById(R.id.progress_dialog_message) as TextView).text = msg
        }
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        if (listener != null) {
            dialog.setOnCancelListener(listener)
        }
        return dialog
    }
}