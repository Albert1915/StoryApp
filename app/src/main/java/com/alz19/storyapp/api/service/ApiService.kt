package com.alz19.storyapp.api.service


import com.alz19.storyapp.response.LoginResponse
import com.alz19.storyapp.response.AllStoryResponse
import com.alz19.storyapp.response.BasicResponse
import com.alz19.storyapp.response.DetailStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun registerAuth (
        @Field("name") name :String,
        @Field("email") email :String,
        @Field("password") password :String
    ) : Call<BasicResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginAuth (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun postStoriesData(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<BasicResponse>

    @GET("stories")
    fun getAllStory ( ) : Call<AllStoryResponse>

    @GET("stories/{id}")
    fun getDetailStory (@Path("id") id :String) : Call<DetailStoryResponse>
}