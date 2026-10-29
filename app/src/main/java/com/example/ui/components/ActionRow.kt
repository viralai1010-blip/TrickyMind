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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
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
import com.example.ui.theme.ActionPillBg
import com.example.ui.theme.ActionPillText
import com.example.ui.theme.ActionPurple
import com.example.ui.theme.ActionYellow
import com.example.ui.theme.GoldCoinColor

@Composable
fun ActionRow(
    onWatchVideo: () -> Unit,
    onShare: () -> Unit,
    onFiftyFifty: () -> Unit,
    onSeeAnswer: () -> Unit,
    onMoreHints: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Watch Video +10 Button
        Row(
            modifier = Modifier
                .testTag("watch_video_button")
                .weight(1.3f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ActionYellow)
                .clickable { onWatchVideo() }
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Watch Video",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B24)
                )
                Text(
                    text = "+10 🪙",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF78350F)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Watch video for coins",
                tint = Color(0xFF1E1B24),
                modifier = Modifier.size(18.dp)
            )
        }

        // 2. Share Button (Purple Square)
        Box(
            modifier = Modifier
                .testTag("share_button")
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ActionPurple)
                .clickable { onShare() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share riddle",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // 3. 50:50 -10 Button
        Row(
            modifier = Modifier
                .testTag("fifty_fifty_button")
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ActionPillBg)
                .clickable { onFiftyFifty() }
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "50:50",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionPillText
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🪙",
                        fontSize = 9.sp
                    )
                    Text(
                        text = "-10",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }
        }

        // 4. See Answer -20 Button
        Row(
            modifier = Modifier
                .testTag("see_answer_button")
                .weight(1.3f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ActionYellow)
                .clickable { onSeeAnswer() }
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "See Answer",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B24)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🪙",
                        fontSize = 9.sp
                    )
                    Text(
                        text = "-20",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "See answer",
                tint = Color(0xFF1E1B24),
                modifier = Modifier.size(18.dp)
            )
        }

        // 5. Hint bulb button (opens the hints popup from screenshot #4)
        Box(
            modifier = Modifier
                .testTag("hint_modal_button")
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2563EB))
                .clickable { onMoreHints() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Hint power-ups",
                tint = GoldCoinColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
