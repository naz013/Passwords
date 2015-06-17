package com.cray.software.passwords.interfaces;

import com.cray.software.passwords.BuildConfig;

public class ModuleManager {
    public boolean isPro(){
        return BuildConfig.IS_PRO;
    }
}
