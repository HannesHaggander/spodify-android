package com.towerowl.spodify.data.api

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ExternalUrls(
    val spotify: String?
) : Parcelable