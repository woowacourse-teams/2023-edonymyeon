package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {

    @GET("/profile/my-posts")
    suspend fun getMyPost(
        @Query("size") size: Int,
        @Query("page") page: Int,
        @Query("notificated") notificated: Long,
    ): ApiResponse<MyPostsResponse>

    @POST("/profile/my-posts/{postId}/purchase-confirm")
    suspend fun postPurchaseConfirm(
        @Path("postId") postId: Long,
        @Body purchaseConfirmRequest: PurchaseConfirmRequest,
    ): ApiResponse<Unit>

    @POST("/profile/my-posts/{postId}/saving-confirm")
    suspend fun postSavingConfirm(
        @Path("postId") postId: Long,
        @Body savingConfirmRequest: SavingConfirmRequest,
    ): ApiResponse<Unit>

    @DELETE("/profile/my-posts/{postId}/confirm-remove")
    suspend fun deleteConfirm(
        @Path("postId") postId: Long,
    ): ApiResponse<Unit>

    @GET("/profile")
    suspend fun getProfile(): ApiResponse<WriterDataModel>

    @DELETE("/withdraw")
    suspend fun withdraw(): ApiResponse<Unit>

    @Multipart
    @PUT("/profile")
    suspend fun updateProfile(
        @Part("nickname") nickname: RequestBody,
        @Part("isImageChanged") isImageChanged: Boolean,
        @Part image: MultipartBody.Part?,
    ): ApiResponse<Unit>

    @GET("/profile/check-duplicate")
    suspend fun checkDuplicate(
        @Query("target") target: String,
        @Query("value") value: String,
    ): ApiResponse<AuthDuplicateResponse>
}
