package com.ar_inspect.proof_of_concept.database

import com.ar_inspect.proof_of_concept.feed.model.FeedInfoDto

/**
 * [ProofOfConceptDbContract] :
 *
 * Data contract class that provides constant data related to [ProofOfConceptDb] & it's related entity models.
 *
 * List of the constants are as below:
 *   1. [ProofOfConceptDbContract.DB_VERSION]
 *   2. [ProofOfConceptDbContract.NEW_DB_VERSION]
 *   3. [ProofOfConceptDbContract.DB_NAME]
 *   4. [ProofOfConceptDbContract.FEEDS_TABLE_NAME]
 *   5. [ProofOfConceptDbContract.COLUMN_FEED_ID]
 *   6. [ProofOfConceptDbContract.COLUMN_FEED_TITLE]
 *   7. [ProofOfConceptDbContract.COLUMN_FEED_DESCRIPTION]
 *   8. [ProofOfConceptDbContract.COLUMN_FEED_URL]
 *
 */
class ProofOfConceptDbContract {
    companion object {
        // Tag for logcat.
        const val TAG = "ProofOfConceptDbContract"
        /**
         * defines database version for [ProofOfConceptDb]
         */
        const val DB_VERSION = 1
        /**
         * defines new database version for [ProofOfConceptDb] migration
         */
        const val NEW_DB_VERSION = 1
        /**
         * defines database name for [ProofOfConceptDb]
         */
        const val DB_NAME = "ProofOfConceptApp"

        /**
         * defines Feeds table name for [FeedInfoDto]
         */
        const val FEEDS_TABLE_NAME = "Feeds"

        /**
         * defines column name `id` for [FeedInfoDto]
         */
        const val COLUMN_FEED_ID = "id"
        /**
         * defines column name `title` for [FeedInfoDto]
         */
        const val COLUMN_FEED_TITLE = "title"
        /**
         * defines column name `description` for [FeedInfoDto]
         */
        const val COLUMN_FEED_DESCRIPTION = "description"
        /**
         * defines column name `image_url` for [FeedInfoDto]
         */
        const val COLUMN_FEED_URL = "image_url"
    }
}