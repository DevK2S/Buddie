package com.buddie.presentation.activities

import android.content.Intent
import android.os.Bundle
import com.buddie.databinding.ActivityMainBinding
import com.buddie.presentation.base.BaseActivity

class MainActivity : BaseActivity() {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.btnLogout.setOnClickListener {
			firebaseAuth.signOut()
			
			val intent = Intent(this@MainActivity, LoginActivity::class.java)
			startActivity(intent)
			finish()
		}
	}
}