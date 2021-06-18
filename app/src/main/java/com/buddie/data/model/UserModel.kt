package com.buddie.data.model

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
	var name: String? = null,
	var phone: String? = null,
	var dob: String? = null,
	var age: String? =null,
	var gender: String? = null,
	var ageIn: String? = null,
	var loc: String? = null
) : Parcelable {
	
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
		parcel.writeString(phone)
		parcel.writeString(dob)
		parcel.writeString(age)
		parcel.writeString(gender)
		parcel.writeString(ageIn)
		parcel.writeString(loc)
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	companion object CREATOR : Parcelable.Creator<UserModel> {
		
		override fun createFromParcel(parcel: Parcel): UserModel {
			return UserModel(parcel)
		}
		
		override fun newArray(size: Int): Array<UserModel?> {
			return arrayOfNulls(size)
		}
	}
}