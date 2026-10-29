package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ActionRow
import com.example.ui.components.AnswerGrid
import com.example.ui.components.CoinStoreDialog
import com.example.ui.components.ExplanationDialog
import com.example.ui.components.HintModal
import com.example.ui.components.LevelSelectorDialog
import com.example.ui.components.RiddleHeaderAndCard
import com.example.ui.components.StarryBackground
import com.example.ui.components.TopBar
import com.example.ui.components.VoiceSettingsDialog
import com.example.ui.viewmodel.RiddleViewModel

@Composable
fun MainGameScreen(
    viewModel: RiddleViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Show toast messages when emitted
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_game_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cosmic starry background
            StarryBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top status bar (Solved Count / Coins)
                TopBar(
                    solvedCount = uiState.solvedCount,
                    totalCount = uiState.totalCount,
                    coins = uiState.coins,
                    onScoreClick = { viewModel.openLevelSelector() },
                    onCoinsClick = { viewModel.openCoinStore() }
                )

                // Navigation controls between riddles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousRiddle() },
                        modifier = Modifier
                            .testTag("prev_riddle_nav")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Riddle",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Text(
                        text = "Level ${uiState.currentIndex + 1} of ${uiState.totalCount}",
                        color = Color(0xFFA78BFA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(
                        onClick = { viewModel.nextRiddle() },
                        modifier = Modifier
                            .testTag("next_riddle_nav")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Riddle",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Riddle Question Header & Glowing Card
                val currentRiddle = uiState.currentRiddle
                if (currentRiddle != null) {
                    RiddleHeaderAndCard(
                        riddleNumber = uiState.currentIndex + 1,
                        questionText = currentRiddle.question,
                        clueText = uiState.clueText,
                        isSpeaking = uiState.isSpeaking,
                        speechStatus = uiState.speechStatus,
                        autoSpeakEnabled = uiState.autoSpeakEnabled,
                        onSpeakerClick = { viewModel.toggleSpeech() },
                        onVoiceSettingsClick = { viewModel.openAudioSettingsModal() }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Lifelines & Tools Action Row
                    ActionRow(
                        onWatchVideo = { viewModel.watchRewardVideo() },
                        onShare = { viewModel.shareRiddle(context) },
                        onFiftyFifty = { viewModel.applyFiftyFifty(10) },
                        onSeeAnswer = { viewModel.seeAnswer(20) },
                        onMoreHints = { viewModel.openHintModal() }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2x2 Option Buttons with English Pronunciation
                    AnswerGrid(
                        options = currentRiddle.options,
                        correctAnswerIndex = currentRiddle.answer,
                        selectedOptionIndex = uiState.selectedOptionIndex,
                        isCorrect = uiState.isCorrect,
                        isAnswerRevealed = uiState.isAnswerRevealed,
                        eliminatedIndices = uiState.eliminatedOptionIndices,
                        onSelectOption = { index -> viewModel.onOptionSelected(index) },
                        onSpeakOption = { index -> viewModel.speakOption(index) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Modals
            if (uiState.showHintModal) {
                HintModal(
                    onDismiss = { viewModel.closeHintModal() },
                    onExposeLetter = { viewModel.exposeClue(15) },
                    onRemoveLetters = { viewModel.applyFiftyFifty(15) },
                    onSkipLevel = { viewModel.skipLevel(30) }
                )
            }

            if (uiState.showCoinStoreModal) {
                CoinStoreDialog(
                    currentCoins = uiState.coins,
                    isVideoPlaying = uiState.isVideoPlaying,
                    onWatchVideo = { viewModel.watchRewardVideo(15) },
                    onClaimDaily = { viewModel.claimDailyBonus() },
                    onShare = {
                        viewModel.shareRiddle(context)
                        viewModel.closeCoinStore()
                    },
                    onDismiss = { viewModel.closeCoinStore() }
                )
            }

            if (uiState.showLevelSelectorModal) {
                LevelSelectorDialog(
                    riddles = uiState.riddles,
                    currentIndex = uiState.currentIndex,
                    solvedIds = uiState.solvedRiddleIds,
                    onSelectRiddle = { index -> viewModel.jumpToRiddle(index) },
                    onDismiss = { viewModel.closeLevelSelector() }
                )
            }

            if (uiState.showAudioSettingsModal) {
                VoiceSettingsDialog(
                    autoSpeakEnabled = uiState.autoSpeakEnabled,
                    includeOptions = uiState.includeOptionsInSpeech,
                    speechRate = uiState.speechRate,
                    onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
                    onToggleIncludeOptions = { include -> viewModel.setIncludeOptionsInSpeech(include) },
                    onChangeSpeechRate = { rate -> viewModel.setSpeechRate(rate) },
                    onTestVoice = { viewModel.toggleSpeech() },
                    onDismiss = { viewModel.closeAudioSettingsModal() }
                )
            }

            if (uiState.showExplanationDialog) {
                val riddle = uiState.currentRiddle
                if (riddle != null) {
                    ExplanationDialog(
                        isCorrect = uiState.isCorrect == true,
                        correctAnswerText = riddle.options.getOrNull(riddle.answer) ?: "",
                        explanationText = riddle.explanation,
                        isAnswerRevealedOnly = uiState.isAnswerRevealed && uiState.selectedOptionIndex == null,
                        onDismiss = { viewModel.closeExplanationDialog() },
                        onNextRiddle = {
                            viewModel.closeExplanationDialog()
                            viewModel.nextRiddle()
                        }
                    )
                }
            }
        }
    }
}
