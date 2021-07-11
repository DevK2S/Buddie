package com.buddie.data.repository.datasource

import com.buddie.data.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseSource {
	
	suspend fun saveCurrentUserData(userModel: UserModel): Task<DocumentSnapshot>?
	suspend fun getCurrentUserData(): Task<DocumentSnapshot>?
}