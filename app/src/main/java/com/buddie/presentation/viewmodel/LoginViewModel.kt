package com.buddie.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddie.data.util.Result
import com.buddie.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
	
	private val mUserDataExists: MutableLiveData<Result<Boolean>> = MutableLiveData()
	val userDataExists: LiveData<Result<Boolean>>
		get() = mUserDataExists
	
	private val mPhoneNumber: MutableLiveData<String> = MutableLiveData()
	val phoneNumber: LiveData<String>
		get() = mPhoneNumber
	
	private val mOtp: MutableLiveData<String> = MutableLiveData()
	val otp: LiveData<String>
		get() = mOtp
	
	init {
		checkUserDataExists()
	}
	
	fun setPhoneNumber(phoneNumber: String) {
		mPhoneNumber.postValue(phoneNumber)
	}
	
	fun setOtp(otp: String) {
		mOtp.postValue(otp)
	}
	
	private fun checkUserDataExists() {
		mUserDataExists.postValue(Result.Loading())
		
		viewModelScope.launch {
			val result = userRepository.checkUserDataExists()
			mUserDataExists.postValue(result)
		}
	}
}