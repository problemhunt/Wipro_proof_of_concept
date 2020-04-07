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
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * [AbstractBinding] :
 *
 * ## Abstract class that represent binding of particular layout file provided by [AbstractBinding.binding]. ##
 *
 * Class provides [ViewDataBinding] object created on inflation to further use from base setup and hold UI logic separate from `Activity/Fragment`
 * to help to maintain code in such a way that `Activity/Fragment` doesn't become too much messy and can be maintained easily.
 *
 * Class also provides callback of [Lifecycle.Event.ON_CREATE] & [Lifecycle.Event.ON_DESTROY] attached to particular ViewLifeCycleObserver of `Activity/Fragment`
 * to do some initialization/destruction about it,
 * provides [AbstractBinding.lifecycle] object about it.
 *
 * Check out some methods & variables:
 *
 *   1. #[AbstractBinding.onCreated]
 *   2. #[AbstractBinding.onDestroy]
 *   3. [AbstractBinding.binding]
 *   4. [AbstractBinding.lifecycle]
 *   5. [AbstractBinding.context]
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 7/2/2019
 * @see [LifecycleObserver]
 * @see [ViewDataBinding]
 */
abstract class AbstractBinding<VB : ViewDataBinding> : LifecycleObserver {
    /**
     * Nullable [ViewDataBinding] object casted to layout binding provided by [VB]
     */
    var binding: VB? = null
    /**
     * Nullable [Lifecycle] object if any child need some lifecycle related method
     */
    var lifecycle: Lifecycle? = null
    /**
     * Nullable [Context] needed for any relational operations
     */
    val context: Context?
        get() {
            return binding?.root?.context
        }

    /**
     * Method that provides execution up on create of ViewLifeCycle usually mapped with [Lifecycle.Event.ON_CREATE]
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    abstract fun onCreated()

    /**
     * Method that provides execution up on destroy of ViewLifeCycle usually mapped with [Lifecycle.Event.ON_DESTROY]
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun onDestroy()
}