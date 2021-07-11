package com.buddie.data.repository

import com.buddie.data.model.UserModel
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.data.util.Result
import com.buddie.domain.repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private var firebaseSource: FirebaseSource) :
	UserRepository {
	
	override suspend fun saveCurrentUserData(userModel: UserModel): Result<UserModel?> =
		userProfileTaskToResult(firebaseSource.saveCurrentUserData(userModel))
	
	override suspend fun getCurrentUserData(): Result<UserModel?> =
		userProfileTaskToResult(firebaseSource.getCurrentUserData())
	
	override suspend fun checkUserDataExists(): Result<Boolean> =
		booleanTaskToResult(firebaseSource.getCurrentUserData())
	
	private suspend fun userProfileTaskToResult(task: Task<DocumentSnapshot>?): Result<UserModel?> {
		lateinit var result: Result<UserModel?>
		
		if (task != null) {
			task.addOnSuccessListener { document ->
				val userModel: UserModel? = document.toObject<UserModel>()
				result = if (userModel != null) {
					Result.Success(userModel)
				} else {
					Result.Success(null)
				}
			}.addOnFailureListener { exception ->
				result = Result.Error(
					exception = exception,
					message = exception.message ?: "Cannot convert task to result"
				)
			}.await()
		} else {
			result = Result.Success(null)
		}
		
		return result
	}
	
	private suspend fun booleanTaskToResult(task: Task<DocumentSnapshot>?): Result<Boolean> {
		lateinit var result: Result<Boolean>
		
		if (task != null) {
			task.addOnSuccessListener { document ->
				result = if (document.exists()) {
					Result.Success(true)
				} else {
					Result.Success(false)
				}
			}.addOnFailureListener { exception ->
				result = Result.Error(
					exception = exception,
					message = exception.message ?: "Cannot convert task to result"
				)
			}.await()
		} else {
			result = Result.Success(false)
		}
		return result
	}
}