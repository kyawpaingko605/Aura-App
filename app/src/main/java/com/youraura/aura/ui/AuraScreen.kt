package com.youraura.aura.ui

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.youraura.aura.AuraViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraScreen(viewModel: AuraViewModel) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isListening by remember { mutableStateOf(false) }
    var isProcessingVoice by remember { mutableStateOf(false) }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(context, "✅ Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "⚠️ Voice features limited", Toast.LENGTH_LONG).show()
        }
    }
    
    // Init TTS
    LaunchedEffect(Unit) {
        viewModel.initTTS(context)
    }
    
    // Voice recognition (simplified)
    fun startVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.BODY_SENSORS
                )
            )
            return
        }
        
        isListening = true
        isProcessingVoice = true
        Toast.makeText(context, "🎤 ပြောပါ...", Toast.LENGTH_SHORT).show()
        
        // Simulate voice recognition
        // In real app: use SpeechRecognizer with RecognitionListener
        coroutineScope.launch {
            delay(3000)
            isListening = false
            isProcessingVoice = false
            
            // For demo: show dialog to input command
            // In real app, this comes from SpeechRecognizer
            Toast.makeText(context, "✅ ပြောပြီးပါပြီ", Toast.LENGTH_SHORT).show()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🌿 Aura",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Voice button
                    IconButton(
                        onClick = { startVoiceRecognition() },
                        enabled = !isProcessingVoice
                    ) {
                        Icon(
                            if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = "Voice Command",
                            tint = if (isListening) Color(0xFF6C63FF) else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E)
                )
            )
        },
        containerColor = Color(0xFF16213E)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mood Emoji with background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A2E))
            ) {
                Text(
                    text = state.mood.split(" ").firstOrNull() ?: "😊",
                    fontSize = 64.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mood text
            Text(
                text = state.mood,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Advice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A2E)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = state.advice,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Heart Rate & Sensor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Heart Rate
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        tint = Color.Red,
                        contentDescription = "Heart"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${state.heartRate} bpm",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                // Intensity
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Speed,
                        tint = Color(0xFF6C63FF),
                        contentDescription = "Intensity"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(state.sensorIntensity * 100 / 20).toInt()}%",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sensor progress bar
            LinearProgressIndicator(
                progress = (state.sensorIntensity / 20f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = when {
                    state.sensorIntensity > 15 -> Color.Red
                    state.sensorIntensity > 8 -> Color(0xFFFFA726)
                    else -> Color(0xFF66BB6A)
                },
                trackColor = Color(0xFF2A2A4A)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Calm Button
            Button(
                onClick = { viewModel.setCalm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🧘 ငြိမ်းချမ်းအောင်လုပ်",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Footer
            Text(
                text = "💡 ဒီနေ့ သင်ဟာ ထူးခြားတယ်",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
            
            // Voice processing indicator
            if (isProcessingVoice) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF6C63FF),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}
