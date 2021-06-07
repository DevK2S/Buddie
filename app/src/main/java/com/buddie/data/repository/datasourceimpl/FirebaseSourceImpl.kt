package com.buddie.data.repository.datasourceimpl

import com.buddie.data.firebase.FirestoreSource
import com.buddie.data.model.UserProfile
import com.buddie.data.repository.datasource.FirebaseSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class FirebaseSourceImpl @Inject constructor(private val firestoreSource: FirestoreSource) :
	FirebaseSource {
	
	override suspend fun saveUserProfile(userProfile: UserProfile): Task<DocumentSnapshot> =
		firestoreSource.saveUserProfile(userProfile)
	
	override suspend fun getUser(): Task<DocumentSnapshot> = firestoreSource.getUser()
}