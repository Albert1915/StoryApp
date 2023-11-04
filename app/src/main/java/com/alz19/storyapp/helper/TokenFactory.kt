package com.alz19.storyapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alz19.storyapp.welcome.add.AddViewModel
import com.alz19.storyapp.welcome.main.MainViewModel
import com.alz19.storyapp.welcome.detail.DetailViewModel
import com.alz19.storyapp.welcome.login.LoginViewModel

class TokenFactory (private val pref :TokenPreference) :ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(pref) as T
        }else if (modelClass.isAssignableFrom(AddViewModel::class.java)){
            return AddViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

}