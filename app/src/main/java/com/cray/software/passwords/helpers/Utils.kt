package com.cray.software.passwords.helpers

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.PopupMenu
import android.view.View

import com.cray.software.passwords.interfaces.LCAMListener
import com.cray.software.passwords.interfaces.Module

/**
 * Copyright 2016 Nazar Suhovich
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object Utils {
    val TAG = "LOG_TAG"

    /**
     * Create ColorStateList for FAB from colors.
     * @param context application context.
     * @param colorNormal color normal.
     * @param colorPressed color pressed.
     * @return ColorStateList
     */
    fun getFabState(context: Context, @ColorRes colorNormal: Int, @ColorRes colorPressed: Int): ColorStateList {
        val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf())
        val colorP = getColor(context, colorPressed)
        val colorN = getColor(context, colorNormal)
        val colors = intArrayOf(colorP, colorN, colorN)
        return ColorStateList(states, colors)
    }

    /**
     * Get drawable from resource.
     * @param context application context.
     * @param resource drawable resource.
     * @return Drawable
     */
    fun getDrawable(context: Context, @DrawableRes resource: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            context.resources.getDrawable(resource, null)
        } else {
            context.resources.getDrawable(resource)
        }
    }

    /**
     * Get color from resource.
     * @param context application context.
     * @param resource color resource.
     * @return Color
     */
    @ColorInt
    fun getColor(context: Context, @ColorRes resource: Int): Int {
        try {
            return if (Module.isMarshmallow)
                context.resources.getColor(resource, null)
            else
                context.resources.getColor(resource)
        } catch (e: Resources.NotFoundException) {
            return resource
        }

    }

    /**
     * Show long click action dialogue for lists.
     * @param context application context.
     * @param listener listener.
     * @param actions list of actions.
     */
    fun showLCAM(context: Context, view: View, listener: LCAMListener?, vararg actions: String) {
        val popupMenu = PopupMenu(context, view)
        for (i in actions.indices) popupMenu.menu.add(1, i, i, actions[i])
        popupMenu.setOnMenuItemClickListener { item ->
            listener?.onAction(item.itemId)
            true
        }
        popupMenu.show()
    }
}
