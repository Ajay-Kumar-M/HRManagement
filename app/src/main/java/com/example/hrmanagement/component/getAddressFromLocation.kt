package com.example.hrmanagement.component

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale

suspend fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double,
    response: (String?) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Async API
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: List<Address?>) {
                        if (!addresses.isEmpty()) {
                            val address: Address = addresses[0]!!
                            val fullAddress = address.getAddressLine(0)
                            response(address.locality)
                        } else {
                            response(null)
                        }
                    }
                    override fun onError(errorMessage: String?) {
                        response(null)
                    }
                }
            )
        }
    } else {
        // Synchronous API on background thread
        withContext(Dispatchers.IO) {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    // You can format the address as needed
                    address.getAddressLine(0) // Full address
                    // Or use address.locality, address.adminArea, etc.
                    response(address.locality)
                } else {
                    response(null)
                }
        }
    }
}


/*
//suspend fun getAddressFromLocation(
//    context: Context,
//    latitude: Double,
//    longitude: Double
//): String? {
//    return withContext(Dispatchers.IO) {
//        try {
//            Log.d("MainScreenViewModel", "getAddressFromLocation called $latitude $longitude")
//            val geocoder = Geocoder(context, Locale.getDefault())
////            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            Log.d("MainScreenViewModel", "getAddressFromLocation called 2 - $addresses")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocation(
//                    latitude,
//                    longitude,
//                    1,
//                    object : Geocoder.GeocodeListener {
//                        override fun onGeocode(addresses: List<Address?>) {
//                            return@withContext null
//                        }
//                        override fun onError(errorMessage: String?) {
//                            // Handle the error
//                            return@withContext null
//                        }
//                    }
//                )
//            } else {
//                // API 28 to 32: Use the synchronous Geocoder API on a background thread
//                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//                if (addresses != null && addresses.isNotEmpty()) {
//                    val address = addresses[0]
//                    // You can format the address as needed
//                    address.getAddressLine(0) // Full address
//                    // Or use address.locality, address.adminArea, etc.
//                    return@withContext address.toString()
//                } else {
//                    return@withContext null
//                }
//            }
////            return@withContext null
//        } catch (e: Exception) {
//            Log.d("MainScreenViewModel", "getAddressFromLocation exception - ${e.message}")
//            Log.d("MainScreenViewModel", "getAddressFromLocation exception - ${e.stackTraceToString()}")
//            Log.d("MainScreenViewModel", "getAddressFromLocation exception - ${e.stackTrace}")
//            return@withContext null // Handle error (e.g., network not available)
//        }
//    }
//}
 */