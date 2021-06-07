package com.buddie.domain.repository

import com.buddie.data.model.UserModel
import com.buddie.data.util.Result

interface UserRepository {
	
	suspend fun saveCurrentUser(userModel: UserModel): Result<UserModel?>
	suspend fun getCurrentUser(): Result<UserModel?>
	suspend fun checkUserExists(): Result<Boolean>
}