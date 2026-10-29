package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OptionCardBg
import com.example.ui.theme.OptionCardCorrect
import com.example.ui.theme.OptionCardEliminated
import com.example.ui.theme.OptionCardIncorrect
import com.example.ui.theme.OptionCardText

@Composable
fun AnswerGrid(
    options: List<String>,
    correctAnswerIndex: Int,
    selectedOptionIndex: Int?,
    isCorrect: Boolean?,
    isAnswerRevealed: Boolean,
    eliminatedIndices: Set<Int>,
    onSelectOption: (Int) -> Unit,
    onSpeakOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: options 0 and 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionButton(
                letter = "A",
                text = options.getOrNull(0) ?: "",
                index = 0,
                correctAnswerIndex = correctAnswerIndex,
                selectedOptionIndex = selectedOptionIndex,
                isCorrect = isCorrect,
                isAnswerRevealed = isAnswerRevealed,
                isEliminated = eliminatedIndices.contains(0),
                onSelect = { onSelectOption(0) },
                onSpeak = { onSpeakOption(0) },
                modifier = Modifier.weight(1f)
            )

            OptionButton(
                letter = "B",
                text = options.getOrNull(1) ?: "",
                index = 1,
                correctAnswerIndex = correctAnswerIndex,
                selectedOptionIndex = selectedOptionIndex,
                isCorrect = isCorrect,
                isAnswerRevealed = isAnswerRevealed,
                isEliminated = eliminatedIndices.contains(1),
                onSelect = { onSelectOption(1) },
                onSpeak = { onSpeakOption(1) },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: options 2 and 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionButton(
                letter = "C",
                text = options.getOrNull(2) ?: "",
                index = 2,
                correctAnswerIndex = correctAnswerIndex,
                selectedOptionIndex = selectedOptionIndex,
                isCorrect = isCorrect,
                isAnswerRevealed = isAnswerRevealed,
                isEliminated = eliminatedIndices.contains(2),
                onSelect = { onSelectOption(2) },
                onSpeak = { onSpeakOption(2) },
                modifier = Modifier.weight(1f)
            )

            OptionButton(
                letter = "D",
                text = options.getOrNull(3) ?: "",
                index = 3,
                correctAnswerIndex = correctAnswerIndex,
                selectedOptionIndex = selectedOptionIndex,
                isCorrect = isCorrect,
                isAnswerRevealed = isAnswerRevealed,
                isEliminated = eliminatedIndices.contains(3),
                onSelect = { onSelectOption(3) },
                onSpeak = { onSpeakOption(3) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OptionButton(
    letter: String,
    text: String,
    index: Int,
    correctAnswerIndex: Int,
    selectedOptionIndex: Int?,
    isCorrect: Boolean?,
    isAnswerRevealed: Boolean,
    isEliminated: Boolean,
    onSelect: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedOptionIndex == index
    val isTargetCorrect = index == correctAnswerIndex

    val targetBgColor = when {
        isEliminated -> OptionCardEliminated
        isAnswerRevealed && isTargetCorrect -> OptionCardCorrect
        isSelected && isCorrect == true -> OptionCardCorrect
        isSelected && isCorrect == false -> OptionCardIncorrect
        else -> OptionCardBg
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 250),
        label = "option_bg_color"
    )

    val contentColor = when {
        isEliminated -> Color.Gray
        (isSelected && isCorrect != null) || (isAnswerRevealed && isTargetCorrect) -> Color.White
        else -> OptionCardText
    }

    Card(
        modifier = modifier
            .testTag("option_button_$index")
            .defaultMinSize(minHeight = 56.dp)
            .alpha(if (isEliminated) 0.2f else 1f)
            .clickable(enabled = !isEliminated && isCorrect != true) { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isEliminated) 0.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if ((isSelected && isCorrect == true) || (isAnswerRevealed && isTargetCorrect)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else if (isSelected && isCorrect == false) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Incorrect",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = "$letter: $text",
                    color = contentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                // Optional audio pronunciation icon for this individual choice
                if (!isEliminated) {
                    IconButton(
                        onClick = onSpeak,
                        modifier = Modifier
                            .testTag("speak_option_$index")
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Pronounce $text in English",
                            tint = contentColor.copy(alpha = 0.65f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
