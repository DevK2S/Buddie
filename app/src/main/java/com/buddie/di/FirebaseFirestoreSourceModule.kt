package com.buddie.di

import com.buddie.data.repository.datasource.FirebaseFirestoreSource
import com.buddie.data.repository.datasourceimpl.FirebaseFirestoreSourceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class FirebaseFirestoreSourceModule {

    @Provides
    @Singleton
    fun providesFirebaseFirestoreSource(
        firebaseAuth : FirebaseAuth,
        firebaseFirestore:FirebaseFirestore
    ):FirebaseFirestoreSource{
        return FirebaseFirestoreSourceImpl(firebaseAuth,firebaseFirestore)
    }
}