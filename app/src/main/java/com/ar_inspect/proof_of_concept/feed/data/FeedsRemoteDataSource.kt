package com.ar_inspect.proof_of_concept.feed.data

import com.ar_inspect.proof_of_concept.BuildConfig
import com.ar_inspect.proof_of_concept.ProofOfConceptApp
import com.ar_inspect.proof_of_concept.api.FeedsApi
import com.ar_inspect.proof_of_concept.feed.model.FeedListDto
import com.awesome_lib.core.api.*
import com.awesome_lib.core.isNetworkConnected

/**
 * [FeedsRemoteDataSource] :
 *
 * Remote connection provider for feeds repository, basically performs network API call asynchronously
 * and provides list of feeds as callback result, see method [FeedsRemoteDataSource.getFeeds]
 *
 * @see FeedsApi
 */
class FeedsRemoteDataSource {
    object HOLDER {
        val instance = FeedsRemoteDataSource()
    }

    companion object {
        /**
         * Provides Singleton object [FeedsRemoteDataSource]
         */
        @JvmStatic
        fun getInstance() = HOLDER.instance
    }

    /**
     * Provides API connection object as [FeedsApi] for Retrofit setup
     *
     * @see provideApiService
     */
    private val mApiService by lazy {
        return@lazy provideApiService<FeedsApi?>(BuildConfig.BASE_URL) {}
    }

    /**
     * Get list of feeds from API end asynchronously and provides result to [onFeedsCallback] as
     * lambda method parameter
     *
     * @param onFeedsCallback receives result from network async and forward it to observer.
     */
    fun getFeeds(onFeedsCallback: (ResultResponse<FeedListDto?>?) -> Unit) {
        if (ProofOfConceptApp.getContext().isNetworkConnected()) {
            mApiService?.getFeedsDetails().enqueueOn().success { _, response ->
                when {
                    response.isSuccessful && response.code() == 200 -> {
                        onFeedsCallback(ResultResponse.Success(response.body()))
                    }
                    else -> {
                        onFeedsCallback(ResultResponse.ErrorMessage(response.errorBody()))
                    }
                }
            } failure { _, t ->
                onFeedsCallback(ResultResponse.Error(t))
            }
        } else
            onFeedsCallback(ResultResponse.NoConnection())
    }
}