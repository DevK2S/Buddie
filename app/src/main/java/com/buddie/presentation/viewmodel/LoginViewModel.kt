package com.buddie.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.buddie.data.model.UserProfile
import com.buddie.data.repository.FireBaseRespository
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val fireBaseRepo : FireBaseRespository): ViewModel(){

    var currentProfile : MutableLiveData<UserProfile> = MutableLiveData()

    fun saveUserProfile(userProfile:UserProfile) {
        fireBaseRepo.saveUserProfile(userProfile)
    }
    fun getUser()
    {
        fireBaseRepo.getUser().addOnSuccessListener {
            var profile = it.toObject<UserProfile>()
            Log.e("LoginViewModel","User ${profile}")

            currentProfile?.value = profile
            Log.e("LoginViewModel","User ${currentProfile.value}")

        }
    }

}


