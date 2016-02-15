package com.cray.software.passwords.helpers;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Constants;
import com.cray.software.passwords.interfaces.Module;

public class ColorSetter {

    Context mContext;
    SharedPrefs sPrefs;

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
        return colorPrimary(new SharedPrefs(mContext).loadInt(Constants.NEW_PREFERENCES_THEME));
    }

    /**
     * Get current theme accent color.
     * @return Color resource
     */
    @ColorRes
    public int colorAccent(){
        return colorAccent(new SharedPrefs(mContext).loadInt(Constants.NEW_PREFERENCES_THEME));
    }

    /**
     * Get accent color by code.
     * @return Color resource
     */
    @ColorRes
    public int colorAccent(int code){
        int color;
        boolean isDark = new SharedPrefs(mContext).loadBoolean(Constants.NEW_PREFERENCES_THEME);
        if (isDark) {
            switch (code) {
                case 0:
                    color = R.color.indigoAccent;
                    break;
                case 1:
                    color = R.color.amberAccent;
                    break;
                case 2:
                    color = R.color.pinkAccent;
                    break;
                case 3:
                    color = R.color.purpleAccent;
                    break;
                case 4:
                    color = R.color.yellowAccent;
                    break;
                case 5:
                    color = R.color.redAccent;
                    break;
                case 6:
                    color = R.color.redAccent;
                    break;
                case 7:
                    color = R.color.greenAccent;
                    break;
                case 8:
                    color = R.color.purpleDeepAccent;
                    break;
                case 9:
                    color = R.color.blueLightAccent;
                    break;
                case 10:
                    color = R.color.pinkAccent;
                    break;
                case 11:
                    color = R.color.blueAccent;
                    break;
                default:
                    if (Module.isPro()) {
                        switch (code) {
                            case 12:
                                color = R.color.greenAccent;
                                break;
                            case 13:
                                color = R.color.purpleAccent;
                                break;
                            case 14:
                                color = R.color.redAccent;
                                break;
                            case 15:
                                color = R.color.pinkAccent;
                                break;
                            default:
                                color = R.color.redAccent;
                                break;
                        }
                    } else color = R.color.redAccent;
                    break;
            }
        } else {
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
                    } else color = R.color.yellowAccent;
                    break;
            }
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
                } else color = R.color.cyanPrimaryDark;
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
        int loadedColor = new SharedPrefs(mContext).loadInt(Constants.NEW_PREFERENCES_THEME);
        return colorPrimaryDark(loadedColor);
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

    public int colorSetter(){
        sPrefs = new SharedPrefs(mContext);
        String loadedColor = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_THEME);
        int color;
        switch (loadedColor) {
            case "1":
                color = getColor(R.color.colorRed);
                break;
            case "2":
                color = getColor(R.color.colorViolet);
                break;
            case "3":
                color = getColor(R.color.colorLightCreen);
                break;
            case "4":
                color = getColor(R.color.colorGreen);
                break;
            case "5":
                color = getColor(R.color.colorLightBlue);
                break;
            case "6":
                color = getColor(R.color.colorBlue);
                break;
            case "7":
                color = getColor(R.color.colorYellow);
                break;
            case "8":
                color = getColor(R.color.colorOrange);
                break;
            case "9":
                color = getColor(R.color.colorGrey);
                break;
            case "10":
                color = getColor(R.color.colorPink);
                break;
            case "11":
                color = getColor(R.color.colorSand);
                break;
            case "12":
                color = getColor(R.color.colorBrown);
                break;
            default:
                color = getColor(R.color.colorGreen);
                break;
        }
        return color;
    }

    public int colorStatus(){
        sPrefs = new SharedPrefs(mContext);
        String loadedColor = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_THEME);
        int color;
        switch (loadedColor) {
            case "1":
                color = getColor(R.color.colorRedDark);
                break;
            case "2":
                color = getColor(R.color.colorVioletDark);
                break;
            case "3":
                color = getColor(R.color.colorLightCreenDark);
                break;
            case "4":
                color = getColor(R.color.colorGreenDark);
                break;
            case "5":
                color = getColor(R.color.colorLightBlueDark);
                break;
            case "6":
                color = getColor(R.color.colorBlueDark);
                break;
            case "7":
                color = getColor(R.color.colorYellowDark);
                break;
            case "8":
                color = getColor(R.color.colorOrangeDark);
                break;
            case "9":
                color = getColor(R.color.colorGreyDark);
                break;
            case "10":
                color = getColor(R.color.colorPinkDark);
                break;
            case "11":
                color = getColor(R.color.colorSandDark);
                break;
            case "12":
                color = getColor(R.color.colorBrownDark);
                break;
            default:
                color = getColor(R.color.colorGreenDark);
                break;
        }
        return color;
    }
}
