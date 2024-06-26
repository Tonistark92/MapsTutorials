package com.iscoding.mapsxml

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.statusBarColor = resources.getColor(android.R.color.white)

        // Ensure the status bar icons are dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted
                handlePermissionGranted()

            } else {
                // Permission is denied
                handlePermissionDenied()
            }
        }

        // Check and request the location permission
        checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun checkPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                handlePermissionGranted()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionRationale()
            }
            else -> {
                // No explanation needed; request the permission
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun handlePermissionGranted() {
        // Handle the permission granted case
            supportFragmentManager.beginTransaction()
                .replace(R.id.mapFragmentContainer, MapsFragment())
                .commitNow()
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    private fun handlePermissionDenied() {
        // Handle the permission denied case
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionRationale() {
        // Show a rationale to explain why the permission is needed
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Location access is required to show maps.")
            .setPositiveButton("OK") { _, _ ->
                // Request the permission again
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}