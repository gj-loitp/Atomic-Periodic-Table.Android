package com.mckimquyen.atomicPeriodicTable.preferences

import android.content.Context
import android.content.SharedPreferences

class ThemePreference(context : Context) {

    val PREFERENCE_NAME = "Theme_Preference"
    val PREFERENCE_VALUE = "Theme_Value"

    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getValue() : Int{
        return preference.getInt (PREFERENCE_VALUE, 100)
    }

    fun setValue(count:Int) {
        val editor = preference.edit()
        editor.putInt(PREFERENCE_VALUE,count)
        editor.apply()
    }
}