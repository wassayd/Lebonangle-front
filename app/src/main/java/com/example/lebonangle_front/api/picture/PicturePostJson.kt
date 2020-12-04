package com.example.lebonangle_front.api.picture

import com.google.gson.annotations.SerializedName

data class PicturePostJson(
        @SerializedName("@context") val context : String,
        @SerializedName("@id") val iri : String,
        @SerializedName("@type") val type : String,
        @SerializedName("contentUrl") val contentUrl : String,
        @SerializedName("advert") val advert : String
)