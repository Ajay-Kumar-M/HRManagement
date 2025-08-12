package com.example.hrmanagement.data

import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSignInStatusRepository @Inject constructor() {
    val userSignInStatusFlow: StateFlow<String> = appDataManager.userEventsFlow
    .stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Not Populated Yet!"
    )
}
