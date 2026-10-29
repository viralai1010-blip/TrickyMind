package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.audio.SpeechManager
import com.example.data.model.Riddle
import com.example.data.repository.RiddleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RiddleUiState(
    val riddles: List<Riddle> = emptyList(),
    val currentIndex: Int = 0,
    val coins: Int = 500,
    val solvedRiddleIds: Set<Int> = emptySet(),
    val eliminatedOptionIndices: Set<Int> = emptySet(),
    val selectedOptionIndex: Int? = null,
    val isCorrect: Boolean? = null,
    val isAnswerRevealed: Boolean = false,
    val clueText: String? = null,
    val showHintModal: Boolean = false,
    val showCoinStoreModal: Boolean = false,
    val showLevelSelectorModal: Boolean = false,
    val showExplanationDialog: Boolean = false,
    val showAudioSettingsModal: Boolean = false,
    val isSpeaking: Boolean = false,
    val speechStatus: String = "",
    val autoSpeakEnabled: Boolean = false,
    val includeOptionsInSpeech: Boolean = true,
    val speechRate: Float = 0.92f,
    val toastMessage: String? = null,
    val isVideoPlaying: Boolean = false
) {
    val currentRiddle: Riddle?
        get() = riddles.getOrNull(currentIndex)

    val solvedCount: Int
        get() = solvedRiddleIds.size

    val totalCount: Int
        get() = riddles.size
}

class RiddleViewModel(
    private val repository: RiddleRepository,
    private val soundManager: SoundManager,
    private val speechManager: SpeechManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiddleUiState())
    val uiState: StateFlow<RiddleUiState> = _uiState.asStateFlow()

    init {
        loadData()
        viewModelScope.launch {
            speechManager.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
            }
        }
        viewModelScope.launch {
            speechManager.speechStatus.collect { status ->
                _uiState.update { it.copy(speechStatus = status) }
            }
        }
    }

    private fun loadData() {
        val riddles = repository.getAllRiddles()
        val savedIndex = repository.getCurrentIndex().coerceIn(0, (riddles.size - 1).coerceAtLeast(0))
        val coins = repository.getCoins()
        val solvedIds = repository.getSolvedRiddleIds()
        val autoSpeak = repository.isAutoSpeakEnabled()
        val includeOptions = repository.isIncludeOptionsInSpeech()
        val rate = repository.getSpeechRate()

        speechManager.setSpeechRate(rate)

        _uiState.update {
            it.copy(
                riddles = riddles,
                currentIndex = savedIndex,
                coins = coins,
                solvedRiddleIds = solvedIds,
                autoSpeakEnabled = autoSpeak,
                includeOptionsInSpeech = includeOptions,
                speechRate = rate
            )
        }
    }

    fun onOptionSelected(optionIndex: Int) {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return

        // Prevent selecting eliminated options or if already correctly solved in this view
        if (state.eliminatedOptionIndices.contains(optionIndex)) return
        if (state.isCorrect == true) return

        soundManager.playClick()
        val isCorrect = optionIndex == riddle.answer

        if (isCorrect) {
            soundManager.playCorrect()
            val alreadySolved = state.solvedRiddleIds.contains(riddle.id)
            val updatedCoins = if (!alreadySolved) {
                repository.addCoins(10)
            } else {
                state.coins
            }
            repository.markRiddleSolved(riddle.id)

            _uiState.update {
                it.copy(
                    selectedOptionIndex = optionIndex,
                    isCorrect = true,
                    coins = updatedCoins,
                    solvedRiddleIds = repository.getSolvedRiddleIds(),
                    showExplanationDialog = true
                )
            }
        } else {
            soundManager.playWrong()
            _uiState.update {
                it.copy(
                    selectedOptionIndex = optionIndex,
                    isCorrect = false
                )
            }
        }
    }

    fun applyFiftyFifty(cost: Int = 10) {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return

        if (state.eliminatedOptionIndices.size >= 2) {
            showToast("50:50 already applied!")
            return
        }

        if (state.coins < cost) {
            showToast("Not enough coins! Need $cost coins.")
            _uiState.update { it.copy(showCoinStoreModal = true) }
            return
        }

        if (repository.spendCoins(cost)) {
            soundManager.playCoin()
            val wrongIndices = riddle.options.indices.filter { it != riddle.answer }.shuffled()
            val toEliminate = wrongIndices.take(2).toSet()

            _uiState.update {
                it.copy(
                    coins = repository.getCoins(),
                    eliminatedOptionIndices = toEliminate,
                    showHintModal = false
                )
            }
            showToast("2 wrong options removed! (-$cost coins)")
        }
    }

    fun seeAnswer(cost: Int = 20) {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return

        if (state.isAnswerRevealed) {
            _uiState.update { it.copy(showExplanationDialog = true) }
            return
        }

        if (state.coins < cost) {
            showToast("Not enough coins! Need $cost coins.")
            _uiState.update { it.copy(showCoinStoreModal = true) }
            return
        }

        if (repository.spendCoins(cost)) {
            soundManager.playCoin()
            _uiState.update {
                it.copy(
                    coins = repository.getCoins(),
                    isAnswerRevealed = true,
                    selectedOptionIndex = riddle.answer,
                    isCorrect = true,
                    showExplanationDialog = true,
                    showHintModal = false
                )
            }
            showToast("Answer revealed! (-$cost coins)")
        }
    }

    fun skipLevel(cost: Int = 30) {
        val state = _uiState.value
        if (state.coins < cost) {
            showToast("Not enough coins! Need $cost coins.")
            _uiState.update { it.copy(showCoinStoreModal = true) }
            return
        }

        if (repository.spendCoins(cost)) {
            soundManager.playCoin()
            _uiState.update { it.copy(coins = repository.getCoins(), showHintModal = false) }
            showToast("Level skipped! (-$cost coins)")
            nextRiddle()
        }
    }

    fun exposeClue(cost: Int = 15) {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return

        if (state.clueText != null) {
            showToast("Clue already revealed!")
            return
        }

        if (state.coins < cost) {
            showToast("Not enough coins! Need $cost coins.")
            _uiState.update { it.copy(showCoinStoreModal = true) }
            return
        }

        if (repository.spendCoins(cost)) {
            soundManager.playCoin()
            val correctAnswer = riddle.options.getOrNull(riddle.answer) ?: ""
            val clue = if (correctAnswer.length > 1) {
                "Starts with '${correctAnswer.first()}' and ends with '${correctAnswer.last()}'"
            } else {
                "First letter is '${correctAnswer.firstOrNull() ?: "?"}'"
            }

            _uiState.update {
                it.copy(
                    coins = repository.getCoins(),
                    clueText = clue,
                    showHintModal = false
                )
            }
            showToast("Clue: $clue (-$cost coins)")
        }
    }

    fun watchRewardVideo(bonus: Int = 15) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVideoPlaying = true) }
            delay(1200)
            val newCoins = repository.addCoins(bonus)
            soundManager.playCoin()
            _uiState.update {
                it.copy(
                    coins = newCoins,
                    isVideoPlaying = false,
                    showCoinStoreModal = false
                )
            }
            showToast("+$bonus Coins Earned! 🎉")
        }
    }

    fun claimDailyBonus() {
        val claimed = repository.claimDailyReward()
        if (claimed != null) {
            soundManager.playCoin()
            _uiState.update { it.copy(coins = repository.getCoins()) }
            showToast("Daily Bonus claimed: +$claimed Coins! 🎁")
        } else {
            showToast("Daily bonus already claimed today! Check back tomorrow.")
        }
    }

    fun toggleSpeech() {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return

        if (state.isSpeaking) {
            speechManager.stop()
        } else {
            speechManager.speakRiddle(
                riddleNumber = state.currentIndex + 1,
                question = riddle.question,
                options = riddle.options,
                includeOptions = state.includeOptionsInSpeech
            )
        }
    }

    fun speakOption(index: Int) {
        val state = _uiState.value
        val riddle = state.currentRiddle ?: return
        val optionText = riddle.options.getOrNull(index) ?: return
        val letters = listOf("A", "B", "C", "D")
        val letter = letters.getOrElse(index) { "${index + 1}" }
        speechManager.speakOption(letter, optionText)
    }

    fun stopSpeech() {
        speechManager.stop()
    }

    fun toggleAutoSpeak() {
        val current = _uiState.value.autoSpeakEnabled
        val newAutoSpeak = !current
        repository.setAutoSpeakEnabled(newAutoSpeak)
        _uiState.update { it.copy(autoSpeakEnabled = newAutoSpeak) }
        showToast(if (newAutoSpeak) "Auto-speech enabled for new levels 🔊" else "Auto-speech disabled 🔇")
    }

    fun setIncludeOptionsInSpeech(include: Boolean) {
        repository.setIncludeOptionsInSpeech(include)
        _uiState.update { it.copy(includeOptionsInSpeech = include) }
    }

    fun setSpeechRate(rate: Float) {
        repository.setSpeechRate(rate)
        speechManager.setSpeechRate(rate)
        _uiState.update { it.copy(speechRate = rate) }
    }

    fun nextRiddle() {
        stopSpeech()
        val total = _uiState.value.riddles.size
        if (total == 0) return
        val next = (_uiState.value.currentIndex + 1) % total
        goToIndex(next)
    }

    fun previousRiddle() {
        stopSpeech()
        val total = _uiState.value.riddles.size
        if (total == 0) return
        val prev = if (_uiState.value.currentIndex - 1 < 0) total - 1 else _uiState.value.currentIndex - 1
        goToIndex(prev)
    }

    fun jumpToRiddle(index: Int) {
        stopSpeech()
        goToIndex(index)
        _uiState.update { it.copy(showLevelSelectorModal = false) }
    }

    private fun goToIndex(index: Int) {
        repository.saveCurrentIndex(index)
        _uiState.update {
            it.copy(
                currentIndex = index,
                selectedOptionIndex = null,
                isCorrect = null,
                isAnswerRevealed = false,
                clueText = null,
                eliminatedOptionIndices = emptySet(),
                showExplanationDialog = false
            )
        }

        // Auto-speech if enabled
        if (_uiState.value.autoSpeakEnabled) {
            viewModelScope.launch {
                delay(400)
                val riddle = _uiState.value.riddles.getOrNull(index) ?: return@launch
                speechManager.speakRiddle(
                    riddleNumber = index + 1,
                    question = riddle.question,
                    options = riddle.options,
                    includeOptions = _uiState.value.includeOptionsInSpeech
                )
            }
        }
    }

    fun openHintModal() {
        soundManager.playClick()
        _uiState.update { it.copy(showHintModal = true) }
    }

    fun closeHintModal() {
        _uiState.update { it.copy(showHintModal = false) }
    }

    fun openCoinStore() {
        soundManager.playClick()
        _uiState.update { it.copy(showCoinStoreModal = true) }
    }

    fun closeCoinStore() {
        _uiState.update { it.copy(showCoinStoreModal = false) }
    }

    fun openLevelSelector() {
        soundManager.playClick()
        _uiState.update { it.copy(showLevelSelectorModal = true) }
    }

    fun closeLevelSelector() {
        _uiState.update { it.copy(showLevelSelectorModal = false) }
    }

    fun openAudioSettingsModal() {
        soundManager.playClick()
        _uiState.update { it.copy(showAudioSettingsModal = true) }
    }

    fun closeAudioSettingsModal() {
        _uiState.update { it.copy(showAudioSettingsModal = false) }
    }

    fun closeExplanationDialog() {
        _uiState.update { it.copy(showExplanationDialog = false) }
    }

    fun shareRiddle(context: Context) {
        val riddle = _uiState.value.currentRiddle ?: return
        val shareText = """
            🧠 Tricky Mind Riddle #${_uiState.value.currentIndex + 1}:
            
            "${riddle.question}"
            
            Can you solve it? Test your brain on Tricky Mind!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Riddle via")
        context.startActivity(shareIntent)
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.shutdown()
        soundManager.release()
    }
}
