package com.buddie.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.buddie.databinding.ActivityLoginBinding
import com.buddie.presentation.viewmodel.LoginViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    //lateinit var loginViewModel: LoginViewModel
    val loginViewModel:LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        checkUser()
        //Log.e("LoginAc","UserProfile ${loginViewModel.currentProfile}")
        setContentView(binding.root)
    }

    fun checkUser()
    {
        if(Firebase.auth.currentUser != null) {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            loginViewModel.getUser()
        }
    }


}