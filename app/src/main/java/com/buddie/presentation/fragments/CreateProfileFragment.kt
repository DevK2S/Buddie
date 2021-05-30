package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.buddie.data.model.UserProfile
import com.buddie.databinding.FragmentCreateProfileBinding
import com.buddie.presentation.activities.LoginActivity
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.viewmodel.LoginViewModel
import com.buddie.presentation.viewmodel.ProfileViewModel

class CreateProfileFragment : BaseFragment() {
	
	private lateinit var binding: FragmentCreateProfileBinding
	
	private val profileViewModel: ProfileViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.createButton.setOnClickListener {
			profileViewModel.saveUserProfile(
				UserProfile(
					binding.firstName.text.toString(),
					firebaseAuth.currentUser?.phoneNumber,
					"23/02/2000",
					"Male",
					"25-30",
					"India"
				)
			)
			
			val intent = Intent(activity as LoginActivity, MainActivity::class.java)
			startActivity(intent)
			requireActivity().finish()
		}
	}
}