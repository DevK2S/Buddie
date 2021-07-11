package com.buddie.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddie.data.model.UserModel
import com.buddie.data.util.Result
import com.buddie.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) :
	ViewModel() {
	
	private val mCurrentUser: MutableLiveData<Result<UserModel?>> = MutableLiveData()
	val currentUser: LiveData<Result<UserModel?>>
		get() = mCurrentUser
	
	init {
		getCurrentUser()
	}
	
	fun saveCurrentUser(userModel: UserModel) {
		mCurrentUser.postValue(Result.Loading())
		
		viewModelScope.launch {
			val result = userRepository.saveCurrentUserData(userModel)
			mCurrentUser.postValue(result)
		}
	}
	
	private fun getCurrentUser() {
		mCurrentUser.postValue(Result.Loading())
		
		viewModelScope.launch {
			val result = userRepository.getCurrentUserData()
			mCurrentUser.postValue(result)
		}
	}
}