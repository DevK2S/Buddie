package com.buddie.data.repository.datasourceimpl

import com.buddie.data.firebase.FirestoreSource
import com.buddie.data.model.UserModel
import com.buddie.data.repository.datasource.FirebaseSource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class FirebaseSourceImpl @Inject constructor(private val firestoreSource: FirestoreSource) :
	FirebaseSource {
	
	override suspend fun saveCurrentUser(userModel: UserModel): Task<DocumentSnapshot> =
		firestoreSource.saveCurrentUser(userModel)
	
	override suspend fun getCurrentUser(): Task<DocumentSnapshot> = firestoreSource.getCurrentUser()
}