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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * [LifecycleCallbackProvider] :
 *
 * Class used to provide callbacks from `Activity/Fragment` with specified logic using Method expressions.
 * Mainly used to provide hacky way to overcome issue of method calls between **binding create method & view ready**.
 *
 * Previously without this class, call was made like `onViewReady() -> onCreated() of binding class`.
 * Issue was resolved by providing proper callback to overcome this reverse callback issue due to
 * binding class receives callback of `onCreated()` with delay.
 *
 * Lifecycle doc suggests it *~700ms delay* is necessary for their internal architecture.
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 10/2/2019
 */
class LifecycleCallbackProvider(
    val lifecycle: Lifecycle,
    val onViewReadyCallback: (() -> Unit)? = null
) : LifecycleObserver {
    private var callShouldHappen = true

    /**
     * Method to provide callback when [Lifecycle.Event.ON_START] happens,
     * It must be in [Lifecycle.State.CREATED] state to provide this callback.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun afterCreate() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED) && callShouldHappen) {
            onViewReadyCallback?.invoke()
            callShouldHappen = false
        }
    }
}