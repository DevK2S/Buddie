package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.buddie.R
import com.buddie.data.util.Result
import com.buddie.databinding.FragmentEnterNumberBinding
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.viewmodel.ProfileViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class EnterNumberFragment : Fragment() {

    private val TAG = EnterNumberFragment::class.simpleName

    //Binding
    private lateinit var enterNumberBinding: FragmentEnterNumberBinding

    //private var forceResendingToken : PhoneAuthProvider.ForceResendingToken?= null

    private val profileViewModel : ProfileViewModel by activityViewModels()

    private var  callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    //private var verificationId: String? = null
    //private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterNumberBinding = FragmentEnterNumberBinding.inflate(inflater, container, false)
        return enterNumberBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.firebaseAuth = FirebaseAuth.getInstance()

        callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(activity, "${e.message}", Toast.LENGTH_SHORT).show()

            }

            override fun onCodeSent(
                veriFicationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(veriFicationId, token)
                Log.d(TAG, "onCodeSent: ${profileViewModel.verificationId}")
                profileViewModel.verificationId = veriFicationId
                profileViewModel.forceResendingToken = token
                requireView().findNavController().navigate(R.id.action_enterNumberFragment_to_enterOtpFragment)
                Toast.makeText(activity, "Verification Code Sent", Toast.LENGTH_SHORT).show()
            }
        }

        enterNumberBinding.phoneContinueBtn.setOnClickListener {
            val phone = enterNumberBinding.phoneEt.text.toString()
            profileViewModel.phNumber = phone
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(activity, "Please Enter Phone Number", Toast.LENGTH_SHORT).show()
            }
            else{
                startPhoneNumberVerification(phone)
            }
        }
    }

    private fun startPhoneNumberVerification(phone: String) {

        val options = PhoneAuthOptions.newBuilder(profileViewModel.firebaseAuth).setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this.requireActivity())
            .setCallbacks(callBacks!!).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        profileViewModel.firebaseAuth.signInWithCredential(credential).addOnSuccessListener { //Login Success
            val phone = profileViewModel.firebaseAuth.currentUser!!.phoneNumber
            Toast.makeText(activity, "Logged in as $phone", Toast.LENGTH_SHORT).show()

            profileViewModel.getUser()

            profileViewModel.currentUser.observe(viewLifecycleOwner, { result ->
                when (result) {
                    is Result.Success -> {
                        Log.e(TAG, "UserProfile ${profileViewModel.currentUser.value?.data}")
                        if (result.data == null) {
                            requireView().findNavController()
                                .navigate(R.id.action_enterNumberFragment_to_createProfileFragment)
                        } else {
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                    is Result.Loading -> {

                    }
                    is Result.Error -> {
                        Log.e(TAG, "UserProfile ${profileViewModel.currentUser.value}")
                    }
                }
            })
        }.addOnFailureListener { e -> //Login Failed
            Toast.makeText(activity, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
