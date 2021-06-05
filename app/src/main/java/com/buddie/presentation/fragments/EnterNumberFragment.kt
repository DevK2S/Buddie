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
import com.davidmiguel.numberkeyboard.NumberKeyboardListener

class EnterNumberFragment : BaseFragment(),NumberKeyboardListener {
	
	private lateinit var enterNumberBinding: FragmentEnterNumberBinding
	
	private lateinit var verificationId: String
	private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken
	private lateinit var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

	private var amount: Long = 0


	private val loginViewModel: LoginViewModel by activityViewModels()
	
	private val phoneNumberHintResult =
		registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
			if (result.resultCode == Activity.RESULT_OK) {
				val credential: Credential? = result.data?.getParcelableExtra(Credential.EXTRA_KEY)
				if (credential?.id != null) {
					val phoneNumber = credential.id
					loginViewModel.setPhoneNumber(phoneNumber)
					enterNumberBinding.phoneEt.setText(phoneNumber)
					
					startPhoneNumberVerification()
				}
			}
		}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		enterNumberBinding = FragmentEnterNumberBinding.inflate(inflater, container, false)
		return enterNumberBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		enterNumberBinding.numPad.setListener(this)

		initPhoneAuthCallbacks()
		
		initOnClickListeners()
		
		displayPhoneNumberHints()
	}

	override fun onLeftAuxButtonClicked() {

	}

	override fun onNumberClicked(number: Int) {
		val newAmount = (amount * 10.0 + number).toLong()
		if (newAmount < 10000000000) {
			amount = newAmount
			var st = amount.toString()
			var ph = "+91$st"
			enterNumberBinding.phoneEt.setText(ph)
		}
	}

	override fun onRightAuxButtonClicked() {
		amount = (amount / 10.0).toLong()
		var st = amount.toString()
		var ph = "+91$st"
		enterNumberBinding.phoneEt.setText(ph)
	}


	private fun initPhoneAuthCallbacks() {
		phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
			override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
				Timber.d(phoneAuthCredential.smsCode)
				
				phoneAuthCredential.smsCode?.let { loginViewModel.setOtp(it) }
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
				
				val navigate =
					EnterNumberFragmentDirections.actionEnterNumberFragmentToEnterOtpFragment(
						verificationId, forceResendingToken
					)
				
				requireView().findNavController().navigate(navigate)
			}
		}
	}
	
	private fun initOnClickListeners() {
		initContinueBtnOnClickListener()
	}
	
	private fun initContinueBtnOnClickListener() {
		enterNumberBinding.phoneContinueBtn.setOnClickListener {
			var phone = enterNumberBinding.phoneEt.text.toString()
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(activity, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()
			} else {
				phone = "$phone"
				
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
			loginViewModel.phoneNumber.observeOnce(viewLifecycleOwner, { phoneNumber ->
				val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
					.setPhoneNumber(phoneNumber)
					.setTimeout(60L, TimeUnit.SECONDS)
					.setActivity(this.requireActivity())
					.setCallbacks(phoneAuthCallbacks)
					.build()
				
				progressDialog.show()
				
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
