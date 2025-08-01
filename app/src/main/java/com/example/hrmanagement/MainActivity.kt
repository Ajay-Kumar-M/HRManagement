package com.example.hrmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.Service.MyApplication.Companion.networkMonitor
import com.example.hrmanagement.Service.MyApplication.Companion.themeModeState
import com.example.hrmanagement.misc.NetworkStatusMonitor
import com.example.hrmanagement.ui.navigation.AppNavigation
import com.example.hrmanagement.ui.theme.HRManagementTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read theme from DataStore
        lifecycleScope.launch {
            appPreferenceDataStore.themeModeFlow.collect { mode ->
                themeModeState.value = mode
            }
        }

        lifecycleScope.launch {
            networkMonitor.networkStatus.collect { status ->
                if (status == NetworkStatusMonitor.NetworkStatus.Disconnected) {
                    Toast.makeText(this@MainActivity, "Network Offline - please check your connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            HRManagementTheme(themeMode = themeModeState.value) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }

        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/*
        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Light)
        } else {
            setTheme(R.style.AppTheme_Dark)
        }
        recreate() // Recreate the activity to apply the new theme
 */