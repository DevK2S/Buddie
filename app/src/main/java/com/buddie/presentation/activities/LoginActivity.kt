package com.buddie.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buddie.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityLoginBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		checkUser()
	}
	
	private fun checkUser() {
		if (Firebase.auth.currentUser != null) {
			val intent = Intent(this@LoginActivity, MainActivity::class.java)
			startActivity(intent)
			finish()
		}
	}
}