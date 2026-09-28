package academy.itech.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import academy.itech.app.data.model.UserRole
import academy.itech.app.ui.theme.*
import academy.itech.app.viewmodel.AcademyViewModel
import academy.itech.app.viewmodel.OsSection

@Composable
fun AcademyOsScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val users by viewModel.users.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val aiAgents by viewModel.aiAgents.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()
    val currentOsSection by viewModel.currentOsSection.collectAsState()

    var showCreateUserDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var newUserEmail by remember { mutableStateOf("") }
    var newUserRole by remember { mutableStateOf(UserRole.STUDENT) }

    var showCreateCourseDialog by remember { mutableStateOf(false) }
    var newCourseTitle by remember { mutableStateOf("") }
    var newCourseDesc by remember { mutableStateOf("") }
    var newCourseLevel by remember { mutableStateOf("Builder") }

    var gradingSubmissionId by remember { mutableStateOf<String?>(null) }
    var gradingScore by remember { mutableStateOf("90") }
    var gradingFeedback by remember { mutableStateOf("Great implementation of core logic.") }

    val role = currentUser?.role ?: UserRole.STUDENT

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp)
    ) {
        // OS Header
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACADEMY OS · ${role.name} WORKSPACE",
                        color = if (role == UserRole.ADMIN) GoldAccent else CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = when (role) {
                            UserRole.ADMIN -> "Control Center"
                            UserRole.TEACHER -> "Instructor Studio"
                            UserRole.STUDENT -> "Learning Portal"
                        },
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Quick role toggle pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentUser?.name ?: "User",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Section Tabs
        val availableSections = when (role) {
            UserRole.ADMIN -> listOf(
                OsSection.OVERVIEW to "Overview",
                OsSection.USERS to "Users",
                OsSection.AI_CONTROL to "AI Faculty",
                OsSection.SECURITY to "Security",
                OsSection.AUDIT to "Audit Log"
            )
            UserRole.TEACHER -> listOf(
                OsSection.OVERVIEW to "Overview",
                OsSection.COURSES to "My Courses",
                OsSection.STUDENTS to "Students",
                OsSection.ASSIGNMENTS to "Grading"
            )
            UserRole.STUDENT -> listOf(
                OsSection.OVERVIEW to "Overview",
                OsSection.COURSES to "My Learning",
                OsSection.SECURITY to "Security"
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableSections) { (sec, label) ->
                val isSelected = currentOsSection == sec
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            if (isSelected) PrimaryBlue else BorderDark,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectOsSection(sec) },
                    color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else SurfaceCard
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) CyanAccent else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Content Area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            when (currentOsSection) {
                OsSection.OVERVIEW -> {
                    item {
                        // Metric Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                value = users.size.toString(),
                                label = "Accounts",
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                value = courses.size.toString(),
                                label = "Courses",
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                value = aiAgents.size.toString(),
                                label = "AI Faculty",
                                modifier = Modifier.weight(1f)
                            )
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
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Academy System Health",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                HealthItem("Account Directory", "READY", true)
                                HealthItem("Course Catalog & Builder", "READY", true)
                                HealthItem("J.A.R.K Faculty Engine", "READY", true)
                                HealthItem("Project Lab Portfolio", "READY", true)
                                HealthItem("Security & Audit Logger", "ACTIVE", true)
                            }
                        }
                    }
                }

                OsSection.USERS -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Directory & Roles",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showCreateUserDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = BgDark),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+ Create Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(users) { u ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = u.name.firstOrNull()?.toString() ?: "U",
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = u.name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${u.email} · ${u.role.name}",
                                        color = TextSubtle,
                                        fontSize = 11.sp
                                    )
                                }
                                if (u.role != UserRole.ADMIN) {
                                    TextButton(onClick = { viewModel.toggleUserStatus(u.id) }) {
                                        Text(
                                            text = if (u.status == "active") "Disable" else "Enable",
                                            color = if (u.status == "active") WarningOrange else SuccessGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                OsSection.COURSES -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Course Management",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (role == UserRole.ADMIN || role == UserRole.TEACHER) {
                                Button(
                                    onClick = { showCreateCourseDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = BgDark),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("+ New Course", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    items(courses) { course ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                            color = SurfaceCard
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = course.title,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = course.status.uppercase(),
                                        color = CyanAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = course.description,
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${course.modules.size} Modules · ${course.modules.sumOf { it.lessons.size }} Lessons · Level: ${course.level}",
                                    color = TextSubtle,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                OsSection.STUDENTS -> {
                    item {
                        Text(
                            text = "Enrolled Student Roster",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(users.filter { it.role == UserRole.STUDENT }) { student ->
                        val studentEnrollments = enrollments.filter { it.studentId == student.id }
                        val avgProgress = if (studentEnrollments.isNotEmpty()) {
                            studentEnrollments.sumOf { it.progress } / studentEnrollments.size
                        } else 0

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = student.email,
                                        color = TextSubtle,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = "${studentEnrollments.size} Courses · $avgProgress% Avg",
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                OsSection.ASSIGNMENTS -> {
                    item {
                        Text(
                            text = "Learner Submissions & Grading",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(submissions) { sub ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                            color = SurfaceCard
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = sub.title,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = sub.status.uppercase(),
                                        color = if (sub.status == "graded") SuccessGreen else WarningOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Learner: ${sub.studentName} · Course: ${sub.courseTitle}",
                                    color = TextSubtle,
                                    fontSize = 11.sp
                                )
                                if (sub.status == "graded") {
                                    Text(
                                        text = "Grade: ${sub.score}% · Feedback: ${sub.feedback}",
                                        color = CyanAccent,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Button(
                                        onClick = { gradingSubmissionId = sub.id },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = BgDark),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Grade Submission →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                OsSection.AI_CONTROL -> {
                    item {
                        Text(
                            text = "AI Faculty Access Policy",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(aiAgents) { agent ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
                            color = SurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = agent.name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${agent.role} · Provider: ${agent.provider}",
                                        color = TextSubtle,
                                        fontSize = 10.sp
                                    )
                                }
                                if (agent.id != "jark") {
                                    TextButton(onClick = { viewModel.toggleAgentStatus(agent.id) }) {
                                        Text(
                                            text = if (agent.status == "active") "Active" else "Disabled",
                                            color = if (agent.status == "active") SuccessGreen else TextSubtle,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Core Engine",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                OsSection.SECURITY -> {
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
                                    text = "Platform Security Architecture",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                SecurityCard("Role-Aware UI & Boundaries", "Admin, Teacher, and Student spaces are strictly isolated with independent capabilities.", true)
                                SecurityCard("Server-Side AI Secret Protection", "Provider credentials remain server-side; client tokens never leak model API keys.", true)
                                SecurityCard("Immutable Audit Trail", "All key platform events, course completions, and grade reviews are recorded.", true)
                            }
                        }
                    }
                }

                OsSection.AUDIT -> {
                    item {
                        Text(
                            text = "Real-Time Platform Audit Events",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(auditLogs) { log ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = log.action,
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = log.time,
                                        color = TextSubtle,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "${log.actor}: ${log.detail}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create User Dialog
    if (showCreateUserDialog) {
        AlertDialog(
            onDismissRequest = { showCreateUserDialog = false },
            title = { Text("Create Academy Account", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newUserName,
                        onValueChange = { newUserName = it },
                        label = { Text("Full Name", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUserEmail,
                        onValueChange = { newUserEmail = it },
                        label = { Text("Email", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { newUserRole = UserRole.STUDENT },
                            colors = ButtonDefaults.buttonColors(containerColor = if (newUserRole == UserRole.STUDENT) CyanAccent else SurfaceDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Student", color = if (newUserRole == UserRole.STUDENT) BgDark else TextPrimary, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { newUserRole = UserRole.TEACHER },
                            colors = ButtonDefaults.buttonColors(containerColor = if (newUserRole == UserRole.TEACHER) PrimaryBlue else SurfaceDark),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Teacher", color = if (newUserRole == UserRole.TEACHER) BgDark else TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUserName.isNotBlank() && newUserEmail.isNotBlank()) {
                            viewModel.createAccount(newUserName, newUserEmail, newUserRole)
                            showCreateUserDialog = false
                            newUserName = ""
                            newUserEmail = ""
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateUserDialog = false }) { Text("Cancel", color = TextMuted) }
            },
            containerColor = SurfaceDark
        )
    }

    // Create Course Dialog
    if (showCreateCourseDialog) {
        AlertDialog(
            onDismissRequest = { showCreateCourseDialog = false },
            title = { Text("Create Build Path Course", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCourseTitle,
                        onValueChange = { newCourseTitle = it },
                        label = { Text("Course Title", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCourseDesc,
                        onValueChange = { newCourseDesc = it },
                        label = { Text("Description", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCourseTitle.isNotBlank()) {
                            viewModel.createCourse(newCourseTitle, newCourseDesc, newCourseLevel)
                            showCreateCourseDialog = false
                            newCourseTitle = ""
                            newCourseDesc = ""
                        }
                    }
                ) {
                    Text("Create Course")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCourseDialog = false }) { Text("Cancel", color = TextMuted) }
            },
            containerColor = SurfaceDark
        )
    }

    // Grading Dialog
    if (gradingSubmissionId != null) {
        AlertDialog(
            onDismissRequest = { gradingSubmissionId = null },
            title = { Text("Review & Grade Submission", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gradingScore,
                        onValueChange = { gradingScore = it },
                        label = { Text("Score (0-100)", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gradingFeedback,
                        onValueChange = { gradingFeedback = it },
                        label = { Text("Feedback for Learner", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = gradingScore.toIntOrNull() ?: 90
                        viewModel.gradeSubmission(gradingSubmissionId!!, score, gradingFeedback)
                        gradingSubmissionId = null
                    }
                ) {
                    Text("Submit Grade")
                }
            },
            dismissButton = {
                TextButton(onClick = { gradingSubmissionId = null }) { Text("Cancel", color = TextMuted) }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun MetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = CyanAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HealthItem(name: String, status: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, color = TextMuted, fontSize = 13.sp)
        Text(
            text = status,
            color = if (ok) SuccessGreen else WarningOrange,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SecurityCard(title: String, desc: String, ok: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (ok) "✓" else "!",
            color = if (ok) SuccessGreen else WarningOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
