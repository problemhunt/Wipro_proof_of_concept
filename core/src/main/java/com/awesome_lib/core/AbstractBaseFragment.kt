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

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

/**
 * [AbstractBaseFragment] :
 *
 * ## **Base** fragment class provides setup methods and functionality useful across entire app. ##
 * This fragment provides **abstract method** [AbstractBaseFragment.setUpBuilder]
 * that accepts configurations needed for fragment to work properly like `contentView` from [AbstractBaseFragment.builder].
 *
 *
 * Calls [AbstractBaseFragment.onViewReady] when setup completes,
 * so that *child fragment* receives callback regarding [Fragment.onViewCreated] happened.
 *
 * Check out some methods & variables:
 *
 *   1. #[AbstractBaseFragment.setUpBuilder]
 *   2. #[AbstractBaseFragment.onViewReady]
 *   3. #[AbstractBaseFragment.setUpToolbar]
 *   4. [AbstractBaseFragment.builder]
 *   5. [AbstractBaseFragment.rootView]
 *   6. [AbstractBaseFragment.sharedPreference]
 *   7. [AbstractBaseFragment.onHiddenChange]
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 7/2/2019
 */
abstract class AbstractBaseFragment : Fragment() {
    /**
     * Variable acts as utility for this fragment & provides necessary fields
     */
    var builder: AbstractBuilder? = null
        private set
    /**
     * Root [ViewGroup] of this fragment
     */
    var rootView: ViewGroup? = null
        private set
    /**
     * provides [SharedPreferences] object to handle local I/O
     */
    val sharedPreference: SharedPreferences? by lazy {
        return@lazy (activity as? AbstractBaseActivity)?.sharedPreference
    }
    /**
     * Variable provides callback for visibility changes (attach/Detach) of this fragment
     */
    var onHiddenChange: (Boolean.() -> Unit)? = null
    private var lifecycleCallbackProvider: LifecycleCallbackProvider? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        builder = setUpBuilder()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (builder?.isCacheFragment == true)
            onHiddenChange?.invoke(hidden)
        super.onHiddenChanged(hidden)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (builder?.abstractBinding != null) {
            builder?.contentView?.let {
                builder?.abstractBinding?.binding =
                    DataBindingUtil.inflate(inflater, it, container, false)
            }
            rootView = builder?.abstractBinding?.binding?.root as? ViewGroup
        } else {
            builder?.contentView?.let {
                rootView = inflater.inflate(it, container, false) as? ViewGroup
            }
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleCallbackProvider = LifecycleCallbackProvider(viewLifecycleOwner.lifecycle) {
            onViewReady(view, savedInstanceState)
        }
        builder?.abstractBinding?.let {
            this@AbstractBaseFragment.viewLifecycleOwner.lifecycle.addObserver(it)
            it.lifecycle = this@AbstractBaseFragment.viewLifecycleOwner.lifecycle
            it.binding?.lifecycleOwner = this@AbstractBaseFragment.viewLifecycleOwner
        }
        lifecycleCallbackProvider?.let {
            this@AbstractBaseFragment.viewLifecycleOwner.lifecycle.addObserver(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        builder?.abstractBinding?.let {
            this@AbstractBaseFragment.viewLifecycleOwner.lifecycle.removeObserver(it)
            it.lifecycle = null
        }
        builder = null
        lifecycleCallbackProvider?.let {
            this@AbstractBaseFragment.viewLifecycleOwner.lifecycle.removeObserver(it)
        }
        lifecycleCallbackProvider = null
    }

    /**
     * Method to replace content of [Toolbar] by provided view, uses [ToolbarUtil] internally to update view of [Toolbar].
     *
     * Provides [callback] to made appropriate changes on inflated [View] during callback
     *
     * @param toolbarId as [Int] resource id of toolbar object of which view needs to be updated
     * @param layoutId as [Int] layout res id that needed to be replaced/updated
     * @param cleanUpAll as boolean flag indicates whether remove all views from toolbar or not
     * @param callback as parameter when view is inflated on toolbar
     */
    fun setUpToolbar(
        @IdRes toolbarId: Int,
        @LayoutRes layoutId: Int,
        cleanUpAll: Boolean = false,
        callback: (View?.() -> Unit)? = null
    ) {
        val toolbarView =
            ToolbarUtil.addRemoveViewFromToolbar(activity, layoutId, cleanUpAll, toolbarId)
        callback?.invoke(toolbarView)
    }

    /**
     * Abstract method provides [AbstractBuilder] needed to build this base fragment.
     * Setup some base parameters from [AbstractBuilder] that this base requires and rest of it would be handled successfully.
     *
     * @return [AbstractBuilder] object required by this [AbstractBuilder] class.
     */
    abstract fun setUpBuilder(): AbstractBuilder

    /**
     * Callback method provided by this [AbstractBaseFragment], mimic behavior of [Fragment.onViewCreated]
     * requires **super** call
     */
    @CallSuper
    open fun onViewReady(view: View, savedInstanceState: Bundle?) {
        if (builder?.isCacheFragment == true)
            onHiddenChanged(false)
    }
}