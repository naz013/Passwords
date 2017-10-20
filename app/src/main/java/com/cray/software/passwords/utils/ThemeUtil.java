package com.cray.software.passwords.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StyleRes;

import com.cray.software.passwords.R;
import com.cray.software.passwords.interfaces.Module;

import java.util.Calendar;

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

public final class ThemeUtil {

    public static final int THEME_AUTO = 0;
    public static final int THEME_WHITE = 1;
    private static final int THEME_DARK = 2;
    public static final int THEME_AMOLED = 3;

    private ContextHolder holder;
    private static ThemeUtil instance;

    public static ThemeUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (ThemeUtil.class) {
                if (instance == null) {
                    instance = new ThemeUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private ThemeUtil() {
    }

    private ThemeUtil(Context context) {
        this.holder = new ContextHolder(context);
    }

    private Context getContext() {
        return holder.getContext();
    }

    @ColorInt
    public int getColor(@ColorRes int color) {
        return ViewUtils.getColor(getContext(), color);
    }

    @ColorRes
    public int colorPrimary() {
        return colorPrimary(Prefs.getInstance(getContext()).getAppThemeColor());
    }

    @ColorRes
    public int colorAccent() {
        return colorAccent(Prefs.getInstance(getContext()).getAppThemeColor());
    }

    @ColorRes
    public int colorAccent(int code) {
        int color;
        if (isDark()) {
            switch (code) {
                case Color.RED:
                    color = R.color.indigoAccent;
                    break;
                case Color.PURPLE:
                    color = R.color.amberAccent;
                    break;
                case Color.LIGHT_GREEN:
                    color = R.color.pinkAccent;
                    break;
                case Color.GREEN:
                    color = R.color.purpleAccent;
                    break;
                case Color.LIGHT_BLUE:
                    color = R.color.yellowAccent;
                    break;
                case Color.BLUE:
                    color = R.color.redAccent;
                    break;
                case Color.YELLOW:
                    color = R.color.redAccent;
                    break;
                case Color.ORANGE:
                    color = R.color.greenAccent;
                    break;
                case Color.CYAN:
                    color = R.color.purpleDeepAccent;
                    break;
                case Color.PINK:
                    color = R.color.blueLightAccent;
                    break;
                case Color.TEAL:
                    color = R.color.pinkAccent;
                    break;
                case Color.AMBER:
                    color = R.color.blueAccent;
                    break;
                default:
                    if (Module.isPro()) {
                        switch (code) {
                            case Color.DEEP_PURPLE:
                                color = R.color.greenAccent;
                                break;
                            case Color.DEEP_ORANGE:
                                color = R.color.purpleAccent;
                                break;
                            case Color.LIME:
                                color = R.color.redAccent;
                                break;
                            case Color.INDIGO:
                                color = R.color.pinkAccent;
                                break;
                            default:
                                color = R.color.redAccent;
                                break;
                        }
                    } else {
                        color = R.color.redAccent;
                    }
                    break;
            }
        } else {
            switch (code) {
                case Color.RED:
                    color = R.color.indigoAccent;
                    break;
                case Color.PURPLE:
                    color = R.color.amberAccent;
                    break;
                case Color.LIGHT_GREEN:
                    color = R.color.purpleDeepAccent;
                    break;
                case Color.GREEN:
                    color = R.color.cyanAccent;
                    break;
                case Color.LIGHT_BLUE:
                    color = R.color.pinkAccent;
                    break;
                case Color.BLUE:
                    color = R.color.yellowAccent;
                    break;
                case Color.YELLOW:
                    color = R.color.cyanAccent;
                    break;
                case Color.ORANGE:
                    color = R.color.pinkAccent;
                    break;
                case Color.CYAN:
                    color = R.color.redAccent;
                    break;
                case Color.PINK:
                    color = R.color.cyanAccent;
                    break;
                case Color.TEAL:
                    color = R.color.redAccent;
                    break;
                case Color.AMBER:
                    color = R.color.indigoAccent;
                    break;
                default:
                    if (Module.isPro()) {
                        switch (code) {
                            case Color.DEEP_PURPLE:
                                color = R.color.greenLightAccent;
                                break;
                            case Color.DEEP_ORANGE:
                                color = R.color.purpleDeepAccent;
                                break;
                            case Color.LIME:
                                color = R.color.purpleAccent;
                                break;
                            case Color.INDIGO:
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
        }
        return color;
    }

    public boolean isDark() {
        Prefs prefs = Prefs.getInstance(getContext());
        int appTheme = prefs.getAppTheme();
        boolean isDark = (appTheme == THEME_DARK || appTheme == THEME_AMOLED);
        if (appTheme == THEME_AUTO) {
            Calendar calendar = Calendar.getInstance();
            long mTime = System.currentTimeMillis();
            calendar.setTimeInMillis(mTime);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            long min = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, 19);
            long max = calendar.getTimeInMillis();
            return !(mTime >= min && mTime <= max);
        }
        return isDark;
    }

    @StyleRes
    public int getStyle() {
        int id;
        int loadedColor = Prefs.getInstance(getContext()).getAppThemeColor();
        if (isDark()) {
            if (Prefs.getInstance(getContext()).getAppTheme() == THEME_AMOLED) {
                switch (loadedColor) {
                    case Color.RED:
                        id = R.style.HomeBlack_Red;
                        break;
                    case Color.PURPLE:
                        id = R.style.HomeBlack_Purple;
                        break;
                    case Color.LIGHT_GREEN:
                        id = R.style.HomeBlack_LightGreen;
                        break;
                    case Color.GREEN:
                        id = R.style.HomeBlack_Green;
                        break;
                    case Color.LIGHT_BLUE:
                        id = R.style.HomeBlack_LightBlue;
                        break;
                    case Color.BLUE:
                        id = R.style.HomeBlack_Blue;
                        break;
                    case Color.YELLOW:
                        id = R.style.HomeBlack_Yellow;
                        break;
                    case Color.ORANGE:
                        id = R.style.HomeBlack_Orange;
                        break;
                    case Color.CYAN:
                        id = R.style.HomeBlack_Cyan;
                        break;
                    case Color.PINK:
                        id = R.style.HomeBlack_Pink;
                        break;
                    case Color.TEAL:
                        id = R.style.HomeBlack_Teal;
                        break;
                    case Color.AMBER:
                        id = R.style.HomeBlack_Amber;
                        break;
                    default:
                        if (Module.isPro()) {
                            switch (loadedColor) {
                                case Color.DEEP_PURPLE:
                                    id = R.style.HomeBlack_DeepPurple;
                                    break;
                                case Color.DEEP_ORANGE:
                                    id = R.style.HomeBlack_DeepOrange;
                                    break;
                                case Color.LIME:
                                    id = R.style.HomeBlack_Lime;
                                    break;
                                case Color.INDIGO:
                                    id = R.style.HomeBlack_Indigo;
                                    break;
                                default:
                                    id = R.style.HomeBlack_Blue;
                                    break;
                            }
                        } else {
                            id = R.style.HomeBlack_Blue;
                        }
                        break;
                }
            } else {
                switch (loadedColor) {
                    case Color.RED:
                        id = R.style.HomeDark_Red;
                        break;
                    case Color.PURPLE:
                        id = R.style.HomeDark_Purple;
                        break;
                    case Color.LIGHT_GREEN:
                        id = R.style.HomeDark_LightGreen;
                        break;
                    case Color.GREEN:
                        id = R.style.HomeDark_Green;
                        break;
                    case Color.LIGHT_BLUE:
                        id = R.style.HomeDark_LightBlue;
                        break;
                    case Color.BLUE:
                        id = R.style.HomeDark_Blue;
                        break;
                    case Color.YELLOW:
                        id = R.style.HomeDark_Yellow;
                        break;
                    case Color.ORANGE:
                        id = R.style.HomeDark_Orange;
                        break;
                    case Color.CYAN:
                        id = R.style.HomeDark_Cyan;
                        break;
                    case Color.PINK:
                        id = R.style.HomeDark_Pink;
                        break;
                    case Color.TEAL:
                        id = R.style.HomeDark_Teal;
                        break;
                    case Color.AMBER:
                        id = R.style.HomeDark_Amber;
                        break;
                    default:
                        if (Module.isPro()) {
                            switch (loadedColor) {
                                case Color.DEEP_PURPLE:
                                    id = R.style.HomeDark_DeepPurple;
                                    break;
                                case Color.DEEP_ORANGE:
                                    id = R.style.HomeDark_DeepOrange;
                                    break;
                                case Color.LIME:
                                    id = R.style.HomeDark_Lime;
                                    break;
                                case Color.INDIGO:
                                    id = R.style.HomeDark_Indigo;
                                    break;
                                default:
                                    id = R.style.HomeDark_Blue;
                                    break;
                            }
                        } else {
                            id = R.style.HomeDark_Blue;
                        }
                        break;
                }
            }
        } else {
            switch (loadedColor) {
                case Color.RED:
                    id = R.style.HomeWhite_Red;
                    break;
                case Color.PURPLE:
                    id = R.style.HomeWhite_Purple;
                    break;
                case Color.LIGHT_GREEN:
                    id = R.style.HomeWhite_LightGreen;
                    break;
                case Color.GREEN:
                    id = R.style.HomeWhite_Green;
                    break;
                case Color.LIGHT_BLUE:
                    id = R.style.HomeWhite_LightBlue;
                    break;
                case Color.BLUE:
                    id = R.style.HomeWhite_Blue;
                    break;
                case Color.YELLOW:
                    id = R.style.HomeWhite_Yellow;
                    break;
                case Color.ORANGE:
                    id = R.style.HomeWhite_Orange;
                    break;
                case Color.CYAN:
                    id = R.style.HomeWhite_Cyan;
                    break;
                case Color.PINK:
                    id = R.style.HomeWhite_Pink;
                    break;
                case Color.TEAL:
                    id = R.style.HomeWhite_Teal;
                    break;
                case Color.AMBER:
                    id = R.style.HomeWhite_Amber;
                    break;
                default:
                    if (Module.isPro()) {
                        switch (loadedColor) {
                            case Color.DEEP_PURPLE:
                                id = R.style.HomeWhite_DeepPurple;
                                break;
                            case Color.DEEP_ORANGE:
                                id = R.style.HomeWhite_DeepOrange;
                                break;
                            case Color.LIME:
                                id = R.style.HomeWhite_Lime;
                                break;
                            case Color.INDIGO:
                                id = R.style.HomeWhite_Indigo;
                                break;
                            default:
                                id = R.style.HomeWhite_Blue;
                                break;
                        }
                    } else {
                        id = R.style.HomeWhite_Blue;
                    }
                    break;
            }
        }
        return id;
    }

    @ColorRes
    public int colorPrimary(int code) {
        int color;
        switch (code) {
            case Color.RED:
                color = R.color.redPrimary;
                break;
            case Color.PURPLE:
                color = R.color.purplePrimary;
                break;
            case Color.LIGHT_GREEN:
                color = R.color.greenLightPrimary;
                break;
            case Color.GREEN:
                color = R.color.greenPrimary;
                break;
            case Color.LIGHT_BLUE:
                color = R.color.blueLightPrimary;
                break;
            case Color.BLUE:
                color = R.color.bluePrimary;
                break;
            case Color.YELLOW:
                color = R.color.yellowPrimary;
                break;
            case Color.ORANGE:
                color = R.color.orangePrimary;
                break;
            case Color.CYAN:
                color = R.color.cyanPrimary;
                break;
            case Color.PINK:
                color = R.color.pinkPrimary;
                break;
            case Color.TEAL:
                color = R.color.tealPrimary;
                break;
            case Color.AMBER:
                color = R.color.amberPrimary;
                break;
            default:
                if (Module.isPro()) {
                    switch (code) {
                        case Color.DEEP_PURPLE:
                            color = R.color.purpleDeepPrimary;
                            break;
                        case Color.DEEP_ORANGE:
                            color = R.color.orangeDeepPrimary;
                            break;
                        case Color.LIME:
                            color = R.color.limePrimary;
                            break;
                        case Color.INDIGO:
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

    @DrawableRes
    public int getIndicator() {
        return getIndicator(Prefs.getInstance(getContext()).getAppThemeColor());
    }

    @DrawableRes
    public int getIndicator(int color) {
        int drawable;
        switch (color) {
            case Color.RED:
                drawable = R.drawable.drawable_red;
                break;
            case Color.PURPLE:
                drawable = R.drawable.drawable_purple;
                break;
            case Color.LIGHT_GREEN:
                drawable = R.drawable.drawable_green_light;
                break;
            case Color.GREEN:
                drawable = R.drawable.drawable_green;
                break;
            case Color.LIGHT_BLUE:
                drawable = R.drawable.drawable_blue_light;
                break;
            case Color.BLUE:
                drawable = R.drawable.drawable_blue;
                break;
            case Color.YELLOW:
                drawable = R.drawable.drawable_yellow;
                break;
            case Color.ORANGE:
                drawable = R.drawable.drawable_orange;
                break;
            case Color.CYAN:
                drawable = R.drawable.drawable_cyan;
                break;
            case Color.PINK:
                drawable = R.drawable.drawable_pink;
                break;
            case Color.TEAL:
                drawable = R.drawable.drawable_teal;
                break;
            case Color.AMBER:
                drawable = R.drawable.drawable_amber;
                break;
            default:
                if (Module.isPro()) {
                    switch (color) {
                        case Color.DEEP_PURPLE:
                            drawable = R.drawable.drawable_deep_purple;
                            break;
                        case Color.DEEP_ORANGE:
                            drawable = R.drawable.drawable_deep_orange;
                            break;
                        case Color.LIME:
                            drawable = R.drawable.drawable_lime;
                            break;
                        case Color.INDIGO:
                            drawable = R.drawable.drawable_indigo;
                            break;
                        default:
                            drawable = R.drawable.drawable_cyan;
                            break;
                    }
                } else {
                    drawable = R.drawable.drawable_cyan;
                }
                break;
        }
        return drawable;
    }

    private Drawable getDrawable(@DrawableRes int i) {
        return ViewUtils.getDrawable(getContext(), i);
    }

    @ColorRes
    public int colorPrimaryDark(int code) {
        int color;
        switch (code) {
            case Color.RED:
                color = R.color.redPrimaryDark;
                break;
            case Color.PURPLE:
                color = R.color.purplePrimaryDark;
                break;
            case Color.LIGHT_GREEN:
                color = R.color.greenLightPrimaryDark;
                break;
            case Color.GREEN:
                color = R.color.greenPrimaryDark;
                break;
            case Color.LIGHT_BLUE:
                color = R.color.blueLightPrimaryDark;
                break;
            case Color.BLUE:
                color = R.color.bluePrimaryDark;
                break;
            case Color.YELLOW:
                color = R.color.yellowPrimaryDark;
                break;
            case Color.ORANGE:
                color = R.color.orangePrimaryDark;
                break;
            case Color.CYAN:
                color = R.color.cyanPrimaryDark;
                break;
            case Color.PINK:
                color = R.color.pinkPrimaryDark;
                break;
            case Color.TEAL:
                color = R.color.tealPrimaryDark;
                break;
            case Color.AMBER:
                color = R.color.amberPrimaryDark;
                break;
            default:
                if (Module.isPro()) {
                    switch (code) {
                        case Color.DEEP_PURPLE:
                            color = R.color.purpleDeepPrimaryDark;
                            break;
                        case Color.DEEP_ORANGE:
                            color = R.color.orangeDeepPrimaryDark;
                            break;
                        case Color.LIME:
                            color = R.color.limePrimaryDark;
                            break;
                        case Color.INDIGO:
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

    @ColorRes
    public int colorPrimaryDark() {
        int loadedColor = Prefs.getInstance(getContext()).getAppThemeColor();
        return colorPrimaryDark(loadedColor);
    }

    @StyleRes
    public int getDialogStyle() {
        int id;
        int loadedColor = Prefs.getInstance(getContext()).getAppThemeColor();
        if (isDark()) {
            if (Prefs.getInstance(getContext()).getAppTheme() == THEME_AMOLED) {
                switch (loadedColor) {
                    case Color.RED:
                        id = R.style.HomeBlackDialog_Red;
                        break;
                    case Color.PURPLE:
                        id = R.style.HomeBlackDialog_Purple;
                        break;
                    case Color.LIGHT_GREEN:
                        id = R.style.HomeBlackDialog_LightGreen;
                        break;
                    case Color.GREEN:
                        id = R.style.HomeBlackDialog_Green;
                        break;
                    case Color.LIGHT_BLUE:
                        id = R.style.HomeBlackDialog_LightBlue;
                        break;
                    case Color.BLUE:
                        id = R.style.HomeBlackDialog_Blue;
                        break;
                    case Color.YELLOW:
                        id = R.style.HomeBlackDialog_Yellow;
                        break;
                    case Color.ORANGE:
                        id = R.style.HomeBlackDialog_Orange;
                        break;
                    case Color.CYAN:
                        id = R.style.HomeBlackDialog_Cyan;
                        break;
                    case Color.PINK:
                        id = R.style.HomeBlackDialog_Pink;
                        break;
                    case Color.TEAL:
                        id = R.style.HomeBlackDialog_Teal;
                        break;
                    case Color.AMBER:
                        id = R.style.HomeBlackDialog_Amber;
                        break;
                    default:
                        if (Module.isPro()) {
                            switch (loadedColor) {
                                case Color.DEEP_PURPLE:
                                    id = R.style.HomeBlackDialog_DeepPurple;
                                    break;
                                case Color.DEEP_ORANGE:
                                    id = R.style.HomeBlackDialog_DeepOrange;
                                    break;
                                case Color.LIME:
                                    id = R.style.HomeBlackDialog_Lime;
                                    break;
                                case Color.INDIGO:
                                    id = R.style.HomeBlackDialog_Indigo;
                                    break;
                                default:
                                    id = R.style.HomeBlackDialog_Blue;
                                    break;
                            }
                        } else {
                            id = R.style.HomeBlackDialog_Blue;
                        }
                        break;
                }
            } else {
                switch (loadedColor) {
                    case Color.RED:
                        id = R.style.HomeDarkDialog_Red;
                        break;
                    case Color.PURPLE:
                        id = R.style.HomeDarkDialog_Purple;
                        break;
                    case Color.LIGHT_GREEN:
                        id = R.style.HomeDarkDialog_LightGreen;
                        break;
                    case Color.GREEN:
                        id = R.style.HomeDarkDialog_Green;
                        break;
                    case Color.LIGHT_BLUE:
                        id = R.style.HomeDarkDialog_LightBlue;
                        break;
                    case Color.BLUE:
                        id = R.style.HomeDarkDialog_Blue;
                        break;
                    case Color.YELLOW:
                        id = R.style.HomeDarkDialog_Yellow;
                        break;
                    case Color.ORANGE:
                        id = R.style.HomeDarkDialog_Orange;
                        break;
                    case Color.CYAN:
                        id = R.style.HomeDarkDialog_Cyan;
                        break;
                    case Color.PINK:
                        id = R.style.HomeDarkDialog_Pink;
                        break;
                    case Color.TEAL:
                        id = R.style.HomeDarkDialog_Teal;
                        break;
                    case Color.AMBER:
                        id = R.style.HomeDarkDialog_Amber;
                        break;
                    default:
                        if (Module.isPro()) {
                            switch (loadedColor) {
                                case Color.DEEP_PURPLE:
                                    id = R.style.HomeDarkDialog_DeepPurple;
                                    break;
                                case Color.DEEP_ORANGE:
                                    id = R.style.HomeDarkDialog_DeepOrange;
                                    break;
                                case Color.LIME:
                                    id = R.style.HomeDarkDialog_Lime;
                                    break;
                                case Color.INDIGO:
                                    id = R.style.HomeDarkDialog_Indigo;
                                    break;
                                default:
                                    id = R.style.HomeDarkDialog_Blue;
                                    break;
                            }
                        } else {
                            id = R.style.HomeDarkDialog_Blue;
                        }
                        break;
                }
            }
        } else {
            switch (loadedColor) {
                case Color.RED:
                    id = R.style.HomeWhiteDialog_Red;
                    break;
                case Color.PURPLE:
                    id = R.style.HomeWhiteDialog_Purple;
                    break;
                case Color.LIGHT_GREEN:
                    id = R.style.HomeWhiteDialog_LightGreen;
                    break;
                case Color.GREEN:
                    id = R.style.HomeWhiteDialog_Green;
                    break;
                case Color.LIGHT_BLUE:
                    id = R.style.HomeWhiteDialog_LightBlue;
                    break;
                case Color.BLUE:
                    id = R.style.HomeWhiteDialog_Blue;
                    break;
                case Color.YELLOW:
                    id = R.style.HomeWhiteDialog_Yellow;
                    break;
                case Color.ORANGE:
                    id = R.style.HomeWhiteDialog_Orange;
                    break;
                case Color.CYAN:
                    id = R.style.HomeWhiteDialog_Cyan;
                    break;
                case Color.PINK:
                    id = R.style.HomeWhiteDialog_Pink;
                    break;
                case Color.TEAL:
                    id = R.style.HomeWhiteDialog_Teal;
                    break;
                case Color.AMBER:
                    id = R.style.HomeWhiteDialog_Amber;
                    break;
                default:
                    if (Module.isPro()) {
                        switch (loadedColor) {
                            case Color.DEEP_PURPLE:
                                id = R.style.HomeWhiteDialog_DeepPurple;
                                break;
                            case Color.DEEP_ORANGE:
                                id = R.style.HomeWhiteDialog_DeepOrange;
                                break;
                            case Color.LIME:
                                id = R.style.HomeWhiteDialog_Lime;
                                break;
                            case Color.INDIGO:
                                id = R.style.HomeWhiteDialog_Indigo;
                                break;
                            default:
                                id = R.style.HomeWhiteDialog_Blue;
                                break;
                        }
                    } else {
                        id = R.style.HomeWhiteDialog_Blue;
                    }
                    break;
            }
        }
        return id;
    }

    @ColorInt
    public int getBackgroundStyle() {
        int id;
        if (isDark()) {
            if (Prefs.getInstance(getContext()).getAppTheme() == THEME_AMOLED) {
                id = getColor(R.color.colorBlack);
            } else {
                id = getColor(R.color.material_grey);
            }
        } else {
            id = getColor(R.color.material_white);
        }
        return id;
    }

    @ColorInt
    public int getCardStyle() {
        int color;
        if (isDark()) {
            if (Prefs.getInstance(getContext()).getAppTheme() == THEME_AMOLED) {
                color = getColor(R.color.colorBlack);
            } else {
                color = getColor(R.color.grey_x);
            }
        } else {
            color = getColor(R.color.colorWhite);
        }
        return color;
    }

    @ColorInt
    public int getNoteColor(int code) {
        return getColor(colorPrimary(code));
    }

    @ColorInt
    public int getNoteDarkColor(int code) {
        return getColor(colorPrimaryDark(code));
    }

    public int adjustAlpha(int color, @IntRange(from = 0, to = 100) int factor) {
        float alpha = (255f * ((float) factor / 100f));
        int red = android.graphics.Color.red(color);
        int green = android.graphics.Color.green(color);
        int blue = android.graphics.Color.blue(color);
        return android.graphics.Color.argb((int) alpha, red, green, blue);
    }

    @ColorInt
    public int getNoteLightColor(int code) {
        int color;
        switch (code) {
            case Color.RED:
                color = R.color.redAccent;
                break;
            case Color.PURPLE:
                color = R.color.purpleAccent;
                break;
            case Color.GREEN:
                color = R.color.greenAccent;
                break;
            case Color.LIGHT_GREEN:
                color = R.color.greenLightAccent;
                break;
            case Color.BLUE:
                color = R.color.blueAccent;
                break;
            case Color.LIGHT_BLUE:
                color = R.color.blueLightAccent;
                break;
            case Color.YELLOW:
                color = R.color.yellowAccent;
                break;
            case Color.ORANGE:
                color = R.color.orangeAccent;
                break;
            case Color.CYAN:
                color = R.color.cyanAccent;
                break;
            case Color.PINK:
                color = R.color.pinkAccent;
                break;
            case Color.TEAL:
                color = R.color.tealAccent;
                break;
            case Color.AMBER:
                color = R.color.amberAccent;
                break;
            default:
                if (Module.isPro()) {
                    switch (code) {
                        case Color.DEEP_PURPLE:
                            color = R.color.purpleDeepAccent;
                            break;
                        case Color.DEEP_ORANGE:
                            color = R.color.orangeDeepAccent;
                            break;
                        case Color.LIME:
                            color = R.color.limeAccent;
                            break;
                        case Color.INDIGO:
                            color = R.color.indigoAccent;
                            break;
                        default:
                            color = R.color.blueAccent;
                            break;
                    }
                } else {
                    color = R.color.blueAccent;
                }
                break;
        }
        return getColor(color);
    }

    private interface Color {
        int RED = 0;
        int PURPLE = 1;
        int LIGHT_GREEN = 2;
        int GREEN = 3;
        int LIGHT_BLUE = 4;
        int BLUE = 5;
        int YELLOW = 6;
        int ORANGE = 7;
        int CYAN = 8;
        int PINK = 9;
        int TEAL = 10;
        int AMBER = 11;
        int DEEP_PURPLE = 12;
        int DEEP_ORANGE = 13;
        int LIME = 14;
        int INDIGO = 15;
    }
}
