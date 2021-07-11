package com.buddie.presentation.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.buddie.R
import com.google.firebase.auth.FirebaseAuth

open class BaseFragment : Fragment() {
	
	protected lateinit var firebaseAuth: FirebaseAuth
	
	protected lateinit var loadingDialog: AlertDialog
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		firebaseAuth = (activity as BaseActivity).firebaseAuth
		
		initLoadingDialog()
	}
	
	private fun initLoadingDialog() {
		val pb = AlertDialog.Builder(requireContext(), R.style.TransparentLoadingBar)
		pb.setView(R.layout.dialog_loading_bar)
		
		loadingDialog = pb.create()
		loadingDialog.setCanceledOnTouchOutside(false)
	}
	
	protected fun getCurrentUserUid(): String? = firebaseAuth.currentUser?.uid
}