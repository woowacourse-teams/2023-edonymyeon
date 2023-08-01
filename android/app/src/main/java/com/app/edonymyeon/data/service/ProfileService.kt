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

    @GET("/profile/myPosts")
    fun getMyPost(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): Response<MyPostsResponse>

    @POST("/profile/myPosts/{postId}/purchase-confirm")
    fun postPurchaseConfirm(
        @Path("postId") postId: Long,
        @Body purchaseConfirmRequest: PurchaseConfirmRequest,
    ): Response<Unit>

    @POST("/profile/myPosts/{postId}/saving-confirm")
    fun postSavingConfirm(
        @Path("postId") postId: Long,
        @Body savingConfirmRequest: SavingConfirmRequest,
    ): Response<Unit>

    @DELETE("/profile/myPosts/{postId}/confirm-remove")
    fun deleteConfirm(
        @Path("postId") postId: Long,
    ): Response<Unit>
}
