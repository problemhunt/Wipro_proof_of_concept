package com.ar_inspect.proof_of_concept

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * [ProofOfConceptApp] :
 *
 * Main Application class as entry point to this app, on create simply set context variable from companion object
 * that can be used further in app.
 *
 * For example, see [ProofOfConceptApp.getContext]
 *
 * @see Application
 */
class ProofOfConceptApp : Application() {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        @Volatile
        @JvmStatic
        private lateinit var context: ProofOfConceptApp

        /**
         * Provides context or throw [UninitializedPropertyAccessException] if it's not initialized yet.
         */
        @Throws
        fun getContext(): Context =
            if (this::context.isInitialized) context
            else throw UninitializedPropertyAccessException("Context is null, did you forget to initialize it?")
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}