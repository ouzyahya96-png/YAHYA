package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.content.Context
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// --- Reusable Elegant Components ---

@Composable
fun GoldGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .graphicsLayer { clip = true }
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) listOf(GoldClassic, GoldDeep) else listOf(MediumGray, MediumGray)
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun OperationsCard(
    modifier: Modifier = Modifier,
    borderAccent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (borderAccent) 1.5.dp else 1.dp,
            color = if (borderAccent) GoldClassic else LightGrayDivider
        ),
        modifier = modifier.fillMaxWidth(),
        content = content
    )
}

@Composable
fun PageHeader(
    title: String,
    subtitle: String,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MediumGray
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.width(16.dp))
            action()
        }
    }
}


// --- SCREEN 1: DASHBOARD ---

@Composable
fun DashboardPage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val dailyAffirmation by viewModel.dailyAffirmation.collectAsState()

    val geminiAnalysis by viewModel.geminiAnalysis.collectAsState()
    val isLoadingAnalysis by viewModel.isLoadingAnalysis.collectAsState()
    val analysisError by viewModel.analysisError.collectAsState()
    val hasApiKey = viewModel.geminiApiKey.collectAsState().value.isNotEmpty()

    val todayStr = viewModel.getTodayDate()

    val remainingTasks = tasks.filter { it.date == todayStr && !it.done }
    val nextTask = remainingTasks.firstOrNull()

    val todayGymSession = gymSessions.firstOrNull { it.date == todayStr }

    val todaySuppLog = supplementLogs.firstOrNull { it.date == todayStr } ?: SupplementLog(date = todayStr)

    val lastNightSleep = sleepLogs.firstOrNull() // Chronological desc, so first is most recent

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            val formattedDate = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).format(Date())
            PageHeader(
                title = "Aujourd'hui",
                subtitle = formattedDate
            )
        }

        // --- ENCART AFFIRMATION DU JOUR ---
        item {
            OperationsCard(borderAccent = true) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Affirmation",
                            tint = GoldClassic,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AFFIRMATION DU JOUR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "« $dailyAffirmation »",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Anthracite,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // --- SECTION IA GEMINI ---
        item {
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = GoldClassic,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ANALYSE GEMINI DU JOUR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic,
                                letterSpacing = 1.sp
                            )
                        }

                        if (hasApiKey && !isLoadingAnalysis) {
                            IconButton(
                                onClick = { viewModel.generateGeminiAnalysis() },
                                modifier = Modifier.size(24.dp).testTag("refresh_analysis")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = MediumGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!hasApiKey) {
                        Text(
                            text = "Configurez votre clé API Gemini dans les Paramètres pour activer l'analyse quotidienne de vos indicateurs de performance.",
                            fontSize = 13.sp,
                            color = MediumGray
                        )
                    } else if (isLoadingAnalysis) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GoldClassic, strokeWidth = 2.5.dp)
                        }
                    } else if (!analysisError.isNullOrEmpty()) {
                        Text(
                            text = analysisError ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFC62828)
                        )
                    } else if (geminiAnalysis.isNotEmpty()) {
                        Text(
                            text = geminiAnalysis,
                            fontSize = 14.sp,
                            color = Anthracite,
                            lineHeight = 20.sp
                        )
                    } else {
                        Text(
                            text = "Aucune analyse générée aujourd'hui. Cliquez sur le bouton de rafraîchissement pour lancer l'analyse croisée de vos données.",
                            fontSize = 13.sp,
                            color = MediumGray
                        )
                    }
                }
            }
        }

         // --- BLOC HERO : FATIGUE NERVEUSE ---
        item {
            val currentRecoveryStreak = viewModel.calculateCurrentStreak()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Recovery Streak
                OperationsCard(
                    modifier = Modifier.weight(1f),
                    borderAccent = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = "Streak",
                            tint = GoldClassic,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "STREAK RÉCUP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentRecoveryStreak Jours",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                    }
                }

                // Card 2: Last Night's Sleep
                OperationsCard(
                    modifier = Modifier.weight(1f),
                    borderAccent = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = "Sleep",
                            tint = GoldClassic,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "DERNIÈRE NUIT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val sleepText = if (lastNightSleep != null) {
                            "${String.format("%.1f", lastNightSleep.durationHours)} h"
                        } else {
                            "-- h"
                        }
                        Text(
                            text = sleepText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                    }
                }
            }
        }

        // --- GRID SECONDAIRE À 2 COLONNES ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // COL 1: Tâches en cours
                OperationsCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp).fillMaxHeight()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tâches",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Box(
                                modifier = Modifier
                                    .background(LightBeige, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${remainingTasks.size} rest.",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldClassic
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (nextTask != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onNavigateToPage(1) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(GoldClassic, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = nextTask.title,
                                        fontSize = 11.sp,
                                        color = Anthracite,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (nextTask.time != null) {
                                        Text(
                                            text = "${nextTask.time}",
                                            fontSize = 9.sp,
                                            color = MediumGray
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Toutes les tâches sont accomplies.",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        }
                    }
                }

                // COL 2: Séance GYM
                OperationsCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp).fillMaxHeight()) {
                        Text(
                            text = "Séance GYM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (todayGymSession != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = todayGymSession.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Anthracite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${todayGymSession.time} • ${todayGymSession.durationMinutes} min",
                                        fontSize = 9.sp,
                                        color = MediumGray
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "Gym",
                                    tint = GoldClassic,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Repos",
                                    fontSize = 11.sp,
                                    color = MediumGray
                                )
                                Icon(
                                    imageVector = Icons.Default.Hotel,
                                    contentDescription = "Rest",
                                    tint = MediumGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // COL 3: Compléments
                OperationsCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp).fillMaxHeight()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Compléments",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Text(
                                text = "Voir tout",
                                fontSize = 10.sp,
                                color = GoldClassic,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { onNavigateToPage(3) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val takenTodayCount = listOf(
                            todaySuppLog.creatine, todaySuppLog.omega3, todaySuppLog.magnesium,
                            todaySuppLog.ashwagandha, todaySuppLog.tongkatAli, todaySuppLog.vitaminD3,
                            todaySuppLog.zinc, todaySuppLog.lTheanine, todaySuppLog.boron, todaySuppLog.lCitrulline
                        ).count { it }
                        val progressPct = takenTodayCount / 10f

                        Text(
                            text = "$takenTodayCount/10 pris aujourd'hui",
                            fontSize = 10.sp,
                            color = MediumGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progressPct },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldClassic,
                            trackColor = LightGrayDivider,
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val listSupps = listOf(
                            "Créatine" to ("creatine" to todaySuppLog.creatine),
                            "Oméga-3" to ("omega3" to todaySuppLog.omega3),
                            "Magnésium" to ("magnesium" to todaySuppLog.magnesium),
                            "Ashwa" to ("ashwagandha" to todaySuppLog.ashwagandha),
                            "Tongkat" to ("tongkatAli" to todaySuppLog.tongkatAli)
                        )

                        listSupps.forEach { (label, data) ->
                            val (key, taken) = data
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, fontSize = 11.sp, color = Anthracite)
                                Checkbox(
                                    checked = taken,
                                    onCheckedChange = { viewModel.toggleSupplement(key, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = GoldClassic),
                                    modifier = Modifier.size(16.dp).scale(0.7f)
                                )
                            }
                        }
                    }
                }

                // COL 4: Sommeil
                OperationsCard(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp).fillMaxHeight()) {
                        Text(
                            text = "Sommeil",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (lastNightSleep != null) {
                            val hours = lastNightSleep.durationHours
                            val pct = (hours / 8f).coerceIn(0f, 1f)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "${String.format("%.1f", hours)}h",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Anthracite
                                )
                                Text(
                                    text = "${lastNightSleep.bedtime} - ${lastNightSleep.waketime}",
                                    fontSize = 9.sp,
                                    color = MediumGray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { pct },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = GoldClassic,
                                    trackColor = LightGrayDivider,
                                )
                            }
                        } else {
                            Text(
                                text = "Aucun sommeil enregistré.",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- SCREEN 2: TO-DO LIST ---

@Composable
fun TodoListPage(viewModel: OperationsViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val todayStr = viewModel.getTodayDate()

    val (completedTasks, pendingTasks) = tasks.partition { it.done }

    var isCompletedCollapsed by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = WhitePure,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoldClassic,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                PageHeader(
                    title = "To-Do List",
                    subtitle = "Gérez vos objectifs quotidiens et stratégiques"
                )
            }

            if (pendingTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "All Done",
                                tint = LightGrayDivider,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aucune tâche en attente",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MediumGray
                            )
                            Text(
                                text = "Cliquez sur '+' pour planifier votre journée.",
                                fontSize = 12.sp,
                                color = MediumGray
                            )
                        }
                    }
                }
            } else {
                val morningTasks = pendingTasks.filter { !it.time.isNullOrEmpty() && it.time < "12:00" }
                val afternoonTasks = pendingTasks.filter { !it.time.isNullOrEmpty() && it.time >= "12:00" && it.time < "18:00" }
                val eveningTasks = pendingTasks.filter { !it.time.isNullOrEmpty() && it.time >= "18:00" }
                val unscheduledTasks = pendingTasks.filter { it.time.isNullOrEmpty() }

                if (morningTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "MATIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(morningTasks, key = { "morning_${it.id}" }) { task ->
                        TaskRow(task = task, onToggle = { viewModel.toggleTaskDone(task) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }

                if (afternoonTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "APRÈS-MIDI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(afternoonTasks, key = { "afternoon_${it.id}" }) { task ->
                        TaskRow(task = task, onToggle = { viewModel.toggleTaskDone(task) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }

                if (eveningTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "SOIR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(eveningTasks, key = { "evening_${it.id}" }) { task ->
                        TaskRow(task = task, onToggle = { viewModel.toggleTaskDone(task) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }

                if (unscheduledTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "SANS HORAIRE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(unscheduledTasks, key = { "unscheduled_${it.id}" }) { task ->
                        TaskRow(task = task, onToggle = { viewModel.toggleTaskDone(task) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }
            }

            if (completedTasks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCompletedCollapsed = !isCompletedCollapsed }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tâches terminées (${completedTasks.size})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MediumGray
                        )
                        Icon(
                            imageVector = if (isCompletedCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Collapse/Expand",
                            tint = MediumGray
                        )
                    }
                }

                if (!isCompletedCollapsed) {
                    items(completedTasks, key = { it.id }) { task ->
                        TaskRow(task = task, onToggle = { viewModel.toggleTaskDone(task) }, onDelete = { viewModel.deleteTask(task.id) })
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                todayStr = todayStr,
                onDismiss = { showAddDialog = false },
                onAdd = { title, desc, priority, date, time, category ->
                    viewModel.addTask(title, desc, priority, date, time, category)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun TaskRow(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    val priorityColor = when (task.priority) {
        "HAUTE" -> GoldClassic
        "MOYENNE" -> MediumGray
        else -> LightGrayDivider
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhitePure)
            .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.done,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = GoldClassic)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(8.dp)
                .background(priorityColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.done) MediumGray else Anthracite,
                textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
            )
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    fontSize = 11.sp,
                    color = MediumGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(LightBeige, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.category.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                }

                if (task.time != null) {
                    Text(
                        text = "${task.date} à ${task.time}",
                        fontSize = 10.sp,
                        color = MediumGray
                    )
                } else {
                    Text(
                        text = task.date,
                        fontSize = 10.sp,
                        color = MediumGray
                    )
                }
            }
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFC62828).copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    todayStr: String,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String?, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MOYENNE") }
    var category by remember { mutableStateOf("perso") }
    var date by remember { mutableStateOf(todayStr) }
    var hasTime by remember { mutableStateOf(false) }
    var timeHour by remember { mutableStateOf("09") }
    var timeMinute by remember { mutableStateOf("00") }

    Dialog(onDismissRequest = onDismiss) {
        OperationsCard {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ajouter une Tâche",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("task_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnelle)", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Selection
                Column {
                    Text("Priorité", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("HAUTE", "MOYENNE", "BASSE").forEach { p ->
                            val isSelected = priority == p
                            val color = when (p) {
                                "HAUTE" -> GoldClassic
                                "MOYENNE" -> MediumGray
                                else -> Color(0xFFDCDCDC)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { priority = p },
                                label = { Text(p, fontSize = 11.sp) },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(color, CircleShape)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightBeige,
                                    selectedLabelColor = GoldClassic
                                )
                            )
                        }
                    }
                }

                // Category Selection
                Column {
                    Text("Catégorie", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("perso", "travail", "santé").forEach { cat ->
                            val isSelected = category == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { category = cat },
                                label = { Text(cat.uppercase(), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightBeige,
                                    selectedLabelColor = GoldClassic
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasTime,
                        onCheckedChange = { hasTime = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldClassic)
                    )
                    Text("Définir une heure précise", fontSize = 12.sp, color = Anthracite)
                }

                if (hasTime) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = timeHour,
                            onValueChange = { if (it.length <= 2) timeHour = it },
                            label = { Text("HH", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(64.dp)
                        )
                        Text(":", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = timeMinute,
                            onValueChange = { if (it.length <= 2) timeMinute = it },
                            label = { Text("MM", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler", color = MediumGray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    GoldGradientButton(
                        text = "Ajouter",
                        onClick = {
                            if (title.isNotEmpty()) {
                                val finalTime = if (hasTime) "$timeHour:$timeMinute" else null
                                onAdd(title, description, priority, date, finalTime, category)
                            }
                        },
                        enabled = title.isNotEmpty(),
                        testTag = "save_task_button"
                    )
                }
            }
        }
    }
}


// --- SCREEN 3: CALENDRIER ---

@Composable
fun CalendrierPage(viewModel: OperationsViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()

    var viewMode by remember { mutableStateOf("Semaine") } // "Semaine", "Jour", "Mois"
    var selectedDayStr by remember { mutableStateOf(viewModel.getTodayDate()) }

    val calendar = remember { Calendar.getInstance() }
    var currentWeekStart by remember { mutableStateOf(Date()) }

    // Initialize to current week start
    LaunchedEffect(Unit) {
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        currentWeekStart = calendar.time
    }

    val sdfDisplay = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    val sdfDb = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Calculate the 7 days of the currently selected week
    val weekDays = remember(currentWeekStart) {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        cal.time = currentWeekStart
        for (i in 0..6) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Modal/Detail State
    var selectedEvent by remember { mutableStateOf<Any?>(null) } // Can be Task or GymSession
    var clickedEmptySlotTime by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(Date, HH:MM)
    var showAddTaskDialogByCalendar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Calendrier",
            subtitle = "Planification hebdomadaire des opérations"
        )

        // Navigation and View switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Week changer
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = currentWeekStart
                    cal.add(Calendar.WEEK_OF_YEAR, -1)
                    currentWeekStart = cal.time
                }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev", tint = GoldClassic)
                }
                Text(
                    text = "Semaine du ${SimpleDateFormat("dd/MM", Locale.getDefault()).format(weekDays.first())}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                IconButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = currentWeekStart
                    cal.add(Calendar.WEEK_OF_YEAR, 1)
                    currentWeekStart = cal.time
                }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next", tint = GoldClassic)
                }
            }

            // Simple switcher
            Row(
                modifier = Modifier
                    .background(LightBeige, RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                listOf("Semaine", "Jour").forEach { mode ->
                    val isSel = viewMode == mode
                    Box(
                        modifier = Modifier
                            .background(if (isSel) GoldClassic else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { viewMode = mode }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else MediumGray
                        )
                    }
                }
            }
        }

        // --- COMPACT EVENT DENSITY ROW ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBeige.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            weekDays.forEach { date ->
                val dateStr = sdfDb.format(date)
                val dayLabelShort = SimpleDateFormat("E", Locale.getDefault()).format(date).uppercase()
                val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
                val isToday = dateStr == viewModel.getTodayDate()
                val isSelected = dateStr == selectedDayStr

                val eventCount = tasks.count { it.date == dateStr } + gymSessions.count { it.date == dateStr }

                Column(
                    modifier = Modifier
                        .clickable {
                            selectedDayStr = dateStr
                            viewMode = "Jour"
                        }
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayLabelShort,
                        fontSize = 9.sp,
                        color = if (isToday) GoldClassic else MediumGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dayNum,
                        fontSize = 11.sp,
                        color = if (isToday) GoldClassic else Anthracite,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (eventCount > 0) {
                        val badgeColor = if (eventCount > 3) GoldClassic else LightGrayDivider
                        val badgeTextColor = if (eventCount > 3) Color.White else Anthracite
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(badgeColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = eventCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .border(0.5.dp, LightGrayDivider, CircleShape)
                        )
                    }
                }
            }
        }

        if (viewMode == "Jour") {
            // Day View - Selected day's events
            val todayDateStr = selectedDayStr
            val dayTasks = tasks.filter { it.date == todayDateStr && it.time != null }
            val dayGym = gymSessions.filter { it.date == todayDateStr }

            Text("Événements du ${SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(sdfDb.parse(selectedDayStr) ?: Date())}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)

            if (dayTasks.isEmpty() && dayGym.isEmpty()) {
                Text("Aucun événement prévu pour cette journée.", fontSize = 12.sp, color = MediumGray, modifier = Modifier.padding(vertical = 20.dp))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    dayGym.forEach { gym ->
                        GymEventCard(gym) { selectedEvent = gym }
                    }
                    dayTasks.forEach { task ->
                        TaskEventCard(task) { selectedEvent = task }
                    }
                }
            }
        } else {
            // Week Grid View
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                    .background(WhitePure)
                    .padding(8.dp)
            ) {
                // Header row of days
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(50.dp)) // Time column offset
                    weekDays.forEach { date ->
                        val dayLabel = viewModel.getDayOfWeekLabel(sdfDb.format(date))
                        val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
                        val isToday = sdfDb.format(date) == viewModel.getTodayDate()
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) GoldClassic else MediumGray
                            )
                            Text(
                                text = dayNum,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) GoldClassic else Anthracite
                            )
                        }
                    }
                }

                Divider(color = LightGrayDivider)

                // Simple grid with select hours (e.g., 08:00, 10:00, 12:00, 14:00, 16:00, 18:00, 20:00)
                val hours = listOf("08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00")
                hours.forEach { hour ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time label
                        Text(
                            text = hour,
                            fontSize = 10.sp,
                            color = MediumGray,
                            modifier = Modifier.width(50.dp),
                            textAlign = TextAlign.Center
                        )

                        // 7 Day Slots
                        weekDays.forEach { date ->
                            val dateStr = sdfDb.format(date)
                            val hourPrefix = hour.substring(0, 2)

                            // Find task in this hour
                            val hourTask = tasks.firstOrNull {
                                it.date == dateStr && it.time != null && it.time.startsWith(hourPrefix)
                            }
                            val hourGym = gymSessions.firstOrNull {
                                it.date == dateStr && it.time.startsWith(hourPrefix)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(0.5.dp, LightGrayDivider.copy(alpha = 0.5f))
                                    .clickable {
                                        if (hourGym != null) {
                                            selectedEvent = hourGym
                                        } else if (hourTask != null) {
                                            selectedEvent = hourTask
                                        } else {
                                            clickedEmptySlotTime = Pair(dateStr, hour)
                                            showAddTaskDialogByCalendar = true
                                        }
                                    }
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (hourGym != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(LightBeige, RoundedCornerShape(4.dp))
                                            .border(1.dp, GoldClassic, RoundedCornerShape(4.dp))
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = "Gym",
                                            tint = GoldClassic,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                } else if (hourTask != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(GoldPale, RoundedCornerShape(4.dp))
                                            .border(1.dp, GoldClassic.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = hourTask.title,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldClassic,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Event details modal/sheet
        if (selectedEvent != null) {
            val ev = selectedEvent
            Dialog(onDismissRequest = { selectedEvent = null }) {
                OperationsCard(borderAccent = true) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Détails de l'Événement",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )

                        if (ev is GymSession) {
                            Text(text = "SÉANCE GYM", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                            Text(text = ev.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                            Text(text = "Date : ${ev.date} à ${ev.time}", fontSize = 13.sp, color = Anthracite)
                            Text(text = "Durée : ${ev.durationMinutes} min", fontSize = 13.sp, color = Anthracite)
                            if (ev.muscleGroups.isNotEmpty()) {
                                Text(text = "Groupes ciblés : ${ev.muscleGroups}", fontSize = 13.sp, color = Anthracite)
                            }
                            if (ev.notes.isNotEmpty()) {
                                Text(text = "Notes : ${ev.notes}", fontSize = 12.sp, color = MediumGray)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    viewModel.deleteGymSession(ev.id)
                                    selectedEvent = null
                                }) {
                                    Text("Supprimer", color = Color(0xFFC62828))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                GoldGradientButton(text = "Fermer", onClick = { selectedEvent = null })
                            }
                        } else if (ev is Task) {
                            Text(text = "TÂCHE", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                            Text(text = ev.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                            Text(text = "Date : ${ev.date} à ${ev.time ?: "Non spécifié"}", fontSize = 13.sp, color = Anthracite)
                            Text(text = "Priorité : ${ev.priority}", fontSize = 13.sp, color = Anthracite)
                            Text(text = "Catégorie : ${ev.category.uppercase()}", fontSize = 13.sp, color = Anthracite)
                            if (ev.description.isNotEmpty()) {
                                Text(text = "Description : ${ev.description}", fontSize = 12.sp, color = MediumGray)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = {
                                    viewModel.deleteTask(ev.id)
                                    selectedEvent = null
                                }) {
                                    Text("Supprimer", color = Color(0xFFC62828))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                GoldGradientButton(text = "Fermer", onClick = { selectedEvent = null })
                            }
                        }
                    }
                }
            }
        }

        // Dialog to add task directly from clicking calendar empty slot
        if (showAddTaskDialogByCalendar && clickedEmptySlotTime != null) {
            val (clickDate, clickHour) = clickedEmptySlotTime!!
            val h = clickHour.substring(0, 2)
            val m = clickHour.substring(3, 5)

            Dialog(onDismissRequest = { showAddTaskDialogByCalendar = false }) {
                OperationsCard {
                    var title by remember { mutableStateOf("") }
                    var desc by remember { mutableStateOf("") }
                    var priority by remember { mutableStateOf("MOYENNE") }
                    var category by remember { mutableStateOf("perso") }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ajouter à ${clickHour} le ${clickDate}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Titre", fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            label = { Text("Description (optionnelle)", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showAddTaskDialogByCalendar = false }) {
                                Text("Annuler", color = MediumGray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GoldGradientButton(
                                text = "Ajouter",
                                onClick = {
                                    if (title.isNotEmpty()) {
                                        viewModel.addTask(title, desc, priority, clickDate, "$h:$m", category)
                                        showAddTaskDialogByCalendar = false
                                    }
                                },
                                enabled = title.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GymEventCard(gym: GymSession, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(LightBeige, RoundedCornerShape(12.dp))
            .border(1.dp, GoldClassic, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "SÉANCE GYM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
            Text(text = gym.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
            Text(text = "À ${gym.time} • ${gym.durationMinutes} min", fontSize = 11.sp, color = MediumGray)
        }
        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Gym", tint = GoldClassic)
    }
}

@Composable
fun TaskEventCard(task: Task, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(GoldPale, RoundedCornerShape(12.dp))
            .border(1.dp, GoldClassic.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "TÂCHE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
            Text(text = task.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
            Text(text = "Prévu à ${task.time} • Priorité: ${task.priority}", fontSize = 11.sp, color = MediumGray)
        }
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Task", tint = GoldClassic.copy(alpha = 0.7f))
    }
}


// --- SCREEN 4: COMPLÉMENTS ALIMENTAIRES ---

@Composable
fun ComplementsPage(viewModel: OperationsViewModel) {
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val todayStr = viewModel.getTodayDate()
    val todayLog = supplementLogs.firstOrNull { it.date == todayStr } ?: SupplementLog(date = todayStr)

    val last7Days = remember {
        val list = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(sdf.format(c.time))
        }
        list
    }

    val listSupplements = listOf(
        SupplementInfo(
            name = "Créatine Pure",
            moment = "Après l'entraînement (ou avec un repas, régularité prioritaire)",
            justification = "L'effet est cumulatif — la régularité quotidienne compte plus que l'heure exacte.",
            key = "creatine",
            taken = todayLog.creatine
        ),
        SupplementInfo(
            name = "Oméga-3 (Fish Oil)",
            moment = "Avec un repas contenant un peu de gras (matin ou midi)",
            justification = "Améliore l'absorption des acides gras liposolubles.",
            key = "omega3",
            taken = todayLog.omega3
        ),
        SupplementInfo(
            name = "Magnésium",
            moment = "Soir, 30-60 min avant le coucher",
            justification = "Effet relaxant sur le système nerveux, favorise un sommeil profond.",
            key = "magnesium",
            taken = todayLog.magnesium
        ),
        SupplementInfo(
            name = "Ashwagandha",
            moment = "Soir (ou matin+soir si dose divisée)",
            justification = "Adaptogène anti-cortisol, soutient la récupération nocturne et réduit le stress chronique.",
            key = "ashwagandha",
            taken = todayLog.ashwagandha
        ),
        SupplementInfo(
            name = "Tongkat Ali",
            moment = "Matin, à jeun",
            justification = "Soutient la testostérone libre naturellement, effet légèrement stimulant à éviter le soir.",
            key = "tongkatAli",
            taken = todayLog.tongkatAli
        ),
        SupplementInfo(
            name = "Vitamine D3",
            moment = "Matin, avec un repas contenant du gras",
            justification = "Carence très fréquente et fortement corrélée à une testostérone basse ; l'absorption est meilleure avec des lipides.",
            key = "vitaminD3",
            taken = todayLog.vitaminD3
        ),
        SupplementInfo(
            name = "Zinc",
            moment = "Soir, à distance des repas riches en calcium/fibres",
            justification = "Cofacteur essentiel de la synthèse hormonale ; une carence, même légère, impacte directement la production de testostérone.",
            key = "zinc",
            taken = todayLog.zinc
        ),
        SupplementInfo(
            name = "L-Théanine",
            moment = "Soir, avec Magnésium et Ashwagandha",
            justification = "Favorise un état de calme sans somnolence ; complète la descente du système nerveux avant le coucher.",
            key = "lTheanine",
            taken = todayLog.lTheanine
        ),
        SupplementInfo(
            name = "Bore (Boron)",
            moment = "Matin, avec un repas",
            justification = "Données montrant une hausse de la testostérone libre (forme active) sur plusieurs semaines d'usage régulier.",
            key = "boron",
            taken = todayLog.boron
        ),
        SupplementInfo(
            name = "L-Citrulline",
            moment = "30-45 min avant l'entraînement (jours GYM), sinon le matin",
            justification = "Précurseur d'oxyde nitrique, améliore la circulation sanguine — pertinent pour la fonction érectile et l'endurance à l'effort.",
            key = "lCitrulline",
            taken = todayLog.lCitrulline
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Compléments",
            subtitle = "Protocole d'optimisation biologique quotidien"
        )

        // --- GRID D'HISTORIQUE 7 JOURS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historique hebdomadaire",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )
            val completionPercent = remember(supplementLogs, last7Days) {
                var totalTaken = 0
                last7Days.forEach { date ->
                    val dateLog = supplementLogs.firstOrNull { it.date == date }
                    if (dateLog != null) {
                        if (dateLog.creatine) totalTaken++
                        if (dateLog.omega3) totalTaken++
                        if (dateLog.magnesium) totalTaken++
                        if (dateLog.ashwagandha) totalTaken++
                        if (dateLog.tongkatAli) totalTaken++
                        if (dateLog.vitaminD3) totalTaken++
                        if (dateLog.zinc) totalTaken++
                        if (dateLog.lTheanine) totalTaken++
                        if (dateLog.boron) totalTaken++
                        if (dateLog.lCitrulline) totalTaken++
                    }
                }
                (totalTaken * 100) / 70
            }
            Text(
                text = "$completionPercent% cette semaine",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldClassic
            )
        }

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                // Days headers row
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Complément", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
                    last7Days.forEach { date ->
                        val label = viewModel.getDayOfWeekLabel(date)
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (date == todayStr) GoldClassic else MediumGray,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val keysList = listOf("creatine", "omega3", "magnesium", "ashwagandha", "tongkatAli", "vitaminD3", "zinc", "lTheanine", "boron", "lCitrulline")
                val labelsList = listOf("Créatine", "Oméga-3", "Magnésium", "Ashwa", "Tongkat", "Vitamine D3", "Zinc", "L-Théanine", "Bore", "Citrulline")

                keysList.forEachIndexed { idx, key ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = labelsList[idx], fontSize = 11.sp, color = Anthracite, modifier = Modifier.width(100.dp))

                        last7Days.forEach { date ->
                            val dateLog = supplementLogs.firstOrNull { it.date == date }
                            val taken = when (key) {
                                "creatine" -> dateLog?.creatine == true
                                "omega3" -> dateLog?.omega3 == true
                                "magnesium" -> dateLog?.magnesium == true
                                "ashwagandha" -> dateLog?.ashwagandha == true
                                "tongkatAli" -> dateLog?.tongkatAli == true
                                "vitaminD3" -> dateLog?.vitaminD3 == true
                                "zinc" -> dateLog?.zinc == true
                                "lTheanine" -> dateLog?.lTheanine == true
                                "boron" -> dateLog?.boron == true
                                "lCitrulline" -> dateLog?.lCitrulline == true
                                else -> false
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .size(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(
                                            if (taken) GoldClassic else Color(0xFFF0F0F0),
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- PROTOCOLE DÉTAILLÉ ---
        Text(
            text = "Fiches d'optimisation scientifique",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        // --- MEDICAL DISCLAIMER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBeige.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .border(width = 0.5.dp, color = LightGrayDivider, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info médicale",
                    tint = GoldClassic,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Avec désormais 10 compléments ciblés dans ton protocole d'optimisation complet, un avis médical est d'autant plus indispensable pour éviter toute interaction indésirable (ex. Ashwagandha avec des traitements thyroïdiens ou antidépresseurs, Magnésium avec certains antibiotiques, Bore avec des régulations hormonales).",
                    fontSize = 11.sp,
                    color = MediumGray,
                    style = androidx.compose.ui.text.TextStyle(lineHeight = 15.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        listSupplements.forEach { info ->
            OperationsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = info.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Timing : ${info.moment}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = GoldClassic
                            )
                            if (info.key == "ashwagandha" || info.key == "tongkatAli") {
                                val streak = viewModel.calculateConsecutiveDaysTaken(info.key)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Pris en continu depuis $streak jours",
                                    fontSize = 11.sp,
                                    color = MediumGray
                                )
                                if (streak >= 56) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .background(Color(0xFFFFF9E6), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, Color(0xFFFFB300), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "Pause",
                                            tint = Color(0xFFFFB300),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "Pause de 2-4 semaines recommandée",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB78103)
                                        )
                                    }
                                }
                            }
                        }
                        Checkbox(
                            checked = info.taken,
                            onCheckedChange = { viewModel.toggleSupplement(info.key, it) },
                            colors = CheckboxDefaults.colors(checkedColor = GoldClassic)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = info.justification,
                        fontSize = 12.sp,
                        color = MediumGray,
                        style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            }
        }
    }
}

data class SupplementInfo(
    val name: String,
    val moment: String,
    val justification: String,
    val key: String,
    val taken: Boolean
)


// --- SCREEN 5: GYM (SPORT) ---

@Composable
fun GymPage(viewModel: OperationsViewModel) {
    val gymSessions by viewModel.gymSessions.collectAsState()
    val gymExercises by viewModel.gymExercises.collectAsState()
    val todayStr = viewModel.getTodayDate()

    var showAddForm by remember { mutableStateOf(false) }

    // Exercise dialog inputs
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var selectedSessionIdForExercise by remember { mutableStateOf<Long?>(null) }
    var editingExercise by remember { mutableStateOf<GymExercise?>(null) }

    var exerciseNameInput by remember { mutableStateOf("") }
    var exerciseSetsInput by remember { mutableStateOf("3") }
    var exerciseRepsInput by remember { mutableStateOf("10") }
    var exerciseWeightInput by remember { mutableStateOf("60") }

    // Form inputs
    var name by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00") }
    var date by remember { mutableStateOf(todayStr) }
    var duration by remember { mutableStateOf("60") }
    var notes by remember { mutableStateOf("") }

    val muscleGroupsAvailable = listOf("Push", "Pull", "Legs", "Full Body", "Cardio", "Autre")
    val selectedMuscleGroups = remember { mutableStateListOf<String>() }

    // Basic stats calculations
    val cal = Calendar.getInstance()
    val sessionsThisWeek = remember(gymSessions) {
        gymSessions.filter {
            // Very simple week calculation
            val dateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            try {
                val d = dateSdf.parse(it.date)
                val diff = Date().time - d.time
                diff < (7 * 24 * 60 * 60 * 1000)
            } catch (e: Exception) {
                false
            }
        }.size
    }

    val sessionsThisMonth = remember(gymSessions) {
        gymSessions.filter {
            it.date.startsWith(todayStr.substring(0, 7))
        }.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "GYM",
            subtitle = "Planification et suivi de votre renforcement athlétique",
            action = {
                GoldGradientButton(
                    text = if (showAddForm) "Fermer" else "Nouvelle séance",
                    onClick = { showAddForm = !showAddForm },
                    testTag = "gym_form_toggle"
                )
            }
        )

        // --- STATS SIMPLE CARDS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OperationsCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sessions cette semaine", fontSize = 11.sp, color = MediumGray)
                    Text("$sessionsThisWeek", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                }
            }
            OperationsCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sessions ce mois", fontSize = 11.sp, color = MediumGray)
                    Text("$sessionsThisMonth", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                }
            }
        }

        // --- ADD SEANCE FORM ---
        AnimatedVisibility(visible = showAddForm) {
            OperationsCard(borderAccent = true) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Enregistrer une Séance", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Anthracite)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom de la séance (ex: Bench Day)", fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                        modifier = Modifier.fillMaxWidth().testTag("gym_name_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Heure (HH:MM)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Durée estimée (minutes)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Muscle selection
                    Column {
                        Text("Groupes musculaires ciblés", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            muscleGroupsAvailable.forEach { grp ->
                                val isSel = selectedMuscleGroups.contains(grp)
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        if (isSel) selectedMuscleGroups.remove(grp) else selectedMuscleGroups.add(grp)
                                    },
                                    label = { Text(grp, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = LightBeige,
                                        selectedLabelColor = GoldClassic
                                    )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes optionnelles (Séries / Reps / Charges)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    GoldGradientButton(
                        text = "Enregistrer la séance",
                        onClick = {
                            if (name.isNotEmpty()) {
                                viewModel.addGymSession(
                                    name,
                                    date,
                                    time,
                                    duration.toIntOrNull() ?: 60,
                                    selectedMuscleGroups.toList(),
                                    notes
                                )
                                // Reset form
                                name = ""
                                notes = ""
                                selectedMuscleGroups.clear()
                                showAddForm = false
                            }
                        },
                        enabled = name.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().testTag("gym_save_button")
                    )
                }
            }
        }

        // --- SUIVI DE CHARGE & PROGRESSION ---
        val uniqueExerciseNames = remember(gymExercises) {
            gymExercises.map { it.exerciseName.trim() }
                .distinct()
                .filter { it.isNotEmpty() }
        }

        if (uniqueExerciseNames.isNotEmpty()) {
            var selectedChartExercise by remember(uniqueExerciseNames) {
                mutableStateOf(uniqueExerciseNames.firstOrNull() ?: "")
            }

            val exerciseHistory = remember(gymExercises, selectedChartExercise) {
                gymExercises.filter { it.exerciseName.trim().equals(selectedChartExercise, ignoreCase = true) }
            }

            val chartPoints = remember(exerciseHistory, gymSessions) {
                exerciseHistory.mapNotNull { exercise ->
                    val session = gymSessions.firstOrNull { it.id == exercise.sessionId } ?: return@mapNotNull null
                    session.date to exercise.weightKg
                }.sortedBy { it.first }
            }

            OperationsCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Suivi de Charge & Progression", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                    Text("Visualisez l'évolution de vos performances au fil des séances.", fontSize = 11.sp, color = MediumGray)

                    // Selection Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uniqueExerciseNames.forEach { exName ->
                            val isSel = exName.equals(selectedChartExercise, ignoreCase = true)
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedChartExercise = exName },
                                label = { Text(exName, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LightBeige,
                                    selectedLabelColor = GoldClassic
                                )
                            )
                        }
                    }

                    if (chartPoints.isNotEmpty()) {
                        Text(
                            text = "Charge maximale pour $selectedChartExercise (kg)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(LightBeige.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp))
                                .border(width = 0.5.dp, color = LightGrayDivider, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val width = size.width
                                val height = size.height

                                val weights = chartPoints.map { it.second }
                                val minWeight = (weights.minOrNull() ?: 0f).let { if (it > 10f) it - 10f else 0f }
                                val maxWeight = (weights.maxOrNull() ?: 100f) + 10f
                                val weightRange = if (maxWeight - minWeight == 0f) 1f else (maxWeight - minWeight)

                                val pointsCount = chartPoints.size
                                val xStep = if (pointsCount > 1) width / (pointsCount - 1) else width

                                // Grid lines
                                val gridLines = 4
                                for (i in 0..gridLines) {
                                    val y = height * i / gridLines
                                    drawLine(
                                        color = LightGrayDivider.copy(alpha = 0.5f),
                                        start = Offset(0f, y),
                                        end = Offset(width, y),
                                        strokeWidth = 1f
                                    )
                                }

                                val path = Path()
                                chartPoints.forEachIndexed { index, pair ->
                                    val x = if (pointsCount > 1) index * xStep else width / 2
                                    val y = height - ((pair.second - minWeight) / weightRange * height)

                                    if (index == 0) {
                                        path.moveTo(x, y)
                                    } else {
                                        path.lineTo(x, y)
                                    }

                                    // Draw point circle
                                    drawCircle(
                                        color = GoldClassic,
                                        radius = 5.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                    drawCircle(
                                        color = WhitePure,
                                        radius = 2.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                }

                                if (pointsCount > 1) {
                                    drawPath(
                                        path = path,
                                        color = GoldClassic,
                                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(chartPoints.first().first, fontSize = 9.sp, color = MediumGray)
                            if (chartPoints.size > 1) {
                                Text(chartPoints.last().first, fontSize = 9.sp, color = MediumGray)
                            }
                        }
                    } else {
                        Text("Données insuffisantes pour tracer la progression.", fontSize = 11.sp, color = MediumGray)
                    }
                }
            }
        }

        // --- HISTORIQUE DES SÉANCES ---
        Text(
            text = "Historique des séances",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        if (gymSessions.isEmpty()) {
            Text(
                text = "Aucune séance de sport enregistrée pour le moment.",
                fontSize = 12.sp,
                color = MediumGray,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                gymSessions.forEach { sess ->
                    val sessExercises = gymExercises.filter { it.sessionId == sess.id }
                    OperationsCard {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sess.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Anthracite
                                    )
                                    Text(
                                        text = "Le ${sess.date} à ${sess.time} • ${sess.durationMinutes} minutes",
                                        fontSize = 11.sp,
                                        color = MediumGray
                                    )
                                    if (sess.muscleGroups.isNotEmpty()) {
                                        Text(
                                            text = sess.muscleGroups,
                                            fontSize = 11.sp,
                                            color = GoldClassic,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    if (sess.notes.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = sess.notes,
                                            fontSize = 11.sp,
                                            color = MediumGray
                                        )
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteGymSession(sess.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFC62828).copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Divider(color = LightGrayDivider, modifier = Modifier.padding(vertical = 8.dp))

                            // Exercices list
                            if (sessExercises.isNotEmpty()) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    sessExercises.forEach { ex ->
                                        val isPR = remember(gymExercises, ex) {
                                            val sameExerciseLogs = gymExercises.filter { it.exerciseName.trim().equals(ex.exerciseName.trim(), ignoreCase = true) }
                                            val maxWeight = sameExerciseLogs.map { it.weightKg }.maxOrNull() ?: 0f
                                            ex.weightKg >= maxWeight && maxWeight > 0f
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(LightBeige.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(ex.exerciseName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                                                    if (isPR) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier
                                                                .background(LightBeige, shape = RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Whatshot,
                                                                contentDescription = "PR",
                                                                tint = GoldClassic,
                                                                modifier = Modifier.size(10.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Text(
                                                                text = "PR",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = GoldClassic
                                                            )
                                                        }
                                                    }
                                                }
                                                Text("${ex.sets} séries x ${ex.reps} reps • ${ex.weightKg} kg", fontSize = 11.sp, color = MediumGray)
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(
                                                    onClick = {
                                                        editingExercise = ex
                                                        exerciseNameInput = ex.exerciseName
                                                        exerciseSetsInput = ex.sets.toString()
                                                        exerciseRepsInput = ex.reps.toString()
                                                        exerciseWeightInput = ex.weightKg.toString()
                                                        selectedSessionIdForExercise = sess.id
                                                        showAddExerciseDialog = true
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldClassic, modifier = Modifier.size(14.dp))
                                                }

                                                IconButton(
                                                    onClick = { viewModel.deleteGymExercise(ex.id) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color(0xFFC62828).copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    "Aucun exercice enregistré pour cette séance.",
                                    fontSize = 11.sp,
                                    color = MediumGray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            // Small Add button
                            TextButton(
                                onClick = {
                                    editingExercise = null
                                    exerciseNameInput = ""
                                    exerciseSetsInput = "3"
                                    exerciseRepsInput = "10"
                                    exerciseWeightInput = "60"
                                    selectedSessionIdForExercise = sess.id
                                    showAddExerciseDialog = true
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ajouter un exercice", color = GoldClassic, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text(if (editingExercise != null) "Modifier l'exercice" else "Ajouter un exercice") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = exerciseNameInput,
                        onValueChange = { exerciseNameInput = it },
                        label = { Text("Nom de l'exercice (ex: Squat)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = exerciseSetsInput,
                            onValueChange = { exerciseSetsInput = it },
                            label = { Text("Séries") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = exerciseRepsInput,
                            onValueChange = { exerciseRepsInput = it },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = exerciseWeightInput,
                        onValueChange = { exerciseWeightInput = it },
                        label = { Text("Poids (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sessId = selectedSessionIdForExercise
                        val nameEx = exerciseNameInput.trim()
                        val sets = exerciseSetsInput.toIntOrNull() ?: 3
                        val reps = exerciseRepsInput.toIntOrNull() ?: 10
                        val weight = exerciseWeightInput.toFloatOrNull() ?: 60f

                        if (sessId != null && nameEx.isNotEmpty()) {
                            val editEx = editingExercise
                            if (editEx != null) {
                                viewModel.updateGymExercise(editEx.id, sessId, nameEx, sets, reps, weight)
                            } else {
                                viewModel.addGymExercise(sessId, nameEx, sets, reps, weight)
                            }
                            showAddExerciseDialog = false
                        }
                    },
                    enabled = exerciseNameInput.isNotEmpty()
                ) {
                    Text("Enregistrer", color = GoldClassic, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}


// --- SCREEN 6: RÉCUPÉRATION (PAGE PRINCIPALE ET RICHE) ---

@Composable
fun RecoveryPage(viewModel: OperationsViewModel) {
    val recoveryStreaks by viewModel.recoveryStreaks.collectAsState()
    val currentStreak = viewModel.calculateCurrentStreak()

    // Kegel states
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val todayStr = viewModel.getTodayDate()
    val todayKegelLog = kegelLogs.firstOrNull { it.date == todayStr } ?: KegelLog(date = todayStr)

    // Breathing states
    val breathingSessions by viewModel.breathingSessions.collectAsState()

    // Journal states
    val journalEntries by viewModel.journalEntries.collectAsState()

    var activeTab by remember { mutableStateOf("Streak") } // "Streak", "Kegel", "Respiration", "Stop-Start", "Journal"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Récupération",
            subtitle = "Régénération nerveuse, hormonale et musculaire globale"
        )

        // --- CONDENSED SUMMARY CARD ---
        val last7Days = remember {
            val list = mutableListOf<String>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            for (i in 6 downTo 0) {
                val c = Calendar.getInstance()
                c.add(Calendar.DAY_OF_YEAR, -i)
                list.add(sdf.format(c.time))
            }
            list
        }
        val kegelCount7Days = remember(kegelLogs, last7Days) {
            kegelLogs.count { it.date in last7Days }
        }
        val lastBreathingSession = remember(breathingSessions) {
            breathingSessions.maxByOrNull { it.date }
        }
        val lastBreathingText = if (lastBreathingSession != null) {
            "Le ${lastBreathingSession.date}"
        } else {
            "Aucune"
        }

        OperationsCard(borderAccent = true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Col 1: Streak
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "STREAK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$currentStreak jours", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(LightGrayDivider))

                // Col 2: Kegel
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "KEGEL (7J)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$kegelCount7Days / 7 séances", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                }

                // Divider
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(LightGrayDivider))

                // Col 3: Respiration
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "RESPIRATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = lastBreathingText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        // Horizontal tabs inside a card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(LightGrayBg, RoundedCornerShape(12.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Streak", "Kegel", "Respiration", "Stop-Start", "Journal").forEach { tab ->
                val isSel = activeTab == tab
                Box(
                    modifier = Modifier
                        .background(if (isSel) GoldClassic else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { activeTab = tab }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tab,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSel) Color.White else MediumGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (activeTab) {
            "Streak" -> StreakSection(currentStreak, recoveryStreaks) { trigger -> viewModel.resetRecoveryStreak(trigger) }
            "Kegel" -> KegelSection(viewModel, todayKegelLog.done, kegelLogs.size)
            "Respiration" -> RespirationSection(viewModel, breathingSessions.size)
            "Stop-Start" -> StopStartSection()
            "Journal" -> JournalSection(viewModel, journalEntries)
        }
    }
}

@Composable
fun StreakSection(currentStreak: Int, pastStreaks: List<RecoveryStreak>, onReset: (String?) -> Unit) {
    var showConfirmReset by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Goal progression
        OperationsCard(borderAccent = true) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Objectif Régénération : 180 Jours",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Progression sur 6 mois pour un reset complet du système nerveux.",
                    fontSize = 12.sp,
                    color = MediumGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                val pct = (currentStreak / 180f).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jour $currentStreak / 180",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                    Text(
                        text = "${String.format("%.1f", pct * 100)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { pct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldClassic,
                    trackColor = LightGrayDivider
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showConfirmReset = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828).copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End).testTag("reset_streak_button")
                ) {
                    Text("Réinitialiser le Streak", color = Color(0xFFC62828), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Streak History Curve / Graph using Canvas
        if (pastStreaks.size >= 2) {
            Text("Courbe d'évolution des streaks", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
            OperationsCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val maxStreak = pastStreaks.maxOf { it.days }.toFloat().coerceAtLeast(5f)
                        val pointsCount = pastStreaks.size
                        val stepX = width / (pointsCount - 1).coerceAtLeast(1)

                        val path = Path()
                        pastStreaks.asReversed().forEachIndexed { idx, streak ->
                            val x = idx * stepX
                            val y = height - (streak.days.toFloat() / maxStreak * height)
                            if (idx == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                            // Draw point
                            drawCircle(color = GoldClassic, radius = 4.dp.toPx(), center = Offset(x, y))
                        }

                        drawPath(
                            path = path,
                            color = GoldClassic,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }

        // Past streaks list
        Text("Historique des streaks précédents", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
        if (pastStreaks.isEmpty()) {
            Text("Aucune réinitialisation enregistrée. Restez fort !", fontSize = 12.sp, color = MediumGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pastStreaks.forEach { streak ->
                    OperationsCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = streak.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                                    if (!streak.trigger.isNullOrEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .background(LightBeige, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = streak.trigger ?: "",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldClassic
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val dateText = if (streak.startDate.isNotEmpty()) {
                                    "Du ${streak.startDate} au ${streak.endDate}"
                                } else {
                                    "Fin le ${streak.endDate}"
                                }
                                Text(text = dateText, fontSize = 11.sp, color = MediumGray)
                            }
                            Text(
                                text = "${streak.days} jours",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic
                            )
                        }
                    }
                }
            }
        }

        if (showConfirmReset) {
            val triggers = listOf("Stress", "Ennui", "Fatigue", "Solitude", "Autre")
            var selectedTriggerInDialog by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { showConfirmReset = false },
                title = { Text("Confirmer la réinitialisation") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cette action enregistrera votre streak actuel de $currentStreak jours dans votre historique et relancera un nouveau streak à partir d'aujourd'hui. Aucun jugement, chaque jour est une opportunité.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Facteur déclencheur optionnel :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            triggers.forEach { trig ->
                                val isSel = selectedTriggerInDialog == trig
                                Box(
                                    modifier = Modifier
                                        .background(if (isSel) GoldClassic else LightBeige.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSel) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedTriggerInDialog = if (isSel) null else trig
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = trig,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MediumGray
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onReset(selectedTriggerInDialog)
                        showConfirmReset = false
                    }) {
                        Text("Confirmer", color = Color(0xFFC62828))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmReset = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun KegelSection(viewModel: OperationsViewModel, isTodayChecked: Boolean, totalSessions: Int) {
    var repsConfig by remember { mutableStateOf(10) }
    var variantConfig by remember { mutableStateOf("Standard") } // "Standard", "Rapides", "Longues"

    val isRunning by viewModel.kegelIsRunning.collectAsState()
    val currentRep by viewModel.kegelRepCount.collectAsState()
    val isContracting by viewModel.kegelIsContracting.collectAsState()
    val secondsLeft by viewModel.kegelSecondsLeft.collectAsState()

    val scaleValue = remember { Animatable(1f) }

    LaunchedEffect(isContracting, isRunning) {
        if (isRunning) {
            if (isContracting) {
                scaleValue.animateTo(
                    targetValue = 1.3f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
                )
            } else {
                scaleValue.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
                )
            }
        } else {
            scaleValue.animateTo(1f)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Routine details card
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Section Kegel — Routine Plancher Pelvien",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Renforcement du muscle pubo-coccygien (PC) pour améliorer la régulation nerveuse.",
                    fontSize = 12.sp,
                    color = MediumGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total sessions complétées : $totalSessions", fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fait aujourd'hui", fontSize = 11.sp, color = MediumGray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Checkbox(
                            checked = isTodayChecked,
                            onCheckedChange = { viewModel.toggleSupplement("kegel", it) }, // simple reuse or custom, but we check/uncheck
                            colors = CheckboxDefaults.colors(checkedColor = GoldClassic),
                            enabled = false // Managed via completing the routine
                        )
                    }
                }
            }
        }

        // Timer GUI Card
        OperationsCard(borderAccent = isRunning) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isRunning) {
                    Text("Lancer une séance guidée", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    // Config reps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Répétitions : $repsConfig", fontSize = 12.sp)
                        Slider(
                            value = repsConfig.toFloat(),
                            onValueChange = { repsConfig = it.toInt() },
                            valueRange = 10f..15f,
                            steps = 5,
                            modifier = Modifier.width(160.dp),
                            colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                        )
                    }

                    // Config variant
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Variante :", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Standard", "Rapides", "Longues").forEach { v ->
                                val isS = variantConfig == v
                                Box(
                                    modifier = Modifier
                                        .background(if (isS) LightBeige else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isS) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                                        .clickable { variantConfig = v }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(v, fontSize = 10.sp, color = if (isS) GoldClassic else MediumGray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GoldGradientButton(
                        text = "Commencer la routine",
                        onClick = { viewModel.startKegelTimer(repsConfig, variantConfig) },
                        modifier = Modifier.fillMaxWidth().testTag("start_kegel_button")
                    )
                } else {
                    // Running state guided circle
                    Text(
                        text = "Répétition $currentRep sur $repsConfig",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .graphicsLayer {
                                scaleX = scaleValue.value
                                scaleY = scaleValue.value
                            }
                            .background(
                                color = if (isContracting) GoldClassic.copy(alpha = 0.15f) else Color(0xFFF5F5F5),
                                shape = CircleShape
                            )
                            .border(2.dp, if (isContracting) GoldClassic else MediumGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isContracting) "CONTRACTER" else "RELACHER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isContracting) GoldClassic else MediumGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${secondsLeft}s",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Anthracite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.stopKegelTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Arrêter la séance", color = Color.White)
                    }
                }
            }
        }

        // Scientific Info Card
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notice Médicale", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Le muscle pubo-coccygien (PC) soutient la vessie et contrôle le flux éjaculatoire. Son renforcement progressif par contractions rythmiques améliore notablement le contrôle nerveux en quelques semaines de pratique régulière.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun RespirationSection(viewModel: OperationsViewModel, totalSessions: Int) {
    var durationMinutes by remember { mutableStateOf(5) }

    val isRunning by viewModel.breathingIsRunning.collectAsState()
    val state by viewModel.breathingState.collectAsState()
    val secondsLeft by viewModel.breathingSecondsLeft.collectAsState()
    val totalElapsed by viewModel.breathingTotalSecondsElapsed.collectAsState()

    val scaleValue = remember { Animatable(1f) }

    LaunchedEffect(state, isRunning) {
        if (isRunning) {
            when (state) {
                "IN" -> scaleValue.animateTo(1.4f, animationSpec = tween(durationMillis = 4000, easing = LinearEasing))
                "HOLD" -> { /* Stays large */ }
                "OUT" -> scaleValue.animateTo(0.9f, animationSpec = tween(durationMillis = 6000, easing = LinearEasing))
            }
        } else {
            scaleValue.animateTo(1f)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Respiration Profonde — Cohérence Cardiaque",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Régulez le système nerveux autonome (baisse du cortisol, réduction du stress).",
                    fontSize = 12.sp,
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total sessions complétées : $totalSessions", fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Bold)
            }
        }

        OperationsCard(borderAccent = isRunning) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isRunning) {
                    Text("Lancer une séance guidée", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Durée : $durationMinutes minutes", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(3, 5, 10).forEach { m ->
                                val isS = durationMinutes == m
                                Box(
                                    modifier = Modifier
                                        .background(if (isS) LightBeige else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isS) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                                        .clickable { durationMinutes = m }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("${m}min", fontSize = 11.sp, color = if (isS) GoldClassic else MediumGray, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    GoldGradientButton(
                        text = "Commencer la cohérence cardiaque",
                        onClick = { viewModel.startBreathingTimer(durationMinutes) },
                        modifier = Modifier.fillMaxWidth().testTag("start_breathing_button")
                    )
                } else {
                    // Running guided breathing UI
                    val currentCycleLabel = when (state) {
                        "IN" -> "INSPIRER (ventre gonflé)"
                        "HOLD" -> "RETENIR L'AIR"
                        "OUT" -> "EXPIRER lentement"
                        else -> ""
                    }

                    val formattedElapsed = "${totalElapsed / 60}:${String.format("%02d", totalElapsed % 60)}"
                    Text(
                        text = "Temps écoulé : $formattedElapsed / ${durationMinutes}:00",
                        fontSize = 13.sp,
                        color = MediumGray
                    )

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .graphicsLayer {
                                scaleX = scaleValue.value
                                scaleY = scaleValue.value
                            }
                            .background(
                                color = when (state) {
                                    "IN" -> GoldClassic.copy(alpha = 0.2f)
                                    "HOLD" -> GoldClassic.copy(alpha = 0.12f)
                                    else -> Color(0xFFEBEBEB)
                                },
                                shape = CircleShape
                            )
                            .border(2.dp, GoldClassic, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = currentCycleLabel,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${secondsLeft}s",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Anthracite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.stopBreathingTimer() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Arrêter la séance", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StopStartSection() {
    var isPhase1Expanded by remember { mutableStateOf(false) }
    var isPhase2Expanded by remember { mutableStateOf(false) }
    var isPhase3Expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // --- CONTEXT & REALISTIC OBJECTIVE CARD ---
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Technique Clinique : Stop-Start",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La technique Stop-Start est la référence clinique mondiale en thérapie sexuelle pour rééduquer la réponse éjaculatoire et stabiliser l'activité nerveuse.",
                    fontSize = 12.sp,
                    color = MediumGray,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = LightGrayDivider)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Objectif Réaliste & Scientifique",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "L'objectif clinique visé est de retrouver un contrôle stable et confortable (5 à 7 minutes de pénétration active), ce qui correspond à la moyenne physiologique masculine saine.\n\nLes standards diffusés par l'industrie pornographique sont totalement artificiels, irréalistes et médicalement aberrants. Se libérer de cette pression mentale est la première étape indispensable pour détendre le système nerveux et retrouver une pleine maîtrise de soi.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 17.sp
                )
            }
        }

        // --- METHODOLOGY STEPS ---
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Protocole d'Entraînement Clinique", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                Divider(color = LightGrayDivider)

                Text(
                    text = "1. Phase d'Éveil : Pratiquez une stimulation graduelle de manière calme et isolée.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
                Text(
                    text = "2. Identification du Seuil : Repérez la montée d'excitation et arrêtez immédiatement tout mouvement avant d'atteindre le point de non-retour (niveau 8/10).",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
                Text(
                    text = "3. Pause Nerveuse : Contractez le muscle PC (Kegel) doucement pendant la pause pour canaliser l'afflux d'énergie et redescendre de 3 points sur votre échelle interne.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
                Text(
                    text = "4. Répétition : Répétez le cycle 3 à 4 fois par session avant d'interrompre complètement.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
            }
        }

        // --- PROGRESSIVE 6-MONTH PLAN SECTION ---
        Text(
            text = "Plan de Reconditionnement sur 6 Mois",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Phase 1 Card
        OperationsCard {
            Column(
                modifier = Modifier
                    .clickable { isPhase1Expanded = !isPhase1Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mois 1-2 : Reconditionnement de Base",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Text(
                            text = "Focus : Réduction du stress d'attente et éveil musculaire",
                            fontSize = 11.sp,
                            color = MediumGray
                        )
                    }
                    Icon(
                        imageVector = if (isPhase1Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand phase 1",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (isPhase1Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = LightGrayDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Exercices de Kegel d'endurance : Effectuez des contractions lentes (5s de maintien, 5s de repos) pour renforcer et assouplir le plancher pelvien sans congestion.\n" +
                               "• Respiration diaphragmatique guidée : Calmez l'activité orthosympathique (le système de l'éjaculation réflexe) en allongeant les expirations lors des moments de tension.\n" +
                               "• Zéro stimulation forcée : Aucun entraînement Stop-Start actif. L'objectif est purement de réinitialiser le tonus de repos du système nerveux et d'éliminer la peur de l'échec.",
                        fontSize = 11.sp,
                        color = Anthracite,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Phase 2 Card
        OperationsCard {
            Column(
                modifier = Modifier
                    .clickable { isPhase2Expanded = !isPhase2Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mois 2-4 : Repérage Passif",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Text(
                            text = "Focus : Stimulation calme et détection du seuil",
                            fontSize = 11.sp,
                            color = MediumGray
                        )
                    }
                    Icon(
                        imageVector = if (isPhase2Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand phase 2",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (isPhase2Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = LightGrayDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Application calme de la technique : Pratiquez la technique Stop-Start de manière très isolée et détendue.\n" +
                               "• Contrôle strict de l'intensité : Maintenez l'excitation à un niveau modéré (5-6/10 max). N'approchez pas le point de non-retour de manière abrupte.\n" +
                               "• Rééducation sensitive : Concentrez-vous sur les sensations physiques précises qui précèdent la montée d'excitation afin de développer une cartographie mentale précise de votre réponse corporelle.",
                        fontSize = 11.sp,
                        color = Anthracite,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Phase 3 Card
        OperationsCard {
            Column(
                modifier = Modifier
                    .clickable { isPhase3Expanded = !isPhase3Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mois 4-6 : Consolidation Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Text(
                            text = "Focus : Maîtrise des rythmes et contrôle souverain",
                            fontSize = 11.sp,
                            color = MediumGray
                        )
                    }
                    Icon(
                        imageVector = if (isPhase3Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand phase 3",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (isPhase3Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = LightGrayDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Descente sous pression : Apprenez à évacuer l'influx nerveux en pleine montée d'excitation en utilisant des contractions PC brèves et légères associées à un relâchement diaphragmatique total.\n" +
                               "• Variations de rythmes : Expérimentez des transitions lentes et dynamiques en restant sous le seuil d'éjaculation de manière stable et sereine.\n" +
                               "• Consolidation des 5-7 minutes : Ancrez l'habitude nerveuse d'une stabilité physique durable, libérée de l'angoisse de performance, pour un contrôle confortable, naturel et souverain.",
                        fontSize = 11.sp,
                        color = Anthracite,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun JournalSection(viewModel: OperationsViewModel, pastEntries: List<JournalEntry>) {
    var journalText by remember { mutableStateOf("") }
    var stressVal by remember { mutableFloatStateOf(5f) }
    var tensionVal by remember { mutableFloatStateOf(5f) }
    var motivationVal by remember { mutableFloatStateOf(5f) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Journal entry form
        OperationsCard(borderAccent = true) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Comment vous sentez-vous aujourd'hui ?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)

                OutlinedTextField(
                    value = journalText,
                    onValueChange = { journalText = it },
                    placeholder = { Text("Notez vos observations nerveuses, hormonales et physiques...", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("journal_notes_input")
                )

                // Stress slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Niveau de Stress", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                        Text("${stressVal.toInt()}/10", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = stressVal,
                        onValueChange = { stressVal = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                    )
                }

                // Tension slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tension Corporelle", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                        Text("${tensionVal.toInt()}/10", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = tensionVal,
                        onValueChange = { tensionVal = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                    )
                }

                // Motivation slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Motivation & Énergie", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                        Text("${motivationVal.toInt()}/10", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = motivationVal,
                        onValueChange = { motivationVal = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                    )
                }

                GoldGradientButton(
                    text = "Sauvegarder l'entrée",
                    onClick = {
                        viewModel.saveJournalEntry(
                            journalText,
                            stressVal.toInt(),
                            tensionVal.toInt(),
                            motivationVal.toInt()
                        )
                        journalText = ""
                        stressVal = 5f
                        tensionVal = 5f
                        motivationVal = 5f
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_journal_button")
                )
            }
        }

        // Past entries list
        Text("Entrées précédentes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
        if (pastEntries.isEmpty()) {
            Text("Aucune entrée enregistrée.", fontSize = 12.sp, color = MediumGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pastEntries.forEach { entry ->
                    OperationsCard {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = entry.date, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Stress: ${entry.stress}", fontSize = 10.sp, color = MediumGray)
                                    Text("Tension: ${entry.tension}", fontSize = 10.sp, color = MediumGray)
                                    Text("Motiv: ${entry.motivation}", fontSize = 10.sp, color = MediumGray)
                                }
                            }
                            if (entry.text.isNotEmpty()) {
                                Text(text = entry.text, fontSize = 12.sp, color = Anthracite)
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- SCREEN 7: SOMMEIL ---

@Composable
fun SommeilPage(viewModel: OperationsViewModel) {
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()

    var bedHour by remember { mutableStateOf("22") }
    var bedMin by remember { mutableStateOf("30") }
    var wakeHour by remember { mutableStateOf("07") }
    var wakeMin by remember { mutableStateOf("00") }
    var selectedQuality by remember { mutableStateOf(3) }

    val avgSleep = remember(sleepLogs) {
        if (sleepLogs.isNotEmpty()) sleepLogs.map { it.durationHours }.average() else 0.0
    }

    val magnesiumInsight = remember(sleepLogs, supplementLogs) {
        val withMag = mutableListOf<Float>()
        val withoutMag = mutableListOf<Float>()
        
        sleepLogs.forEach { sLog ->
            val supLog = supplementLogs.firstOrNull { it.date == sLog.date }
            if (supLog != null && supLog.magnesium) {
                withMag.add(sLog.durationHours)
            } else {
                withoutMag.add(sLog.durationHours)
            }
        }
        
        if (withMag.size >= 2 && withoutMag.size >= 2) {
            val avgWith = withMag.average()
            val avgWithout = withoutMag.average()
            val diff = avgWith - avgWithout
            
            val comparisonText = if (diff > 0) {
                "Vos nuits avec Magnésium durent en moyenne ${String.format("%.1f", diff)} h de plus. Cela valide l'impact du Magnésium sur l'activation du système nerveux parasympathique et la relaxation musculaire avant le coucher."
            } else if (diff < 0) {
                "Vos nuits avec Magnésium sont en moyenne ${String.format("%.1f", -diff)} h plus courtes, mais peuvent être plus denses. Observez votre sensation de fatigue au réveil pour valider l'impact."
            } else {
                "Aucune différence significative de durée constatée avec ou sans Magnésium pour le moment. La qualité subjective du sommeil reste le meilleur indicateur de récupération nerveuse."
            }
            
            Triple(avgWith, avgWithout, comparisonText)
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Sommeil",
            subtitle = "Restructuration biologique nocturne"
        )

        // Average stats card
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Moyenne de sommeil", fontSize = 11.sp, color = MediumGray)
                Text(
                    text = "${String.format("%.1f", avgSleep)} heures / nuit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Objectif recommandé : 7.5 - 8.0 heures", fontSize = 11.sp, color = MediumGray)
            }
        }

        // --- INSIGHT CARD ---
        if (magnesiumInsight != null) {
            val (avgWith, avgWithout, text) = magnesiumInsight
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Insight Sommeil",
                            tint = GoldClassic,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Insight Scientifique : Corrélation Magnésium",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = text,
                        fontSize = 12.sp,
                        color = MediumGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Avec Magnésium", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", avgWith)} h", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sans Magnésium", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", avgWithout)} h", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        }
                    }
                }
            }
        }

        // Add Sleep Log Form Card
        OperationsCard(borderAccent = true) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Enregistrer une nuit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Heure de Coucher", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = bedHour,
                                onValueChange = { if (it.length <= 2) bedHour = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("bed_hour_input")
                            )
                            Text(":", modifier = Modifier.padding(horizontal = 4.dp))
                            OutlinedTextField(
                                value = bedMin,
                                onValueChange = { if (it.length <= 2) bedMin = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Heure de Réveil", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = wakeHour,
                                onValueChange = { if (it.length <= 2) wakeHour = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("wake_hour_input")
                            )
                            Text(":", modifier = Modifier.padding(horizontal = 4.dp))
                            OutlinedTextField(
                                value = wakeMin,
                                onValueChange = { if (it.length <= 2) wakeMin = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Column {
                    Text("Qualité subjective du sommeil", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            val isFilled = star <= selectedQuality
                            IconButton(
                                onClick = { selectedQuality = star },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star Étoiles",
                                    tint = if (isFilled) GoldClassic else LightGrayDivider,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                GoldGradientButton(
                    text = "Valider la nuit",
                    onClick = {
                        val bedH = bedHour.toIntOrNull() ?: 22
                        val bedM = bedMin.toIntOrNull() ?: 30
                        val wakeH = wakeHour.toIntOrNull() ?: 7
                        val wakeM = wakeMin.toIntOrNull() ?: 0

                        // Calculate sleep duration float hours
                        var totalMin = (wakeH * 60 + wakeM) - (bedH * 60 + bedM)
                        if (totalMin < 0) {
                            totalMin += 24 * 60 // Crossover midnight
                        }
                        val finalHours = totalMin / 60f

                        viewModel.addSleepLog(
                            "$bedHour:$bedMin",
                            "$wakeHour:$wakeMin",
                            finalHours,
                            selectedQuality
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_sleep_button")
                )
            }
        }

        // Sleep chart using custom Canvas
        if (sleepLogs.isNotEmpty()) {
            Text("Graphique hebdomadaire", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
            OperationsCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val recentLogs = sleepLogs.take(7).asReversed()
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            val barCount = 7
                            val barWidth = 24.dp.toPx()
                            val spacing = (width - (barCount * barWidth)) / (barCount + 1)

                            // Draw goal horizontal dotted line (7.5 hours / 8.0 hours)
                            val goalY = height - (7.5f / 10f * height)
                            drawLine(
                                color = GoldClassic.copy(alpha = 0.5f),
                                start = Offset(0f, goalY),
                                end = Offset(width, goalY),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )

                            for (i in 0 until barCount) {
                                val log = recentLogs.getOrNull(i)
                                val x = spacing + i * (barWidth + spacing)
                                if (log != null) {
                                    val logH = log.durationHours
                                    val barHeight = (logH / 10f * height).coerceIn(0f, height)
                                    val y = height - barHeight

                                    drawRoundRect(
                                        color = GoldClassic,
                                        topLeft = Offset(x, y),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                                    )
                                } else {
                                    // Empty state bar
                                    drawRoundRect(
                                        color = LightGrayDivider,
                                        topLeft = Offset(x, height - 10.dp.toPx()),
                                        size = androidx.compose.ui.geometry.Size(barWidth, 10.dp.toPx()),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row of days
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val recentLogs = sleepLogs.take(7).asReversed()
                        for (i in 0 until 7) {
                            val log = recentLogs.getOrNull(i)
                            val label = if (log != null) viewModel.getDayOfWeekLabel(log.date) else ""
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MediumGray,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Historical list
        Text("Nuits précédentes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Anthracite)
        if (sleepLogs.isEmpty()) {
            Text("Aucune nuit enregistrée.", fontSize = 12.sp, color = MediumGray)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                sleepLogs.forEach { log ->
                    OperationsCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = log.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                                Text(text = "Coucher: ${log.bedtime} • Réveil: ${log.waketime}", fontSize = 11.sp, color = MediumGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    for (star in 1..5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (star <= log.quality) GoldClassic else LightGrayDivider.copy(alpha = 0.5f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${String.format("%.1f", log.durationHours)} h",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- SCREEN 8: PARAMÈTRES ---

@Composable
fun SettingsPage(viewModel: OperationsViewModel) {
    val apiKey by viewModel.geminiApiKey.collectAsState()
    val notifsEnabled by viewModel.notificationsEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val digestEnabled by viewModel.digestModeEnabled.collectAsState()

    var apiKeyInput by remember { mutableStateOf(apiKey) }
    var showApiKey by remember { mutableStateOf(false) }

    var showConfirmResetAll by remember { mutableStateOf(false) }

    LaunchedEffect(apiKey) {
        apiKeyInput = apiKey
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Paramètres",
            subtitle = "Configuration technique globale de votre tableau de bord"
        )

        // --- GEMINI KEY CARD ---
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Clé API Gemini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "La clé API Gemini est stockée localement dans les SharedPreferences de votre appareil Android. Elle n'est jamais transmise à un serveur tiers.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    label = { Text("Clé API Gemini", fontSize = 12.sp) },
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                    modifier = Modifier.fillMaxWidth().testTag("api_key_input")
                )

                GoldGradientButton(
                    text = "Enregistrer la clé",
                    onClick = { viewModel.updateSettings(apiKeyInput, notifsEnabled, soundEnabled, digestEnabled) },
                    modifier = Modifier.align(Alignment.End).testTag("save_settings_button")
                )
            }
        }

        // --- SÉCURITÉ CARD ---
        val appLockEnabled by viewModel.appLockEnabled.collectAsState()
        val fallbackPinHash by viewModel.fallbackPinHash.collectAsState()
        var showPinDialog by remember { mutableStateOf(false) }
        var tempPinInput by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf<String?>(null) }

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Sécurité et Verrouillage", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Verrouiller l'application", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Active la vérification biométrique ou par code PIN à l'ouverture de l'application.", fontSize = 11.sp, color = MediumGray)
                    }
                    Switch(
                        checked = appLockEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                if (fallbackPinHash.isEmpty()) {
                                    showPinDialog = true
                                } else {
                                    viewModel.setAppLockEnabled(true)
                                }
                            } else {
                                viewModel.setAppLockEnabled(false)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige),
                        modifier = Modifier.testTag("app_lock_switch")
                    )
                }

                if (fallbackPinHash.isNotEmpty()) {
                    Divider(color = LightGrayDivider)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Code PIN de secours", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Un code PIN est configuré comme moyen de secours.", fontSize = 11.sp, color = MediumGray)
                        }
                        TextButton(onClick = {
                            tempPinInput = ""
                            pinError = null
                            showPinDialog = true
                        }) {
                            Text("Modifier le PIN", color = GoldClassic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showPinDialog) {
            AlertDialog(
                onDismissRequest = {
                    showPinDialog = false
                    pinError = null
                },
                title = { Text("Définir le code PIN de secours") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Saisissez un code PIN à 4 chiffres pour déverrouiller l'application en cas d'échec biométrique.", fontSize = 12.sp, color = MediumGray)
                        OutlinedTextField(
                            value = tempPinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    tempPinInput = it
                                }
                            },
                            label = { Text("Code PIN (4 chiffres)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (pinError != null) {
                            Text(pinError!!, color = Color(0xFFC62828), fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (tempPinInput.length == 4) {
                            viewModel.setFallbackPin(tempPinInput)
                            viewModel.setAppLockEnabled(true)
                            showPinDialog = false
                            pinError = null
                        } else {
                            pinError = "Le code PIN doit contenir exactement 4 chiffres."
                        }
                    }) {
                        Text("Enregistrer", color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPinDialog = false
                        pinError = null
                    }) {
                        Text("Annuler")
                    }
                }
            )
        }

        // --- NOTIFICATIONS OPTIONS CARD ---
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Notifications et Alertes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Activer les notifications natives", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Permet de recevoir les rappels de compléments, Kegel et séances de sport.", fontSize = 11.sp, color = MediumGray)
                    }
                    Switch(
                        checked = notifsEnabled,
                        onCheckedChange = { viewModel.updateSettings(apiKeyInput, it, soundEnabled, digestEnabled) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige)
                    )
                }

                Divider(color = LightGrayDivider)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Effets sonores", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Joue un son discret lors des notifications.", fontSize = 11.sp, color = MediumGray)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { viewModel.updateSettings(apiKeyInput, notifsEnabled, it, digestEnabled) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige)
                    )
                }

                Divider(color = LightGrayDivider)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mode Digest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Regroupe les rappels de la même tranche horaire en une seule notification au lieu de plusieurs.", fontSize = 11.sp, color = MediumGray)
                    }
                    Switch(
                        checked = digestEnabled,
                        onCheckedChange = { viewModel.updateSettings(apiKeyInput, notifsEnabled, soundEnabled, it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige),
                        modifier = Modifier.testTag("digest_mode_switch")
                    )
                }
            }
        }

        // --- EXPORT / IMPORT CARD ---
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var showConfirmImport by remember { mutableStateOf(false) }
        var selectedImportUri by remember { mutableStateOf<android.net.Uri?>(null) }
        var infoMessage by remember { mutableStateOf<String?>(null) }

        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    try {
                        val jsonString = viewModel.exportData()
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.write(jsonString.toByteArray())
                        }
                        infoMessage = "Données exportées avec succès."
                    } catch (e: Exception) {
                        e.printStackTrace()
                        infoMessage = "Erreur lors de l'exportation : ${e.localizedMessage}"
                    }
                }
            }
        }

        val importLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                selectedImportUri = uri
                showConfirmImport = true
            }
        }

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Sauvegarde et Restauration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "Exportez l'intégralité de vos données locales sous format JSON pour les sauvegarder, ou importez un fichier de sauvegarde pour restaurer votre historique.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { exportLauncher.launch("directeur_operations_backup.json") },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("export_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = WhitePure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exporter JSON", color = WhitePure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { importLauncher.launch(arrayOf("application/json")) },
                        colors = ButtonDefaults.buttonColors(containerColor = Anthracite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("import_button")
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = WhitePure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Importer JSON", color = WhitePure, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (infoMessage != null) {
            AlertDialog(
                onDismissRequest = { infoMessage = null },
                title = { Text("Information") },
                text = { Text(infoMessage!!) },
                confirmButton = {
                    TextButton(onClick = { infoMessage = null }) {
                        Text("OK", color = GoldClassic)
                    }
                }
            )
        }

        if (showConfirmImport) {
            AlertDialog(
                onDismissRequest = { showConfirmImport = false },
                title = { Text("Confirmer l'importation") },
                text = { Text("Cette action remplacera toutes vos données actuelles. Êtes-vous sûr de vouloir continuer ?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmImport = false
                        selectedImportUri?.let { uri ->
                            coroutineScope.launch {
                                try {
                                    val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        inputStream.bufferedReader().use { it.readText() }
                                    }
                                    if (jsonString != null) {
                                        val success = viewModel.importData(jsonString)
                                        infoMessage = if (success) {
                                            "Données importées avec succès."
                                        } else {
                                            "Échec de l'importation : Format JSON invalide."
                                        }
                                    } else {
                                        infoMessage = "Impossible de lire le fichier de sauvegarde."
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    infoMessage = "Erreur lors de l'importation : ${e.localizedMessage}"
                                }
                            }
                        }
                    }) {
                        Text("Importer & Écraser", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmImport = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        // --- HISTORIQUE DES BILANS CARD ---
        val sharedPrefsWeekly = LocalContext.current.getSharedPreferences("directeur_ops_settings", Context.MODE_PRIVATE)
        val weeklyReportHistory = remember { sharedPrefsWeekly.getString("weekly_report_history", "") ?: "" }

        if (weeklyReportHistory.isNotEmpty()) {
            var isWeeklyExpanded by remember { mutableStateOf(false) }
            OperationsCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWeeklyExpanded = !isWeeklyExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Derniers Bilans Hebdomadaires", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                            Text("Consultez l'historique de vos bilans de performance.", fontSize = 11.sp, color = MediumGray)
                        }
                        Icon(
                            imageVector = if (isWeeklyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = GoldClassic
                        )
                    }

                    if (isWeeklyExpanded) {
                        Divider(color = LightGrayDivider)

                        val reports = weeklyReportHistory.split("\n\n===\n\n")
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            reports.forEach { report ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(LightBeige.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                                        .border(width = 0.5.dp, color = LightGrayDivider, shape = RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = report,
                                        fontSize = 11.sp,
                                        color = Anthracite,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- RESET DATA CARD ---
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Zone de Danger", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                Text(
                    text = "La réinitialisation supprimera définitivement toutes les données enregistrées dans votre base de données locale Room (tâches, historique de sport, sommeil, compléments et streaks). Cette opération est irréversible.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Button(
                    onClick = { showConfirmResetAll = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("full_reset_button")
                ) {
                    Text("Réinitialiser toutes les données", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showConfirmResetAll) {
            AlertDialog(
                onDismissRequest = { showConfirmResetAll = false },
                title = { Text("Confirmer la réinitialisation complète") },
                text = { Text("Êtes-vous absolument sûr de vouloir supprimer TOUTES vos données et préférences ? Vos historiques ne pourront plus être récupérés.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.fullResetData()
                        showConfirmResetAll = false
                    }) {
                        Text("Supprimer définitivement", color = Color(0xFFC62828))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmResetAll = false }) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}

@Composable
fun TestosteronePage(viewModel: OperationsViewModel) {
    val sunExposureLogs by viewModel.sunExposureLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()

    val todayStr = viewModel.getTodayDate()
    val todaySunLog = sunExposureLogs.firstOrNull { it.date == todayStr } ?: SunExposureLog(date = todayStr)

    val last7Days = remember {
        val list = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            list.add(sdf.format(c.time))
        }
        list
    }

    var selectedMinutes by remember { mutableStateOf(15) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Testostérone",
            subtitle = "Suivi des leviers biologiques de la vitalité"
        )

        // --- BANDEAU D'AVERTISSEMENT SCIENTIFIQUE ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBeige, RoundedCornerShape(10.dp))
                .border(width = 0.5.dp, color = LightGrayDivider, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Avertissement",
                    tint = GoldClassic,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Cette page suit tes habitudes de vie favorables à une production hormonale saine. Elle ne remplace pas une analyse médicale — seule une prise de sang (testostérone totale + libre, le matin à jeun) donne un chiffre réel.",
                    fontSize = 12.sp,
                    color = Anthracite,
                    lineHeight = 18.sp
                )
            }
        }

        // --- SECTION 1 : EXPOSITION SOLEIL MATINAL ---
        Text(
            text = "Exposition Solaire Matinale",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                if (todaySunLog.done) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(LightBeige, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Enregistré",
                                tint = GoldClassic,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Dose de soleil enregistrée pour aujourd'hui",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Text(
                                text = "Durée : ${todaySunLog.minutesExposed} minutes",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Enregistrer la dose de soleil d'aujourd'hui",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sélectionne la durée d'exposition :",
                        fontSize = 11.sp,
                        color = MediumGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val durationOptions = listOf(
                            "5-10 min" to 10,
                            "10-20 min" to 15,
                            "20-30 min" to 25,
                            "30 min+" to 45
                        )
                        durationOptions.forEach { (label, minutes) ->
                            val isSelected = selectedMinutes == minutes
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) LightBeige else Color(0xFFF9F9F9),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) GoldClassic else LightGrayDivider,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedMinutes = minutes }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GoldClassic else Anthracite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.logSunExposure(selectedMinutes) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "J'ai pris ma dose de soleil",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = LightGrayDivider)
                Spacer(modifier = Modifier.height(12.dp))

                // Grille historique 7 jours
                Text(
                    text = "Suivi des 7 derniers jours :",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7Days.forEach { date ->
                        val label = viewModel.getDayOfWeekLabel(date)
                        val dateLog = sunExposureLogs.firstOrNull { it.date == date }
                        val isDone = dateLog?.done == true
                        val minutes = dateLog?.minutesExposed ?: 0

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                color = if (date == todayStr) GoldClassic else MediumGray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (isDone) GoldClassic else Color(0xFFF0F0F0),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Text(
                                        text = "${minutes}m",
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(MediumGray.copy(alpha = 0.5f), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💡 La lumière naturelle du matin cale l'horloge circadienne, qui régule directement le pic matinal de testostérone et de cortisol.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
            }
        }

        // --- SECTION 2 : APERÇU DES LEVIERS DE LA SEMAINE ---
        Text(
            text = "Fiche Diagnostic Hebdomadaire",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        val indicators = remember(sleepLogs, gymSessions, journalEntries, sunExposureLogs, supplementLogs, last7Days) {
            // 1. Sleep avg
            val recentSleeps = sleepLogs.filter { it.date in last7Days }
            val avgSleep = if (recentSleeps.isNotEmpty()) recentSleeps.map { it.durationHours }.sum() / recentSleeps.size else 0f
            val sleepOk = avgSleep >= 7.0f

            // 2. Gym count
            val gymCount = gymSessions.count { it.date in last7Days }
            val gymOk = gymCount >= 3

            // 3. Stress avg
            val recentJournals = journalEntries.filter { it.date in last7Days }
            val avgStress = if (recentJournals.isNotEmpty()) recentJournals.map { it.stress }.sum().toFloat() / recentJournals.size else 0f
            val stressOk = avgStress <= 4f && recentJournals.isNotEmpty()

            // 4. Sun count
            val sunCount = sunExposureLogs.count { it.date in last7Days && it.done }
            val sunOk = sunCount >= 4

            // 5. Supps count (Vitamine D3 + Zinc)
            val recentSupps = supplementLogs.filter { it.date in last7Days }
            val vitD3Count = recentSupps.count { it.vitaminD3 }
            val zincCount = recentSupps.count { it.zinc }
            val suppsOk = vitD3Count >= 4 && zincCount >= 4

            listOf(
                IndicatorItem(
                    label = "Sommeil Moyen",
                    value = if (avgSleep > 0f) "${String.format("%.1f", avgSleep)} h / nuit" else "-- h",
                    desc = "Cible: ≥ 7.0 h",
                    isGood = sleepOk,
                    icon = Icons.Default.NightsStay
                ),
                IndicatorItem(
                    label = "Séances Muscu",
                    value = "$gymCount séances",
                    desc = "Cible: ≥ 3 par semaine",
                    isGood = gymOk,
                    icon = Icons.Default.FitnessCenter
                ),
                IndicatorItem(
                    label = "Niveau de Stress",
                    value = if (recentJournals.isNotEmpty()) "${String.format("%.1f", avgStress)} / 10" else "Pas de journal",
                    desc = "Cible: ≤ 4.0 / 10",
                    isGood = stressOk,
                    icon = Icons.Default.Warning
                ),
                IndicatorItem(
                    label = "Exposition Solaire",
                    value = "$sunCount / 7 jours",
                    desc = "Cible: ≥ 4 jours",
                    isGood = sunOk,
                    icon = Icons.Default.WbSunny
                ),
                IndicatorItem(
                    label = "Vitamine D3 + Zinc",
                    value = "D3: $vitD3Count • Zinc: $zincCount",
                    desc = "Cible: ≥ 4 fois chacun",
                    isGood = suppsOk,
                    icon = Icons.Default.LocalPharmacy
                )
            )
        }

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Aperçu de la semaine",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )

                indicators.forEach { indicator ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(LightBeige, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = indicator.icon,
                                    contentDescription = indicator.label,
                                    tint = GoldClassic,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = indicator.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Anthracite
                                )
                                Text(
                                    text = indicator.desc,
                                    fontSize = 10.sp,
                                    color = MediumGray
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = indicator.value,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (indicator.isGood) LightBeige else Color(0xFFF2F2F2),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (indicator.isGood) "Optimal" else "À améliorer",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (indicator.isGood) GoldClassic else MediumGray
                                )
                            }
                        }
                    }
                    if (indicator != indicators.last()) {
                        Divider(color = LightGrayDivider)
                    }
                }
            }
        }

        // --- SECTION 3 : LES 6 LEVIERS NATURELS (EDUCATIVE ACCORDION) ---
        Text(
            text = "Les 6 leviers d'optimisation naturelle",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        val leviers = remember {
            listOf(
                LevierItem(
                    title = "1. Lumière naturelle du matin",
                    content = "Une exposition à la lumière directe du soleil pendant 10 à 30 minutes au réveil cale l'horloge biologique circadienne. Cela déclenche l'arrêt de la mélatonine, régule le pic de cortisol sain du matin et favorise la libération nocturne adéquate de l'hormone lutéinisante (LH) qui signale la production de testostérone."
                ),
                LevierItem(
                    title = "2. Sommeil suffisant (7-9h)",
                    content = "La majorité de la testostérone est synthétisée durant le sommeil paradoxal (REM). Une seule semaine de restriction de sommeil à 5 heures par nuit peut faire chuter le taux de testostérone de 10 à 15 % chez les hommes sains."
                ),
                LevierItem(
                    title = "3. Entraînement de force & Mouvements composés",
                    content = "Les séances de musculation intenses (durée < 60 min) stimulant d'importantes masses musculaires (squats, soulevé de terre, tractions, développés) provoquent une réponse hormonale aiguë favorable à la testostérone et à l'hormone de croissance."
                ),
                LevierItem(
                    title = "4. Contrôle du stress et cortisol",
                    content = "Le stress chronique sécrète du cortisol. Or, le cortisol et la testostérone sont engagés dans une relation inversement proportionnelle : un taux élevé de cortisol bloque les enzymes clés de la synthèse de la testostérone dans les cellules de Leydig."
                ),
                LevierItem(
                    title = "5. Apport calorique et lipidique suffisant",
                    content = "Le cholestérol est le précurseur biochimique indispensable de toutes les hormones stéroïdiennes, dont la testostérone. Les régimes extrêmement pauvres en lipides (< 20% des calories) ou restrictifs chroniquement sapent la production hormonale."
                ),
                LevierItem(
                    title = "6. Correction des carences en micronutriments",
                    content = "La vitamine D3, le zinc et le magnésium sont des cofacteurs essentiels. Les études montrent que la supplémentation de ces micronutriments chez les sujets carencés restaure des taux physiologiques sains de testostérone libre et totale."
                )
            )
        }

        leviers.forEach { levier ->
            var expanded by remember { mutableStateOf(false) }
            OperationsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = levier.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Réduire" else "Développer",
                            tint = GoldClassic,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = levier.content,
                            fontSize = 11.sp,
                            color = MediumGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class IndicatorItem(
    val label: String,
    val value: String,
    val desc: String,
    val isGood: Boolean,
    val icon: ImageVector
)

data class LevierItem(
    val title: String,
    val content: String
)
