package com.example.hrmanagement.misc

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkStatusMonitor(context: Context) {
    private var _networkStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Disconnected)
    val networkStatus = _networkStatus.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            _networkStatus.value = determineNetworkStatus(networkCapabilities)
        }

        override fun onLost(network: Network) {
            _networkStatus.value = NetworkStatus.Disconnected
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            _networkStatus.value = determineNetworkStatus(networkCapabilities)
        }
    }

    fun startMonitor() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun stopMonitor() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun determineNetworkStatus(networkCapabilities: NetworkCapabilities?): NetworkStatus {
        return when {
            networkCapabilities == null -> NetworkStatus.Disconnected
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                        NetworkStatus.WiFi

                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                        NetworkStatus.Cellular

                    else -> NetworkStatus.Connected
                }
            }

            else -> NetworkStatus.Disconnected
        }
    }

    sealed class NetworkStatus {
        data object Disconnected : NetworkStatus()
        data object Connected : NetworkStatus()
        data object WiFi : NetworkStatus()
        data object Cellular : NetworkStatus()
    }
}