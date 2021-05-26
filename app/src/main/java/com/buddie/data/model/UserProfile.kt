package com.buddie.data.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.auth.User
import java.io.Serializable


data class UserProfile(var name :String? = null,
                       var dob:String ? =null,
                       var gender:String ? = null,
                       var ageIn:String ? = null,
                       var loc:String ?= null
                       ):Serializable