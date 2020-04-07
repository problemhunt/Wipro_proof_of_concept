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

/**
 * [Event] :
 *
 * Wrapper class used to receive single live event based on `LiveData` of particular state where
 * it needs to be notified just once per call, like calling `create, update or delete APIs` rather than
 * fetching list of data.
 *
 * [Credits](https://gist.github.com/JoseAlcerreca/5b661f1800e1e654f07cc54fe87441af)
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 4/18/2019
 */
open class Event<out T>(private val content: T) {

    /**
     * Object as [Boolean] flag used to dedicate status of whether [T] has been notified or not.
     */
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled as exceptional case.
     */
    fun peekContent(): T = content
}