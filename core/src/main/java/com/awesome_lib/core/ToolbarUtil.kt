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

package com.awesome_lib.core

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity

/**
 * [ToolbarUtil] : Util class for [Toolbar] that calculates views this particular toolbar is handling
 * and manages for refreshing child views by adding and removing child views from it.
 *
 * @author Mitesh Vanaliya
 * @since 03/01/2018
 * @version 1.0.1
 *
 * @see [Toolbar]
 * @see [ToolbarUtil.addRemoveViewFromToolbar]
 * @see [ToolbarUtil.removeViewFromToolbar]
 * @see [ToolbarUtil.setToolbarTitle]
 */
class ToolbarUtil private constructor() {
    init {
        // Initializing this class as object would be a fatal error, why? see message below.
        throw UnsupportedOperationException("Object construction is not necessary for this Util class")
    }

    companion object {
        /**
         * Method that add & remove child views from toolbar based on [fragmentActivity] context provided to it
         * with resource id passed as [resourceId]
         *
         * @param fragmentActivity as nullable context to [FragmentActivity] on which [Toolbar] is added
         * @param resourceId as resId of [Toolbar] in [FragmentActivity]
         *
         * @return view as Nullable [View] object, can be null or be a view object (Beware that !).
         */
        @JvmStatic
        fun addRemoveViewFromToolbar(
            fragmentActivity: FragmentActivity?,
            @LayoutRes resourceId: Int,
            cleanUpAll: Boolean = false,
            @IdRes toolbarId: Int
        ): View? {
            val toolbar =
                if (cleanUpAll) removeAllViewFromToolbar(
                    fragmentActivity,
                    toolbarId
                ) else removeViewFromToolbar(
                    fragmentActivity,
                    toolbarId
                )// Clean ups the toolbar
            return if (resourceId == 0) {
                null // return 'null' if resourceId is 0
            } else {
                val view: View? = LayoutInflater.from(fragmentActivity)
                    .inflate(resourceId, toolbar, false) // inflate view to toolbar from resource id
                toolbar?.addView(view) // Adding view to toolbar
                view // returns inflated view
            }
        }

        /**
         * Method to clean up toolbar by removing all of it's view from parent [Toolbar] view group
         * excluding view@zero (i.e. Hamburger icon or action icon with Id 'android.R.id.home')
         *
         * @param fragmentActivity as nullable context to [FragmentActivity] on which [Toolbar] is added
         *
         * @return [Toolbar] as cleaned up toolbar with only one child view@zero
         *
         * @version 1.0.1 by Jeel Vankhede : Fixed issue for [NullPointerException] when removing view from [Toolbar]
         */
        @JvmStatic
        private fun removeViewFromToolbar(fragmentActivity: FragmentActivity?, @IdRes toolbarId: Int) =
            removeToolbarExceptBackArrow(fragmentActivity?.findViewById(toolbarId)) // Find view id of Toolbar layout for specified fragment activity

        /**
         * Method to clean up toolbar by removing all of it's view from parent [Toolbar] view group
         *
         * @param fragmentActivity as nullable context to [FragmentActivity] on which [Toolbar] is added
         *
         * @return [Toolbar] as cleaned up toolbar with only one child view@zero
         *
         * @version 1.0.1 by Jeel Vankhede : Fixed issue for [NullPointerException] when removing view from [Toolbar]
         */
        @JvmStatic
        private fun removeAllViewFromToolbar(fragmentActivity: FragmentActivity?, @IdRes toolbarId: Int) =
            removeAllToolbarViewFromActivity(fragmentActivity?.findViewById(toolbarId)) // Find view id of Toolbar layout for specified fragment activity

        /**
         * Method inherited from [setToolbarTitle] with more dynamic approach to set [title] based on [resourceId]
         * passed on [parentView] as Child wrapper inside toolbar
         *
         * @param parentView as Child wrapper view container as direct child to [Toolbar]
         * @param resourceId as [Int] resource id of [TextView] that relies inside [parentView] acting as [Toolbar] title
         * @param title can be "Nullable" [String] as if title found nullable than 'empty string' would be considered as by default value
         */
        @JvmStatic
        fun setToolbarTitle(parentView: View?, @IdRes resourceId: Int, title: String? = "") {
            val toolbarTextView = parentView?.findViewById<TextView>(resourceId)
            toolbarTextView?.text = title
        }

        /**
         * Method to set [imageId] based on [resourceId] for icon passed on [parentView] as Child wrapper inside toolbar
         *
         * @param parentView as Child wrapper view container as direct child to [Toolbar]
         * @param resourceId as [Int] resource id of [ImageView] that relies inside [parentView] acting as [Toolbar] icon
         * @param imageId as resource for [ImageView] of toolbar icon
         */
        @JvmStatic
        fun setToolbarIcon(parentView: View?, @IdRes resourceId: Int, imageId: Int) {
            val toolbarImageView = parentView?.findViewById<ImageView>(resourceId)
            toolbarImageView?.setImageResource(imageId)
        }

        //For activity

        /**
         * Method that add & remove child views from toolbar based on [fragmentActivity] context provided to it
         * with resource id passed as [resourceId]
         *
         * @param fragmentActivity as nullable context to [FragmentActivity] on which [Toolbar] is added
         * @param resourceId as resId of [Toolbar] in [FragmentActivity]
         *
         * @return view as Nullable [View] object, can be null or be a view object (Beware that !).
         */
        @JvmStatic
        fun updateToolbarViewFromActivity(
            fragmentActivity: FragmentActivity?,
            @LayoutRes resourceId: Int,
            cleanUpAll: Boolean = false,
            toolbar: Toolbar?
        ): View? {
            val updatedToolbar =
                if (cleanUpAll) removeAllToolbarViewFromActivity(toolbar) else removeToolbarExceptBackArrow(
                    toolbar
                )// Clean ups the toolbar
            return if (resourceId == 0) {
                null // return 'null' if resourceId is 0
            } else {
                val view: View? = LayoutInflater.from(fragmentActivity)
                    .inflate(
                        resourceId,
                        updatedToolbar,
                        false
                    ) // inflate view to toolbar from resource id
                updatedToolbar?.addView(view) // Adding view to toolbar
                view // returns inflated view
            }
        }

        /**
         * Method to clean up toolbar by removing all of it's view from parent [Toolbar] view group
         * excluding view@zero (i.e. Hamburger icon or action icon with Id 'android.R.id.home')
         *
         * @return [Toolbar] as cleaned up toolbar with only one child view@zero
         *
         * @version 1.0.1 by Jeel Vankhede : Fixed issue for [NullPointerException] when removing view from [Toolbar]
         */
        @JvmStatic
        fun removeToolbarExceptBackArrow(toolbar: Toolbar?): Toolbar? {
            if (toolbar != null) { // Checking for "Null safety" on toolbar
                if (toolbar.childCount > 1) { // At least more than one child be there in toolbar
                    for (i in 1 until toolbar.childCount) { // let's remove all child from
                        toolbar.removeViewAt(toolbar.childCount.minus(1)) // removing child at top of stack (last element left in toolbar, just like pop out)
                    }
                }
            }
            return toolbar
        }

        @JvmStatic
        fun removeAllToolbarViewFromActivity(toolbar: Toolbar?): Toolbar? {
            if (toolbar != null) { // Checking for "Null safety" on toolbar
                if (toolbar.childCount > 0) { // At least more than one child be there in toolbar
                    for (i in 0 until toolbar.childCount) { // let's remove all child from
                        toolbar.removeViewAt(toolbar.childCount.minus(1)) // removing child at top of stack (last element left in toolbar, just like pop out)
                    }
                }
            }
            return toolbar
        }
    }
}