package com.buddie.presentation.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.buddie.R
import com.buddie.R.id.action_getStarted_to_enterNumber
import com.buddie.databinding.GetStartedPageBinding

class getStarted:Fragment(R.layout.get_started_page) {

    private var getStartedPageBinding:GetStartedPageBinding?=null


    //Before Implementing View Binding
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view= inflater!!.inflate(R.layout.get_started_page, container, false)
//        return view
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = GetStartedPageBinding.bind(view)
        getStartedPageBinding=binding

        binding.startBtn.setOnClickListener {view->
            Toast.makeText(activity, "Button Pressed", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.action_getStarted_to_enterNumber)
        }

        //Before implementing View Binding
//        btn.setOnClickListener{ view->
//            Toast.makeText(context, "Button Pressed", Toast.LENGTH_SHORT).show()
//            view.findNavController().navigate(R.id.action_getStarted_to_enterNumber)
//        }
    }


    override fun onDestroyView() {
        getStartedPageBinding=null
        super.onDestroyView()
    }
}