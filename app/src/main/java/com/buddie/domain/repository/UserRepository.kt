package com.buddie.domain.repository

import com.buddie.data.model.UserProfile
import com.buddie.data.util.Result

interface UserRepository {
	suspend fun saveUserProfile(userProfile: UserProfile): Result<UserProfile?>
	suspend fun getUser(): Result<UserProfile?>
	suspend fun checkUserExists(): Result<Boolean>
}