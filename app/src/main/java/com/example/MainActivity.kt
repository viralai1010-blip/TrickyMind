package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.audio.SoundManager
import com.example.audio.SpeechManager
import com.example.data.repository.RiddleRepository
import com.example.ui.screens.MainGameScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RiddleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RiddleViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = RiddleRepository(applicationContext)
                val soundManager = SoundManager(applicationContext)
                val speechManager = SpeechManager(applicationContext)
                return RiddleViewModel(repository, soundManager, speechManager) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainGameScreen(viewModel = viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopSpeech()
    }
}
