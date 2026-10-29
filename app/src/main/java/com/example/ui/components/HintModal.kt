package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GoldCoinColor
import com.example.ui.theme.LightbulbBlue
import com.example.ui.theme.ModalDarkNavy

@Composable
fun HintModal(
    onDismiss: () -> Unit,
    onExposeLetter: () -> Unit,
    onRemoveLetters: () -> Unit,
    onSkipLevel: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .testTag("hint_modal")
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = ModalDarkNavy
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("hint_modal_close")
                    ) {
                        Text(
                            text = "Close",
                            color = Color(0xFF94A3B8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Circular blue badge with glowing bulb matching the screenshot
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(LightbulbBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint Lightbulb",
                        tint = GoldCoinColor,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Option 1: Expose a letter / clue
                HintItemRow(
                    title = "Expose a letter / clue",
                    cost = 15,
                    testTag = "hint_item_expose",
                    onClick = onExposeLetter
                )

                HorizontalDivider(
                    color = Color(0xFF334155),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Option 2: Remove letters / 50:50
                HintItemRow(
                    title = "Remove options (50:50)",
                    cost = 15,
                    testTag = "hint_item_remove",
                    onClick = onRemoveLetters
                )

                HorizontalDivider(
                    color = Color(0xFF334155),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Option 3: Skip the level
                HintItemRow(
                    title = "Skip the level",
                    cost = 30,
                    testTag = "hint_item_skip",
                    onClick = onSkipLevel
                )
            }
        }
    }
}

@Composable
private fun HintItemRow(
    title: String,
    cost: Int,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$cost",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "🪙",
                fontSize = 14.sp
            )
        }
    }
}
