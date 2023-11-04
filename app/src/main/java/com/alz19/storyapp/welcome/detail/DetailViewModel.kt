package com.alz19.storyapp.welcome.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alz19.storyapp.api.config.ConfigData
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.response.DetailStoryResponse
import com.alz19.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel (private val pref : TokenPreference) : ViewModel() {

    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyDetail = MutableLiveData<ListStoryItem?>()
    val storyDetail : LiveData<ListStoryItem?> = _storyDetail

    fun getStoryDetail (id : String){
        _isLoading.value = true
        val token = runBlocking { pref.getToken().first() }
        val client = ConfigData.getApiService(token).getDetailStory(id)

        client.enqueue(object : Callback<DetailStoryResponse>{

            override fun onResponse(call: Call<DetailStoryResponse>, response: Response<DetailStoryResponse>) {
                if (response.isSuccessful){
                    _storyDetail.value = response.body()?.story
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }

        })
    }

}