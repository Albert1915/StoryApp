package com.alz19.storyapp.welcome.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alz19.storyapp.api.config.ConfigAuth
import com.alz19.storyapp.response.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    init {
        _isLoading.value = false
    }


    fun regisUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ConfigAuth.getApiService().registerAuth(name, email, password)
        client.enqueue(
            object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful){
                        _isError.value = false
                    }else{
                        Log.e(TAG, "onFailure: ${response.message()}")
                        _isError.value = true
                    }
                    _isLoading.value = false
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                    _isLoading.value = false
                }

            }
        )
    }

    companion object {
        private const val TAG = "TAG"
    }


}