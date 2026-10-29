package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Riddle
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class RiddleRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tricky_mind_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COINS = "user_coins"
        private const val KEY_CURRENT_INDEX = "current_riddle_index"
        private const val KEY_SOLVED_IDS = "solved_riddle_ids"
        private const val KEY_LAST_DAILY = "last_daily_reward_time"
        private const val KEY_AUTO_SPEAK = "auto_speak_questions"
        private const val KEY_INCLUDE_OPTIONS = "include_options_in_speech"
        private const val KEY_SPEECH_RATE = "speech_rate_float"
        private const val DEFAULT_COINS = 500
    }

    private val cachedRiddles: List<Riddle> by lazy {
        loadRiddlesFromAssets()
    }

    fun getAllRiddles(): List<Riddle> = cachedRiddles

    private fun loadRiddlesFromAssets(): List<Riddle> {
        val list = mutableListOf<Riddle>()
        try {
            val inputStream = context.assets.open("questions.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val question = obj.getString("question")
                val optionsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                val answer = obj.getInt("answer")
                val explanation = obj.optString("explanation", "")
                list.add(
                    Riddle(
                        id = i + 1,
                        question = question,
                        options = options,
                        answer = answer,
                        explanation = explanation
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun getCoins(): Int = prefs.getInt(KEY_COINS, DEFAULT_COINS)

    fun addCoins(amount: Int): Int {
        val newCoins = (getCoins() + amount).coerceAtLeast(0)
        prefs.edit().putInt(KEY_COINS, newCoins).apply()
        return newCoins
    }

    fun spendCoins(amount: Int): Boolean {
        val current = getCoins()
        return if (current >= amount) {
            prefs.edit().putInt(KEY_COINS, current - amount).apply()
            true
        } else {
            false
        }
    }

    fun getCurrentIndex(): Int = prefs.getInt(KEY_CURRENT_INDEX, 0)

    fun saveCurrentIndex(index: Int) {
        prefs.edit().putInt(KEY_CURRENT_INDEX, index).apply()
    }

    fun getSolvedRiddleIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_SOLVED_IDS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun markRiddleSolved(riddleId: Int) {
        val currentSet = prefs.getStringSet(KEY_SOLVED_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(riddleId.toString())
        prefs.edit().putStringSet(KEY_SOLVED_IDS, currentSet).apply()
    }

    fun claimDailyReward(): Int? {
        val now = System.currentTimeMillis()
        val lastClaim = prefs.getLong(KEY_LAST_DAILY, 0L)
        val oneDayMillis = 24 * 60 * 60 * 1000L
        return if (now - lastClaim >= oneDayMillis) {
            prefs.edit().putLong(KEY_LAST_DAILY, now).apply()
            addCoins(50)
            50
        } else {
            null
        }
    }

    // Voice / TTS settings persistence
    fun isAutoSpeakEnabled(): Boolean = prefs.getBoolean(KEY_AUTO_SPEAK, false)

    fun setAutoSpeakEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SPEAK, enabled).apply()
    }

    fun isIncludeOptionsInSpeech(): Boolean = prefs.getBoolean(KEY_INCLUDE_OPTIONS, true)

    fun setIncludeOptionsInSpeech(include: Boolean) {
        prefs.edit().putBoolean(KEY_INCLUDE_OPTIONS, include).apply()
    }

    fun getSpeechRate(): Float = prefs.getFloat(KEY_SPEECH_RATE, 0.92f)

    fun setSpeechRate(rate: Float) {
        prefs.edit().putFloat(KEY_SPEECH_RATE, rate).apply()
    }
}
