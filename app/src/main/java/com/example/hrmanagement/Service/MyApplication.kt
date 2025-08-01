package com.example.hrmanagement.Service

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.example.hrmanagement.data.AppDataManager
import com.example.hrmanagement.data.AppPreferenceDataStore
import com.example.hrmanagement.data.AppThemeMode
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.data.UserSignInStatusRepository
import com.example.hrmanagement.misc.NetworkStatusMonitor
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class MyApplication: Application() {

    private lateinit var _appUserDetails: UserLoginData
    val appUserDetails: UserLoginData
        get() = _appUserDetails

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        appPreferenceDataStore = AppPreferenceDataStore(this)
        googleAuthenticationService = GoogleAuthenticationService()
        runBlocking {
            appUserEmailId = appPreferenceDataStore.emailFlow.firstOrNull().toString()
        }
        networkMonitor = NetworkStatusMonitor(this).also { it.startMonitor() }
        themeModeState = mutableStateOf(AppThemeMode.SYSTEM)
        appDataManager = AppDataManager()
    }

    companion object {
        internal lateinit var appPreferenceDataStore: AppPreferenceDataStore
        internal lateinit var googleAuthenticationService: GoogleAuthenticationService
//        internal val apiService = ApiService(client = HttpClient(OkHttp))
        internal lateinit var networkMonitor: NetworkStatusMonitor
        internal lateinit var appDataManager: AppDataManager
        internal lateinit var appUserEmailId: String
        internal lateinit var themeModeState: MutableState<AppThemeMode>
    }

    fun updateAppUserData(userLoginData: UserLoginData){
        _appUserDetails = userLoginData
    }
}