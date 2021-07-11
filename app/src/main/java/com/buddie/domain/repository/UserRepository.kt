package com.buddie.domain.repository

import com.buddie.data.model.UserModel
import com.buddie.data.util.Result

interface UserRepository {
	
	suspend fun saveCurrentUserData(userModel: UserModel): Result<UserModel?>
	suspend fun getCurrentUserData(): Result<UserModel?>
	suspend fun checkUserDataExists(): Result<Boolean>
}