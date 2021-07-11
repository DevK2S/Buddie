package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
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
import com.buddie.presentation.viewmodel.LoginViewModel
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class EnterOtpFragment : BaseFragment(), NumberKeyboardListener {
	
	private lateinit var binding: FragmentEnterOtpBinding
	
	private lateinit var verificationId: String
	private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken
	private lateinit var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
	
	private val loginViewModel: LoginViewModel by activityViewModels()
	
	private val args: EnterOtpFragmentArgs by navArgs()
	
	private var count: Int = 0
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentEnterOtpBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		verificationId = args.verificationId
		forceResendingToken = args.forceResendingToken
		
		binding.numberKeyboard.setListener(this)
		
		initPhoneAuthCallbacks()
		
		initOnClickListeners()
		
		val phoneNumber = loginViewModel.phoneNumber.value
		if (phoneNumber != null) {
			val phoneNumberString =
				phoneNumber.substring(0, phoneNumber.length - 10) + " " + phoneNumber.substring(
					phoneNumber.length - 10, phoneNumber.length - 5
				) + " " + phoneNumber.substring(phoneNumber.length - 5)
			
			binding.tvNumber.text = phoneNumberString
		}
		
		binding.etCode1.addTextChangedListener(EnteredTextWatcher(binding.etCode2))
		binding.etCode1.inputType = InputType.TYPE_NULL
		binding.etCode2.addTextChangedListener(EnteredTextWatcher(binding.etCode3))
		binding.etCode2.inputType = InputType.TYPE_NULL
		binding.etCode3.addTextChangedListener(EnteredTextWatcher(binding.etCode4))
		binding.etCode3.inputType = InputType.TYPE_NULL
		binding.etCode4.addTextChangedListener(EnteredTextWatcher(binding.etCode5))
		binding.etCode4.inputType = InputType.TYPE_NULL
		binding.etCode5.addTextChangedListener(EnteredTextWatcher(binding.etCode6))
		binding.etCode5.inputType = InputType.TYPE_NULL
		binding.etCode6.inputType = InputType.TYPE_NULL
		
		binding.etCode1.requestFocus()
		
		loginViewModel.otp.observe(viewLifecycleOwner, { otp ->
			val codeEt = binding.etCode1.text.toString().trim() + binding.etCode2.text.toString()
				.trim() + binding.etCode3.text.toString().trim() + binding.etCode4.text.toString()
				.trim() + binding.etCode5.text.toString().trim() + binding.etCode6.text.toString()
				.trim()
			
			if ((codeEt.isBlank() || codeEt != otp) && !otp.isNullOrEmpty()) {
				binding.etCode1.setText(otp[0].toString())
				binding.etCode2.setText(otp[1].toString())
				binding.etCode3.setText(otp[2].toString())
				binding.etCode4.setText(otp[3].toString())
				binding.etCode5.setText(otp[4].toString())
				binding.etCode6.setText(otp[5].toString())
				
				count = 6
			}
		})
	}
	
	private fun initPhoneAuthCallbacks() {
		phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
			override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
				loadingDialog.cancel()
				
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
			}
		}
	}
	
	private fun initOnClickListeners() {
		initResendCodeBtnOnClickListener()
		initSubmitBtnOnClickListener()
	}
	
	private fun initResendCodeBtnOnClickListener() {
		binding.tvResendCode.setOnClickListener {
			loginViewModel.phoneNumber.observeOnce(viewLifecycleOwner, { phoneNumber ->
				resendVerificationCode(phoneNumber, forceResendingToken)
			})
		}
	}
	
	private fun initSubmitBtnOnClickListener() {
		binding.btnSubmit.setOnClickListener {
			loadingDialog.show()
			
			val verificationCode =
				binding.etCode1.text.toString().trim() + binding.etCode2.text.toString()
					.trim() + binding.etCode3.text.toString()
					.trim() + binding.etCode4.text.toString()
					.trim() + binding.etCode5.text.toString()
					.trim() + binding.etCode6.text.toString().trim()
			
			loginViewModel.setOtp(verificationCode)
			
			loginViewModel.otp.observeOnce(viewLifecycleOwner, { otp ->
				when {
					TextUtils.isEmpty(otp) -> {
						loadingDialog.cancel()
						
						Toast.makeText(
							activity, "Please Enter Verification Code", Toast.LENGTH_SHORT
						).show()
					}
					otp.length != 6 -> {
						loadingDialog.cancel()
						
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
			
			loadingDialog.show()
			
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
			
			loginViewModel.userDataExists.observe(viewLifecycleOwner, { result ->
				when (result) {
					is Result.Success -> {
						loadingDialog.cancel()
						
						Toast.makeText(
							requireContext(),
							"Logged in with ${loginViewModel.phoneNumber.value}",
							Toast.LENGTH_SHORT
						).show()
						
						Timber.d("User Exists returned ${loginViewModel.userDataExists.value?.data}")
						
						if (result.data == true) {
							val intent = Intent(requireActivity(), MainActivity::class.java)
							startActivity(intent)
							loginViewModel.setOtp("")
							
							requireActivity().finish()
						} else {
							requireView().findNavController()
								.navigate(R.id.action_enterOtpFragment_to_createProfileFragment)
							
							loginViewModel.setOtp("")
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
		}.addOnFailureListener { exception ->
			loadingDialog.cancel()
			
			handleLoginException(exception)
		}
	}
	
	override fun onLeftAuxButtonClicked() {
	}
	
	override fun onNumberClicked(number: Int) {
		when (count) {
			0 -> {
				count++
				binding.etCode1.setText(number.toString())
				binding.etCode2.requestFocus()
			}
			1 -> {
				count++
				binding.etCode2.setText(number.toString())
				binding.etCode3.requestFocus()
			}
			2 -> {
				count++
				binding.etCode3.setText(number.toString())
				binding.etCode4.requestFocus()
			}
			3 -> {
				count++
				binding.etCode4.setText(number.toString())
				binding.etCode5.requestFocus()
			}
			4 -> {
				count++
				binding.etCode5.setText(number.toString())
				binding.etCode6.requestFocus()
			}
			5 -> {
				count++
				binding.etCode6.setText(number.toString())
			}
		}
	}
	
	override fun onRightAuxButtonClicked() {
		when (count) {
			1 -> {
				count--
				binding.etCode1.setText("")
				binding.etCode1.requestFocus()
			}
			2 -> {
				count--
				binding.etCode2.setText("")
				binding.etCode2.requestFocus()
			}
			3 -> {
				count--
				binding.etCode3.setText("")
				binding.etCode3.requestFocus()
			}
			4 -> {
				count--
				binding.etCode4.setText("")
				binding.etCode4.requestFocus()
			}
			5 -> {
				count--
				binding.etCode5.setText("")
				binding.etCode5.requestFocus()
			}
			6 -> {
				count--
				binding.etCode6.setText("")
				binding.etCode6.requestFocus()
			}
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
	
	class EnteredTextWatcher internal constructor(
		private val nextView: View
	) : TextWatcher {
		
		override fun afterTextChanged(editable: Editable) {
			val text = editable.toString()
			if (text.length == 1) nextView.requestFocus()
		}
		
		override fun beforeTextChanged(
			arg0: CharSequence, start: Int, count: Int, after: Int
		) {
		}
		
		override fun onTextChanged(
			arg0: CharSequence, start: Int, before: Int, count: Int
		) {
		}
	}
}