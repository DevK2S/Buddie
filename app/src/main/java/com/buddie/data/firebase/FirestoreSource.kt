package com.buddie.data.firebase

import com.buddie.data.model.UserModel
import com.buddie.data.util.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreSource @Inject constructor(
	private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore
) {
	
	suspend fun saveCurrentUserData(userModel: UserModel): Task<DocumentSnapshot>? {
		firebaseFirestore.collection(Constants.FIRESTORE_USERS_COLLECTION)
			.document(firebaseAuth.currentUser!!.uid)
			.collection(Constants.FIRESTORE_USERS_INFO_COLLECTION)
			.document(Constants.FIRESTORE_USERS_PROFILE)
			.set(userModel)
		
		return getCurrentUserData()
	}
	
	suspend fun getCurrentUserData(): Task<DocumentSnapshot>? =
		if (firebaseAuth.currentUser != null) {
			firebaseFirestore.collection(Constants.FIRESTORE_USERS_COLLECTION)
				.document(firebaseAuth.currentUser!!.uid)
				.collection(Constants.FIRESTORE_USERS_INFO_COLLECTION)
				.document(Constants.FIRESTORE_USERS_PROFILE)
				.get()
		} else {
			null;
		}
}