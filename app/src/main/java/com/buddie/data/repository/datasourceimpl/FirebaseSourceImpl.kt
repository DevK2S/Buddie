package com.buddie.data.repository.datasourceimpl

import com.buddie.data.model.UserProfile
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.data.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseSourceImpl @Inject constructor(
	private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore
) : FirebaseSource {
	
	override suspend fun saveUserProfile(userProfile: UserProfile): Task<DocumentSnapshot> {
		firebaseFirestore.collection(Constants.USERS_COLLECTION)
			.document(firebaseAuth.currentUser!!.uid).collection(Constants.USERS_INFO_COLLECTION)
			.document(Constants.USERS_PROFILE).set(userProfile)
		
		return getUser()
	}
	
	override suspend fun getUser(): Task<DocumentSnapshot> =
		firebaseFirestore.collection(Constants.USERS_COLLECTION)
			.document(firebaseAuth.currentUser!!.uid).collection(Constants.USERS_INFO_COLLECTION)
			.document(Constants.USERS_PROFILE).get()
	
}