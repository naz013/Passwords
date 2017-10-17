package com.cray.software.passwords.helpers;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Module;
import com.cray.software.passwords.utils.Prefs;

public class ColorSetter {

    private Context mContext;

    public ColorSetter(Context context){
        this.mContext = context;
    }

    /**
     * Get color from resource.
     * @param color resource.
     * @return Color
     */
    @ColorInt
    public int getColor(@ColorRes int color){
        return Utils.getColor(mContext, color);
    }

    /**
     * Get current theme primary color.
     * @return Color resource
     */
    @ColorRes
    public int colorPrimary(){
        return colorPrimary(Prefs.getInstance(mContext).getTheme());
    }

    /**
     * Get current theme accent color.
     * @return Color resource
     */
    @ColorRes
    public int colorAccent(){
        return colorAccent(Prefs.getInstance(mContext).getTheme());
    }

    /**
     * Get accent color by code.
     * @return Color resource
     */
    @ColorRes
    public int colorAccent(int code){
        int color;
        switch (code) {
            case 0:
                color = R.color.indigoAccent;
                break;
            case 1:
                color = R.color.amberAccent;
                break;
            case 2:
                color = R.color.purpleDeepAccent;
                break;
            case 3:
                color = R.color.cyanAccent;
                break;
            case 4:
                color = R.color.pinkAccent;
                break;
            case 5:
                color = R.color.yellowAccent;
                break;
            case 6:
                color = R.color.cyanAccent;
                break;
            case 7:
                color = R.color.pinkAccent;
                break;
            case 8:
                color = R.color.redAccent;
                break;
            case 9:
                color = R.color.cyanAccent;
                break;
            case 10:
                color = R.color.redAccent;
                break;
            case 11:
                color = R.color.indigoAccent;
                break;
            default:
                if (Module.isPro()) {
                    switch (code) {
                        case 12:
                            color = R.color.greenLightAccent;
                            break;
                        case 13:
                            color = R.color.purpleDeepAccent;
                            break;
                        case 14:
                            color = R.color.purpleAccent;
                            break;
                        case 15:
                            color = R.color.pinkAccent;
                            break;
                        default:
                            color = R.color.yellowAccent;
                            break;
                    }
                } else {
                    color = R.color.yellowAccent;
                }
                break;
        }
        return color;
    }

    /**
     * Get color resource by code.
     * @param code code.
     * @return Color resource
     */
    @ColorRes
    public int colorPrimary(int code) {
        int color;
        switch (code) {
            case 0:
                color = R.color.redPrimary;
                break;
            case 1:
                color = R.color.purplePrimary;
                break;
            case 2:
                color = R.color.greenLightPrimary;
                break;
            case 3:
                color = R.color.greenPrimary;
                break;
            case 4:
                color = R.color.blueLightPrimary;
                break;
            case 5:
                color = R.color.bluePrimary;
                break;
            case 6:
                color = R.color.yellowPrimary;
                break;
            case 7:
                color = R.color.orangePrimary;
                break;
            case 8:
                color = R.color.cyanPrimary;
                break;
            case 9:
                color = R.color.pinkPrimary;
                break;
            case 10:
                color = R.color.tealPrimary;
                break;
            case 11:
                color = R.color.amberPrimary;
                break;
            case 16:
                color = R.color.material_white;
                break;
            default:
                if (Module.isPro()){
                    switch (code) {
                        case 12:
                            color = R.color.purpleDeepPrimary;
                            break;
                        case 13:
                            color = R.color.orangeDeepPrimary;
                            break;
                        case 14:
                            color = R.color.limePrimary;
                            break;
                        case 15:
                            color = R.color.indigoPrimary;
                            break;
                        default:
                            color = R.color.cyanPrimary;
                            break;
                    }
                } else {
                    color = R.color.cyanPrimary;
                }
                break;
        }
        return color;
    }

    /**
     * Get color primary dark by code.
     * @param code code
     * @return Color resource
     */
    @ColorRes
    public int colorPrimaryDark(int code) {
        int color;
        switch (code) {
            case 0:
                color = R.color.redPrimaryDark;
                break;
            case 1:
                color = R.color.purplePrimaryDark;
                break;
            case 2:
                color = R.color.greenLightPrimaryDark;
                break;
            case 3:
                color = R.color.greenPrimaryDark;
                break;
            case 4:
                color = R.color.blueLightPrimaryDark;
                break;
            case 5:
                color = R.color.bluePrimaryDark;
                break;
            case 6:
                color = R.color.yellowPrimaryDark;
                break;
            case 7:
                color = R.color.orangePrimaryDark;
                break;
            case 8:
                color = R.color.cyanPrimaryDark;
                break;
            case 9:
                color = R.color.pinkPrimaryDark;
                break;
            case 10:
                color = R.color.tealPrimaryDark;
                break;
            case 11:
                color = R.color.amberPrimaryDark;
                break;
            default:
                if (Module.isPro()){
                    switch (code) {
                        case 12:
                            color = R.color.purpleDeepPrimaryDark;
                            break;
                        case 13:
                            color = R.color.orangeDeepPrimaryDark;
                            break;
                        case 14:
                            color = R.color.limePrimaryDark;
                            break;
                        case 15:
                            color = R.color.indigoPrimaryDark;
                            break;
                        default:
                            color = R.color.cyanPrimaryDark;
                            break;
                    }
                } else {
                    color = R.color.cyanPrimaryDark;
                }
                break;
        }
        return color;
    }

    /**
     * Get status bar color based on current application theme.
     * @return Color resource
     */
    @ColorRes
    public int colorPrimaryDark(){
        return colorPrimaryDark(Prefs.getInstance(mContext).getTheme());
    }

    /**
     * Get note light color by color code.
     * @param code color code.
     * @return Color
     */
    @ColorInt
    public int getPasswordColor(int code){
        return getColor(colorPrimary(code));
    }

    /**
     * Get color indicator by identifier.
     * @param color color identifier.
     * @return Drawable resource
     */
    @DrawableRes
    public int getIndicator(int color){
        int drawable;
        switch (color) {
            case 0:
                drawable = R.drawable.drawable_red;
                break;
            case 1:
                drawable = R.drawable.drawable_purple;
                break;
            case 2:
                drawable = R.drawable.drawable_green_light;
                break;
            case 3:
                drawable = R.drawable.drawable_green;
                break;
            case 4:
                drawable = R.drawable.drawable_blue_light;
                break;
            case 5:
                drawable = R.drawable.drawable_blue;
                break;
            case 6:
                drawable = R.drawable.drawable_yellow;
                break;
            case 7:
                drawable = R.drawable.drawable_orange;
                break;
            case 8:
                drawable = R.drawable.drawable_cyan;
                break;
            case 9:
                drawable = R.drawable.drawable_pink;
                break;
            case 10:
                drawable = R.drawable.drawable_teal;
                break;
            case 11:
                drawable = R.drawable.drawable_amber;
                break;
            case 12:
                drawable = R.drawable.drawable_deep_purple;
                break;
            case 13:
                drawable = R.drawable.drawable_deep_orange;
                break;
            case 14:
                drawable = R.drawable.drawable_lime;
                break;
            case 15:
                drawable = R.drawable.drawable_indigo;
                break;
            default:
                drawable = R.drawable.drawable_cyan;
                break;
        }
        return drawable;
    }
}
