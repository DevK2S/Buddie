package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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

    class EnteredTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.codeEt1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeEt2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeEt3 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeEt4 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.codeEt5 -> if (text.length == 1) nextView!!.requestFocus()
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
        }

    }

    class DeleteTextWatcher internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.codeEt1 && currentView.text.isEmpty()) {
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }


    }

    private lateinit var binding: FragmentEnterOtpBinding

    private lateinit var verificationId: String
    private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneAuthCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var count:Int = 0
    private var st: String = ""

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val args: EnterOtpFragmentArgs by navArgs()

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

        binding.numPad.setListener(this)

        initPhoneAuthCallbacks()

        initOnClickListeners()

        binding.dispNum.text =
            "Please Enter the OTP we've sent on ${loginViewModel.phoneNumber.value}"

        binding.codeEt1.addTextChangedListener(EnteredTextWatcher(binding.codeEt1, binding.codeEt2))
        binding.codeEt2.addTextChangedListener(EnteredTextWatcher(binding.codeEt2, binding.codeEt3))
        binding.codeEt3.addTextChangedListener(EnteredTextWatcher(binding.codeEt3, binding.codeEt4))
        binding.codeEt4.addTextChangedListener(EnteredTextWatcher(binding.codeEt4, binding.codeEt5))
        binding.codeEt5.addTextChangedListener(EnteredTextWatcher(binding.codeEt5, binding.codeEt6))
        binding.codeEt6.addTextChangedListener(EnteredTextWatcher(binding.codeEt6, null))

        binding.codeEt1.setOnKeyListener(DeleteTextWatcher(binding.codeEt1, null))
        binding.codeEt2.setOnKeyListener(DeleteTextWatcher(binding.codeEt2, binding.codeEt1))
        binding.codeEt3.setOnKeyListener(DeleteTextWatcher(binding.codeEt3, binding.codeEt2))
        binding.codeEt4.setOnKeyListener(DeleteTextWatcher(binding.codeEt4, binding.codeEt3))
        binding.codeEt5.setOnKeyListener(DeleteTextWatcher(binding.codeEt5, binding.codeEt4))
        binding.codeEt6.setOnKeyListener(DeleteTextWatcher(binding.codeEt6, binding.codeEt5))

        binding.codeEt1.requestFocus()
        count=1

        loginViewModel.otp.observe(viewLifecycleOwner, { otp ->
            //Needs change I replaced codeEt with codeEt1
            val codeEt = binding.codeEt1.text.toString().trim() + binding.codeEt2.text.toString()
                .trim() + binding.codeEt3.text.toString().trim() + binding.codeEt4.text.toString()
                .trim() + binding.codeEt5.text.toString().trim() + binding.codeEt6.text.toString()
                .trim()
            if ((codeEt.isNullOrBlank() || codeEt != otp) && !otp.isNullOrEmpty() ) {
                binding.codeEt1.setText(otp[0].toString())
                binding.codeEt2.setText(otp[1].toString())
                binding.codeEt3.setText(otp[2].toString())
                binding.codeEt4.setText(otp[3].toString())
                binding.codeEt5.setText(otp[4].toString())
                binding.codeEt6.setText(otp[5].toString())
                count=6
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
        binding.resendcodeTv.setOnClickListener {
            loginViewModel.phoneNumber.observeOnce(viewLifecycleOwner, { phoneNumber ->
                resendVerificationCode(phoneNumber, forceResendingToken)
            })
        }
    }

    private fun initSubmitBtnOnClickListener() {
        binding.codesubmitBtn.setOnClickListener {
            loadingDialog.show()

            //I made a change from codeEt to codeEt1, which is now corrected.
            val vCode = binding.codeEt1.text.toString().trim() + binding.codeEt2.text.toString()
                .trim() + binding.codeEt3.text.toString().trim() + binding.codeEt4.text.toString()
                .trim() + binding.codeEt5.text.toString().trim() + binding.codeEt6.text.toString()
                .trim()
            loginViewModel.setOtp(vCode)

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

            loginViewModel.checkUserExists()

            loginViewModel.userExists.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Success -> {
                        loadingDialog.cancel()

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

    override fun onLeftAuxButtonClicked() {
    }

    override fun onNumberClicked(number: Int) {
        var s = number.toString()
        when(count){
            1-> {
                binding.codeEt1.setText(s)
                count++
                binding.codeEt2.requestFocus()
            }
            2-> {
                binding.codeEt2.setText(s)
                count++
                binding.codeEt3.requestFocus()
            }
            3-> {
                binding.codeEt3.setText(s)
                count++
                binding.codeEt4.requestFocus()
            }
            4-> {
                binding.codeEt4.setText(s)
                count++
                binding.codeEt5.requestFocus()
            }
            5-> {
                binding.codeEt5.setText(s)
                count++
                binding.codeEt6.requestFocus()
            }
            6-> {
                binding.codeEt6.setText(s)
            }
        }
//        var s = number.toString()
//        if(st.length<6){
//            st += s
//            binding.codeEt.setText(st)
//        }
    }

    override fun onRightAuxButtonClicked() {
        when(count){
            1-> {
                binding.codeEt1.setText("")
            }
            2-> {
                binding.codeEt2.setText("")
                count--
                binding.codeEt1.requestFocus()
            }
            3-> {
                binding.codeEt3.setText("")
                count--
                binding.codeEt2.requestFocus()
            }
            4-> {
                binding.codeEt4.setText("")
                count--
                binding.codeEt3.requestFocus()
            }
            5-> {
                binding.codeEt5.setText("")
                count--
                binding.codeEt4.requestFocus()
            }
            6-> {
                binding.codeEt6.setText("")
                count--
                binding.codeEt5.requestFocus()
            }
        }
//        if (binding.codeEt.text.toString() != "XXXXXX") {
//            st = binding.codeEt.text.toString()
//        }
//        st = st.dropLast(1)
//        binding.codeEt.setText("XXXXXX")
//        if (st == "") {
//            binding.codeEt.setText("XXXXXX")
//        } else {
//            binding.codeEt.setText(st)
//        }
    }
}