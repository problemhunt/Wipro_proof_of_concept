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

import android.content.Context
import android.util.Log
import com.awesome_lib.core.BuildConfig
import com.awesome_lib.core.provider.ContextProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * [RetrofitSingleton] :
 *
 * Singleton object provides method to build your [Retrofit] API endpoint service using [RetrofitSingleton.provideRetrofit]
 *
 * @author : Jeel Vankhede
 * @version 1.0.0
 * @since 7/10/2019
 */
object RetrofitSingleton {
    private const val TAG = "RetrofitSingleton"
    private const val CONTENT_TYPE = "application/json"
    private const val PROTOCOL = "SSL"
    private const val CHUCK_CLASSLOADER = "com.readystatesoftware.chuck.ChuckInterceptor"

    /**
     * Method provides Retrofit object based on [config] object provided and
     * provides [reqBuilderCallback] to help you setup your API service
     *
     * @param baseUrl as [String] required for Retrofit object as `baseUrl`
     * @param connectTimeout as [Long] object to set timeout on connection, default value is `80L`
     * @param readTimeout as [Long] object to set timeout on read, default value is `80L`
     * @param writeTimeout as [Long] object to set timeout on write, default value is `80L`
     * @param timeUnit as [TimeUnit] object to set timeout type, default value is [TimeUnit.SECONDS]
     * @param reqBuilderCallback as callback of [Request.Builder] to help you add-on your own choice of your request
     *
     * @return [Retrofit] as object pre-build upon your provided configuration
     */
    @JvmOverloads
    @JvmStatic
    fun provideRetrofit(
        baseUrl: String,
        connectTimeout: Long = 80,
        readTimeout: Long = 80,
        writeTimeout: Long = 80,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        reqBuilderCallback: Request.Builder.() -> Unit
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(provideGsonConverterFactory())
            .client(
                getUnsafeOkHttpClient(
                    connectTimeout,
                    readTimeout,
                    writeTimeout,
                    timeUnit,
                    reqBuilderCallback
                ).build()
            )
            .build()
    }

    private fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(provideGson())
    }

    private fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    private fun getUnsafeOkHttpClient(
        connectTimeout: Long,
        readTimeout: Long,
        writeTimeout: Long,
        timeUnit: TimeUnit,
        reqBuilderCallback: Request.Builder.() -> Unit
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        try {
            val trustAllCerts = arrayOf<TrustManager>(TrustAllManagers())
            val sslContext = SSLContext.getInstance(PROTOCOL)
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Exception At Retrofit", e.message)
        } catch (e: KeyManagementException) {
            Log.e("Exception At Retrofit", e.message)
        }
        builder.hostnameVerifier { hostname, session ->
            hostname.equals(
                session.peerHost,
                ignoreCase = true
            )
        }
        builder.connectTimeout(connectTimeout, timeUnit)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val original = chain.request()
                chain.proceed(with(original.newBuilder()) {
                    header("Content-Type", CONTENT_TYPE)
                    header("Accept", CONTENT_TYPE)
                    reqBuilderCallback(this)
                    method(original.method(), original.body())
                    build()
                })
            }
        builder.addNetworkInterceptor(networkCacheInterceptor())
        if (BuildConfig.DEBUG) {
            try {
                builder.addInterceptor(
                    Class.forName(CHUCK_CLASSLOADER).getConstructor(Context::class.java).newInstance(
                        ContextProvider.getInstance().context?.applicationContext
                    ) as Interceptor
                )
            } catch (e: Exception) {
                Log.e(TAG, "getUnsafeOkHttpClient: ", e)
            }
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        return builder
    }

    private fun networkCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl = CacheControl.Builder()
                .maxAge(2, TimeUnit.MINUTES)
                .build()
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
            response
        }
    }

    internal class TrustAllManagers : X509TrustManager {

        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            Log.e(TAG, "checkClientTrusted: ClientTrustedError")
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            Log.e(TAG, "checkServerTrusted: CheckServerTrusted")
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        companion object {
            private const val TAG = "TrustAllManagers"
        }
    }
}