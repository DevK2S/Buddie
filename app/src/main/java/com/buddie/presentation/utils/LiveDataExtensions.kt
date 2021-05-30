package com.buddie.presentation.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
	observeForever(object : Observer<T> {
		override fun onChanged(t: T?) {
			removeObserver(this)
			observer.onChanged(t)
		}
	})
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
	observe(lifecycleOwner, object : Observer<T> {
		override fun onChanged(t: T?) {
			removeObserver(this)
			observer.onChanged(t)
		}
	})
}

inline fun <T> LiveData<T>.observeOnce(crossinline observer: (T) -> Unit) {
	observeForever(object : Observer<T> {
		override fun onChanged(value: T) {
			removeObserver(this)
			observer(value)
		}
	})
}

inline fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, crossinline observer: (T) -> Unit) {
	observe(owner, object : Observer<T> {
		override fun onChanged(value: T) {
			removeObserver(this)
			observer(value)
		}
	})
}