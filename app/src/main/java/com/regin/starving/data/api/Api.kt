package com.regin.starving.data.api

import com.regin.starving.data.api.response.PoiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("venues/search")
    suspend fun loadPoi(
        @Query("ll") location: String,
        @Query("radius") radius: Double,
        @Query("categoryId") categoryIds: List<String>,
        @Query("limit") limit: Int = 10,
        @Query("intent") intent: String = "browse"
    ): PoiResponse

}
