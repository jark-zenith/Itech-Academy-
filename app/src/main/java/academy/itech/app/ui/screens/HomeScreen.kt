package academy.itech.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.data.model.Course
import academy.itech.app.ui.components.OrbGraphic
import academy.itech.app.ui.theme.*
import academy.itech.app.viewmodel.AcademyTab
import academy.itech.app.viewmodel.AcademyViewModel

@Composable
fun HomeScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.courses.collectAsState()
    val projects by viewModel.projects.collectAsState()

    var terminalCommand by remember { mutableStateOf("academy status") }
    var terminalOutput by remember {
        mutableStateOf(
            """SYSTEM       ONLINE
COURSES      LOADED
PROJECT LAB  READY
BUILDERS     CONNECTED

$ start --learning
> Your next build starts here."""
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Hero Section
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanAccent)
                    )
                    Text(
                        text = "FUTURE ITECH ACADEMY",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = "Don't Just Watch\nTechnology.\nBuild It.",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Learn by building real things. From your first line of code to AI, robotics, cybersecurity and the systems behind tomorrow.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.selectTab(AcademyTab.LEARN) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = BgDark
                        )
                    ) {
                        Text("Start Learning", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }

                    OutlinedButton(
                        onClick = { viewModel.selectTab(AcademyTab.PROJECTS) },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(BorderLight)
                        )
                    ) {
                        Text("Explore Projects", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // 4-Step Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StepItem(number = "01", label = "Learn")
                    StepItem(number = "02", label = "Experiment")
                    StepItem(number = "03", label = "Build")
                    StepItem(number = "04", label = "Share")
                }
            }
        }

        // Animated Central Orb
        item {
            OrbGraphic()
        }

        // Build Paths Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "THE ACADEMY",
                            color = CyanAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Choose your build path",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = { viewModel.selectTab(AcademyTab.LEARN) }) {
                        Text("View all", color = CyanAccent, fontSize = 12.sp)
                    }
                }

                courses.take(4).forEach { course ->
                    CourseCard(
                        course = course,
                        onClick = { viewModel.openCourse(course) }
                    )
                }
            }
        }

        // Tracks Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SurfaceDark, SurfaceCard)
                        )
                    )
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LEARNING SYSTEM",
                    color = GoldAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "From beginner to builder",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                TrackRow("01", "Foundation", "Understand the core tools and computing models.")
                TrackRow("02", "Builder", "Make real interactive software and services.")
                TrackRow("03", "Engineer", "Design complete resilient systems & APIs.")
                TrackRow("04", "Innovator", "Create next-generation AI and connected tech.")
            }
        }

        // Featured Project Lab
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROJECT LAB",
                            color = CyanAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Knowledge becomes something",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { viewModel.selectTab(AcademyTab.PROJECTS) },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ New Project", color = CyanAccent, fontSize = 11.sp)
                    }
                }

                projects.firstOrNull()?.let { featured ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectTab(AcademyTab.PROJECTS) },
                        color = SurfaceCard
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = featured.category,
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = featured.level,
                                    color = TextSubtle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = featured.title,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = featured.description,
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                            LinearProgressIndicator(
                                progress = { featured.progress / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = PrimaryBlue,
                                trackColor = SurfaceDark
                            )
                            Text(
                                text = "${featured.progress}% starter progress",
                                color = TextSubtle,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Academy Terminal
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF03070D))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ErrorRed))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarningOrange))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "itech@academy:~",
                        color = TextSubtle,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("$", color = CyanAccent, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        Text(
                            text = terminalCommand,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = terminalOutput,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                terminalCommand = "academy run --faculty"
                                terminalOutput = "J.A.R.K ORCHESTRATOR: ACTIVE\nCLAUDE MENTOR: READY\nGEMINI MULTIMODAL: CONNECTED\nSTATUS: Awaiting learner instruction."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text("Run Faculty", color = CyanAccent, fontSize = 10.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                terminalCommand = "academy start --project"
                                terminalOutput = "PROJECT LAB: INITIALIZING\nTARGET: Build Personal AI Assistant\nWORKSPACE: Ready in Academy OS."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Text("Start Build", color = PrimaryBlue, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItem(number: String, label: String) {
    Column {
        Text(text = number, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = SurfaceCard
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = course.icon, color = CyanAccent, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.level.uppercase(),
                    color = TextSubtle,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = course.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = course.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            Text(
                text = "Open →",
                color = CyanAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TrackRow(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = step,
            color = GoldAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}
