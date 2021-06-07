package com.buddie.di

import com.buddie.data.firebase.FirestoreSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseSourceModule {
	
	@Provides
	@Singleton
	fun providesFirestoreSource(
		firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore
	): FirestoreSource {
		return FirestoreSource(firebaseAuth, firebaseFirestore)
	}
}