package com.fomat.newsonline.Services

import com.fomat.newsonline.Models.NewsData
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("marks")
    fun getAllNews() : Call<NewsData>

    @GET("news?access_key=c6413a7cf6e8ec5a79d3c07a47c81534&languages=es")
    fun getCathegorizedNews(@Query(value="categories", encoded=true) categories: String) : Call<NewsData>
}