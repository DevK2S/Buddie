package com.buddie.data.model

import java.io.Serializable

data class UserProfile(
	var name: String? = null,
	var phone: String? = null,
	var dob: String? = null,
	var gender: String? = null,
	var ageIn: String? = null,
	var loc: String? = null
) : Serializable