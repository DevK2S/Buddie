package com.buddie.data.repository


import android.util.Log
import com.buddie.data.model.UserProfile
import com.buddie.di.FireBaseSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FireBaseRespository @Inject constructor(private var fireBaseSource: FireBaseSource){


    fun saveUserProfile(userProfile: UserProfile)=fireBaseSource.saveUserProfile(userProfile)
    fun getUser() = fireBaseSource.getUser()
    /*var firestoreDB = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    suspend fun saveUserProfile(userProfile: UserProfile){
        var ref= firestoreDB.collection("users").document(user!!.uid).collection("UserInfo").document("Profile")
        ref.set(userProfile)

    }

   fun getUserProfile() : UserProfile? {

        var userProfile=UserProfile("","","","","")
       firestoreDB.collection("users").document(
           user!!.uid.toString()
       ).collection("UserInfo").document("Profile")
           .get().addOnSuccessListener {
                if(it!= null) {
                    val name = it.getString("name")!!
                    val dob = it.getString("dob")!!
                    val gender = it.getString("dob")!!
                    val ageIn = it.getString("dob")!!
                    val loc = it.getString("dob")!!
                    userProfile = UserProfile(name, dob, gender, ageIn, loc)
                    Log.d("Test", "DocumentSnapshot data: ${it.data}")
                }
           }
        return userProfile
   }*/

}