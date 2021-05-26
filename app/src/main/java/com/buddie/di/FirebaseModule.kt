package com.buddie.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {
	
	@Provides
	@Singleton
	fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
	
	@Provides
	@Singleton
	fun providesFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

