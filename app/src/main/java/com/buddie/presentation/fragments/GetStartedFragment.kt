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

		//loginViewModel.checkUserExists()

		binding.startBtn.setOnClickListener {
			userExists()
		}

	}

	private fun userExists() {
		if (getCurrentUserUid() != null) {
			loginViewModel.userExists.observe(viewLifecycleOwner, { result ->
				when (result) {
					is Result.Success -> {
						//progressDialog.cancel()

						Toast.makeText(
							requireContext(),
							"Logged in as ${loginViewModel.phoneNumber.value}",
							Toast.LENGTH_SHORT
						).show()

						Timber.d("User Exists returned ${loginViewModel.userExists.value?.data}")

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
						//progressDialog.cancel()

						loginViewModel.setOtp("")

						//result.exception?.let { exception -> handleLoginException(exception) }
					}
				}
			})
		}
		else
		{
			requireView().findNavController().navigate(R.id.action_getStartedFragment_to_enterNumberFragment)
		}
	}
}