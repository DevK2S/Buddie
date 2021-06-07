package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.buddie.data.model.UserModel
import com.buddie.databinding.FragmentCreateProfileBinding
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
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
		
		val data: MutableList<String> = ArrayList()
		data.add("Male")
		data.add("Female")
		data.add("China")
		data.add("Pakis")
		data.add("Austr")
		
		binding.scrollGender.addItems(data, 2)
		
		binding.btnNext.setOnClickListener {
			profileViewModel.saveCurrentUser(
				UserModel(
					binding.editTextFirstName.text.toString(),
					firebaseAuth.currentUser?.phoneNumber,
					"23/02/2000",
					"Male",
					"25-30",
					"India"
				)
			)
			
			val intent = Intent(requireContext(), MainActivity::class.java)
			startActivity(intent)
			requireActivity().finish()
		}
	}
}