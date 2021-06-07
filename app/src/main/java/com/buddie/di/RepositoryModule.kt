package com.buddie.di

import com.buddie.data.repository.UserRepositoryImpl
import com.buddie.data.repository.datasource.FirebaseSource
import com.buddie.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
	
	@Provides
	@Singleton
	fun providesUserRepository(firebaseSource: FirebaseSource): UserRepository {
		return UserRepositoryImpl(firebaseSource)
	}
}