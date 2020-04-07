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

@file:JvmName("ResultResponseDsl")

package com.awesome_lib.core.api

/**
 * Extension method for [ResultResponse] based on result value received from API response,
 * This method indicates that invoke [response] block only if result is of type [ResultResponse.Success]
 *
 * @param response as [ResultResponse.Success] from method expression to be invoked
 *
 * @return [ResultResponse] if result is not valid [ResultResponse.Success] type return as is else `null`
 */
infix fun <T> ResultResponse<T>?.takeIfSuccess(
    response: ResultResponse.Success<T>.() -> Unit
) =
    when (this) {
        is ResultResponse.Success -> {
            response(this)
            this
        }
        is ResultResponse.Error, is ResultResponse.ErrorMessage, is ResultResponse.NoConnection, is ResultResponse.HandledException -> {
            this
        }
        else -> null
    }

/**
 * Extension method for [ResultResponse] based on result value received from API response,
 * This method indicates that invoke [response] block only if result is of type [ResultResponse.Error]
 *
 * @param response as [ResultResponse.Error] from method expression to be invoked
 *
 * @return [ResultResponse] if result is not valid [ResultResponse.Error] type return as is else `null`
 */
infix fun <T> ResultResponse<T>?.takeIfError(
    response: ResultResponse.Error.() -> Unit
) =
    when (this) {
        is ResultResponse.Error -> {
            response(this)
            this
        }
        is ResultResponse.Success, is ResultResponse.ErrorMessage, is ResultResponse.NoConnection, is ResultResponse.HandledException -> {
            this
        }
        else -> null
    }

/**
 * Extension method for [ResultResponse] based on result value received from API response,
 * This method indicates that invoke [response] block only if result is of type [ResultResponse.ErrorMessage]
 *
 * @param response as [ResultResponse.ErrorMessage] from method expression to be invoked
 *
 * @return [ResultResponse] if result is not valid [ResultResponse.ErrorMessage] type return as is else `null`
 */
infix fun <T> ResultResponse<T>?.takeIfErrorMessage(
    response: ResultResponse.ErrorMessage.() -> Unit
) =
    when (this) {
        is ResultResponse.ErrorMessage -> {
            response(this)
            this
        }
        is ResultResponse.Success, is ResultResponse.NoConnection, is ResultResponse.Error, is ResultResponse.HandledException -> {
            this
        }
        else -> null
    }

/**
 * Extension method for [ResultResponse] based on result value received from API response,
 * This method indicates that invoke [response] block only if result is of type [ResultResponse.NoConnection]
 *
 * @param response as [ResultResponse.NoConnection] from method expression to be invoked
 *
 * @return [ResultResponse] if result is not valid [ResultResponse.NoConnection] type return as is else `null`
 */
infix fun <T> ResultResponse<T>?.takeIfNoConnection(
    response: ResultResponse.NoConnection.() -> Unit
) =
    when (this) {
        is ResultResponse.NoConnection -> {
            response(this)
            this
        }
        is ResultResponse.Success, is ResultResponse.Error, is ResultResponse.ErrorMessage, is ResultResponse.HandledException -> {
            this
        }
        else -> null
    }

/**
 * Extension method for [ResultResponse] based on result value received from API response,
 * This method indicates that invoke [response] block only if result is of type [ResultResponse.HandledException]
 *
 * @param response as [ResultResponse.HandledException] from method expression to be invoked
 *
 * @return [ResultResponse] if result is not valid [ResultResponse.HandledException] type return as is else `null`
 */
infix fun <T> ResultResponse<T>?.takeIfNeedToHandle(
    response: ResultResponse.HandledException.() -> Unit
) =
    when (this) {
        is ResultResponse.HandledException -> {
            response(this)
            this
        }
        is ResultResponse.Success, is ResultResponse.NoConnection, is ResultResponse.Error, is ResultResponse.ErrorMessage -> {
            this
        }
        else -> null
    }

/**
 * Extension method for [ResultResponse] based on result value received from API response
 * to end this chaining of responses based on return type `Unit` so that you can't invoke it no more
 *
 * @param response as [ResultResponse] from method expression to be invoked before ending this chain, works like **default (else) case**
 *
 * @return [Unit] as end of operation like *break* works in switch case.
 */
infix fun <T> ResultResponse<T>?.elseDoNothing(
    response: ResultResponse<T>?.() -> Unit
) =
    response(this)