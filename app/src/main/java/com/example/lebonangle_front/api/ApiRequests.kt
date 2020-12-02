package com.example.lebonangle_front.api

import com.example.lebonangle_front.api.advert.AdvertJson
import com.example.lebonangle_front.api.advert.AdvertJsonItem
import com.example.lebonangle_front.api.advert.AdvertPostJsonItem
import com.example.lebonangle_front.api.category.CategoriesJson
import com.example.lebonangle_front.api.picture.PictureJson
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiRequests {

    @GET("adverts.json")
    fun getAdverts(
        @Query("category.id") categoryId: Int,
        @Query("page") page: Int = 1
    ): Call<AdvertJson>

    @GET("categories.json")
    fun getCategories(): Call<CategoriesJson>

    @GET("pictures/{id}.json")
    fun getPicture(@Path("id") id: Int): Call<PictureJson>

    @GET("pictures.json")
    fun getPictures(@Query("advert.id") advertId: Int): Call<ArrayList<PictureJson>>

    @POST("adverts")
    fun postAdvert(
        @Body advert: AdvertPostJsonItem
    ): Call<AdvertJsonItem>
}