package com.buddie.data.util

sealed class Result<T>(
	val data: T? = null, val exception: Exception? = null, val message: String? = null
) {
	
	class Success<T>(data: T) : Result<T>(data)
	class Loading<T>(data: T? = null) : Result<T>(data)
	class Error<T>(data: T? = null, exception: Exception? = null, message: String? = null) :
		Result<T>(data, exception, message)
}
