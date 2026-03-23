package com.shivam.simplecalculator.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.shivam.simplecalculator.R

class LoadingDialog(private val context: Context) {
    private var dialog: AlertDialog? = null

    fun show() {
        if (dialog == null) {
            val view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)
            dialog = AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create()
        }
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}
