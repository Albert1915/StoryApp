package com.alz19.storyapp.welcome.add


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alz19.storyapp.api.config.ConfigData
import com.alz19.storyapp.helper.TokenPreference
import com.alz19.storyapp.response.BasicResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddViewModel (private val pref : TokenPreference) :ViewModel() {

    companion object {
        private const val TAG = "AddViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message  : LiveData<String> = _message

    init {
        _isLoading.value = false
    }

    fun postMultiPart(file : MultipartBody.Part, description : RequestBody) {
        _isLoading.value = true
        val token = runBlocking { pref.getToken().first() }
        val client = ConfigData.getApiService(token).postStoriesData(file, description)
        client.enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (!response.isSuccessful){
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
                _message.value = response.body()?.message!!
                _isLoading.value = false
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _isLoading.value = false
            }
        })

    }

}