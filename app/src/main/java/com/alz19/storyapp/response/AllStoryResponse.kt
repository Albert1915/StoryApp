package com.alz19.storyapp.response

import com.google.gson.annotations.SerializedName

data class AllStoryResponse(

    @field:SerializedName("listStory")
	val listStory: List<ListStoryItem?>? = null,

    @field:SerializedName("error")
	val error: Boolean? = null,

    @field:SerializedName("message")
	val message: String? = null
)


