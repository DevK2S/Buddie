package com.buddie.data.repository.datasource

import com.buddie.data.model.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


interface FirebaseSource {
	suspend fun saveUserProfile(userProfile: UserProfile): Task<DocumentSnapshot>
	suspend fun getUser(): Task<DocumentSnapshot>
}