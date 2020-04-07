package com.ar_inspect.proof_of_concept.feed.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * [FeedListDto] :
 *
 * Wrapper DTO class that holds list of [FeedInfoDto]s objects and a title received from network

 *
 * @see FeedInfoDto
 */
data class FeedListDto(
    @SerializedName("title")
    var feedTitle: String? = null,
    @SerializedName("rows")
    var feedsList: MutableList<FeedInfoDto?>? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        feedTitle = parcel.readString(),
        feedsList = parcel.createTypedArrayList(FeedInfoDto.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feedTitle)
        parcel.writeTypedList(feedsList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedListDto> {
        override fun createFromParcel(parcel: Parcel): FeedListDto {
            return FeedListDto(parcel)
        }

        override fun newArray(size: Int): Array<FeedListDto?> {
            return arrayOfNulls(size)
        }
    }
}