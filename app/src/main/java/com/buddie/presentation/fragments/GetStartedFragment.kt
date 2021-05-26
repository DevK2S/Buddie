package com.buddie.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.buddie.R
import com.buddie.databinding.FragmentGetStartedBinding

class GetStartedFragment : Fragment() {
	
	private lateinit var binding: FragmentGetStartedBinding
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		binding = FragmentGetStartedBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.startBtn.setOnClickListener {
			view.findNavController().navigate(R.id.action_getStarted_to_enterNumber)
		}
	}
}