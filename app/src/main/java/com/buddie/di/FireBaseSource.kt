package com.buddie.di

import android.content.ContentValues.TAG
import android.util.Log
import com.buddie.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject


class FireBaseSource @Inject constructor(private val firebaseAuth:FirebaseAuth, private val firestore: FirebaseFirestore) {


   fun saveUserProfile(userProfile: UserProfile)= firestore.collection("users").document(firebaseAuth.currentUser!!.uid).collection("UserInfo").document("Profile").set(userProfile)
   fun getUser() = firestore.collection("users").document(firebaseAuth.currentUser!!.uid).
                        collection("UserInfo").document("Profile").
                        get()


}