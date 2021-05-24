package com.buddie.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.buddie.R
import com.buddie.databinding.ActivityOtpVerifyBinding

class VerifyOtp:Fragment(R.layout.activity_otp_verify) {

    private var verifyBinding:ActivityOtpVerifyBinding?=null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ActivityOtpVerifyBinding.bind(view)
        binding.yourNumberTv.text="Your Phone Number Will be Show Here".toString()
    }

    override fun onDestroyView() {
        verifyBinding = null
        super.onDestroyView()
    }

}