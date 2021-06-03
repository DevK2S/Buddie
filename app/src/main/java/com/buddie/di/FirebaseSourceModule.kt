package com.buddie.di

import com.buddie.data.repository.datasource.FirebaseFirestoreSource
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.data.repository.datasourceimpl.FirebaseSourceImpl
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
	fun providesFirebaseSource(
		firetoreSource: FirebaseFirestoreSource
	): FirebaseSource {
		return FirebaseSourceImpl(firetoreSource)
	}
}