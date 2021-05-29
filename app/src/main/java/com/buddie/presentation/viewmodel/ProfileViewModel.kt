package com.buddie.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddie.data.model.UserProfile
import com.buddie.data.util.Result
import com.buddie.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) :
	ViewModel() {
	
	private val mCurrentUser: MutableLiveData<Result<UserProfile?>> = MutableLiveData()
	val currentUser: LiveData<Result<UserProfile?>>
		get() = mCurrentUser

	lateinit var phNumber:String

	var forceResendingToken : PhoneAuthProvider.ForceResendingToken?= null

	var verificationId: String? = null

	lateinit var firebaseAuth: FirebaseAuth

	fun saveUserProfile(userProfile: UserProfile) {
		mCurrentUser.postValue(Result.Loading())
		
		viewModelScope.launch {
			val result = userRepository.saveUserProfile(userProfile)
			mCurrentUser.postValue(result)
		}
	}
	
	fun getUser() {
		mCurrentUser.postValue(Result.Loading())
		
		viewModelScope.launch {
			val result = userRepository.getUser()
			mCurrentUser.postValue(result)
		}
	}
}


