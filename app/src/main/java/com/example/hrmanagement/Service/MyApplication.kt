package com.example.hrmanagement.Service

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.hrmanagement.data.AppDataManager
import com.example.hrmanagement.data.AppPreferenceDataStore
import com.example.hrmanagement.data.AppThemeMode
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.misc.NetworkStatusMonitor
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.firstOrNull
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
        networkMonitor = NetworkStatusMonitor(this).also { it.startMonitor() }
        appDataManager = AppDataManager()
        runBlocking {
            appUserEmailId = appPreferenceDataStore.emailFlow.firstOrNull().toString()
        }
        themeModeState = mutableStateOf(AppThemeMode.SYSTEM)
        appContext = this
    }

    companion object {
        internal lateinit var appPreferenceDataStore: AppPreferenceDataStore
        internal lateinit var googleAuthenticationService: GoogleAuthenticationService
//        internal val apiService = ApiService(client = HttpClient(OkHttp))
        internal lateinit var networkMonitor: NetworkStatusMonitor
        internal lateinit var appDataManager: AppDataManager
        internal lateinit var appUserEmailId: String
        internal lateinit var themeModeState: MutableState<AppThemeMode>
        internal lateinit var appContext: Context
    }

    fun updateAppUserData(userLoginData: UserLoginData){
        _appUserDetails = userLoginData
    }
}