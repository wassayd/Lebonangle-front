package com.example.lebonangle_front.api.advert

import java.io.Serializable

data class AdvertPostJsonItem(
    val author: String,
    val category: String,
    val content: String,
    val email: String,
    val price: Float,
    val title: String,
    val pictures: List<String>
): Serializable