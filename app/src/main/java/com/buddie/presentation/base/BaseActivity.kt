package com.buddie.presentation.base

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
	
	@Inject
	lateinit var firebaseAuth: FirebaseAuth
	
	protected fun getCurrentUserUid(): String? = firebaseAuth.currentUser?.uid
}