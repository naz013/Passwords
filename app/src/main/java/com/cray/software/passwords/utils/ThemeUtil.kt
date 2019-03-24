package com.cray.software.passwords.utils

import android.content.Context
import androidx.annotation.IntRange
import androidx.annotation.StyleRes
import com.cray.software.passwords.R
import java.util.*

class ThemeUtil(private val context: Context, private val prefs: Prefs) {

    val isDark: Boolean
        get() {
            val appTheme = prefs.appTheme
            val isDark = appTheme  >= THEME_DARK
            if (appTheme == THEME_AUTO) {
                return isNight
            }
            return isDark
        }

    private val isNight: Boolean
        get() {
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

    val style: Int
        @StyleRes
        get() {
            return when (prefs.appTheme) {
                THEME_AUTO -> {
                    if (isDark) {
                        R.style.Dark3
                    } else {
                        R.style.Light1
                    }
                }
                THEME_WHITE -> R.style.PureWhite
                THEME_LIGHT -> R.style.Light1
                THEME_DARK -> R.style.Dark3
                THEME_AMOLED -> R.style.PureBlack
                else -> {
                    R.style.Dark3
                }
            }
        }

    val dialogStyle: Int
        @StyleRes
        get() {
            return when (prefs.appTheme) {
                THEME_AUTO -> {
                    if (isDark) {
                        R.style.Dark3_Dialog
                    } else {
                        R.style.Light1_Dialog
                    }
                }
                THEME_WHITE -> R.style.PureWhite_Dialog
                THEME_LIGHT -> R.style.Light1_Dialog
                THEME_DARK -> R.style.Dark3_Dialog
                THEME_AMOLED -> R.style.PureBlack_Dialog
                else -> {
                    R.style.Dark3_Dialog
                }
            }
        }

    fun adjustAlpha(color: Int, @IntRange(from = 0, to = 100) factor: Int): Int {
        val alpha = 255f * (factor.toFloat() / 100f)
        val red = android.graphics.Color.red(color)
        val green = android.graphics.Color.green(color)
        val blue = android.graphics.Color.blue(color)
        return android.graphics.Color.argb(alpha.toInt(), red, green, blue)
    }

    companion object {
        const val THEME_AUTO = 0
        const val THEME_WHITE = 1
        const val THEME_LIGHT = 2
        const val THEME_DARK = 3
        const val THEME_AMOLED = 4
    }
}
