package academy.itech.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.data.model.Course
import academy.itech.app.data.model.Lesson
import academy.itech.app.data.model.Module
import academy.itech.app.ui.theme.*
import academy.itech.app.viewmodel.AcademyViewModel

@Composable
fun LearnScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.courses.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()
    val selectedCourse by viewModel.selectedCourse.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        when {
            selectedLesson != null && selectedCourse != null -> {
                LessonWorkspace(
                    course = selectedCourse!!,
                    lesson = selectedLesson!!,
                    onBack = { viewModel.closeLesson() },
                    onComplete = {
                        viewModel.completeLesson(selectedCourse!!.id, selectedLesson!!.id)
                        viewModel.closeLesson()
                    }
                )
            }
            selectedCourse != null -> {
                CourseDetailView(
                    course = selectedCourse!!,
                    enrollmentProgress = enrollments.find { it.courseId == selectedCourse!!.id }?.progress ?: 0,
                    onBack = { viewModel.closeCourse() },
                    onOpenLesson = { lesson ->
                        viewModel.openLesson(selectedCourse!!, lesson)
                    }
                )
            }
            else -> {
                CourseCatalogView(
                    courses = courses,
                    enrollments = enrollments,
                    onSelectCourse = { course ->
                        viewModel.openCourse(course)
                    }
                )
            }
        }
    }
}

@Composable
private fun CourseCatalogView(
    courses: List<Course>,
    enrollments: List<academy.itech.app.data.model.Enrollment>,
    onSelectCourse: (Course) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ACADEMY CURRICULUM",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Build Paths & Courses",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Practical project-first courses designed to take you from foundational concepts to production engineering.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        items(courses) { course ->
            val progress = enrollments.find { it.courseId == course.id }?.progress
            val totalLessons = course.modules.sumOf { it.lessons.size }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                    .clickable { onSelectCourse(course) },
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceDark)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = course.level.uppercase(),
                                color = GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (progress != null) {
                            Text(
                                text = "$progress% Completed",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Available",
                                color = TextSubtle,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceElevated)
                                .border(1.dp, BorderLight, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = course.icon, color = CyanAccent, fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                text = course.title,
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${course.modules.size} Modules · $totalLessons Practical Lessons",
                                color = TextSubtle,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = course.description,
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    if (progress != null) {
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = PrimaryBlue,
                            trackColor = SurfaceDark
                        )
                    }

                    Button(
                        onClick = { onSelectCourse(course) },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (progress != null) PrimaryBlue else SurfaceElevated,
                            contentColor = if (progress != null) BgDark else TextPrimary
                        )
                    ) {
                        Text(
                            text = if (progress != null) "Continue Path →" else "Start Course →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseDetailView(
    course: Course,
    enrollmentProgress: Int,
    onBack: () -> Unit,
    onOpenLesson: (Lesson) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text(
                        text = course.level.uppercase(),
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Path Overview",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = course.description,
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                    LinearProgressIndicator(
                        progress = { enrollmentProgress / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = PrimaryBlue,
                        trackColor = SurfaceDark
                    )
                    Text(
                        text = "$enrollmentProgress% completed",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        items(course.modules) { module ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                color = SurfaceDark
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = module.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    module.lessons.forEach { lesson ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                                .clickable { onOpenLesson(lesson) },
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (lesson.completed) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (lesson.completed) SuccessGreen else CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.title,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${lesson.type.replaceFirstChar { it.uppercase() }} · ${lesson.duration}",
                                        color = TextSubtle,
                                        fontSize = 10.sp
                                    )
                                }
                                Text("Open →", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonWorkspace(
    course: Course,
    lesson: Lesson,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text(
                        text = "${course.title} · ${lesson.type.uppercase()}",
                        color = CyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lesson.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                color = SurfaceCard
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Core Concept & Objectives",
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lesson.description.isNotBlank()) lesson.description else "In this practical build unit, you will master real architectural principles and implement them through code.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "2. Practical Lab / Challenge",
                        color = CyanAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Implement the module logic, test boundary cases, and ensure proper error handling before completing the verification test.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    // Code snippet box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF03070D))
                            .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = """// Practical implementation snippet
fun executeAcademyBuild(context: BuildContext) {
    println("Status: Verified.")
    return context.deploy()
}""",
                            color = CyanAccent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = BgDark
                        )
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Complete Lesson & Save Progress", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
