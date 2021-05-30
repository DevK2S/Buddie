package com.buddie.presentation

import android.app.Application
import com.buddie.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class BuddieApp : Application() {
	
	override fun onCreate() {
		super.onCreate()
		
		if (BuildConfig.DEBUG) {
			Timber.plant(DebugTree())
		}
	}
}