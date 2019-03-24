package com.cray.software.passwords.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.cray.software.passwords.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Dialogues(private val themeUtil: ThemeUtil) {
    fun getMaterialDialog(context: Context): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context, themeUtil.dialogStyle)
    }

    fun getNullableDialog(context: Context?): MaterialAlertDialogBuilder? {
        return if (context != null) {
            getMaterialDialog(context)
        } else null
    }

    fun askConfirmation(context: Context, title: String, onAction: (Boolean) -> Unit) {
        getMaterialDialog(context)
                .setTitle(title)
                .setMessage(context.getString(R.string.are_you_sure))
                .setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    onAction.invoke(true)
                }
                .setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                    onAction.invoke(false)
                }
                .create()
                .show()
    }

    companion object {

        fun showPopup(anchor: View,
                      listener: ((Int) -> Unit)?, vararg actions: String) {
            val popupMenu = PopupMenu(anchor.context, anchor)
            popupMenu.setOnMenuItemClickListener { item ->
                listener?.invoke(item.order)
                true
            }
            for (i in actions.indices) {
                popupMenu.menu.add(1, i + 1000, i, actions[i])
            }
            popupMenu.show()
        }

        fun setFullWidthDialog(dialog: AlertDialog, activity: Activity?) {
            if (activity == null) return
            val window = dialog.window
            window?.setGravity(Gravity.CENTER)
            window?.setLayout((MeasureUtils.dp2px(activity, 380)), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
