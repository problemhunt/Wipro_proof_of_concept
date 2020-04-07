package com.ar_inspect.proof_of_concept.feed.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ar_inspect.proof_of_concept.database.ProofOfConceptDbContract
import com.ar_inspect.proof_of_concept.feed.model.FeedInfoDto

/**
 * [FeedDao] :
 *
 * Interface provided to expose some method related to CRUD operations on database for [FeedInfoDto]
 */
@Dao
interface FeedDao {
    /**
     * insert all feeds to the database, replace entries on conflict
     *
     * @param feeds as [List] of [FeedInfoDto]s object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllFeeds(feeds: List<FeedInfoDto?>): List<Long?>

    /**
     * Get list of [FeedInfoDto] from local db
     *
     * @return list of [FeedInfoDto]
     */
    @Query("SELECT * FROM ${ProofOfConceptDbContract.FEEDS_TABLE_NAME}")
    fun getAllFeeds(): MutableList<FeedInfoDto?>?

    /**
     *  get count (no of rows) from the table [ProofOfConceptDbContract.FEEDS_TABLE_NAME]
     *
     *  @return count: [Long] number of rows present in table
     */
    @Query("SELECT COUNT(${ProofOfConceptDbContract.COLUMN_FEED_ID}) FROM ${ProofOfConceptDbContract.FEEDS_TABLE_NAME}")
    fun getFeedsCount(): Long

    /**
     * Clear table [ProofOfConceptDbContract.FEEDS_TABLE_NAME]
     */
    @Query("DELETE FROM ${ProofOfConceptDbContract.FEEDS_TABLE_NAME}")
    fun clearFeedsTable()
}