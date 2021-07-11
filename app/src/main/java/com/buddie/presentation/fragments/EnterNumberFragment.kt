package com.buddie.presentation.fragments

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.buddie.databinding.FragmentEnterNumberBinding
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.utils.observeOnce
import com.buddie.presentation.viewmodel.LoginViewModel
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class EnterNumberFragment : BaseFragment(), NumberKeyboardListener {
	
	private lateinit var binding: FragmentEnterNumberBinding
	
	private lateinit var verificationId: String
	private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken
	private lateinit var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
	
	private val loginViewModel: LoginViewModel by activityViewModels()
	
	private val phoneNumberHintResult =
		registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
			if (result.resultCode == Activity.RESULT_OK) {
				val credential: Credential? = result.data?.getParcelableExtra(Credential.EXTRA_KEY)
				if (credential?.id != null) {
					var phoneNumber = credential.id
					
					loginViewModel.setPhoneNumber(phoneNumber)
					
					phoneNumber = phoneNumber.substring(phoneNumber.length - 10)
					binding.etPhoneNumber.setText(phoneNumber)
					
					startPhoneNumberVerification()
				}
			}
		}
	
	private var phoneNumber: Long = 0
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentEnterNumberBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.countryCodePicker.registerCarrierNumberEditText(binding.etPhoneNumber)
		
		binding.numberKeyboard.setListener(this)
		
		initPhoneAuthCallbacks()
		
		initOnClickListeners()
		
		displayPhoneNumberHints()
	}
	
	override fun onLeftAuxButtonClicked() {
	
	}
	
	override fun onNumberClicked(number: Int) {
		val numberInput = phoneNumber * 10 + number
		
		if (numberInput == 0.toLong()) {
			phoneNumber = 0
			binding.etPhoneNumber.setText("")
		} else if (numberInput.toString().length <= 10) {
			phoneNumber = numberInput
			binding.etPhoneNumber.setText(phoneNumber.toString())
		}
	}
	
	override fun onRightAuxButtonClicked() {
		phoneNumber /= 10
		
		if (phoneNumber == 0.toLong()) {
			binding.etPhoneNumber.setText("")
		} else {
			binding.etPhoneNumber.setText(phoneNumber.toString())
		}
	}
	
	private fun initPhoneAuthCallbacks() {
		phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
			override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
				Timber.d(phoneAuthCredential.smsCode)
				
				phoneAuthCredential.smsCode?.let { loginViewModel.setOtp(it) }
			}
			
			override fun onVerificationFailed(exception: FirebaseException) {
				loadingDialog.cancel()
				
				handleLoginException(exception)
			}
			
			override fun onCodeSent(
				vId: String, token: PhoneAuthProvider.ForceResendingToken
			) {
				super.onCodeSent(vId, token)
				
				Timber.d("Code Sent")
				
				verificationId = vId
				forceResendingToken = token
				
				loadingDialog.cancel()
				
				Toast.makeText(requireContext(), "Verification Code Sent", Toast.LENGTH_SHORT)
					.show()
				
				val navigate =
					EnterNumberFragmentDirections.actionEnterNumberFragmentToEnterOtpFragment(
						verificationId, forceResendingToken
					)
				
				requireView().findNavController().navigate(navigate)
			}
		}
	}
	
	private fun initOnClickListeners() {
		initSendOtpButtonOnClickListener()
	}
	
	private fun initSendOtpButtonOnClickListener() {
		binding.btnSendOtp.setOnClickListener {
			val phone = binding.countryCodePicker.fullNumberWithPlus.toString()
			
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(activity, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()
			} else {
				if (Patterns.PHONE.matcher(phone).matches()) {
					loginViewModel.setPhoneNumber(phone)
					
					startPhoneNumberVerification()
				} else {
					Toast.makeText(activity, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT)
						.show()
				}
			}
		}
	}
	
	private fun displayPhoneNumberHints() {
		val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
		
		val pendingIntent =
			Credentials.getClient(requireActivity()).getHintPickerIntent(hintRequest)
		
		val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
		
		phoneNumberHintResult.launch(intentSenderRequest)
	}
	
	private fun startPhoneNumberVerification() {
		if (this::phoneAuthCallbacks.isInitialized) {
			loadingDialog.show()
			
			loginViewModel.phoneNumber.observeOnce(viewLifecycleOwner, { phoneNumber ->
				val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
					.setPhoneNumber(phoneNumber)
					.setTimeout(60L, TimeUnit.SECONDS)
					.setActivity(this.requireActivity())
					.setCallbacks(phoneAuthCallbacks)
					.build()
				
				PhoneAuthProvider.verifyPhoneNumber(options)
			})
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
