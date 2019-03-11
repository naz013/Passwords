package com.cray.software.passwords.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import com.cray.software.passwords.R
import com.cray.software.passwords.interfaces.Module
import java.util.*

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

class ThemeUtil(private val context: Context, private val prefs: Prefs) {

    val isDark: Boolean
        get() {
            val appTheme = prefs.appTheme
            val isDark = appTheme == THEME_DARK || appTheme == THEME_AMOLED
            if (appTheme == THEME_AUTO) {
                val calendar = Calendar.getInstance()
                val mTime = System.currentTimeMillis()
                calendar.timeInMillis = mTime
                calendar.set(Calendar.HOUR_OF_DAY, 8)
                calendar.set(Calendar.MINUTE, 0)
                val min = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 19)
                val max = calendar.timeInMillis
                return mTime !in min..max
            }
            return isDark
        }

    val style: Int
        @StyleRes
        get() {
            val id: Int
            val loadedColor = prefs.appThemeColor
            if (isDark) {
                if (prefs.appTheme == THEME_AMOLED) {
                    when (loadedColor) {
                        Color.RED -> id = R.style.HomeBlack_Red
                        Color.PURPLE -> id = R.style.HomeBlack_Purple
                        Color.LIGHT_GREEN -> id = R.style.HomeBlack_LightGreen
                        Color.GREEN -> id = R.style.HomeBlack_Green
                        Color.LIGHT_BLUE -> id = R.style.HomeBlack_LightBlue
                        Color.BLUE -> id = R.style.HomeBlack_Blue
                        Color.YELLOW -> id = R.style.HomeBlack_Yellow
                        Color.ORANGE -> id = R.style.HomeBlack_Orange
                        Color.CYAN -> id = R.style.HomeBlack_Cyan
                        Color.PINK -> id = R.style.HomeBlack_Pink
                        Color.TEAL -> id = R.style.HomeBlack_Teal
                        Color.AMBER -> id = R.style.HomeBlack_Amber
                        else -> id = if (Module.isPro) {
                            when (loadedColor) {
                                Color.DEEP_PURPLE -> R.style.HomeBlack_DeepPurple
                                Color.DEEP_ORANGE -> R.style.HomeBlack_DeepOrange
                                Color.LIME -> R.style.HomeBlack_Lime
                                Color.INDIGO -> R.style.HomeBlack_Indigo
                                else -> R.style.HomeBlack_Blue
                            }
                        } else {
                            R.style.HomeBlack_Blue
                        }
                    }
                } else {
                    when (loadedColor) {
                        Color.RED -> id = R.style.HomeDark_Red
                        Color.PURPLE -> id = R.style.HomeDark_Purple
                        Color.LIGHT_GREEN -> id = R.style.HomeDark_LightGreen
                        Color.GREEN -> id = R.style.HomeDark_Green
                        Color.LIGHT_BLUE -> id = R.style.HomeDark_LightBlue
                        Color.BLUE -> id = R.style.HomeDark_Blue
                        Color.YELLOW -> id = R.style.HomeDark_Yellow
                        Color.ORANGE -> id = R.style.HomeDark_Orange
                        Color.CYAN -> id = R.style.HomeDark_Cyan
                        Color.PINK -> id = R.style.HomeDark_Pink
                        Color.TEAL -> id = R.style.HomeDark_Teal
                        Color.AMBER -> id = R.style.HomeDark_Amber
                        else -> id = if (Module.isPro) {
                            when (loadedColor) {
                                Color.DEEP_PURPLE -> R.style.HomeDark_DeepPurple
                                Color.DEEP_ORANGE -> R.style.HomeDark_DeepOrange
                                Color.LIME -> R.style.HomeDark_Lime
                                Color.INDIGO -> R.style.HomeDark_Indigo
                                else -> R.style.HomeDark_Blue
                            }
                        } else {
                            R.style.HomeDark_Blue
                        }
                    }
                }
            } else {
                when (loadedColor) {
                    Color.RED -> id = R.style.HomeWhite_Red
                    Color.PURPLE -> id = R.style.HomeWhite_Purple
                    Color.LIGHT_GREEN -> id = R.style.HomeWhite_LightGreen
                    Color.GREEN -> id = R.style.HomeWhite_Green
                    Color.LIGHT_BLUE -> id = R.style.HomeWhite_LightBlue
                    Color.BLUE -> id = R.style.HomeWhite_Blue
                    Color.YELLOW -> id = R.style.HomeWhite_Yellow
                    Color.ORANGE -> id = R.style.HomeWhite_Orange
                    Color.CYAN -> id = R.style.HomeWhite_Cyan
                    Color.PINK -> id = R.style.HomeWhite_Pink
                    Color.TEAL -> id = R.style.HomeWhite_Teal
                    Color.AMBER -> id = R.style.HomeWhite_Amber
                    else -> id = if (Module.isPro) {
                        when (loadedColor) {
                            Color.DEEP_PURPLE -> R.style.HomeWhite_DeepPurple
                            Color.DEEP_ORANGE -> R.style.HomeWhite_DeepOrange
                            Color.LIME -> R.style.HomeWhite_Lime
                            Color.INDIGO -> R.style.HomeWhite_Indigo
                            else -> R.style.HomeWhite_Blue
                        }
                    } else {
                        R.style.HomeWhite_Blue
                    }
                }
            }
            return id
        }

    val indicator: Int
        @DrawableRes
        get() = getIndicator(prefs.appThemeColor)

    val dialogStyle: Int
        @StyleRes
        get() {
            val id: Int
            val loadedColor = prefs.appThemeColor
            if (isDark) {
                if (prefs.appTheme == THEME_AMOLED) {
                    when (loadedColor) {
                        Color.RED -> id = R.style.HomeBlackDialog_Red
                        Color.PURPLE -> id = R.style.HomeBlackDialog_Purple
                        Color.LIGHT_GREEN -> id = R.style.HomeBlackDialog_LightGreen
                        Color.GREEN -> id = R.style.HomeBlackDialog_Green
                        Color.LIGHT_BLUE -> id = R.style.HomeBlackDialog_LightBlue
                        Color.BLUE -> id = R.style.HomeBlackDialog_Blue
                        Color.YELLOW -> id = R.style.HomeBlackDialog_Yellow
                        Color.ORANGE -> id = R.style.HomeBlackDialog_Orange
                        Color.CYAN -> id = R.style.HomeBlackDialog_Cyan
                        Color.PINK -> id = R.style.HomeBlackDialog_Pink
                        Color.TEAL -> id = R.style.HomeBlackDialog_Teal
                        Color.AMBER -> id = R.style.HomeBlackDialog_Amber
                        else -> id = if (Module.isPro) {
                            when (loadedColor) {
                                Color.DEEP_PURPLE -> R.style.HomeBlackDialog_DeepPurple
                                Color.DEEP_ORANGE -> R.style.HomeBlackDialog_DeepOrange
                                Color.LIME -> R.style.HomeBlackDialog_Lime
                                Color.INDIGO -> R.style.HomeBlackDialog_Indigo
                                else -> R.style.HomeBlackDialog_Blue
                            }
                        } else {
                            R.style.HomeBlackDialog_Blue
                        }
                    }
                } else {
                    when (loadedColor) {
                        Color.RED -> id = R.style.HomeDarkDialog_Red
                        Color.PURPLE -> id = R.style.HomeDarkDialog_Purple
                        Color.LIGHT_GREEN -> id = R.style.HomeDarkDialog_LightGreen
                        Color.GREEN -> id = R.style.HomeDarkDialog_Green
                        Color.LIGHT_BLUE -> id = R.style.HomeDarkDialog_LightBlue
                        Color.BLUE -> id = R.style.HomeDarkDialog_Blue
                        Color.YELLOW -> id = R.style.HomeDarkDialog_Yellow
                        Color.ORANGE -> id = R.style.HomeDarkDialog_Orange
                        Color.CYAN -> id = R.style.HomeDarkDialog_Cyan
                        Color.PINK -> id = R.style.HomeDarkDialog_Pink
                        Color.TEAL -> id = R.style.HomeDarkDialog_Teal
                        Color.AMBER -> id = R.style.HomeDarkDialog_Amber
                        else -> id = if (Module.isPro) {
                            when (loadedColor) {
                                Color.DEEP_PURPLE -> R.style.HomeDarkDialog_DeepPurple
                                Color.DEEP_ORANGE -> R.style.HomeDarkDialog_DeepOrange
                                Color.LIME -> R.style.HomeDarkDialog_Lime
                                Color.INDIGO -> R.style.HomeDarkDialog_Indigo
                                else -> R.style.HomeDarkDialog_Blue
                            }
                        } else {
                            R.style.HomeDarkDialog_Blue
                        }
                    }
                }
            } else {
                when (loadedColor) {
                    Color.RED -> id = R.style.HomeWhiteDialog_Red
                    Color.PURPLE -> id = R.style.HomeWhiteDialog_Purple
                    Color.LIGHT_GREEN -> id = R.style.HomeWhiteDialog_LightGreen
                    Color.GREEN -> id = R.style.HomeWhiteDialog_Green
                    Color.LIGHT_BLUE -> id = R.style.HomeWhiteDialog_LightBlue
                    Color.BLUE -> id = R.style.HomeWhiteDialog_Blue
                    Color.YELLOW -> id = R.style.HomeWhiteDialog_Yellow
                    Color.ORANGE -> id = R.style.HomeWhiteDialog_Orange
                    Color.CYAN -> id = R.style.HomeWhiteDialog_Cyan
                    Color.PINK -> id = R.style.HomeWhiteDialog_Pink
                    Color.TEAL -> id = R.style.HomeWhiteDialog_Teal
                    Color.AMBER -> id = R.style.HomeWhiteDialog_Amber
                    else -> id = if (Module.isPro) {
                        when (loadedColor) {
                            Color.DEEP_PURPLE -> R.style.HomeWhiteDialog_DeepPurple
                            Color.DEEP_ORANGE -> R.style.HomeWhiteDialog_DeepOrange
                            Color.LIME -> R.style.HomeWhiteDialog_Lime
                            Color.INDIGO -> R.style.HomeWhiteDialog_Indigo
                            else -> R.style.HomeWhiteDialog_Blue
                        }
                    } else {
                        R.style.HomeWhiteDialog_Blue
                    }
                }
            }
            return id
        }

    val backgroundStyle: Int
        @ColorInt
        get() {
            return if (isDark) {
                if (prefs.appTheme == THEME_AMOLED) {
                    getColor(R.color.colorBlack)
                } else {
                    getColor(R.color.material_grey)
                }
            } else {
                getColor(R.color.material_white)
            }
        }

    val cardStyle: Int
        @ColorInt
        get() {
            return if (isDark) {
                if (prefs.appTheme == THEME_AMOLED) {
                    getColor(R.color.colorBlack)
                } else {
                    getColor(R.color.grey_x)
                }
            } else {
                getColor(R.color.colorWhite)
            }
        }

    @ColorInt
    fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    @ColorRes
    @JvmOverloads
    fun colorAccent(code: Int = prefs.appThemeColor): Int {
        val color: Int
        if (isDark) {
            when (code) {
                Color.RED -> color = R.color.indigoAccent
                Color.PURPLE -> color = R.color.amberAccent
                Color.LIGHT_GREEN -> color = R.color.pinkAccent
                Color.GREEN -> color = R.color.purpleAccent
                Color.LIGHT_BLUE -> color = R.color.yellowAccent
                Color.BLUE -> color = R.color.redAccent
                Color.YELLOW -> color = R.color.redAccent
                Color.ORANGE -> color = R.color.greenAccent
                Color.CYAN -> color = R.color.purpleDeepAccent
                Color.PINK -> color = R.color.blueLightAccent
                Color.TEAL -> color = R.color.pinkAccent
                Color.AMBER -> color = R.color.blueAccent
                else -> color = if (Module.isPro) {
                    when (code) {
                        Color.DEEP_PURPLE -> R.color.greenAccent
                        Color.DEEP_ORANGE -> R.color.purpleAccent
                        Color.LIME -> R.color.redAccent
                        Color.INDIGO -> R.color.pinkAccent
                        else -> R.color.redAccent
                    }
                } else {
                    R.color.redAccent
                }
            }
        } else {
            when (code) {
                Color.RED -> color = R.color.indigoAccent
                Color.PURPLE -> color = R.color.amberAccent
                Color.LIGHT_GREEN -> color = R.color.purpleDeepAccent
                Color.GREEN -> color = R.color.cyanAccent
                Color.LIGHT_BLUE -> color = R.color.pinkAccent
                Color.BLUE -> color = R.color.yellowAccent
                Color.YELLOW -> color = R.color.cyanAccent
                Color.ORANGE -> color = R.color.pinkAccent
                Color.CYAN -> color = R.color.redAccent
                Color.PINK -> color = R.color.cyanAccent
                Color.TEAL -> color = R.color.redAccent
                Color.AMBER -> color = R.color.indigoAccent
                else -> color = if (Module.isPro) {
                    when (code) {
                        Color.DEEP_PURPLE -> R.color.greenLightAccent
                        Color.DEEP_ORANGE -> R.color.purpleDeepAccent
                        Color.LIME -> R.color.purpleAccent
                        Color.INDIGO -> R.color.pinkAccent
                        else -> R.color.yellowAccent
                    }
                } else {
                    R.color.yellowAccent
                }
            }
        }
        return color
    }

    @ColorRes
    @JvmOverloads
    fun colorPrimary(code: Int = prefs.appThemeColor): Int {
        val color: Int
        when (code) {
            Color.RED -> color = R.color.redPrimary
            Color.PURPLE -> color = R.color.purplePrimary
            Color.LIGHT_GREEN -> color = R.color.greenLightPrimary
            Color.GREEN -> color = R.color.greenPrimary
            Color.LIGHT_BLUE -> color = R.color.blueLightPrimary
            Color.BLUE -> color = R.color.bluePrimary
            Color.YELLOW -> color = R.color.yellowPrimary
            Color.ORANGE -> color = R.color.orangePrimary
            Color.CYAN -> color = R.color.cyanPrimary
            Color.PINK -> color = R.color.pinkPrimary
            Color.TEAL -> color = R.color.tealPrimary
            Color.AMBER -> color = R.color.amberPrimary
            else -> color = if (Module.isPro) {
                when (code) {
                    Color.DEEP_PURPLE -> R.color.purpleDeepPrimary
                    Color.DEEP_ORANGE -> R.color.orangeDeepPrimary
                    Color.LIME -> R.color.limePrimary
                    Color.INDIGO -> R.color.indigoPrimary
                    else -> R.color.cyanPrimary
                }
            } else {
                R.color.cyanPrimary
            }
        }
        return color
    }

    @DrawableRes
    fun getIndicator(color: Int): Int {
        val drawable: Int
        when (color) {
            Color.RED -> drawable = R.drawable.drawable_red
            Color.PURPLE -> drawable = R.drawable.drawable_purple
            Color.LIGHT_GREEN -> drawable = R.drawable.drawable_green_light
            Color.GREEN -> drawable = R.drawable.drawable_green
            Color.LIGHT_BLUE -> drawable = R.drawable.drawable_blue_light
            Color.BLUE -> drawable = R.drawable.drawable_blue
            Color.YELLOW -> drawable = R.drawable.drawable_yellow
            Color.ORANGE -> drawable = R.drawable.drawable_orange
            Color.CYAN -> drawable = R.drawable.drawable_cyan
            Color.PINK -> drawable = R.drawable.drawable_pink
            Color.TEAL -> drawable = R.drawable.drawable_teal
            Color.AMBER -> drawable = R.drawable.drawable_amber
            else -> drawable = if (Module.isPro) {
                when (color) {
                    Color.DEEP_PURPLE -> R.drawable.drawable_deep_purple
                    Color.DEEP_ORANGE -> R.drawable.drawable_deep_orange
                    Color.LIME -> R.drawable.drawable_lime
                    Color.INDIGO -> R.drawable.drawable_indigo
                    else -> R.drawable.drawable_cyan
                }
            } else {
                R.drawable.drawable_cyan
            }
        }
        return drawable
    }

    private fun getDrawable(@DrawableRes i: Int): Drawable {
        return ViewUtils.getDrawable(context, i)
    }

    @ColorRes
    fun colorPrimaryDark(code: Int): Int {
        val color: Int
        when (code) {
            Color.RED -> color = R.color.redPrimaryDark
            Color.PURPLE -> color = R.color.purplePrimaryDark
            Color.LIGHT_GREEN -> color = R.color.greenLightPrimaryDark
            Color.GREEN -> color = R.color.greenPrimaryDark
            Color.LIGHT_BLUE -> color = R.color.blueLightPrimaryDark
            Color.BLUE -> color = R.color.bluePrimaryDark
            Color.YELLOW -> color = R.color.yellowPrimaryDark
            Color.ORANGE -> color = R.color.orangePrimaryDark
            Color.CYAN -> color = R.color.cyanPrimaryDark
            Color.PINK -> color = R.color.pinkPrimaryDark
            Color.TEAL -> color = R.color.tealPrimaryDark
            Color.AMBER -> color = R.color.amberPrimaryDark
            else -> color = if (Module.isPro) {
                when (code) {
                    Color.DEEP_PURPLE -> R.color.purpleDeepPrimaryDark
                    Color.DEEP_ORANGE -> R.color.orangeDeepPrimaryDark
                    Color.LIME -> R.color.limePrimaryDark
                    Color.INDIGO -> R.color.indigoPrimaryDark
                    else -> R.color.cyanPrimaryDark
                }
            } else {
                R.color.cyanPrimaryDark
            }
        }
        return color
    }

    @ColorRes
    fun colorPrimaryDark(): Int {
        val loadedColor = prefs.appThemeColor
        return colorPrimaryDark(loadedColor)
    }

    @ColorInt
    fun getNoteColor(code: Int): Int {
        return getColor(colorPrimary(code))
    }

    @ColorInt
    fun getNoteDarkColor(code: Int): Int {
        return getColor(colorPrimaryDark(code))
    }

    fun adjustAlpha(color: Int, @IntRange(from = 0, to = 100) factor: Int): Int {
        val alpha = 255f * (factor.toFloat() / 100f)
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)
        return android.graphics.Color.argb(alpha.toInt(), red, green, blue)
    }

    @ColorInt
    fun getNoteLightColor(code: Int): Int {
        val color: Int
        when (code) {
            Color.RED -> color = R.color.redAccent
            Color.PURPLE -> color = R.color.purpleAccent
            Color.GREEN -> color = R.color.greenAccent
            Color.LIGHT_GREEN -> color = R.color.greenLightAccent
            Color.BLUE -> color = R.color.blueAccent
            Color.LIGHT_BLUE -> color = R.color.blueLightAccent
            Color.YELLOW -> color = R.color.yellowAccent
            Color.ORANGE -> color = R.color.orangeAccent
            Color.CYAN -> color = R.color.cyanAccent
            Color.PINK -> color = R.color.pinkAccent
            Color.TEAL -> color = R.color.tealAccent
            Color.AMBER -> color = R.color.amberAccent
            else -> color = if (Module.isPro) {
                when (code) {
                    Color.DEEP_PURPLE -> R.color.purpleDeepAccent
                    Color.DEEP_ORANGE -> R.color.orangeDeepAccent
                    Color.LIME -> R.color.limeAccent
                    Color.INDIGO -> R.color.indigoAccent
                    else -> R.color.blueAccent
                }
            } else {
                R.color.blueAccent
            }
        }
        return getColor(color)
    }

    private interface Color {
        companion object {
            val RED = 0
            val PURPLE = 1
            val LIGHT_GREEN = 2
            val GREEN = 3
            val LIGHT_BLUE = 4
            val BLUE = 5
            val YELLOW = 6
            val ORANGE = 7
            val CYAN = 8
            val PINK = 9
            val TEAL = 10
            val AMBER = 11
            val DEEP_PURPLE = 12
            val DEEP_ORANGE = 13
            val LIME = 14
            val INDIGO = 15
        }
    }

    companion object {

        val THEME_AUTO = 0
        val THEME_WHITE = 1
        private val THEME_DARK = 2
        val THEME_AMOLED = 3
    }
}
