package com.buddie.presentation.activities

import android.content.Intent
import android.os.Bundle
import com.buddie.databinding.ActivityLoginBinding
import com.buddie.presentation.base.BaseActivity

class LoginActivity : BaseActivity() {
	
	private lateinit var binding: ActivityLoginBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		checkUserLoggedIn()
	}
	
	private fun checkUserLoggedIn() {
		if (getCurrentUserUid() != null) {
			val intent = Intent(this@LoginActivity, MainActivity::class.java)
			startActivity(intent)
			finish()
		}
	}
}