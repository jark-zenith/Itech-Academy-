package academy.itech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.ui.components.AcademyTopBar
import academy.itech.app.ui.screens.*
import academy.itech.app.ui.theme.*
import academy.itech.app.viewmodel.AcademyTab
import academy.itech.app.viewmodel.AcademyViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AcademyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ITechAcademyTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AcademyViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val quickInfoDialog by viewModel.quickInfoDialog.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        containerColor = BgDark,
        topBar = {
            AcademyTopBar(
                currentUser = currentUser,
                onSwitchRole = { role -> viewModel.switchRole(role) },
                onOpenOs = { viewModel.selectTab(AcademyTab.ACADEMY_OS) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = SurfaceDark,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    NavigationItem(AcademyTab.HOME, "Home", Icons.Default.Home),
                    NavigationItem(AcademyTab.LEARN, "Learn", Icons.Default.AutoStories),
                    NavigationItem(AcademyTab.AI_FACULTY, "AI Faculty", Icons.Default.AutoAwesome),
                    NavigationItem(AcademyTab.PROJECTS, "Projects", Icons.Default.RocketLaunch),
                    NavigationItem(AcademyTab.ACADEMY_OS, "Academy OS", Icons.Default.Dashboard)
                )

                navItems.forEach { item ->
                    val selected = currentTab == item.tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) CyanAccent else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (selected) TextPrimary else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SurfaceElevated
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "TabCrossfade") { tab ->
                when (tab) {
                    AcademyTab.HOME -> HomeScreen(viewModel = viewModel)
                    AcademyTab.LEARN -> LearnScreen(viewModel = viewModel)
                    AcademyTab.AI_FACULTY -> AiFacultyScreen(viewModel = viewModel)
                    AcademyTab.PROJECTS -> ProjectsScreen(viewModel = viewModel)
                    AcademyTab.ACADEMY_OS -> AcademyOsScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (quickInfoDialog != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissQuickInfo() },
            title = {
                Text(
                    text = "J.A.R.K Academy Tutor",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = quickInfoDialog ?: "",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissQuickInfo()
                        viewModel.selectTab(AcademyTab.AI_FACULTY)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = BgDark)
                ) {
                    Text("Open AI Faculty →", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissQuickInfo() }) {
                    Text("Close", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

private data class NavigationItem(
    val tab: AcademyTab,
    val label: String,
    val icon: ImageVector
)
