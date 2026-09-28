package academy.itech.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import academy.itech.app.data.model.*
import academy.itech.app.data.repository.AcademyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AcademyTab {
    HOME,
    LEARN,
    AI_FACULTY,
    PROJECTS,
    ACADEMY_OS
}

enum class OsSection {
    OVERVIEW,
    COURSES,
    USERS,
    STUDENTS,
    ASSIGNMENTS,
    AI_CONTROL,
    SECURITY,
    AUDIT
}

class AcademyViewModel(
    private val repository: AcademyRepository = AcademyRepository()
) : ViewModel() {

    val currentUser: StateFlow<User?> = repository.currentUser
    val users: StateFlow<List<User>> = repository.users
    val courses: StateFlow<List<Course>> = repository.courses
    val enrollments: StateFlow<List<Enrollment>> = repository.enrollments
    val aiAgents: StateFlow<List<AiAgent>> = repository.aiAgents
    val projects: StateFlow<List<ProjectLabItem>> = repository.projects
    val submissions: StateFlow<List<AssignmentSubmission>> = repository.submissions
    val auditLogs: StateFlow<List<AuditEvent>> = repository.auditLogs
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages

    private val _currentTab = MutableStateFlow(AcademyTab.HOME)
    val currentTab: StateFlow<AcademyTab> = _currentTab.asStateFlow()

    private val _currentOsSection = MutableStateFlow(OsSection.OVERVIEW)
    val currentOsSection: StateFlow<OsSection> = _currentOsSection.asStateFlow()

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    private val _selectedLesson = MutableStateFlow<Lesson?>(null)
    val selectedLesson: StateFlow<Lesson?> = _selectedLesson.asStateFlow()

    private val _activeMentor = MutableStateFlow<AiAgent?>(null)
    val activeMentor: StateFlow<AiAgent?> = _activeMentor.asStateFlow()

    private val _routedAgent = MutableStateFlow<AiAgent?>(null)
    val routedAgent: StateFlow<AiAgent?> = _routedAgent.asStateFlow()

    private val _quickInfoDialog = MutableStateFlow<String?>(null)
    val quickInfoDialog: StateFlow<String?> = _quickInfoDialog.asStateFlow()

    fun selectTab(tab: AcademyTab) {
        _currentTab.value = tab
    }

    fun selectOsSection(section: OsSection) {
        _currentOsSection.value = section
    }

    fun switchRole(role: UserRole) {
        repository.loginAs(role)
        _currentOsSection.value = OsSection.OVERVIEW
    }

    fun showQuickInfo(text: String) {
        _quickInfoDialog.value = text
    }

    fun dismissQuickInfo() {
        _quickInfoDialog.value = null
    }

    fun openCourse(course: Course) {
        _selectedCourse.value = course
        _selectedLesson.value = null
        repository.enrollCourse(course.id)
        _currentTab.value = AcademyTab.LEARN
    }

    fun openLesson(course: Course, lesson: Lesson) {
        _selectedCourse.value = course
        _selectedLesson.value = lesson
        _currentTab.value = AcademyTab.LEARN
    }

    fun closeLesson() {
        _selectedLesson.value = null
    }

    fun closeCourse() {
        _selectedCourse.value = null
        _selectedLesson.value = null
    }

    fun completeLesson(courseId: String, lessonId: String) {
        repository.completeLesson(courseId, lessonId)
        _selectedCourse.value = courses.value.find { it.id == courseId }
    }

    fun selectMentor(agent: AiAgent) {
        _activeMentor.value = agent
        _currentTab.value = AcademyTab.AI_FACULTY
    }

    fun routeTask(task: String) {
        if (task.isBlank()) return
        val agent = repository.recommendAgent(task)
        _routedAgent.value = agent
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val mentorId = _activeMentor.value?.id ?: _routedAgent.value?.id
        repository.sendChatMessage(text, mentorId)
    }

    fun submitNewProject(title: String, category: String, description: String) {
        repository.submitProject(title, category, description)
    }

    fun gradeSubmission(submissionId: String, score: Int, feedback: String) {
        repository.gradeSubmission(submissionId, score, feedback)
    }

    fun toggleAgentStatus(agentId: String) {
        repository.toggleAgentStatus(agentId)
    }

    fun toggleUserStatus(userId: String) {
        repository.toggleUserStatus(userId)
    }

    fun createAccount(name: String, email: String, role: UserRole) {
        repository.createUser(name, email, role)
    }

    fun createCourse(title: String, description: String, level: String) {
        repository.createCourse(title, description, level)
    }

    fun addModule(courseId: String, title: String) {
        repository.addModule(courseId, title)
        _selectedCourse.value = courses.value.find { it.id == courseId }
    }

    fun addLesson(courseId: String, moduleId: String, title: String, type: String, duration: String) {
        repository.addLesson(courseId, moduleId, title, type, duration)
        _selectedCourse.value = courses.value.find { it.id == courseId }
    }
}
