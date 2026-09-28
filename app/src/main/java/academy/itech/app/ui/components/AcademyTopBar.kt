package academy.itech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.data.model.User
import academy.itech.app.data.model.UserRole
import academy.itech.app.ui.theme.*

@Composable
fun AcademyTopBar(
    currentUser: User?,
    onSwitchRole: (UserRole) -> Unit,
    onOpenOs: () -> Unit
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgDark.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Mark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceCard)
                            .border(1.5.dp, PrimaryBlue, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "I",
                            color = CyanAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ITech ",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Academy",
                                color = PrimaryBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "BUILD TECHNOLOGY",
                            color = TextSubtle,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                // Right side: Role selector & OS shortcut
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                .clickable { roleMenuExpanded = true },
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(if (currentUser?.role == UserRole.ADMIN) GoldAccent else CyanAccent)
                                )
                                Text(
                                    text = currentUser?.role?.name ?: "ROLE",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Role",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Student Mode (Demo Student)", color = CyanAccent, fontSize = 13.sp) },
                                onClick = {
                                    onSwitchRole(UserRole.STUDENT)
                                    roleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Teacher Mode (Lead Instructor)", color = PrimaryBlue, fontSize = 13.sp) },
                                onClick = {
                                    onSwitchRole(UserRole.TEACHER)
                                    roleMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Admin Mode (Academy Admin)", color = GoldAccent, fontSize = 13.sp) },
                                onClick = {
                                    onSwitchRole(UserRole.ADMIN)
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenOs,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyanAccent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(BorderLight)
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "OS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Divider(color = BorderDark.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}
