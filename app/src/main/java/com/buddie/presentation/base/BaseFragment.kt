package com.buddie.presentation.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.buddie.R
import com.google.firebase.auth.FirebaseAuth

open class BaseFragment: Fragment() {
	
	protected lateinit var firebaseAuth: FirebaseAuth
	protected lateinit var progressDialog: AlertDialog
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		firebaseAuth = (activity as BaseActivity).firebaseAuth
		
		initProgressBar()
	}
	
	private fun initProgressBar() {
		val pb = AlertDialog.Builder(requireContext(), R.style.TransparentProgressDialog)
		pb.setView(R.layout.progress_bar)
		
		progressDialog = pb.create()
		progressDialog.setCanceledOnTouchOutside(false)
	}
}