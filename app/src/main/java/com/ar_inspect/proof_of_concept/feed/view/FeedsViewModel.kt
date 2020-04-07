package com.ar_inspect.proof_of_concept.feed.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ar_inspect.proof_of_concept.feed.data.FeedsRepository
import com.ar_inspect.proof_of_concept.feed.model.FeedInfoDto
import com.awesome_lib.core.Event
import com.awesome_lib.core.api.ResultResponse

/**
 * [FeedsViewModel] :
 *
 * [ViewModel] class provides communication between UI and repository with some observable data.
 */
class FeedsViewModel : ViewModel() {
    private val feedsDetailRepository by lazy {
        return@lazy FeedsRepository.getInstance()
    }

    val feedsLiveData: LiveData<MutableList<FeedInfoDto?>?> = MediatorLiveData()
    val errorLiveData: LiveData<Event<ResultResponse<MutableList<FeedInfoDto?>?>>> =
        MediatorLiveData()
    private val feeds = feedsDetailRepository.getFeeds()
    private val error = feedsDetailRepository.errorLiveData

    init {
        (feedsLiveData as? MediatorLiveData)?.addSource(feeds) {
            (feedsLiveData as? MediatorLiveData)?.value = it
        }
        (errorLiveData as? MediatorLiveData)?.addSource(error) {
            (errorLiveData as? MediatorLiveData)?.value = it
        }
    }

    /**
     * Get list of feeds from repository
     */
    fun getFeeds() {
        (feedsLiveData as? MediatorLiveData)?.value = feedsDetailRepository.getFeeds().value
    }

    /**
     * Remove old feeds and get latest ones
     */
    fun refreshFeeds() {
        feedsDetailRepository.clearAllLocalFeeds()
        getFeeds()
    }

    /**
     * Get page title
     */
    fun getPageTitle() = feedsDetailRepository.getTitle()
}