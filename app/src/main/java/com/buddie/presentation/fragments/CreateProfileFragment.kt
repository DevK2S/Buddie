package com.buddie.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.buddie.R
import com.buddie.data.model.UserModel
import com.buddie.data.util.GendersInterest
import com.buddie.data.util.GendersSelf
import com.buddie.databinding.FragmentCreateProfileBinding
import com.buddie.presentation.activities.LoginActivity
import com.buddie.presentation.activities.MainActivity
import com.buddie.presentation.base.BaseFragment
import com.buddie.presentation.viewmodel.ProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class CreateProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentCreateProfileBinding
    private lateinit var datePicker: MaterialDatePicker<Long>
    private var dateOfBirth: Long = 0
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
        binding.etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilFirstName.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilLastName.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.actvGender.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilGender.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.tvDay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilDOB.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.actvInterest.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilInterest.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.fabAddImage.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(CreateProfileFragmentDirections.actionCreateProfileFragmentToAddPhotosFragment())
        }



        binding.tilDOB.setOnClickListener {
            initDatePicker()
        }
        binding.btnLetsGo.setOnClickListener {
            loadingDialog.show()
            if (validateFields()) {
                Timber.d("FieldsValidated")
            } else {
                loadingDialog.cancel()
            }

        }
    }

    private fun saveProfile() {
        profileViewModel.saveCurrentUser(
            UserModel(
                binding.etFirstName.text.toString() + " " + binding.etLastName.text.toString(),
                firebaseAuth.currentUser?.phoneNumber,
                dateOfBirth,
                GendersSelf.valueOf(binding.actvGender.text.toString()),
                GendersInterest.valueOf(binding.actvInterest.text.toString()),
                binding.rsAgeIn.valueFrom.toInt(),
                binding.rsAgeIn.valueTo.toInt(),
                "India"
            )
        )
        loadingDialog.cancel()
        val intent = Intent(activity as LoginActivity, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initDropDown() {

        val gendersSelf = enumValues<GendersSelf>()
        val gendersInterest = enumValues<GendersInterest>()
        val adapterSelf = ArrayAdapter(requireContext(), R.layout.gender_item, gendersSelf)
        val adapterInterest = ArrayAdapter(requireContext(), R.layout.gender_item, gendersInterest)
        binding.actvGender.setAdapter(adapterSelf)
        binding.actvInterest.setAdapter(adapterInterest)
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
        binding.tilDOB.isFocusableInTouchMode = false
        datePicker.show(parentFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            dateOfBirth = it
            setDate(it)
        }
    }

    private fun setDate(it: Long) {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dob = sdf.format(it)
        binding.tvDay.text = dob.substring(0, 2)
        binding.tvMonth.text = dob.substring(3, 5)
        binding.tvYear.text = dob.substring(6)

    }

    private fun validateFields(): Boolean {
        val firstName = binding.etFirstName.text.toString()
        val firstNameValidation = validateName(firstName)
        val lastName = binding.etLastName.text.toString()
        val lastNameValidation = validateName(lastName)
        val gender = binding.actvGender.text.toString()
        val genderValidation = validateGender(gender)
        val interest = binding.actvInterest.text.toString()
        val interestValidation = validateInterest(interest)
        val dobValidation = validateDob()
        if (firstNameValidation == null && lastNameValidation == null && dobValidation == null && genderValidation == null && interestValidation == null) {
            Timber.d("validateFields: all fields valid")
            return true
        }
        if (firstNameValidation != null) {
            binding.tilFirstName.error = firstNameValidation
        }
        if (lastNameValidation != null) {
            binding.tilLastName.error = lastNameValidation
        }
        if (dobValidation != null) {
            binding.tilDOB.error = dobValidation
        }
        if (genderValidation != null) {
            binding.tilGender.error = genderValidation
        }
        if (interestValidation != null) {
            binding.tilInterest.error = interestValidation
        }

        return false
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
        if (binding.tvDay.text.toString() == "dd" || binding.tvMonth.text.toString() == "mm" || binding.tvYear.text.toString() == "yyyy") {
            return "Date Of Birth cannot be empty"
            Timber.d("day %s", binding.tvDay.text.toString())
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
        val year = Integer.parseInt(binding.tvYear.text.toString())
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return currentYear - year
    }


}