package com.kinetra.app.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface KinetraApi {

    @GET("profile_get.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int
    ): Response<ProfileResponse>

    @POST("profile_update.php")
    suspend fun updateProfile(
        @Body request: ProfileUpdateRequest
    ): Response<ProfileResponse>

    @GET("notifications_get.php")
    suspend fun getNotifications(
        @Query("user_id") userId: Int
    ): Response<NotificationListResponse>

    @POST("notifications_mark_read.php")
    suspend fun markNotificationRead(
        @Body request: MarkReadRequest
    ): Response<BaseResponse>
}

