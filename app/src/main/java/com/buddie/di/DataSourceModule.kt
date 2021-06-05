package com.buddie.di

import com.buddie.data.firebase.FirestoreSource
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.data.repository.datasourceimpl.FirebaseSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataSourceModule {
	
	@Provides
	@Singleton
	fun providesFirebaseSource(
		firestoreSource: FirestoreSource
	): FirebaseSource {
		return FirebaseSourceImpl(firestoreSource)
	}
}