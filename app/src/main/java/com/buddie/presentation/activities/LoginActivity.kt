package com.buddie.presentation.activities

import android.os.Bundle
import com.buddie.databinding.ActivityLoginBinding
import com.buddie.presentation.base.BaseActivity

class LoginActivity : BaseActivity() {
	
	private lateinit var binding: ActivityLoginBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
	}
}