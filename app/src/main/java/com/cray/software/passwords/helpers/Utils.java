package com.cray.software.passwords.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.cray.software.passwords.interfaces.LCAMListener;
import com.cray.software.passwords.interfaces.Module;

/**
 * Copyright 2016 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Utils {
    public static final String TAG = "LOG_TAG";

    /**
     * Create ColorStateList for FAB from colors.
     * @param context application context.
     * @param colorNormal color normal.
     * @param colorPressed color pressed.
     * @return ColorStateList
     */
    public static ColorStateList getFabState(Context context, @ColorRes int colorNormal, @ColorRes int colorPressed) {
        int[][] states = {
                new int[] {android.R.attr.state_pressed},
                new int[] {android.R.attr.state_focused}, new int[] {}
        };
        int colorP = getColor(context, colorPressed);
        int colorN = getColor(context, colorNormal);
        int colors[] = {colorP, colorN, colorN};
        return new ColorStateList(states, colors);
    }

    /**
     * Get drawable from resource.
     * @param context application context.
     * @param resource drawable resource.
     * @return Drawable
     */
    public static Drawable getDrawable (Context context, @DrawableRes int resource){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return context.getResources().getDrawable(resource, null);
        } else {
            return context.getResources().getDrawable(resource);
        }
    }

    /**
     * Get color from resource.
     * @param context application context.
     * @param resource color resource.
     * @return Color
     */
    @ColorInt
    public static int getColor(Context context, @ColorRes int resource) {
        try {
            if (Module.isMarshmallow())
                return context.getResources().getColor(resource, null);
            else
                return context.getResources().getColor(resource);
        } catch (Resources.NotFoundException e) {
            return resource;
        }
    }

    /**
     * Show long click action dialogue for lists.
     * @param context application context.
     * @param listener listener.
     * @param actions list of actions.
     */
    public static void showLCAM(Context context, final LCAMListener listener, String... actions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(actions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
                if (listener != null) listener.onAction(item);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
