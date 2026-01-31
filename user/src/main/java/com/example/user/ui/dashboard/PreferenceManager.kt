package com.example.user.ui.dashboard

import android.content.Context

object PreferenceManager {
    private const val PREF_NAME = "MyPrefs"
    private const val KEY_IS_BANNER_SHOWN = "isBannerShown"

    fun setIsBannerShown(context: Context, isShown: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_BANNER_SHOWN, isShown)
        editor.apply()
    }

    fun isBannerShown(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_BANNER_SHOWN, false)
    }
}
