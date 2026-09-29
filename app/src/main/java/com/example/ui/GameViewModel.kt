package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.data.Category
import com.example.data.WordItem
import com.example.data.WordRepository
import com.example.sound.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class GameState(
    val level: Int = 1,
    val totalStars: Int = 100,
    val streak: Int = 0,
    val selectedCategory: Category = Category.ALL,
    val currentWordItem: WordItem = WordRepository.wordList[0],
    val currentGuessedLetters: List<Char?> = emptyList(), // Filled or blank positions
    val keyboardLetters: List<Char> = emptyList(), // Available letters to pick
    val usedKeyboardIndices: Set<Int> = emptySet(),
    val isWordCompleted: Boolean = false,
    val showVictoryDialog: Boolean = false,
    val showHintText: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isWrongAnswerFlash: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)

    private val prefs = application.getSharedPreferences("wordy_kids_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        GameState(
            totalStars = prefs.getInt("stars", 100),
            level = prefs.getInt("level", 1)
        )
    )
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private var availableWords = listOf<WordItem>()
    private var wordIndex = 0

    init {
        setupCategory(Category.ALL)
    }

    fun selectCategory(category: Category) {
        soundManager.playTap()
        _uiState.update { it.copy(selectedCategory = category) }
        setupCategory(category)
    }

    private fun setupCategory(category: Category) {
        availableWords = if (category == Category.ALL) {
            WordRepository.wordList.shuffled()
        } else {
            WordRepository.wordList.filter { it.category == category }.shuffled()
        }
        wordIndex = 0
        loadWord(availableWords.getOrNull(0) ?: WordRepository.wordList[0])
    }

    private fun loadWord(wordItem: WordItem) {
        val word = wordItem.word.uppercase()
        val len = word.length

        // Decide blank slot layout (for younger kids, fill a couple letters or leave blank slots)
        val initialGuessed = MutableList<Char?>(len) { null }
        
        // If word length >= 5, pre-fill 1 letter as helpful anchor
        if (len >= 5) {
            val anchorIndex = Random.nextInt(len)
            initialGuessed[anchorIndex] = word[anchorIndex]
        }

        // Build keyboard options: original letters + distractor letters to total 8 or 10 letters
        val wordLetters = word.toList()
        val alphabet = ('A'..'Z').toList()
        val extraCount = maxOf(4, 10 - wordLetters.size)
        val distractorLetters = alphabet.filter { !wordLetters.contains(it) }.shuffled().take(extraCount)
        
        val keyboard = (wordLetters + distractorLetters).shuffled()

        _uiState.update {
            it.copy(
                currentWordItem = wordItem,
                currentGuessedLetters = initialGuessed,
                keyboardLetters = keyboard,
                usedKeyboardIndices = emptySet(),
                isWordCompleted = false,
                showVictoryDialog = false,
                showHintText = false,
                isWrongAnswerFlash = false
            )
        }

        // Auto speak word hint
        soundManager.speak(wordItem.word)
    }

    fun onKeyboardLetterClick(index: Int, letter: Char) {
        val state = _uiState.value
        if (state.isWordCompleted || state.usedKeyboardIndices.contains(index)) return

        soundManager.playTap()

        // Find first empty slot in currentGuessedLetters
        val targetIndex = state.currentGuessedLetters.indexOfFirst { it == null }
        if (targetIndex != -1) {
            val updatedGuessed = state.currentGuessedLetters.toMutableList()
            updatedGuessed[targetIndex] = letter
            val newUsedIndices = state.usedKeyboardIndices + index

            _uiState.update {
                it.copy(
                    currentGuessedLetters = updatedGuessed,
                    usedKeyboardIndices = newUsedIndices
                )
            }

            // Check if word is fully filled
            if (!updatedGuessed.contains(null)) {
                verifyWord(updatedGuessed.joinToString(""))
            }
        }
    }

    fun onRemoveLetterClick(slotIndex: Int) {
        val state = _uiState.value
        if (state.isWordCompleted) return

        val charToRemove = state.currentGuessedLetters.getOrNull(slotIndex) ?: return
        
        // Find corresponding index in keyboard to un-use
        val keyboardIndexToFree = state.usedKeyboardIndices.firstOrNull { 
            state.keyboardLetters.getOrNull(it) == charToRemove 
        }

        val updatedGuessed = state.currentGuessedLetters.toMutableList()
        updatedGuessed[slotIndex] = null

        val updatedUsedIndices = if (keyboardIndexToFree != null) {
            state.usedKeyboardIndices - keyboardIndexToFree
        } else {
            state.usedKeyboardIndices
        }

        soundManager.playTap()
        _uiState.update {
            it.copy(
                currentGuessedLetters = updatedGuessed,
                usedKeyboardIndices = updatedUsedIndices,
                isWrongAnswerFlash = false
            )
        }
    }

    private fun verifyWord(userAttempt: String) {
        val targetWord = _uiState.value.currentWordItem.word.uppercase()
        if (userAttempt == targetWord) {
            // Correct answer!
            soundManager.playCorrect()
            soundManager.playFanfare()
            soundManager.speak("Awesome! ${targetWord}!")

            val newStars = _uiState.value.totalStars + 15
            val newLevel = _uiState.value.level + 1
            val newStreak = _uiState.value.streak + 1

            prefs.edit().putInt("stars", newStars).putInt("level", newLevel).apply()

            _uiState.update {
                it.copy(
                    isWordCompleted = true,
                    showVictoryDialog = true,
                    totalStars = newStars,
                    level = newLevel,
                    streak = newStreak
                )
            }
        } else {
            // Wrong attempt! Flash red warning & shake
            soundManager.playError()
            _uiState.update { it.copy(isWrongAnswerFlash = true) }
        }
    }

    fun useHint() {
        val state = _uiState.value
        if (state.isWordCompleted) return

        val targetWord = state.currentWordItem.word.uppercase()
        
        // Show hint text & reveal first incorrect/blank slot if possible
        soundManager.playTap()
        soundManager.speak(state.currentWordItem.hint)

        // Find first empty or wrong position
        val firstWrongOrEmptyIndex = state.currentGuessedLetters.indices.firstOrNull { i ->
            state.currentGuessedLetters[i] != targetWord[i]
        }

        if (firstWrongOrEmptyIndex != null) {
            val correctChar = targetWord[firstWrongOrEmptyIndex]
            
            // Find keyboard index for this char
            val kbIndex = state.keyboardLetters.indices.firstOrNull { k ->
                state.keyboardLetters[k] == correctChar && !state.usedKeyboardIndices.contains(k)
            }

            val updatedGuessed = state.currentGuessedLetters.toMutableList()
            updatedGuessed[firstWrongOrEmptyIndex] = correctChar

            val updatedUsed = if (kbIndex != null) state.usedKeyboardIndices + kbIndex else state.usedKeyboardIndices

            _uiState.update {
                it.copy(
                    currentGuessedLetters = updatedGuessed,
                    usedKeyboardIndices = updatedUsed,
                    showHintText = true
                )
            }

            if (!updatedGuessed.contains(null)) {
                verifyWord(updatedGuessed.joinToString(""))
            }
        } else {
            _uiState.update { it.copy(showHintText = true) }
        }
    }

    fun speakCurrentWord() {
        soundManager.speak(_uiState.value.currentWordItem.word)
    }

    fun nextWord() {
        soundManager.playTap()
        wordIndex++
        if (wordIndex >= availableWords.size) {
            availableWords = availableWords.shuffled()
            wordIndex = 0
        }
        loadWord(availableWords[wordIndex])
    }

    fun toggleSound() {
        val newSoundState = !_uiState.value.isSoundEnabled
        soundManager.isSoundEnabled = newSoundState
        _uiState.update { it.copy(isSoundEnabled = newSoundState) }
        if (newSoundState) soundManager.playTap()
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.shutdown()
    }
}
