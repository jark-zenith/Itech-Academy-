package academy.itech.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    ADMIN, TEACHER, STUDENT
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val status: String = "active"
)

@Serializable
data class Lesson(
    val id: String,
    val title: String,
    val type: String, // "lesson", "challenge", "project"
    val duration: String,
    val description: String = "",
    val codeSnippet: String = "",
    val challengePrompt: String = "",
    val completed: Boolean = false
)

@Serializable
data class Module(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
)

@Serializable
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val teacherId: String,
    val status: String, // "published", "draft"
    val level: String, // "Foundation", "Builder", "Engineer", "Innovator"
    val icon: String = "⌘",
    val modules: List<Module>
)

@Serializable
data class Enrollment(
    val id: String,
    val studentId: String,
    val courseId: String,
    val progress: Int,
    val status: String = "active"
)

@Serializable
data class AiAgent(
    val id: String,
    val name: String,
    val provider: String,
    val model: String,
    val role: String,
    val description: String,
    val status: String = "active",
    val courses: List<String> = listOf("*")
)

@Serializable
data class ProjectLabItem(
    val id: String,
    val title: String,
    val category: String, // "AI", "WEB", "SEC", "ROBOTICS"
    val level: String,
    val description: String,
    val progress: Int = 0,
    val tags: List<String> = emptyList(),
    val starterSteps: List<String> = emptyList()
)

@Serializable
data class AssignmentSubmission(
    val id: String,
    val studentId: String,
    val studentName: String,
    val courseTitle: String,
    val title: String,
    val status: String, // "pending", "reviewed", "graded"
    val score: Int = 0,
    val feedback: String = "",
    val submittedAt: String
)

@Serializable
data class AuditEvent(
    val id: String,
    val action: String,
    val actor: String,
    val detail: String,
    val time: String
)

@Serializable
data class ChatMessage(
    val id: String,
    val sender: String, // "user" or agent name
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)
