package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrownGold
import com.example.ui.theme.GoldCoinColor
import com.example.ui.theme.GreenSolved

@Composable
fun TopBar(
    solvedCount: Int,
    totalCount: Int,
    coins: Int,
    onScoreClick: () -> Unit,
    onCoinsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Score & Level Pill
        Row(
            modifier = Modifier
                .testTag("score_pill")
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F0721).copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .clickable { onScoreClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Green checkmark badge
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(GreenSolved),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Solved riddles",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "$solvedCount : $totalCount",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Gold crown icon
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard crown",
                tint = CrownGold,
                modifier = Modifier.size(18.dp)
            )
        }

        // Right Coins Pill
        Row(
            modifier = Modifier
                .testTag("coins_pill")
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F0721).copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .clickable { onCoinsClick() }
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin Icon
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(GoldCoinColor)
                    .border(1.dp, Color(0xFFD97706), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🪙",
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = coins.toString(),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Add (+) button
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEA580C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Coins",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
