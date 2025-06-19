package com.example.hrmanagement.Service

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.hrmanagement.data.AppDataManager
import com.example.hrmanagement.data.AppPreferenceDataStore
import com.example.hrmanagement.data.AppThemeMode
import com.example.hrmanagement.data.UserLoginData
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
        themeModeState = mutableStateOf(AppThemeMode.SYSTEM)
    }

    companion object {
        internal lateinit var appPreferenceDataStore: AppPreferenceDataStore
        internal lateinit var googleAuthenticationService: GoogleAuthenticationService
        internal val apiService = ApiService(client = HttpClient(OkHttp))
        internal lateinit var networkMonitor: NetworkStatusMonitor
        internal lateinit var appDataManager: AppDataManager
        internal lateinit var appUserEmailId: String
        internal lateinit var appUserDetails: UserLoginData
        internal lateinit var themeModeState: MutableState<AppThemeMode>
    }
}