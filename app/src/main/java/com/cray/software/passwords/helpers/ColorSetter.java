package com.cray.software.passwords.helpers;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Constants;

public class ColorSetter {

    Context mContext;
    SharedPrefs sPrefs;

    public ColorSetter(Context context){
        this.mContext = context;
    }

    @ColorInt
    private int getColor(@ColorRes int color){
        return Utils.getColor(mContext, color);
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

    public int colorCompareChooser(int colorToCompare){
        int color;
        if (colorToCompare == getColor(R.color.colorSemiTrRed)){
            color = getColor(R.color.colorRed);
        } else if (colorToCompare == getColor(R.color.colorSemiTrViolet)){
            color = getColor(R.color.colorViolet);
        } else if (colorToCompare == getColor(R.color.colorSemiTrLightCreen)){
            color = getColor(R.color.colorLightCreen);
        } else if (colorToCompare == getColor(R.color.colorSemiTrGreen)){
            color = getColor(R.color.colorGreen);
        } else if (colorToCompare == getColor(R.color.colorSemiTrLightBlue)){
            color = getColor(R.color.colorLightBlue);
        } else if (colorToCompare == getColor(R.color.colorSemiTrBlue)){
            color = getColor(R.color.colorBlue);
        } else if (colorToCompare == getColor(R.color.colorSemiTrYellow)){
            color = getColor(R.color.colorYellow);
        } else if (colorToCompare == getColor(R.color.colorSemiTrOrange)){
            color = getColor(R.color.colorOrange);
        } else if (colorToCompare == getColor(R.color.colorSemiTrGrayDark)){
            color = getColor(R.color.colorGrey);
        } else if (colorToCompare == getColor(R.color.colorSemiTrPink)){
            color = getColor(R.color.colorPink);
        } else if (colorToCompare == getColor(R.color.colorSemiTrSand)){
            color = getColor(R.color.colorSand);
        } else if (colorToCompare == getColor(R.color.colorSemiTrBrown)){
            color = getColor(R.color.colorBrown);
        } else {
            color = getColor(R.color.colorGrey);
        }
        return color;
    }

    public int colorDarkChooser(int colorToCompare){
        int color;
        if (colorToCompare == getColor(R.color.colorSemiTrRed)){
            color = getColor(R.color.colorRedDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrViolet)){
            color = getColor(R.color.colorVioletDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrLightCreen)){
            color = getColor(R.color.colorLightCreenDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrGreen)){
            color = getColor(R.color.colorGreenDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrLightBlue)){
            color = getColor(R.color.colorLightBlueDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrBlue)){
            color = getColor(R.color.colorBlueDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrYellow)){
            color = getColor(R.color.colorYellowDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrOrange)){
            color = getColor(R.color.colorOrangeDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrGrayDark)){
            color = getColor(R.color.colorGreyDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrPink)){
            color = getColor(R.color.colorPinkDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrSand)){
            color = getColor(R.color.colorSandDark);
        } else if (colorToCompare == getColor(R.color.colorSemiTrBrown)){
            color = getColor(R.color.colorBrownDark);
        } else {
            color = getColor(R.color.colorGreyDark);
        }
        return color;
    }

    public int colorChooser(){
        sPrefs = new SharedPrefs(mContext);
        String loadedColor = sPrefs.loadPrefs(Constants.NEW_PREFERENCES_THEME);
        int color;
        switch (loadedColor) {
            case "1":
                color = getColor(R.color.colorSemiTrRed);
                break;
            case "2":
                color = getColor(R.color.colorSemiTrViolet);
                break;
            case "3":
                color = getColor(R.color.colorSemiTrLightCreen);
                break;
            case "4":
                color = getColor(R.color.colorSemiTrGreen);
                break;
            case "5":
                color = getColor(R.color.colorSemiTrLightBlue);
                break;
            case "6":
                color = getColor(R.color.colorSemiTrBlue);
                break;
            case "7":
                color = getColor(R.color.colorSemiTrYellow);
                break;
            case "8":
                color = getColor(R.color.colorSemiTrOrange);
                break;
            case "9":
                color = getColor(R.color.colorSemiTrGrayDark);
                break;
            case "10":
                color = getColor(R.color.colorSemiTrPink);
                break;
            case "11":
                color = getColor(R.color.colorSemiTrSand);
                break;
            case "12":
                color = getColor(R.color.colorSemiTrBrown);
                break;
            default:
                color = getColor(R.color.colorSemiTrRed);
                break;
        }
        return color;
    }
}
