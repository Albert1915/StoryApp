package com.alz19.storyapp.welcome.main

import android.util.Log
import androidx.lifecycle.*
import com.alz19.storyapp.api.config.ConfigData
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.response.AllStoryResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel (private val pref : TokenPreference) :ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _allStoryResponse = MutableLiveData<AllStoryResponse>()
    val allStoryResponse : LiveData<AllStoryResponse> = _allStoryResponse

    fun getSession() :LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun logoutSession(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    init {
        val token = runBlocking { pref.getToken().first() }
        if (token.isNotEmpty()){
            getAllDataStory(token)
        }
    }

    private fun getAllDataStory (token :String){
        _isLoading.value = true
        val client = ConfigData.getApiService(token).getAllStory()
        client.enqueue(object : Callback<AllStoryResponse>{
            override fun onResponse(call: Call<AllStoryResponse>, response: Response<AllStoryResponse>) {
                if (response.isSuccessful){
                    _allStoryResponse.value = response.body()
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }

        })

    }

}