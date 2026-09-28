package academy.itech.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.ui.theme.*

@Composable
fun OrbGraphic(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow canvas
        Canvas(modifier = Modifier.size(240.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.25f * pulseAlpha),
                        CyanAccent.copy(alpha = 0.08f * pulseAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.minDimension / 1.5f
                )
            )
            // Orbit Ring 1
            drawOval(
                color = PrimaryBlue.copy(alpha = 0.35f),
                style = Stroke(width = 1.5f),
                size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.45f),
                topLeft = Offset(0f, size.height * 0.275f)
            )
        }

        // Rotating Ring 2
        Canvas(
            modifier = Modifier
                .size(210.dp)
                .rotate(rotation)
        ) {
            drawCircle(
                color = GoldAccent.copy(alpha = 0.25f),
                style = Stroke(width = 1f)
            )
            drawCircle(
                color = CyanAccent,
                radius = 4f,
                center = Offset(size.width / 2, 4f)
            )
            drawCircle(
                color = GoldAccent,
                radius = 3.5f,
                center = Offset(size.width - 10f, size.height * 0.75f)
            )
        }

        // Central Orb
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF163E68),
                            Color(0xFF0C223A),
                            Color(0xFF061120),
                            BgDark
                        )
                    )
                )
                .border(1.5.dp, PrimaryBlue.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ITECH",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "LAB",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp
                )
            }
        }

        // Bottom Caption Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceCard.copy(alpha = 0.9f))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                )
                Text(
                    text = "BUILD SYSTEM",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ONLINE",
                    color = CyanAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
