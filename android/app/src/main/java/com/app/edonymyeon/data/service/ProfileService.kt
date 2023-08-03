package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {

    @GET("/profile/my-posts")
    suspend fun getMyPost(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): Response<MyPostsResponse>

    @POST("/profile/my-posts/{postId}/purchase-confirm")
    suspend fun postPurchaseConfirm(
        @Path("postId") postId: Long,
        @Body purchaseConfirmRequest: PurchaseConfirmRequest,
    ): Response<Unit>

    @POST("/profile/my-posts/{postId}/saving-confirm")
    suspend fun postSavingConfirm(
        @Path("postId") postId: Long,
        @Body savingConfirmRequest: SavingConfirmRequest,
    ): Response<Unit>

    @DELETE("/profile/my-posts/{postId}/confirm-remove")
    suspend fun deleteConfirm(
        @Path("postId") postId: Long,
    ): Response<Unit>
}
