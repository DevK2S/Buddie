package com.buddie.data.repository.datasourceimpl

import com.buddie.data.model.UserProfile
import com.buddie.data.repository.datasource.FirebaseFirestoreSource
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.data.util.Constants
import com.buddie.data.util.Result
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseSourceImpl @Inject constructor( private val firestoreSource:FirebaseFirestoreSource) :
	FirebaseSource {

	override suspend fun saveUserProfile(userProfile: UserProfile): Task<DocumentSnapshot>{
		return firestoreSource.saveUserProfile(userProfile)
	}
	
	override suspend fun getUser(): Task<DocumentSnapshot>
	{
		return firestoreSource.getUser()
	}

	

	
}