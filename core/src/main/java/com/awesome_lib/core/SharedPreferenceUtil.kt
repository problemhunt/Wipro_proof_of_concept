/*
 * Copyright (C) 2019 Jeel Vankhedde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("SharedPreferenceUtil")

package com.awesome_lib.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Extension method that provides [SharedPreferences] object called upon,
 * provide [fileName] of your shared preference whereas default value is \"app-prefs\"
 */
@JvmOverloads
fun Context?.getDefaultPreference(
    fileName: String? = "app-prefs",
    mode: Int = Context.MODE_PRIVATE
): SharedPreferences? =
    this?.getSharedPreferences(fileName, mode)

/**
 * Extension on [SharedPreferences] to put value of [T] type synchronously passed as [value] based on [key] String provided,
 * determines value based on reified type [T].
 */
inline fun <reified T : Any?> SharedPreferences?.putValue(key: String, value: T) {
    val editor = this?.edit()
    when (value) {
        is Int -> {
            editor?.putInt(key, value)
        }
        is Long -> {
            editor?.putLong(key, value)
        }
        is Float -> {
            editor?.putFloat(key, value)
        }
        is Double -> {
            editor?.putString(key, value.toString())
        }
        is Boolean -> {
            editor?.putBoolean(key, value)
        }
        is String -> {
            editor?.putString(key, value)
        }
        else -> {
            // Left out because "Unused"
        }
    }
    editor?.commit()
}

/**
 * Extension on [SharedPreferences] to put value of [T] type asynchronously passed as [value] based on [key] String provided,
 * determines value based on reified type [T].
 */
inline fun <reified T : Any> SharedPreferences?.putValueAsync(key: String, value: T) {
    val editor = this?.edit()
    when (value) {
        is Int -> {
            editor?.putInt(key, value)
        }
        is Long -> {
            editor?.putLong(key, value)
        }
        is Float -> {
            editor?.putFloat(key, value)
        }
        is Double -> {
            editor?.putString(key, value.toString())
        }
        is Boolean -> {
            editor?.putBoolean(key, value)
        }
        is String -> {
            editor?.putString(key, value)
        }
        else -> {
            // Left out because "Unused"
        }
    }
    editor?.apply()
}

/**
 * Extension on [SharedPreferences] to retrieve value of [T] type based on [key] String provided,
 * determines value based on reified type [T] else **null** if something is wrong.
 */
inline fun <reified T : Any> SharedPreferences?.getValue(key: String): T? {
    return when (T::class) {
        Int::class -> {
            this?.getInt(key, -1) as? T
        }
        Long::class -> {
            this?.getLong(key, 0L) as? T
        }
        Float::class -> {
            this?.getFloat(key, 0F) as? T
        }
        Double::class -> {
            this?.getString(key, "0.00")?.toDouble() as? T
        }
        Boolean::class -> {
            this?.getBoolean(key, false) as? T
        }
        String::class -> {
            this?.getString(key, "") as? T
        }
        else -> {
            null
        }
    }
}

/**
 * Extension on [SharedPreferences] to remove item from preference based on [key] String provided asynchronously.
 */
fun SharedPreferences?.removeItem(key: String) {
    this?.edit()?.remove(key)?.apply()
}

/**
 * Extension on [SharedPreferences] to clear entire shared preference asynchronously.
 */
fun SharedPreferences?.clearAll() {
    this?.edit()?.clear()?.apply()
}