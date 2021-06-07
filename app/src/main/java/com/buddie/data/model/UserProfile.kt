package com.buddie.data.model

import android.os.Parcel
import android.os.Parcelable

data class UserProfile(
	var name: String? = null,
	var phone: String? = null,
	var dob: String? = null,
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
		parcel.readString()
	)
	
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
		parcel.writeString(phone)
		parcel.writeString(dob)
		parcel.writeString(gender)
		parcel.writeString(ageIn)
		parcel.writeString(loc)
	}
	
	override fun describeContents(): Int {
		return 0
	}
	
	companion object CREATOR : Parcelable.Creator<UserProfile> {
		
		override fun createFromParcel(parcel: Parcel): UserProfile {
			return UserProfile(parcel)
		}
		
		override fun newArray(size: Int): Array<UserProfile?> {
			return arrayOfNulls(size)
		}
	}
}