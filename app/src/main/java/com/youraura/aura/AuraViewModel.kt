package com.youraura.aura

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class AuraState(
    val mood: String = "😊 အေးအေးဆေးဆေး",
    val advice: String = "ဒီနေ့ လေ့ကျင့်ခန်းလုပ်ပါ။",
    val heartRate: Int = 72,
    val isListening: Boolean = false,
    val sensorIntensity: Float = 0f
)

class AuraViewModel : ViewModel(), SensorEventListener {
    
    private val _state = MutableStateFlow(AuraState())
    val state: StateFlow<AuraState> = _state
    
    private var sensorManager: SensorManager? = null
    private var tts: TextToSpeech? = null
    
    // Init TTS
    fun initTTS(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("my", "MM")
            }
        }
    }
    
    // Start sensor listener
    fun startSensorListener(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    // Stop sensor listener
    fun stopSensorListener() {
        sensorManager?.unregisterListener(this)
    }
    
    // SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]
            val intensity = (Math.abs(x) + Math.abs(y) + Math.abs(z)) / 3
            
            viewModelScope.launch {
                _state.emit(_state.value.copy(sensorIntensity = intensity))
                
                when {
                    intensity > 15 -> {
                        _state.emit(_state.value.copy(
                            mood = "😡 စိတ်တိုနေတယ်",
                            advice = "အသက် ၃ ခါ ရှူပါ။ ရေအေးသောက်ပါ။"
                        ))
                        speak("စိတ်အေးအေးထားပါ")
                    }
                    intensity > 8 -> {
                        _state.emit(_state.value.copy(
                            mood = "😰 စိတ်လှုပ်ရှားနေတယ်",
                            advice = "ဖြည်းဖြည်းလမ်းလျှောက်ပါ။ ဂီတနားထောင်ပါ။"
                        ))
                        speak("အနားယူပါ")
                    }
                    else -> {
                        _state.emit(_state.value.copy(
                            mood = "😊 အေးအေးဆေးဆေး",
                            advice = "ကောင်းပါတယ်။ ဆက်လုပ်ပါ။"
                        ))
                    }
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }
    
    // Text-to-Speech
    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    // Voice command
    fun processVoiceCommand(command: String) {
        viewModelScope.launch {
            when {
                command.contains("အိပ်") -> {
                    _state.emit(_state.value.copy(
                        advice = "😴 အိပ်ချိန်ရောက်ပြီ။ လက်ဖက်ရည်ကြမ်းသောက်ပြီး အိပ်ပါ။"
                    ))
                    speak("အိပ်ပါ")
                }
                command.contains("အလုပ်") -> {
                    _state.emit(_state.value.copy(
                        advice = "⏰ ၂၅ မိနစ် အလုပ်၊ ၅ မိနစ် အနား (Pomodoro)"
                    ))
                    speak("အလုပ်လုပ်ပါ")
                }
                command.contains("လေ့ကျင့်") -> {
                    _state.emit(_state.value.copy(
                        advice = "🏋️ ထိုင်ထ ၁၀ ကြိမ်၊ ခုန် ၁၀ ကြိမ် လုပ်ပါ။"
                    ))
                    speak("လေ့ကျင့်ခန်း လုပ်ပါ")
                }
                command.contains("စိတ်") || command.contains("ငြိမ်း") -> {
                    setCalm()
                }
                else -> {
                    _state.emit(_state.value.copy(
                        advice = "💬 ခင်ဗျားပြောတာ မှတ်မိပါတယ်။"
                    ))
                }
            }
        }
    }
    
    // Manual calm
    fun setCalm() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(
                mood = "🧘 ငြိမ်းချမ်းတယ်",
                advice = "🌸 မင်းအတွက် ဂုဏ်ယူတယ်။ ဆက်ထိန်းထားပါ။"
            ))
            speak("ငြိမ်းချမ်းတယ်")
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopSensorListener()
        tts?.shutdown()
    }
}
