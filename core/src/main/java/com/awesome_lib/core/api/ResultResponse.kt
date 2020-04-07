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

package com.awesome_lib.core.api

import android.util.Log
import com.awesome_lib.core.isNetworkConnected
import com.awesome_lib.core.provider.ContextProvider
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 * [ResultResponse] :
 *
 * ### Generic sealed class provides variations of response from API based on conditions like: ###
 *
 * 1. If success then [ResultResponse.Success]
 * 2. If error then [ResultResponse.Error]
 * 3. If error message then [ResultResponse.ErrorMessage]
 * 4. If no connection then [ResultResponse.NoConnection]
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 1/29/2019
 */
sealed class ResultResponse<out T : Any?> {
    /**
     * [Success] :
     *
     * Class that provides **success response** from API when response needs to denote like success.
     *
     * Provides [data] as [T] as success data
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/29/2019
     */
    data class Success<out T : Any?>(val data: T) : ResultResponse<T>()

    /**
     * [Error] :
     *
     * Class that provides **throwable** from API when any kind of error occurred from API call.
     *
     * Provides [tr] as [Throwable] as error data
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/29/2019
     */
    data class Error(val tr: Throwable) : ResultResponse<Nothing>()

    /**
     * [ErrorMessage] :
     *
     * Class that provides **Error message** from API when any kind of error occurred from API call
     * and provides it's error message
     *
     * Provides [errorBody] as [ResponseBody] to provide error data message
     *
     * See method #[ErrorMessage.getErrorMessage]
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/29/2019
     */
    data class ErrorMessage(val errorBody: ResponseBody?) : ResultResponse<Nothing>() {

        /**
         * Method converts [errorBody] to simple [String] error message
         *
         * @return [String] error message
         */
        fun getErrorMessage(): String {
            return if (errorBody == null)
                ""
            else {
                try {
                    JSONObject(errorBody.string()).getString("Message")
                        .replace("\\[\"".toRegex(), "")
                        .replace("\"]".toRegex(), "")
                } catch (e: JSONException) {
                    Log.e("Error", e.message)
                    ""
                } catch (e: IOException) {
                    Log.e("Error", e.message)
                    ""
                }
            }
        }
    }

    /**
     * [NoConnection] :
     *
     * Class that provides **response** from API when there's no internet connection.
     *
     * Provides [msg] of [String] as no connection string
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 1/29/2019
     */
    data class NoConnection(val msg: String = "") : ResultResponse<Nothing>()

    /**
     * [HandledException] :
     *
     * Class that provides **response** from API to handle scenarios
     * like SocketTimeout or Other Connection related exceptions.
     *
     * Provides [tr] as [Throwable] based on exception caused
     *
     * @author : Jeel Vankhede
     * @version 1.0.0
     * @since 10/1/2019
     */
    data class HandledException(val tr: Throwable?) : ResultResponse<Nothing>() {
        fun filterResponses(): ResultResponse<Nothing> {
            return tr?.let {
                when (tr.cause) {
                    is ConnectException, is SSLHandshakeException, is SocketTimeoutException, is HttpException, is IOException -> {
                        if (ContextProvider.getInstance().context.isNetworkConnected())
                            Error(tr)
                        else
                            NoConnection()
                    }
                    else -> {
                        Error(tr)
                    }
                }
            } ?: NoConnection()
        }
    }
}