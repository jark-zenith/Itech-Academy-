package academy.itech.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import academy.itech.app.data.model.AiAgent
import academy.itech.app.ui.theme.*
import academy.itech.app.viewmodel.AcademyViewModel
import kotlinx.coroutines.launch

@Composable
fun AiFacultyScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val aiAgents by viewModel.aiAgents.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val activeMentor by viewModel.activeMentor.collectAsState()
    val routedAgent by viewModel.routedAgent.collectAsState()

    var taskInput by remember { mutableStateOf("") }
    var chatInput by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0: Faculty & Router, 1: Live Tutor Chat

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(CyanAccent))
                Text(
                    text = "ACADEMY AI FACULTY",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
            Text(
                text = "Specialist AI Faculty & Tutor",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Sub-tabs: "Faculty Mentors" vs "J.A.R.K Live Chat"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceCard)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabButton(
                title = "Faculty & Router",
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                title = "Tutor Chat (${chatMessages.size})",
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        if (activeTab == 0) {
            // Faculty list & smart task routing
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Smart Router Card
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, PrimaryBlue.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                        color = SurfaceCard
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "SMART TASK ROUTING",
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "What are you working on?",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = taskInput,
                                onValueChange = { taskInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g. Debug my JavaScript API / Explain defensive Linux permissions", color = TextSubtle, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = BorderDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    viewModel.routeTask(taskInput)
                                },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = BgDark)
                            ) {
                                Text("Find My AI Mentor →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            if (routedAgent != null) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceDark)
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "RECOMMENDED: ${routedAgent!!.name}",
                                            color = GoldAccent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = routedAgent!!.description,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.selectMentor(routedAgent!!)
                                                activeTab = 1
                                                if (taskInput.isNotBlank()) {
                                                    viewModel.sendChatMessage(taskInput)
                                                    taskInput = ""
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(36.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = BgDark)
                                        ) {
                                            Text("Open Mentor Chat →", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Academy AI Faculty Directory",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(aiAgents) { agent ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.selectMentor(agent)
                                activeTab = 1
                            },
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
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = agent.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceDark)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = agent.provider, color = CyanAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = agent.role,
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = agent.description,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Interactive Chat View
            Column(modifier = Modifier.fillMaxSize()) {
                val mentor = activeMentor ?: aiAgents.first()

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard)
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Text(
                            text = "Active Mentor: ${mentor.name} (${mentor.role})",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatMessages) { msg ->
                        ChatBubble(msg)
                    }
                }

                // Chat Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask your AI tutor...", color = TextSubtle, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                viewModel.sendChatMessage(chatInput)
                                chatInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = BgDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: academy.itech.app.data.model.ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = "${msg.sender} · ${msg.timestamp}",
            color = TextSubtle,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
        Surface(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (msg.isUser) 14.dp else 2.dp,
                        bottomEnd = if (msg.isUser) 2.dp else 14.dp
                    )
                )
                .border(
                    1.dp,
                    if (msg.isUser) PrimaryBlue.copy(alpha = 0.5f) else BorderDark,
                    RoundedCornerShape(14.dp)
                ),
            color = if (msg.isUser) SurfaceElevated else SurfaceCard
        ) {
            Text(
                text = msg.text,
                color = if (msg.isUser) CyanAccent else TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (selected) PrimaryBlue else Color.Transparent
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (selected) BgDark else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
