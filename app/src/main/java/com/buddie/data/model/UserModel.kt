package com.buddie.data.model

import android.os.Parcelable
import com.buddie.data.util.GendersInterest
import com.buddie.data.util.GendersSelf
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    @get:PropertyName("name") @set:PropertyName("name") var name: String?,

    @get:PropertyName("phone_number") @set:PropertyName("phone_number") var phoneNumber: String?,

    @get:PropertyName("date_of_birth") @set:PropertyName("date_of_birth") var dateOfBirth: Long?,

    @get:PropertyName("gender") @set:PropertyName("gender") var gender: GendersSelf,

    @get:PropertyName("gender_interest") @set:PropertyName("gender_interest") var genderInterest: GendersInterest,

    @get:PropertyName("age_interest_start") @set:PropertyName("age_interest_start") var ageInterestStart: Int,

    @get:PropertyName("age_interest_end") @set:PropertyName("age_interest_end") var ageInterestEnd: Int,

    @get:PropertyName("location_city") @set:PropertyName("location_city") var locationCity: String
) : Parcelable {
	
	constructor() : this("", "", 0, GendersSelf.MALE, GendersInterest.FEMALE, 18, 100, "India")
}