package com.buddie.presentation.viewmodel
import com.buddie.data.util.Result
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buddie.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val mUserExists: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val userExists: LiveData<Result<Boolean>>
        get() = mUserExists

    private val mPhoneNumber: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: LiveData<String>
        get() = mPhoneNumber

    private val mOtp: MutableLiveData<String> = MutableLiveData()
    val otp: LiveData<String>
        get() = mOtp

    fun checkUserExists() {
        mUserExists.postValue(Result.Loading())

        viewModelScope.launch {
            val result = userRepository.checkUserExists()
            mUserExists.postValue(result)
        }
    }

    fun setPhoneNumber(phoneNumber: String) {
        mPhoneNumber.postValue(phoneNumber)
    }

    fun setOtp(otp: String) {
        mOtp.postValue(otp)
    }
}