package com.alz19.storyapp.response

import com.google.gson.annotations.SerializedName

data class DetailStoryResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("story")
	val story: ListStoryItem? = null
)


