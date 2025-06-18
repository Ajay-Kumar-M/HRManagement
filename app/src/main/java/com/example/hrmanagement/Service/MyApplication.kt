package com.example.hrmanagement.Service

import android.app.Application
import com.example.hrmanagement.data.AppDataManager
import com.example.hrmanagement.data.AppPreferenceDataStore
import com.example.hrmanagement.misc.NetworkStatusMonitor
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.security.AuthProvider

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        appPreferenceDataStore = AppPreferenceDataStore(this)
        googleAuthenticationService = GoogleAuthenticationService()
        networkMonitor = NetworkStatusMonitor(this).also { it.startMonitor() }
        appDataManager = AppDataManager()
        runBlocking {
            appUserEmailId = appPreferenceDataStore.emailFlow.firstOrNull().toString()
        }
    }

    companion object {
        lateinit var appPreferenceDataStore: AppPreferenceDataStore
        lateinit var googleAuthenticationService: GoogleAuthenticationService
        val apiService = ApiService(client = HttpClient(OkHttp))
        lateinit var networkMonitor: NetworkStatusMonitor
        lateinit var appDataManager: AppDataManager
        lateinit var appUserEmailId: String
    }
}