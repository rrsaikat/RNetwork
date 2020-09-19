package com.rezwan.knetworklib


import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

class KNDialog(activity: Activity) {
    private val dialog: AlertDialog

    init {
        dialog = build(activity)
    }

    private fun build(activity: Activity): AlertDialog {
        return AlertDialog.Builder(activity)
                .setTitle("Opps")
                .setMessage("You have no internet connection\nPlease check your internet connection or enable it from settings.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, OkClick(activity))
                .create()
    }

    fun show() {
        dialog.show()
    }

    fun hide() {
        dialog.hide()
    }

    internal class OkClick(private val activity: Activity) : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            dialog.dismiss()
        }
    }

    internal class CancelClick(private val activity: Activity) : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface, which: Int) {
            dialog.dismiss()
        }
    }
}