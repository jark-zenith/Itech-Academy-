package academy.itech.app.data.repository

import academy.itech.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class AcademyRepository {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private fun currentTime(): String = dateFormat.format(Date())

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(initialUsers())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(initialCourses())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _enrollments = MutableStateFlow<List<Enrollment>>(initialEnrollments())
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    private val _aiAgents = MutableStateFlow<List<AiAgent>>(initialAiAgents())
    val aiAgents: StateFlow<List<AiAgent>> = _aiAgents.asStateFlow()

    private val _projects = MutableStateFlow<List<ProjectLabItem>>(initialProjects())
    val projects: StateFlow<List<ProjectLabItem>> = _projects.asStateFlow()

    private val _submissions = MutableStateFlow<List<AssignmentSubmission>>(initialSubmissions())
    val submissions: StateFlow<List<AssignmentSubmission>> = _submissions.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditEvent>>(initialAuditLogs())
    val auditLogs: StateFlow<List<AuditEvent>> = _auditLogs.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(initialChatMessages())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        // Default to student login for human-friendly interactive exploration
        _currentUser.value = _users.value.find { it.role == UserRole.STUDENT }
    }

    fun loginAs(role: UserRole) {
        val user = _users.value.find { it.role == role } ?: _users.value.first()
        _currentUser.value = user
        logAction("AUTH_LOGIN", user.email, "Switched role session to ${role.name}")
    }

    fun login(email: String, role: UserRole): Boolean {
        val user = _users.value.find { it.email.equals(email.trim(), ignoreCase = true) && it.role == role }
        if (user != null && user.status == "active") {
            _currentUser.value = user
            logAction("LOGIN", user.email, "Signed in as ${role.name}")
            return true
        }
        return false
    }

    fun logout() {
        val actor = _currentUser.value?.email ?: "system"
        logAction("LOGOUT", actor, "User session closed")
        _currentUser.value = null
    }

    fun toggleUserStatus(userId: String) {
        _users.value = _users.value.map { u ->
            if (u.id == userId && u.role != UserRole.ADMIN) {
                val newStatus = if (u.status == "active") "disabled" else "active"
                logAction("ACCOUNT_STATUS", _currentUser.value?.email ?: "admin", "${u.email} -> $newStatus")
                u.copy(status = newStatus)
            } else u
        }
    }

    fun createUser(name: String, email: String, role: UserRole) {
        val newUser = User(
            id = "user-${System.currentTimeMillis()}",
            name = name,
            email = email,
            role = role,
            status = "active"
        )
        _users.value = _users.value + newUser
        logAction("ACCOUNT_CREATE", _currentUser.value?.email ?: "admin", "Created ${role.name} account for $email")
    }

    fun toggleAgentStatus(agentId: String) {
        if (agentId == "jark") return
        _aiAgents.value = _aiAgents.value.map { a ->
            if (a.id == agentId) {
                val nextStatus = if (a.status == "active") "disabled" else "active"
                logAction("AI_AGENT_STATUS", _currentUser.value?.email ?: "admin", "${a.name} -> $nextStatus")
                a.copy(status = nextStatus)
            } else a
        }
    }

    fun createCourse(title: String, description: String, level: String) {
        val user = _currentUser.value
        val newCourse = Course(
            id = "course-${System.currentTimeMillis()}",
            title = title,
            description = description,
            teacherId = user?.id ?: "teacher-1",
            status = "draft",
            level = level,
            icon = "✦",
            modules = listOf(
                Module(
                    id = "mod-1",
                    title = "Getting Started & Core Concepts",
                    lessons = listOf(
                        Lesson(
                            id = "les-1",
                            title = "Introduction & System Architecture",
                            type = "lesson",
                            duration = "15 min",
                            description = "Understand the foundational concepts and build environment."
                        )
                    )
                )
            )
        )
        _courses.value = _courses.value + newCourse
        logAction("COURSE_CREATE", user?.email ?: "teacher", "Created course: $title")
    }

    fun addModule(courseId: String, moduleTitle: String) {
        _courses.value = _courses.value.map { c ->
            if (c.id == courseId) {
                val newMod = Module(
                    id = "mod-${System.currentTimeMillis()}",
                    title = moduleTitle,
                    lessons = emptyList()
                )
                c.copy(modules = c.modules + newMod)
            } else c
        }
        logAction("MODULE_CREATE", _currentUser.value?.email ?: "teacher", "Added module: $moduleTitle")
    }

    fun addLesson(courseId: String, moduleId: String, title: String, type: String, duration: String) {
        _courses.value = _courses.value.map { c ->
            if (c.id == courseId) {
                val updatedModules = c.modules.map { m ->
                    if (m.id == moduleId) {
                        val newLesson = Lesson(
                            id = "les-${System.currentTimeMillis()}",
                            title = title,
                            type = type,
                            duration = duration,
                            description = "Hands-on practical $type focused on tangible outcomes."
                        )
                        m.copy(lessons = m.lessons + newLesson)
                    } else m
                }
                c.copy(modules = updatedModules)
            } else c
        }
        logAction("LESSON_CREATE", _currentUser.value?.email ?: "teacher", "Added $type: $title")
    }

    fun completeLesson(courseId: String, lessonId: String) {
        val student = _currentUser.value ?: return
        var foundCourseTitle = ""
        _courses.value = _courses.value.map { c ->
            if (c.id == courseId) {
                foundCourseTitle = c.title
                val updatedModules = c.modules.map { m ->
                    val updatedLessons = m.lessons.map { l ->
                        if (l.id == lessonId) l.copy(completed = true) else l
                    }
                    m.copy(lessons = updatedLessons)
                }
                c.copy(modules = updatedModules)
            } else c
        }

        _enrollments.value = _enrollments.value.map { e ->
            if (e.studentId == student.id && e.courseId == courseId) {
                val newProgress = (e.progress + 25).coerceAtMost(100)
                e.copy(progress = newProgress)
            } else e
        }
        logAction("LESSON_COMPLETE", student.email, "Completed activity in $foundCourseTitle")
    }

    fun enrollCourse(courseId: String) {
        val student = _currentUser.value ?: return
        if (_enrollments.value.none { it.studentId == student.id && it.courseId == courseId }) {
            val newEnrollment = Enrollment(
                id = "enr-${System.currentTimeMillis()}",
                studentId = student.id,
                courseId = courseId,
                progress = 5
            )
            _enrollments.value = _enrollments.value + newEnrollment
            logAction("COURSE_ENROLL", student.email, "Enrolled in course $courseId")
        }
    }

    fun submitProject(title: String, category: String, description: String) {
        val student = _currentUser.value ?: return
        val newSub = AssignmentSubmission(
            id = "sub-${System.currentTimeMillis()}",
            studentId = student.id,
            studentName = student.name,
            courseTitle = category,
            title = title,
            status = "pending",
            submittedAt = currentTime()
        )
        _submissions.value = listOf(newSub) + _submissions.value
        logAction("PROJECT_SUBMIT", student.email, "Submitted project: $title")
    }

    fun gradeSubmission(submissionId: String, score: Int, feedback: String) {
        val teacher = _currentUser.value
        _submissions.value = _submissions.value.map { s ->
            if (s.id == submissionId) {
                s.copy(status = "graded", score = score, feedback = feedback)
            } else s
        }
        logAction("SUBMISSION_GRADE", teacher?.email ?: "teacher", "Graded submission with score $score%")
    }

    fun recommendAgent(task: String): AiAgent {
        val s = task.lowercase()
        val agentId = when {
            s.contains("code") || s.contains("debug") || s.contains("javascript") || s.contains("python") || s.contains("api") || s.contains("database") || s.contains("sql") -> "claude-code"
            s.contains("research") || s.contains("paper") || s.contains("theory") || s.contains("explain") || s.contains("concept") -> "chatgpt-research"
            s.contains("ai") || s.contains("model") || s.contains("multimodal") || s.contains("vision") || s.contains("experiment") || s.contains("gemini") -> "gemini-innovation"
            s.contains("cyber") || s.contains("security") || s.contains("linux") || s.contains("network") || s.contains("firewall") || s.contains("permission") -> "cyber"
            s.contains("project") || s.contains("build") || s.contains("prototype") || s.contains("challenge") || s.contains("portfolio") -> "lab"
            s.contains("exam") || s.contains("quiz") || s.contains("revision") || s.contains("test") -> "exam"
            else -> "jark"
        }
        val found = _aiAgents.value.find { it.id == agentId && it.status == "active" }
        return found ?: _aiAgents.value.first()
    }

    fun sendChatMessage(text: String, selectedAgentId: String? = null) {
        val user = _currentUser.value
        val userMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            sender = user?.name ?: "Learner",
            text = text,
            isUser = true,
            timestamp = currentTime()
        )
        _chatMessages.value = _chatMessages.value + userMsg

        val agent = if (selectedAgentId != null) {
            _aiAgents.value.find { it.id == selectedAgentId } ?: recommendAgent(text)
        } else {
            recommendAgent(text)
        }

        val responseText = generateTutorGuidance(agent, text)
        val aiMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis() + 1}",
            sender = agent.name,
            text = responseText,
            isUser = false,
            timestamp = currentTime()
        )
        _chatMessages.value = _chatMessages.value + aiMsg
        logAction("AI_TUTOR_CHAT", user?.email ?: "student", "Interacted with ${agent.name}")
    }

    private fun generateTutorGuidance(agent: AiAgent, query: String): String {
        return when (agent.id) {
            "claude-code" -> "Let's inspect the code structure step by step.\n\n1. Check the inputs & types\n2. Verify the state lifecycle\n3. Write a small reproducible test.\n\nWhat error or unexpected behavior are you seeing?"
            "gemini-innovation" -> "Great direction! For intelligent systems and multimodal features, consider combining structured input with proactive system design. What capability are you aiming to prototype?"
            "cyber" -> "Security check initiated. Always follow principle of least privilege, sanitize inputs, enforce strict access policies, and audit logs. How can we secure this layer together?"
            "chatgpt-research" -> "Here is a structured breakdown of the core concept:\n- Underlying principle\n- Practical trade-offs\n- Recommended implementation pattern.\n\nWhich section would you like to explore deeper?"
            "lab" -> "Let's turn this idea into a concrete milestone! Step 1: Wire the skeleton. Step 2: Implement core logic. Step 3: Test and package. What is your first target?"
            "exam" -> "Here is a quick concept verification question to test your mastery. Think through the mechanics before answering!"
            else -> "Hello, builder! I'm J.A.R.K, your Lead AI Tutor.\n\nAt ITech Academy, our framework is:\nExplain ➔ Ask ➔ Guide ➔ Practice ➔ Review ➔ Build.\n\nLet's tackle your current challenge together. What are you building right now?"
        }
    }

    private fun logAction(action: String, actor: String, detail: String) {
        val event = AuditEvent(
            id = "audit-${System.currentTimeMillis()}",
            action = action,
            actor = actor,
            detail = detail,
            time = currentTime()
        )
        _auditLogs.value = listOf(event) + _auditLogs.value.take(49)
    }

    private fun initialUsers(): List<User> = listOf(
        User("admin-1", "Academy Admin", "admin@itech.academy", UserRole.ADMIN),
        User("teacher-1", "Lead Instructor", "teacher@itech.academy", UserRole.TEACHER),
        User("student-1", "Demo Student", "student@itech.academy", UserRole.STUDENT)
    )

    private fun initialCourses(): List<Course> = listOf(
        Course(
            id = "web-1",
            title = "Software Development",
            description = "Web development, programming, Git, APIs and building complete applications.",
            teacherId = "teacher-1",
            status = "published",
            level = "Builder",
            icon = "{ }",
            modules = listOf(
                Module(
                    id = "web-m1",
                    title = "Web Foundations & UI",
                    lessons = listOf(
                        Lesson("web-l1", "How the Web Works & HTTP Protocol", "lesson", "12 min", "Understand client-server architecture, DNS, HTTP methods and modern browsers.", completed = true),
                        Lesson("web-l2", "Build a Responsive Interface", "challenge", "25 min", "Construct a fully adaptive layout with modern styling techniques.", completed = true)
                    )
                ),
                Module(
                    id = "web-m2",
                    title = "Logic & Interactive Systems",
                    lessons = listOf(
                        Lesson("web-l3", "State Management & Reactivity", "lesson", "18 min", "Manage reactive state, async operations and unidirectional data flow."),
                        Lesson("web-l4", "Full-Stack API Integration Challenge", "project", "40 min", "Connect frontend UI to REST/GraphQL services with error boundaries.")
                    )
                )
            )
        ),
        Course(
            id = "ai-1",
            title = "AI & Intelligent Systems",
            description = "AI concepts, assistants, automation, models and responsible system design.",
            teacherId = "teacher-1",
            status = "published",
            level = "Engineer",
            icon = "✦",
            modules = listOf(
                Module(
                    id = "ai-m1",
                    title = "Intelligent Agents & Reasoning",
                    lessons = listOf(
                        Lesson("ai-l1", "What Makes a System Intelligent?", "lesson", "15 min", "Explore LLMs, reasoning loops, tokens and system architectures.", completed = true),
                        Lesson("ai-l2", "Prompting as System Architecture", "challenge", "20 min", "Design deterministic prompts, few-shot templates and tool schemas.")
                    )
                ),
                Module(
                    id = "ai-m2",
                    title = "Autonomous Agents & Tool Use",
                    lessons = listOf(
                        Lesson("ai-l3", "Function Calling & Orchestration", "project", "35 min", "Build an agent with execution tools, memory and state management.")
                    )
                )
            )
        ),
        Course(
            id = "cyber-1",
            title = "Cybersecurity & Defense",
            description = "Security foundations, Linux, networks, defensive thinking and ethical practice.",
            teacherId = "teacher-1",
            status = "published",
            level = "Engineer",
            icon = "⌁",
            modules = listOf(
                Module(
                    id = "cy-m1",
                    title = "Defensive Security Fundamentals",
                    lessons = listOf(
                        Lesson("cy-l1", "Accounts, Permissions & Least Privilege", "lesson", "20 min", "Secure user accounts, permissions, SSH access and Linux file policies.")
                    )
                )
            )
        ),
        Course(
            id = "ict-1",
            title = "ICT Foundations",
            description = "Computer systems, operating systems, files, productivity and digital confidence.",
            teacherId = "teacher-1",
            status = "published",
            level = "Foundation",
            icon = "⌘",
            modules = listOf(
                Module(
                    id = "ict-m1",
                    title = "Computing Hardware & Systems",
                    lessons = listOf(
                        Lesson("ict-l1", "Operating Systems & Filesystems", "lesson", "15 min", "File structures, processes, memory management and terminal commands.")
                    )
                )
            )
        ),
        Course(
            id = "iot-1",
            title = "Robotics & IoT",
            description = "Sensors, automation, connected devices and prototypes that interact with the world.",
            teacherId = "teacher-1",
            status = "published",
            level = "Innovator",
            icon = "◈",
            modules = listOf(
                Module(
                    id = "iot-m1",
                    title = "Microcontrollers & Telemetry",
                    lessons = listOf(
                        Lesson("iot-l1", "Sensor Data & Real-Time Streams", "lesson", "25 min", "Read analog/digital sensor telemetry and dispatch control signals.")
                    )
                )
            )
        )
    )

    private fun initialEnrollments(): List<Enrollment> = listOf(
        Enrollment("enr-1", "student-1", "web-1", 50),
        Enrollment("enr-2", "student-1", "ai-1", 25)
    )

    private fun initialAiAgents(): List<AiAgent> = listOf(
        AiAgent("jark", "J.A.R.K", "ITech Academy", "Orchestrator", "Lead AI Tutor", "Coordinates the Academy AI Faculty, teaches concepts, tracks learning context and routes tasks.", "active", listOf("*")),
        AiAgent("claude-code", "Claude Code Mentor", "Anthropic", "Claude 3.7", "Code Mentor", "Programming, architecture, debugging, code review and software engineering guidance.", "active", listOf("web-1", "ai-1")),
        AiAgent("chatgpt-research", "ChatGPT Research Mentor", "OpenAI", "GPT-5", "Research Mentor", "Concept explanations, structured research, problem solving and source-aware learning support.", "active", listOf("*")),
        AiAgent("gemini-innovation", "Gemini Innovation Mentor", "Google", "Gemini 2.5", "Innovation Mentor", "Multimodal learning, AI experiments, brainstorming and creative technical exploration.", "active", listOf("ai-1", "iot-1")),
        AiAgent("cyber", "Cyber Mentor", "ITech Academy", "Security Engine", "Cybersecurity Mentor", "Defensive cybersecurity, Linux, networking, permissions and safe security labs.", "active", listOf("cyber-1")),
        AiAgent("lab", "Project Lab Coach", "ITech Academy", "Project Coach", "Project Coach", "Turns lessons into practical builds, challenges, milestones and portfolio projects.", "active", listOf("*")),
        AiAgent("exam", "Exam Coach", "ITech Academy", "Study Engine", "Study Coach", "Creates revision plans, quizzes and practice questions from Academy learning material.", "active", listOf("*"))
    )

    private fun initialProjects(): List<ProjectLabItem> = listOf(
        ProjectLabItem(
            id = "proj-1",
            title = "Build a Personal AI Assistant",
            category = "AI",
            level = "CHALLENGE 01",
            description = "Create an assistant that can remember, reason, respond and automate useful tasks.",
            progress = 38,
            tags = listOf("Python", "AI", "Agents"),
            starterSteps = listOf("Design System Prompt", "Connect Context Memory", "Implement Tool Execution", "Deploy Live Service")
        ),
        ProjectLabItem(
            id = "proj-2",
            title = "Launch Your First Web Application",
            category = "WEB",
            level = "BUILD 02",
            description = "Plan, code, publish and improve a responsive web experience with live APIs.",
            progress = 10,
            tags = listOf("Kotlin/Compose", "REST", "Responsive"),
            starterSteps = listOf("Wireframe Views", "Build Component Tree", "Connect ViewModel Flow", "Publish APK")
        ),
        ProjectLabItem(
            id = "proj-3",
            title = "Secure a Linux System & Network",
            category = "SEC",
            level = "LAB 03",
            description = "Learn defensive checks, permissions, firewall rules and basic account hardening.",
            progress = 0,
            tags = listOf("Linux", "Security", "SSH"),
            starterSteps = listOf("Audit User Permissions", "Configure UFW Firewall", "Disable Root Login", "Inspect System Logs")
        )
    )

    private fun initialSubmissions(): List<AssignmentSubmission> = listOf(
        AssignmentSubmission("sub-1", "student-1", "Demo Student", "Software Development", "Interactive Dashboard Prototype", "graded", 94, "Excellent state management and clean UI separation.", "10:14:02")
    )

    private fun initialAuditLogs(): List<AuditEvent> = listOf(
        AuditEvent("aud-1", "SYSTEM_BOOT", "system", "ITech Academy OS initialized on Android", "08:00:00"),
        AuditEvent("aud-2", "AI_FACULTY_LOADED", "system", "7 AI specialist mentors registered", "08:00:01"),
        AuditEvent("aud-3", "POLICY_VERIFIED", "admin", "Role-based access rules and secure routing enforced", "08:00:02")
    )

    private fun initialChatMessages(): List<ChatMessage> = listOf(
        ChatMessage("init-1", "J.A.R.K", "Hello, builder! I am J.A.R.K, Lead AI Tutor at ITech Academy.\n\nDon't just watch technology. Build it.\nWhat would you like to create or explore today?", false, "08:00:00")
    )
}
