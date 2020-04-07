package com.ar_inspect.proof_of_concept.feed.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.ar_inspect.proof_of_concept.database.ProofOfConceptDbContract
import com.google.gson.annotations.SerializedName

/**
 * [FeedInfoDto] : Database class defined to provide and store data using [FeedInfoDto] for [RoomDatabase] as ORM help for this project.
 *
 *
 * @see ProofOfConceptDbContract
 */
@Entity(
    tableName = ProofOfConceptDbContract.FEEDS_TABLE_NAME
)
data class FeedInfoDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ProofOfConceptDbContract.COLUMN_FEED_ID)
    @SerializedName("id")
    var mId: Long,

    @ColumnInfo(name = ProofOfConceptDbContract.COLUMN_FEED_TITLE)
    @SerializedName("title")
    var mTitle: String?,

    @ColumnInfo(name = ProofOfConceptDbContract.COLUMN_FEED_DESCRIPTION)
    @SerializedName("description")
    var mDescription: String?,

    @ColumnInfo(name = ProofOfConceptDbContract.COLUMN_FEED_URL)
    @SerializedName("imageHref")
    var mImageUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(mId)
        parcel.writeString(mTitle)
        parcel.writeString(mDescription)
        parcel.writeString(mImageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedInfoDto> {
        override fun createFromParcel(parcel: Parcel): FeedInfoDto {
            return FeedInfoDto(parcel)
        }

        override fun newArray(size: Int): Array<FeedInfoDto?> {
            return arrayOfNulls(size)
        }
    }
}



