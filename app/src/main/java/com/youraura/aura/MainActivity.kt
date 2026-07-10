package com.youraura.aura

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.youraura.aura.ui.AuraScreen
import com.youraura.aura.ui.theme.AuraTheme
import android.widget.Toast

class MainActivity : ComponentActivity() {
    
    private val viewModel: AuraViewModel by viewModels()
    
    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(this, "✅ Permissions granted", Toast.LENGTH_SHORT).show()
            viewModel.startSensorListener(this)
        } else {
            Toast.makeText(this, "⚠️ Permissions needed for full features", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check and request permissions
        checkPermissions()
        
        setContent {
            AuraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuraScreen(viewModel)
                }
            }
        }
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BODY_SENSORS
        )
        
        val needPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (needPermissions.isNotEmpty()) {
            permissionLauncher.launch(needPermissions)
        } else {
            viewModel.startSensorListener(this)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopSensorListener()
    }
}
