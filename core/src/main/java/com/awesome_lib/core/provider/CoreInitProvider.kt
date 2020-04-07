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
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * [CoreInitProvider] :
 *
 * [ContentProvider] class that can be used to initiate library without providing or calling from external app
 * Content provider classes gets called when process initiates, so that you don't for your consumer for
 * initializing your library from application class every time, in-such way you can be independent from
 * `Application` class initialization.
 *
 * After [ContentProvider.onCreate] invocation, [ContextProvider] gets initiated to retrieve context from it.
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 8/23/2019
 *
 * @see [ContextProvider]
 * @see [ContentProvider]
 */
class CoreInitProvider : ContentProvider() {
    companion object {
        // Tag for logcat.
        const val TAG = "CoreInitProvider"
    }

    override fun onCreate(): Boolean {
        ContextProvider.init(this.context as? Application)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw NotImplementedError("unimplemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        throw NotImplementedError("unimplemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw NotImplementedError("unimplemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw NotImplementedError("unimplemented")
    }

    override fun getType(uri: Uri): String? {
        throw NotImplementedError("unimplemented")
    }
}