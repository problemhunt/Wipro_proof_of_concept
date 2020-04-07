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

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil

/**
 * [AbstractBaseActivity] :
 *
 * ## **Base** activity class provides setup methods and functionality useful across entire app. ##
 * This activity provides **abstract method** [AbstractBaseActivity.setUpBuilder]
 * that accepts configurations needed for activity to work properly like `contentView` from [AbstractBaseActivity.builder].
 *
 *
 * Calls [AbstractBaseActivity.onViewReady] when setup completes,
 * so that *child activity* receives callback regarding [AppCompatActivity.onCreate] happened.
 *
 * Check out some methods & variables:
 *
 *   1. #[AbstractBaseActivity.setUpBuilder]
 *   2. #[AbstractBaseActivity.onViewReady]
 *   3. #[AbstractBaseActivity.setUpToolbar]
 *   4. #[AbstractBaseActivity.showBackArrow]
 *   5. [AbstractBaseActivity.builder]
 *   6. [AbstractBaseActivity.rootView]
 *   7. [AbstractBaseActivity.sharedPreference]
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 7/3/2019
 */
abstract class AbstractBaseActivity : AppCompatActivity() {
    /**
     * Variable acts as utility for this activity & provides necessary fields
     */
    var builder: AbstractBuilder? = null
        private set
    /**
     * Root [ViewGroup] of this activity
     */
    var rootView: ViewGroup? = null
        private set
    /**
     * provides [SharedPreferences] object to handle local I/O
     */
    val sharedPreference: SharedPreferences? by lazy {
        return@lazy this.getDefaultPreference()
    }
    private var lifecycleCallbackProvider: LifecycleCallbackProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = setUpBuilder()
        setUiContent()
        lifecycleCallbackProvider = LifecycleCallbackProvider(lifecycle) {
            onViewReady(savedInstanceState)
        }
        provideLifeCycle()
    }

    /**
     * Method helps you set back arrow on Action bar to navigate up/back from it.
     */
    fun showBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setUiContent() {
        if (builder?.abstractBinding != null) {
            builder?.contentView?.let {
                builder?.abstractBinding?.binding = DataBindingUtil.setContentView(this, it)
            }
            rootView = builder?.abstractBinding?.binding?.root as? ViewGroup
        } else {
            builder?.contentView?.let {
                setContentView(it)
                rootView = window?.decorView?.findViewById<ViewGroup?>(android.R.id.content)
            }
        }
    }

    private fun provideLifeCycle() {
        builder?.abstractBinding?.let {
            this@AbstractBaseActivity.lifecycle.addObserver(it)
            it.lifecycle = this@AbstractBaseActivity.lifecycle
            it.binding?.lifecycleOwner = this@AbstractBaseActivity
        }
        lifecycleCallbackProvider?.let {
            this@AbstractBaseActivity.lifecycle.addObserver(it)
        }
    }

    private fun revokeLifeCycle() {
        builder?.abstractBinding?.let {
            this@AbstractBaseActivity.lifecycle.removeObserver(it)
            it.lifecycle = null
        }
        lifecycleCallbackProvider?.let {
            this@AbstractBaseActivity.lifecycle.removeObserver(it)
        }
        lifecycleCallbackProvider = null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        revokeLifeCycle()
    }

    /**
     * Method to replace content of [Toolbar] by provided view, uses [ToolbarUtil] internally to update view of [Toolbar].
     *
     * Provides [callback] to made appropriate changes on inflated [View] during callback
     *
     * @param toolbar as [Toolbar] object of which view needs to be updated
     * @param showBackArrow as flag whether action bar should show navigation up as home
     * @param layoutId as [Int] layout res id that needed to be replaced/updated
     * @param callback as parameter when view is inflated on toolbar
     */
    fun setUpToolbar(
        toolbar: Toolbar?,
        showBackArrow: Boolean = true,
        @LayoutRes layoutId: Int? = null,
        callback: (View?.() -> Unit)? = null
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        layoutId?.let {
            val toolbarView = ToolbarUtil.updateToolbarViewFromActivity(this, it, true, toolbar)
            callback?.invoke(toolbarView)
        }
        if (showBackArrow) {
            showBackArrow()
        }
    }

    /**
     * Abstract method provides [AbstractBuilder] needed to build this base activity.
     * Setup some base parameters from [AbstractBuilder] that this base requires and rest of it would be handled successfully.
     *
     * @return [AbstractBuilder] object required by this [AbstractBaseActivity] class.
     */
    abstract fun setUpBuilder(): AbstractBuilder

    /**
     * Callback method provided by this [AbstractBaseActivity] when all setup of [onCreate] completes
     */
    abstract fun onViewReady(savedInstanceState: Bundle?)
}