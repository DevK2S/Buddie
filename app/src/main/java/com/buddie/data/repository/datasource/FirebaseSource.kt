package com.buddie.data.repository.datasource

import com.buddie.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

interface FirebaseSource {
	
	suspend fun saveUserProfile(userProfile: UserProfile): Task<DocumentSnapshot>
	suspend fun getUser(): Task<DocumentSnapshot>
}