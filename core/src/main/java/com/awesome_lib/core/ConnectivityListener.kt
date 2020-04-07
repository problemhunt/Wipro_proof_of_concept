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
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.awesome_lib.core.provider.ContextProvider

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ConnectivityListener : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager? =
        ContextProvider.getInstance().context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            val capabilities =
                connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                val isConnected =
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                postValue(isConnected)
            }
        } else {
            val networkInfo = connectivityManager?.activeNetworkInfo
            val isConnected = networkInfo != null && networkInfo.isConnected
            postValue(isConnected)
        }
        val builder = NetworkRequest.Builder()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.registerDefaultNetworkCallback(callback)
        } else {
            connectivityManager?.registerNetworkCallback(builder.build(), callback)
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager?.unregisterNetworkCallback(callback)
    }
}