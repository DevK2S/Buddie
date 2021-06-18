package com.buddie.presentation.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.buddie.R
import com.buddie.data.model.UserModel
import com.buddie.databinding.FragmentCreateProfileBinding
import com.buddie.presentation.activities.LoginActivity
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.viewmodel.ProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*
import kotlin.collections.ArrayList
import timber.log.Timber

class CreateProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateProfileBinding
    private lateinit var datePicker: MaterialDatePicker<Long>
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDropDown()
        initDatePicker()
        binding.txtInputLayoutDOB.setOnClickListener(View.OnClickListener {
            datePicker.show(parentFragmentManager, datePicker.toString())
        })
        datePicker.addOnPositiveButtonClickListener {
            setdate()
        }
        binding.btnNext.setOnClickListener {
            if(validateFields())
                Timber.d("FieldsValidated")
            /*profileViewModel.saveCurrentUser(
                UserModel(
                    binding.editTextFirstName.text.toString(),
                    firebaseAuth.currentUser?.phoneNumber,
                    "23/02/2000",
                    "23",
                    "Male",
                    "25-30",
                    "India"
                )
            )*/

           /* val intent = Intent(activity as LoginActivity, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()*/
        }
    }

    private fun initDropDown() {
        val data: MutableList<String> = ArrayList()
        data.add("Male")
        data.add("Female")
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.gender_item, items)
        binding.autoComplete.setAdapter(adapter)
        binding.autoCompleteGender.setAdapter(adapter)
    }

    private fun initDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = today
        calendar[Calendar.YEAR] = Calendar.getInstance().get(Calendar.YEAR) - 18
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(calendar.timeInMillis)
        datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(calendar.timeInMillis)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
        binding.txtInputLayoutDOB.isFocusableInTouchMode = false
    }

    private fun setdate() {
        var date = datePicker.headerText
        var year = date.substring(7).replace(" ", "")
        var month = date.substring(0, 4).replace(" ", "")
        var months = listOf<String>(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )
        var setmonth = months.indexOf(month) + 1
        var day = date.substring(4, 7).replace(" ", "")
        day = day.replace(",", "")
        binding.txtDay.text = day
        binding.txtMonth.text = setmonth.toString()
        binding.txtYear.text = year
       // dateB = day + "/" + setmonth.toString() + "/" + year
        //Log.e("Date Picekr","${datePicker.headerText} ${setDate} ${it}")
        //binding.DateEditText.setText(setDate)
    }

    fun validateFields(): Boolean {
        var firstName = binding.editTextFirstName.text.toString()
        val firstNameValidation = validateName(firstName)
        var lastName = binding.editTextLastName.text.toString()
        val lastNameValidaton = validateName(lastName)
        var gender = binding.autoCompleteGender.text.toString()
        val genderValidation = validateGender(gender)
        var interest = binding.autoComplete.text.toString()
        val interestValidation = validateInterest(interest)
        val dobValidation = validateDob()
        if(firstNameValidation==null && lastNameValidaton == null && dobValidation == null && genderValidation == null && interestValidation == null ) {
            Timber.d("validateFields: all fields valid")
            return true
        }
        if(firstNameValidation !=null){
            binding.textInputLayoutFirstName.error=firstNameValidation
        }
        if(lastNameValidaton !=null){
            binding.textInputLayoutLastName.error=lastNameValidaton
        }
        if(dobValidation !=null){
            binding.txtInputLayoutDOB.error= dobValidation
        }
        if(genderValidation !=null){
            binding.textInputLayoutGender.error=genderValidation
        }
        if(interestValidation != null){
            binding.textInputLayoutInterest.error=interestValidation
        }

        return false;
    }

    private fun validateInterest(interest: String): String? {
        if (interest.trim { it <= ' ' }.isEmpty()) {
            return "Interest cannot be empty"
        }
        return null
    }

    private fun validateGender(gender: String): String? {
        if (gender.trim { it <= ' ' }.isEmpty()) {
            return "Gender cannot be empty"
        }
        return null
    }

    private fun validateDob(): String? {
        if(binding.txtDay.text.toString() == "dd" || binding.txtMonth.text.toString() == "mm" || binding.txtYear.text.toString()=="yyyy") {
            return "Date Of Birth cannot be empty"
           Timber.d("day %s",binding.txtDay.text.toString())
        }
        return null
    }

    private fun validateName(name: String): String? {
        if (name.trim { it <= ' ' }.isEmpty()) {
            return "Name cannot be empty"
        } else if (name.trim { it <= ' ' }.matches(Regex("^[0-9]+$"))) {
            return "Name cannot have numbers in it"
        } else if (!name.trim { it <= ' ' }.matches(Regex("^[a-zA-Z][a-zA-Z ]++$"))) {
            return "Invalid Name"
        }
        return null
    }

    private fun calculateAge(): Int {
        val year = Integer.parseInt(binding.txtYear.text.toString())
        val currentYear =Calendar.getInstance().get(Calendar.YEAR)
        return currentYear-year
    }
    private fun createDOB():String {
        var dateB = binding.txtDay.text.toString() + "/" + binding.txtMonth.text.toString() + "/" + binding.txtYear.text.toString()
        return dateB
    }


}