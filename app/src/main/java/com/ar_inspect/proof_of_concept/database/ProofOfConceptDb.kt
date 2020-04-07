package com.ar_inspect.proof_of_concept.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ar_inspect.proof_of_concept.ProofOfConceptApp
import com.ar_inspect.proof_of_concept.feed.data.FeedDao
import com.ar_inspect.proof_of_concept.feed.model.FeedInfoDto

/**
 * [ProofOfConceptDb] :
 *
 * Main Database class defined to provide and store data using [FeedDao] for [RoomDatabase] as ORM help for this project.
 * So, basically, it manages `Room` object instance and helps you provide [feedDao] to connect local SQLite db for feeds related operations.
 * @see RoomDatabase
 */
@Database(
    entities = [FeedInfoDto::class],
    version = ProofOfConceptDbContract.DB_VERSION
)
abstract class ProofOfConceptDb : RoomDatabase() {
    private object Holder {
        val INSTANCE = with(ProofOfConceptApp.getContext()) {
            Room.databaseBuilder(
                this,
                ProofOfConceptDb::class.java,
                ProofOfConceptDbContract.DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()

        }
    }

    companion object {
        // Tag for logcat.
        const val TAG = "ProofOfConceptDb"

        /**
         * Singleton instance of [ProofOfConceptDb]
         */
        val instance: ProofOfConceptDb? by lazy { Holder.INSTANCE }

    }

    /**
     * Main Feeds DataAccessObject to handle local db connections using interface APIs
     */
    abstract val feedDao: FeedDao
}