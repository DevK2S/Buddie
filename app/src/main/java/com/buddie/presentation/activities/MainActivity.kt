package com.buddie.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buddie.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.btnLogout.setOnClickListener {
			Firebase.auth.signOut()
			
			val intent = Intent(this@MainActivity, LoginActivity::class.java)
			startActivity(intent)
			finish()
		}
	}
}