package com.buddie.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buddie.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
	
	@Inject
	lateinit var firebaseAuth: FirebaseAuth
	
	private lateinit var binding: ActivityLoginBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		checkUserLoggedIn()
	}
	
	private fun checkUserLoggedIn() {
		if (firebaseAuth.currentUser != null) {
			val intent = Intent(this@LoginActivity, MainActivity::class.java)
			startActivity(intent)
			finish()
		}
	}
}