package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.buddie.data.model.UserProfile
import com.buddie.databinding.FragmentCreateProfileBinding
import com.buddie.presentation.activities.LoginActivity
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.viewmodel.LoginViewModel


class CreateProfileFragment : Fragment() {

    private lateinit var createProfileBinding: FragmentCreateProfileBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        createProfileBinding =FragmentCreateProfileBinding.inflate(inflater, container, false)
        return createProfileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createProfileBinding= FragmentCreateProfileBinding.bind(view)
        loginViewModel=(activity as LoginActivity).loginViewModel
        createProfileBinding.createButton.setOnClickListener {
            loginViewModel.saveUserProfile(
                UserProfile(createProfileBinding.firstName.text.toString(),"23/02/2000","Male","25-30","India")
            )
            val intent = Intent(activity as LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        //profileViewModel = (activity as LoginActivity).profileViewModel

    }
}