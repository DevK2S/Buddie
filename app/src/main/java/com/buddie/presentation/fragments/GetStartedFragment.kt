package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.buddie.R
import com.buddie.data.util.Result
import com.buddie.databinding.FragmentGetStartedBinding
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.viewmodel.LoginViewModel
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import timber.log.Timber

class GetStartedFragment : BaseFragment() {
	
	private lateinit var binding: FragmentGetStartedBinding
	
	private val loginViewModel: LoginViewModel by activityViewModels()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentGetStartedBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.btnGetStarted.setOnClickListener {
			userExists()
		}
	}
	
	private fun userExists() {
		loadingDialog.show()
		if (getCurrentUserUid() != null) {
			loginViewModel.userDataExists.observe(viewLifecycleOwner, { result ->
				when (result) {
					is Result.Success -> {
						loadingDialog.cancel()
						
						if (firebaseAuth.currentUser != null) {
							Toast.makeText(
								requireContext(),
								"Logged in with ${firebaseAuth.currentUser!!.phoneNumber}",
								Toast.LENGTH_SHORT
							).show()
						}
						
						Timber.d("User Exists returned ${loginViewModel.userDataExists.value?.data}")
						
						if (result.data == true) {
							val intent = Intent(requireActivity(), MainActivity::class.java)
							startActivity(intent)
							loginViewModel.setOtp("")
							
							requireActivity().finish()
						} else {
							loginViewModel.setOtp("")
							
							requireView().findNavController()
								.navigate(R.id.action_getStartedFragment_to_createProfileFragment)
						}
					}
					
					is Result.Loading -> {
					}
					
					is Result.Error -> {
						loadingDialog.cancel()
						
						loginViewModel.setOtp("")
						
						result.exception?.let { exception -> handleLoginException(exception) }
					}
				}
			})
		} else {
			loadingDialog.cancel()
			
			requireView().findNavController()
				.navigate(R.id.action_getStartedFragment_to_enterNumberFragment)
		}
	}
	
	private fun handleLoginException(exception: Exception) {
		Timber.e(exception)
		
		when (exception) {
			is FirebaseTooManyRequestsException -> {
				Toast.makeText(
					requireContext(),
					"Too many attempts. Please try again later.",
					Toast.LENGTH_LONG
				).show()
			}
			is FirebaseAuthInvalidCredentialsException -> {
				Toast.makeText(
					requireContext(), "Incorrect Verification Code", Toast.LENGTH_SHORT
				).show()
			}
			else -> {
				Toast.makeText(requireContext(), "${exception.message}", Toast.LENGTH_SHORT).show()
			}
		}
	}
}