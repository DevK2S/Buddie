package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.buddie.R
import com.buddie.data.util.Result
import com.buddie.databinding.FragmentEnterOtpBinding
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.utils.observeOnce
import com.buddie.presentation.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class EnterOtpFragment : BaseFragment() {
	
	private lateinit var enterOtpBinding: FragmentEnterOtpBinding
	
	private lateinit var verificationId: String
	private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken
	private lateinit var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
	
	private val profileViewModel: ProfileViewModel by activityViewModels()
	private val args: EnterOtpFragmentArgs by navArgs()
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		enterOtpBinding = FragmentEnterOtpBinding.inflate(inflater, container, false)
		return enterOtpBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		verificationId = args.verificationId
		forceResendingToken = args.forceResendingToken
		
		initPhoneAuthCallbacks()
		
		initOnClickListeners()
		
		enterOtpBinding.dispNum.text =
			"Please Enter the OTP we've sent on ${profileViewModel.phoneNumber.value}"
		
		profileViewModel.otp.observe(viewLifecycleOwner, { otp ->
			if (enterOtpBinding.codeEt.text.isNullOrBlank()) {
				enterOtpBinding.codeEt.setText(otp)
			}
		})
	}
	
	private fun initPhoneAuthCallbacks() {
		phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
			override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
				progressDialog.cancel()
				
				Timber.d(phoneAuthCredential.smsCode)
				
				phoneAuthCredential.smsCode?.let { profileViewModel.setOtp(it) }
			}
			
			override fun onVerificationFailed(exception: FirebaseException) {
				progressDialog.cancel()
				
				handleLoginException(exception)
			}
			
			override fun onCodeSent(
				vId: String, token: PhoneAuthProvider.ForceResendingToken
			) {
				super.onCodeSent(vId, token)
				
				Timber.d("Code Sent")
				
				verificationId = vId
				forceResendingToken = token
				
				progressDialog.cancel()
				
				Toast.makeText(requireContext(), "Verification Code Sent", Toast.LENGTH_SHORT)
					.show()
			}
		}
	}
	
	private fun initOnClickListeners() {
		initResendCodeBtnOnClickListener()
		initSubmitBtnOnClickListener()
	}
	
	private fun initResendCodeBtnOnClickListener() {
		enterOtpBinding.resendcodeTv.setOnClickListener {
			profileViewModel.phoneNumber.observeOnce(viewLifecycleOwner, { phoneNumber ->
				resendVerificationCode(phoneNumber, forceResendingToken)
			})
		}
	}
	
	private fun initSubmitBtnOnClickListener() {
		enterOtpBinding.codesubmitBtn.setOnClickListener {
			progressDialog.show()
			
			val vCode = enterOtpBinding.codeEt.text.toString().trim()
			profileViewModel.setOtp(vCode)
			
			profileViewModel.otp.observeOnce(viewLifecycleOwner, { otp ->
				when {
					TextUtils.isEmpty(otp) -> {
						progressDialog.cancel()
						
						Toast.makeText(
							activity, "Please Enter Verification Code", Toast.LENGTH_SHORT
						).show()
					}
					otp.length != 6 -> {
						progressDialog.cancel()
						
						Toast.makeText(
							activity, "Please Enter Valid Verification Code", Toast.LENGTH_SHORT
						).show()
					}
					else -> {
						verifyPhoneNumberWithCode(verificationId, otp)
					}
				}
			})
		}
	}
	
	private fun resendVerificationCode(
		phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken
	) {
		if (this::phoneAuthCallbacks.isInitialized) {
			val options = PhoneAuthOptions.newBuilder(firebaseAuth)
				.setPhoneNumber(phoneNumber)
				.setTimeout(60L, TimeUnit.SECONDS)
				.setActivity(this.requireActivity())
				.setCallbacks(phoneAuthCallbacks)
				.setForceResendingToken(token)
				.build()
			
			progressDialog.show()
			
			PhoneAuthProvider.verifyPhoneNumber(options)
		}
	}
	
	private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
		val credential = PhoneAuthProvider.getCredential(verificationId, code)
		
		signInWithPhoneAuthCredential(credential)
	}
	
	private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
		
		firebaseAuth.signInWithCredential(credential).addOnSuccessListener {
			Timber.d("Login Success")
			
			profileViewModel.getUser()
			
			profileViewModel.currentUser.observe(viewLifecycleOwner, { result ->
				when (result) {
					is Result.Success -> {
						progressDialog.cancel()
						
						Toast.makeText(
							requireContext(),
							"Logged in as ${profileViewModel.phoneNumber.value}",
							Toast.LENGTH_SHORT
						).show()
						
						Timber.d("${profileViewModel.currentUser.value?.data}")
						
						if (result.data == null) {
							requireView().findNavController()
								.navigate(R.id.action_enterOtpFragment_to_createProfileFragment)
						} else {
							val intent = Intent(requireActivity(), MainActivity::class.java)
							startActivity(intent)
							requireActivity().finish()
						}
					}
					
					is Result.Loading -> {
					}
					
					is Result.Error -> {
						progressDialog.cancel()
						
						result.exception?.let { exception -> handleLoginException(exception) }
					}
				}
			})
		}.addOnFailureListener { exception ->
			progressDialog.cancel()
			
			handleLoginException(exception)
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