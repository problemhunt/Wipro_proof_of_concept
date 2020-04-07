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

package com.awesome_lib.core.provider

import android.app.Application
import android.util.Log

/**
 * [ContextProvider] :
 *
 * Base class holds context using [Application] object as Singleton implementation.
 *
 * Use this class in your library projects where you need to access static context from any class,
 * because context can't be hold inside when class is accessed statically *(due to chance of leaking context)*
 * it provides your object of main [Application] which can be used to retrieve `applicationContext`.
 *
 * ### How to use: ###
 *
 *     val context : Context? = ContextProvider.getInstance().context?.applicationContext
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 8/23/2019
 */
class ContextProvider {
    object HOLDER {
        val contextProvider = ContextProvider()
    }

    companion object {
        private const val TAG = "ContextProvider"

        /**
         * Method to init `context` statically using [appContext],
         * Use this method to initialize [ContextProvider.context] statically without having chance of
         * leaking it *(Although it's not synchronized yet)*.
         *
         * @param appContext as [Application] class object to stored and referenced
         */
        @JvmStatic
        fun init(appContext: Application?) {
            if (appContext == null) {
                Log.e(TAG, "init : failed to retrieve context")
            } else {
                HOLDER.contextProvider.context = appContext
            }
        }

        /**
         * Method to retrieve instance of [ContextProvider] statically
         *
         * @return [ContextProvider] as object to be used further
         */
        @JvmStatic
        fun getInstance(): ContextProvider {
            return HOLDER.contextProvider
        }
    }

    /**
     * [Application] class that can provide `applicationContext`
     */
    var context: Application? = null
        private set
}