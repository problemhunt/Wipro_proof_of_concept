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

@file:JvmName("ExtensionsKt")

package com.awesome_lib.core

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

private const val TAG = "Extensions"

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

@JvmOverloads
fun View.scaleUp(
    startX: Float = 1F,
    startY: Float = 1F,
    scaleX: Float = 1.1F,
    scaleY: Float = 1.1F,
    duration: Long = 700L
) {
    val animation = ScaleAnimation(
        startX,
        scaleX,
        startY,
        scaleY,
        Animation.RELATIVE_TO_SELF,
        0F,
        Animation.RELATIVE_TO_SELF,
        0.5F
    )
    animation.duration = duration
    animation.fillAfter = true
    this.startAnimation(animation)
}

@JvmOverloads
fun View.scaleDown(
    startX: Float = 1.1F,
    startY: Float = 1.1F,
    scaleX: Float = 1F,
    scaleY: Float = 1F,
    duration: Long = 700L
) {
    val animation = ScaleAnimation(
        startX,
        scaleX,
        startY,
        scaleY,
        Animation.RELATIVE_TO_SELF,
        0F,
        Animation.RELATIVE_TO_SELF,
        0.5F
    )
    animation.duration = duration
    animation.fillAfter = true
    this.startAnimation(animation)
}

fun View.animateVibrate() {
    this.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.lb_core_error_vibrate))
}

fun View.hideWithAnimation(onAnimationEnd: ((Animation?) -> Unit)? = null) {
    val initialHeight = this.measuredHeight
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1F) {
                this@hideWithAnimation.visibility = View.VISIBLE
            } else {
                this@hideWithAnimation.layoutParams.height =
                    initialHeight.minus(initialHeight.times(interpolatedTime)).toInt()
                this@hideWithAnimation.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    // 1dp/ms
    animation.duration =
        (this.context?.resources?.displayMetrics?.density?.let { initialHeight.div(it).toLong() })
            ?: 500L
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            /*Deliberately left unimplemented */
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            /*Deliberately left unimplemented */
        }
    })
    this.startAnimation(animation)
}

fun View.showWithAnimation(onAnimationEnd: ((Animation?) -> Unit)? = null) {
    this.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = this.measuredHeight
    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    this.layoutParams.height = 1
    this.visibility = View.VISIBLE
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            this@showWithAnimation.layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            this@showWithAnimation.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    animation.duration =
        (this.context?.resources?.displayMetrics?.density?.let { targetHeight.div(it).toLong() })
            ?: 500L
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            /*Deliberately left unimplemented */
        }

        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            /*Deliberately left unimplemented */
        }
    })
    this.startAnimation(animation)
}

fun <T> LiveData<T>?.notifyObservers() where T : Any? {
    val data = this?.value
    when (this) {
        is MutableLiveData -> {
            this.value = data
        }
        is MediatorLiveData -> {
            this.value = data
        }
    }
}

fun <T : View> T.animateValue(
    initValue: Int = 0,
    finalValue: Int,
    duration: Long = 1000L,
    transitionCallback: (T?, ValueAnimator?) -> Unit
) {
    val animator = ValueAnimator.ofInt(initValue, finalValue)
    animator?.duration = duration
    animator?.addUpdateListener {
        transitionCallback(this, it)
    }
    animator?.interpolator = AccelerateDecelerateInterpolator()
    animator?.start()
}

fun Context.dpToPx(dp: Int): Int {
    val displayMetrics = this.resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}


fun RecyclerView.setPaddingTop(dp: Int = 70) {
    this.setPadding(0, this.context.dpToPx(dp), 0, 0)
    this.clipToPadding = false
}

fun RecyclerView.setPaddingBottom(dp: Int = 70) {
    this.setPadding(0, 0, 0, this.context.dpToPx(dp))
    this.clipToPadding = false
}

fun <T : Any?> MutableCollection<T>?.removeItemIterator(condition: (T) -> Boolean) {
    val itr = this?.iterator()
    while (itr?.hasNext() == true) {
        val item = itr.next()
        if (condition(item))
            itr.remove()
    }
}

fun Context?.getResourceColor(@ColorRes color: Int): Int {
    var finalColor: Int = Color.TRANSPARENT
    this?.let {
        finalColor = ContextCompat.getColor(it, color)
    }
    return finalColor
}

fun Context?.getResourceDrawable(@DrawableRes drawableId: Int): Drawable? {
    var finalDrawable: Drawable? = null
    this?.let {
        finalDrawable = ContextCompat.getDrawable(it, drawableId)
    }
    return finalDrawable
}

var TextView.drawableStart: Drawable?
    get() = compoundDrawablesRelative[0]
    set(value) = setDrawables(start = value)
var TextView.drawableTop: Drawable?
    get() = compoundDrawablesRelative[1]
    set(value) = setDrawables(top = value)
var TextView.drawableEnd: Drawable?
    get() = compoundDrawablesRelative[2]
    set(value) = setDrawables(end = value)
var TextView.drawableBottom: Drawable?
    get() = compoundDrawablesRelative[3]
    set(value) = setDrawables(bottom = value)

fun TextView.setDrawables(
    start: Drawable? = null,
    top: Drawable? = null,
    end: Drawable? = null,
    bottom: Drawable? = null
) = setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)

fun PopupMenu?.enableIcons(fieldName: String = "mPopup") {
    this?.let { popup ->
        try {
            val fields = popup::class.java.declaredFields
            for (field in fields) {
                if (fieldName == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popup)
                    val classPopupHelper = Class.forName(menuPopupHelper::class.java.name)
                    val setForceIcons =
                        classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "enableIcons", e)
        }
    }
}

fun String?.getAsciiNumber(): Int {
    val arr = this?.toCharArray()
    var number = -1
    arr?.forEach {
        number += it.toInt()
    }
    return number
}

fun <T, OBJ> OBJ.runInBackground(code: OBJ.() -> T?): T? {
    var t: T? = null
    val runnable = object : Runnable {
        override fun run() {
            t = code()
        }

        fun getValue() = t
    }
    val thread = Thread(runnable)
    thread.start()
    thread.join()
    return runnable.getValue()
}

fun newBundle(bundle: Bundle.() -> Unit) = Bundle().apply(bundle)

fun newIntent(intent: Intent.() -> Unit) = Intent().apply(intent)

/**
 * Extension Method to determine active internet connection to perform API service based on [Context]
 * returns true/false based on active internet,
 * also provides callback [onConnectionEstablished] to detect change when connection re-established
 *
 * @param onConnectionEstablished to provide callback about active network with status [Boolean] flag
 *
 * @return [Boolean] flag about active network
 */
fun Context?.isNetworkConnected(onConnectionEstablished: ((Boolean?) -> Unit)? = null): Boolean {
    val connectivityManager =
        this?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    return if (connectivityManager?.activeNetworkInfo != null) {
        if (connectivityManager.activeNetworkInfo.isConnected) {
            true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                connectivityManager.addDefaultNetworkActiveListener {
                    onConnectionEstablished?.invoke(connectivityManager.activeNetworkInfo.isConnected)
                }
            }
            false
        }
    } else false
}

@JvmOverloads
fun Context?.showToast(
    message: String = "",
    @StringRes messageIdRes: Int? = null,
    isLong: Boolean = false
) {
    this?.let { ctx ->
        Toast.makeText(
            ctx,
            if (messageIdRes != null) ctx.getString(messageIdRes) else message,
            if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        )
            .show()
    }
}

/**
 * DSL-Method to provide imperative logic for object creation of [AbstractBuilder] with validations
 *
 * @param builder as lambda method parameter for setup as callback to apply
 *
 * @return [AbstractBuilder]
 */
fun absBuilder(builder: AbstractBuilder.() -> Unit): AbstractBuilder {
    val abstractBuilder = AbstractBuilder()
    abstractBuilder.apply(builder)
    return when {
        (abstractBuilder.contentView == null) or (abstractBuilder.contentView == -1) -> {
            throw IllegalArgumentException("Content view must not be null or -1")
        }
        else -> {
            abstractBuilder
        }
    }
}

/**
 * Extension method to launch activity class by simply passing [clazz] parameter,
 * provides additional callback [intent] if you want to pass some extras
 *
 * @param clazz of type [Class] as Activity to be launch
 * @param intent as callback parameter if you want to provide some intent extras of just simple leave as it is
 */
fun <T : Activity> Activity?.startNewActivity(
    clazz: Class<T>,
    intent: (Intent.() -> Unit)? = null
) {
    if (intent != null) {
        this?.startActivity(Intent(this, clazz).apply(intent))
    } else {
        this?.startActivity(Intent(this, clazz))
    }
}

/**
 * Extension method works same as [Activity.startNewActivity], only difference is passing request code [reqCode] to receive result on to.
 *
 * @param clazz of type [Class] as Activity to be launch
 * @param reqCode to track result of launched [Activity] in [Activity.onActivityResult]
 * @param intent as callback parameter if you want to provide some intent extras of just simple leave as it is
 */
fun <T : Activity> Activity?.startNewActivityForResult(
    clazz: Class<T>,
    reqCode: Int,
    intent: (Intent.() -> Unit)? = null
) {
    if (intent != null) {
        this?.startActivityForResult(Intent(this, clazz).apply(intent), reqCode)
    } else {
        this?.startActivityForResult(Intent(this, clazz), reqCode)
    }
}

/**
 * Extension method to launch activity class by simply passing [clazz] parameter,
 * provides additional callback [intent] if you want to pass some extras
 *
 * @param clazz of type [Class] as Activity to be launch
 * @param intent as callback parameter if you want to provide some intent extras of just simple leave as it is
 */
fun <T : Activity> Fragment?.startNewActivity(
    clazz: Class<T>,
    intent: (Intent.() -> Unit)? = null
) {
    if (intent != null) {
        this?.startActivity(Intent(this.activity, clazz).apply(intent))
    } else {
        this?.startActivity(Intent(this.activity, clazz))
    }
}

/**
 * Extension method works same as [Fragment.startNewActivity], only difference is passing request code [reqCode] to receive result on to.
 *
 * @param clazz of type [Class] as Activity to be launch
 * @param reqCode to track result of launched [Activity] in [Activity.onActivityResult]
 * @param intent as callback parameter if you want to provide some intent extras of just simple leave as it is
 */
fun <T : Activity> Fragment?.startNewActivityForResult(
    clazz: Class<T>,
    reqCode: Int,
    intent: (Intent.() -> Unit)? = null
) {
    if (intent != null) {
        this?.startActivityForResult(Intent(this.activity, clazz).apply(intent), reqCode)
    } else {
        this?.startActivityForResult(Intent(this.activity, clazz), reqCode)
    }
}
