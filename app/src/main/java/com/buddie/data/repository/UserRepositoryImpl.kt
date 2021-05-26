package com.buddie.data.repository

import com.buddie.data.model.UserProfile
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
	
	override suspend fun saveUserProfile(userProfile: UserProfile): Result<UserProfile> =
		taskToResource(firebaseSource.saveUserProfile(userProfile))
	
	override suspend fun getUser(): Result<UserProfile> = taskToResource(firebaseSource.getUser())
	
	private suspend fun taskToResource(task: Task<DocumentSnapshot>): Result<UserProfile> {
		lateinit var result: Result<UserProfile>
		
		task.addOnSuccessListener { document ->
			val userProfile: UserProfile? = document.toObject<UserProfile>()
			result = if (userProfile != null) {
				Result.Success(userProfile)
			} else {
				Result.Error("Cannot convert task to user")
			}
		}.addOnFailureListener { exception ->
			exception.printStackTrace()
			result = if (exception.message != null) {
				Result.Error(exception.message!!)
			} else {
				Result.Error("Cannot convert task to user")
			}
		}.await()
		
		return result
	}
}