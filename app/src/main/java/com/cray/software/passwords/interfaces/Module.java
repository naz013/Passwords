package com.cray.software.passwords.interfaces;

import android.os.Build;

import com.cray.software.passwords.BuildConfig;

public class Module {
    public static boolean isPro(){
        return BuildConfig.IS_PRO;
    }

    public static boolean isLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
