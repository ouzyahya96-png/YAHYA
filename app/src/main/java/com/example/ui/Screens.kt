package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
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
    borderBrush: Brush? = null,
    borderWidth: androidx.compose.ui.unit.Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        shape = RoundedCornerShape(16.dp),
        border = if (borderBrush != null) {
            BorderStroke(borderWidth, borderBrush)
        } else {
            BorderStroke(
                width = if (borderAccent) 1.5.dp else 1.dp,
                color = if (borderAccent) GoldClassic else LightGrayDivider
            )
        },
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


data class AppPageInfo(
    val title: String,
    val index: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val category: String
)


// --- SCREEN 1: DASHBOARD ---

@Composable
fun DashboardPage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    val showAlertsOption by viewModel.showAlerts.collectAsState()
    val showCountdownOption by viewModel.showCountdown.collectAsState()
    val showWeeklyPreviewOption by viewModel.showWeeklyPreview.collectAsState()
    val showAffirmationOption by viewModel.showAffirmation.collectAsState()
    val showGratitudeOption by viewModel.showGratitude.collectAsState()
    val showFavoritesOption by viewModel.showFavorites.collectAsState()
    val showSecondaryGridOption by viewModel.showSecondaryGrid.collectAsState()
    val showGeminiAnalysisOption by viewModel.showGeminiAnalysis.collectAsState()

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val adaptiveGradient = remember(currentHour) { com.example.ui.theme.getTimeAdaptiveGradient(currentHour) }
    val haptic = LocalHapticFeedback.current

    val tasks by viewModel.tasks.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val dailyAffirmation by viewModel.dailyAffirmation.collectAsState()

    val restDays by viewModel.restDays.collectAsState()
    val dailyWins by viewModel.dailyWins.collectAsState()
    val whyStatement by viewModel.whyStatement.collectAsState()

    val chantiers by viewModel.chantiers.collectAsState()
    val allMilestones by viewModel.allMilestones.collectAsState()
    val pelvicTensionChecks by viewModel.pelvicTensionChecks.collectAsState()
    val goalName by viewModel.goalName.collectAsState()
    val goalTargetDate by viewModel.goalTargetDate.collectAsState()

    val geminiAnalysis by viewModel.geminiAnalysis.collectAsState()
    val geminiAnalysisDate by viewModel.geminiAnalysisDate.collectAsState()
    val isAnalysisOffline by viewModel.isAnalysisOffline.collectAsState()
    val isLoadingAnalysis by viewModel.isLoadingAnalysis.collectAsState()
    val analysisError by viewModel.analysisError.collectAsState()
    val hasApiKey = viewModel.geminiApiKey.collectAsState().value.isNotEmpty()

    // Additional collected states for DailyRitualAggregator
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val breathingSessions by viewModel.breathingSessions.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val sunExposureLogs by viewModel.sunExposureLogs.collectAsState()
    val communicationPracticeLogs by viewModel.communicationPracticeLogs.collectAsState()
    val delayTrainingLogs by viewModel.delayTrainingLogs.collectAsState()
    val cardioHealthLogs by viewModel.cardioHealthLogs.collectAsState()
    val morningErectionLogs by viewModel.morningErectionLogs.collectAsState()
    val gratitudeLogs by viewModel.gratitudeLogs.collectAsState()

    val todayStr = viewModel.getTodayDate()
    val isRestDayActive = restDays.any { it.date == todayStr && it.active }

    val randomWin = remember(dailyWins) {
        val recentWins = dailyWins.sortedByDescending { it.date }.take(10)
        if (recentWins.isNotEmpty()) recentWins.random() else null
    }

    val remainingTasks = tasks.filter { it.date == todayStr && !it.done }
    val nextTask = remainingTasks.firstOrNull()

    val todayGymSession = gymSessions.firstOrNull { it.date == todayStr }

    val todaySuppLog = supplementLogs.firstOrNull { it.date == todayStr } ?: SupplementLog(date = todayStr)

    val lastNightSleep = sleepLogs.firstOrNull() // Chronological desc, so first is most recent

    // Compute ritual completion
    val ritualPlan = remember(
        todayStr, tasks, gymSessions, supplementLogs, kegelLogs, breathingSessions,
        journalEntries, sleepLogs, sunExposureLogs, communicationPracticeLogs,
        delayTrainingLogs, cardioHealthLogs, morningErectionLogs, dailyWins,
        gratitudeLogs, dailyAffirmation
    ) {
        com.example.data.DailyRitualAggregator.buildTodayRitual(viewModel, todayStr)
    }

    // Deterministic daily items
    val deterministicAffirmation = remember(todayStr) {
        com.example.data.AffirmationsData.getDailyDeterministicItem(com.example.data.AffirmationsData.affirmations, todayStr, 0)
    }
    val deterministicConfidence = remember(todayStr) {
        com.example.data.AffirmationsData.getDailyDeterministicItem(com.example.data.AffirmationsData.confidenceStatements, todayStr, 1)
    }

    // Greeting calculations
    val greeting = remember {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            currentHour in 5..11 -> "Bonjour"
            currentHour in 12..17 -> "Bon après-midi"
            else -> "Bonsoir"
        }
    }
    val formattedDate = remember {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(todayStr) ?: Date()
            SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).format(Date())
        }
    }

    // Gratitude States
    val todayGratitude = remember(gratitudeLogs, todayStr) {
        gratitudeLogs.find { it.date == todayStr }
    }
    val isGratitudeFilled = todayGratitude != null &&
            todayGratitude.gratitude1.isNotEmpty() &&
            todayGratitude.gratitude2.isNotEmpty() &&
            todayGratitude.gratitude3.isNotEmpty()

    var g1 by remember { mutableStateOf("") }
    var g2 by remember { mutableStateOf("") }
    var g3 by remember { mutableStateOf("") }
    var showGratitudeAnimation by remember { mutableStateOf(false) }

    // Initialize inputs when gratitude is saved or changed
    LaunchedEffect(todayGratitude) {
        if (todayGratitude != null) {
            g1 = todayGratitude.gratitude1
            g2 = todayGratitude.gratitude2
            g3 = todayGratitude.gratitude3
        } else {
            g1 = ""
            g2 = ""
            g3 = ""
        }
    }

    // Favorite Pages States
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("dashboard_prefs", android.content.Context.MODE_PRIVATE) }
    var favoriteIndices by remember {
        val saved = sharedPrefs.getString("dashboard_favorite_pages", null)
        val parsed = if (saved != null) {
            saved.split(",").mapNotNull { it.toIntOrNull() }
        } else {
            listOf(1, 2, 3, 4, 5, 15) // Defaults: Todo List, Calendrier, Compléments, GYM, Récupération, Chantiers
        }
        mutableStateOf(parsed)
    }

    var showFavoritesDialog by remember { mutableStateOf(false) }
    var tempFavorites by remember { mutableStateOf(favoriteIndices) }
    var showAllPages by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // List of all pages with corresponding category and icons
    val allPagesList = remember {
        listOf(
            // Santé & Récupération
            AppPageInfo("Compléments", 3, Icons.Filled.LocalPharmacy, "Santé & Récupération"),
            AppPageInfo("GYM", 4, Icons.Filled.FitnessCenter, "Santé & Récupération"),
            AppPageInfo("Récupération", 5, Icons.Filled.FlashOn, "Santé & Récupération"),
            AppPageInfo("Testostérone", 6, Icons.Filled.WbSunny, "Santé & Récupération"),
            AppPageInfo("Sommeil", 8, Icons.Filled.NightsStay, "Santé & Récupération"),

            // Croissance & Esprit
            AppPageInfo("Communication", 7, Icons.Filled.Forum, "Croissance & Esprit"),
            AppPageInfo("Leadership", 14, Icons.Filled.Groups, "Croissance & Esprit"),
            AppPageInfo("Neurosciences", 13, Icons.Filled.Psychology, "Croissance & Esprit"),
            AppPageInfo("Affirmations", 12, Icons.Filled.SelfImprovement, "Croissance & Esprit"),
            AppPageInfo("Pourquoi", 11, Icons.Filled.Favorite, "Croissance & Esprit"),

            // Organisation
            AppPageInfo("To-Do List", 1, Icons.Filled.CheckCircle, "Organisation"),
            AppPageInfo("Calendrier", 2, Icons.Filled.DateRange, "Organisation"),
            AppPageInfo("Rituel", 16, Icons.Filled.Checklist, "Organisation"),
            AppPageInfo("Mes Streaks", 17, Icons.Filled.LocalFireDepartment, "Organisation"),

            // Professionnel & Système
            AppPageInfo("Chantiers", 15, Icons.Filled.Construction, "Professionnel & Système"),
            AppPageInfo("Survive The Great Reset", 9, Icons.Filled.Inventory2, "Professionnel & Système"),
            AppPageInfo("Paramètres", 10, Icons.Filled.Settings, "Professionnel & Système")
        )
    }

    var dismissedAlertsToday by remember(todayStr) {
        val dismissedStr = sharedPrefs.getString("dismissed_alerts_$todayStr", "") ?: ""
        mutableStateOf(dismissedStr.split(",").filter { it.isNotBlank() }.toSet())
    }

    val activeAlerts = remember(chantiers, allMilestones, pelvicTensionChecks, supplementLogs, kegelLogs, dismissedAlertsToday) {
        com.example.data.PriorityAlertsEngine.getTopAlerts(viewModel)
            .filter { it.alertId !in dismissedAlertsToday }
            .take(2)
    }

    val daysRemaining = remember(todayStr, goalTargetDate) {
        if (goalTargetDate.isBlank()) return@remember null
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayVal = sdf.parse(todayStr)
            val targetVal = sdf.parse(goalTargetDate)
            if (todayVal != null && targetVal != null) {
                val diffMs = targetVal.time - todayVal.time
                (diffMs / (1000 * 60 * 60 * 24)).toInt()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    var showStreakMenu by remember { mutableStateOf(false) }
    var showSleepQuickDialog by remember { mutableStateOf(false) }
    var quickBedtime by remember(lastNightSleep) { mutableStateOf(lastNightSleep?.bedtime ?: "22:30") }
    var quickWaketime by remember(lastNightSleep) { mutableStateOf(lastNightSleep?.waketime ?: "07:00") }

    fun calculateSleepDuration(bedtime: String, waketime: String): Float {
        try {
            val bedParts = bedtime.split(":")
            val wakeParts = waketime.split(":")
            if (bedParts.size == 2 && wakeParts.size == 2) {
                val bedH = bedParts[0].toIntOrNull() ?: 22
                val bedM = bedParts[1].toIntOrNull() ?: 30
                val wakeH = wakeParts[0].toIntOrNull() ?: 7
                val wakeM = wakeParts[1].toIntOrNull() ?: 0
                
                var totalMin = (wakeH * 60 + wakeM) - (bedH * 60 + bedM)
                if (totalMin < 0) {
                    totalMin += 24 * 60
                }
                return totalMin / 60f
            }
        } catch (e: Exception) {}
        return 8f
    }

    if (showSleepQuickDialog) {
        AlertDialog(
            onDismissRequest = { showSleepQuickDialog = false },
            title = {
                Text(
                    text = "Sommeil de la nuit dernière 🌙",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Saisissez rapidement vos horaires de coucher et de réveil pour mettre à jour vos statistiques.",
                        fontSize = 11.sp,
                        color = MediumGray,
                        lineHeight = 16.sp
                    )
                    
                    OutlinedTextField(
                        value = quickBedtime,
                        onValueChange = { quickBedtime = it },
                        label = { Text("Heure de coucher (ex: 22:30)", fontSize = 12.sp) },
                        placeholder = { Text("hh:mm", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                        modifier = Modifier.fillMaxWidth().testTag("quick_sleep_bedtime_input")
                    )

                    OutlinedTextField(
                        value = quickWaketime,
                        onValueChange = { quickWaketime = it },
                        label = { Text("Heure de réveil (ex: 07:00)", fontSize = 12.sp) },
                        placeholder = { Text("hh:mm", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                        modifier = Modifier.fillMaxWidth().testTag("quick_sleep_waketime_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalHours = calculateSleepDuration(quickBedtime, quickWaketime)
                        viewModel.addSleepLog(
                            bedtime = quickBedtime,
                            waketime = quickWaketime,
                            durationHours = finalHours,
                            quality = 3,
                            stretchingDone = false,
                            screensOffBeforeBed = false
                        )
                        showSleepQuickDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = GoldClassic)
                ) {
                    Text("Enregistrer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSleepQuickDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // a) En-tête + Pourquoi button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageHeader(
                    title = greeting,
                    subtitle = formattedDate
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { onNavigateToPage(11) },
                        colors = ButtonDefaults.textButtonColors(contentColor = GoldClassic)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Pourquoi",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mon Pourquoi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = { onNavigateToPage(10) },
                        modifier = Modifier.size(36.dp).testTag("dashboard_customize_quick_access")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Personnaliser",
                            tint = MediumGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Priority Alerts Banner (up to 2 alerts)
        if (showAlertsOption && activeAlerts.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activeAlerts.forEach { alert ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF8E1))
                                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Alert",
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alert.text,
                                        fontSize = 12.sp,
                                        color = Color(0xFF5D4037),
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = { onNavigateToPage(alert.sourcePageIndex) },
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF57F17)),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Voir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        val newDismissed = dismissedAlertsToday + alert.alertId
                                        sharedPrefs.edit().putString("dismissed_alerts_$todayStr", newDismissed.joinToString(",")).apply()
                                        dismissedAlertsToday = newDismissed
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Fermer",
                                        tint = Color(0xFF8D6E63),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // b) Ligne Hero (Streak de récupération + Sommeil de la nuit dernière)
        item {
            val currentRecoveryStreak = viewModel.calculateCurrentStreak()
            var isStreakPressed by remember { mutableStateOf(false) }
            val streakScale by animateFloatAsState(targetValue = if (isStreakPressed) 0.95f else 1f)
            var showStreakMenu by remember { mutableStateOf(false) }

            var isSleepPressed by remember { mutableStateOf(false) }
            val sleepScale by animateFloatAsState(targetValue = if (isSleepPressed) 0.95f else 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recovery Streak
                Box(modifier = Modifier.weight(1f)) {
                    OperationsCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = streakScale
                                scaleY = streakScale
                            }
                            .pointerInput(todayStr) {
                                detectTapGestures(
                                    onPress = {
                                        try {
                                            isStreakPressed = true
                                            awaitRelease()
                                        } finally {
                                            isStreakPressed = false
                                        }
                                    },
                                    onTap = { onNavigateToPage(5) },
                                    onLongPress = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showStreakMenu = true
                                    }
                                )
                            },
                        borderBrush = adaptiveGradient,
                        borderWidth = 2.dp
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

                    DropdownMenu(
                        expanded = showStreakMenu,
                        onDismissRequest = { showStreakMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isRestDayActive) "Désactiver Mode Pause" else "Activer Mode Pause aujourd'hui") },
                            onClick = {
                                viewModel.toggleRestDay(todayStr)
                                showStreakMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GoldClassic,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Last Night's Sleep
                OperationsCard(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = sleepScale
                            scaleY = sleepScale
                        }
                        .pointerInput(todayStr) {
                            detectTapGestures(
                                onPress = {
                                    try {
                                        isSleepPressed = true
                                        awaitRelease()
                                    } finally {
                                        isSleepPressed = false
                                    }
                                },
                                onTap = { onNavigateToPage(8) },
                                onLongPress = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showSleepQuickDialog = true
                                }
                            )
                        },
                    borderBrush = adaptiveGradient,
                    borderWidth = 2.dp
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

        // Countdown to Goal Personal Objective
        if (showCountdownOption) {
            item {
                OperationsCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderBrush = adaptiveGradient,
                    borderWidth = 2.dp
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (daysRemaining != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AnimatedCountText(
                                value = maxOf(0, daysRemaining),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldClassic
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (daysRemaining == 1 || daysRemaining == 0) "jour" else "jours",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "avant $goalName",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MediumGray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "Configure ton objectif dans Paramètres pour voir ton compte à rebours ici.",
                            fontSize = 12.sp,
                            color = MediumGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { onNavigateToPage(10) },
                            colors = ButtonDefaults.textButtonColors(contentColor = GoldClassic)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Paramètres",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Configurer maintenant", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

        // c) Bannière Rituel
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(adaptiveGradient)
                    .clickable { onNavigateToPage(16) }
                    .padding(16.dp)
                    .testTag("dashboard_view_ritual_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Voir mon Rituel du jour 🕊️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Progression actuelle : ${ritualPlan.completionPercent}% complété",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Voir le Rituel",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // d) Aperçu de la semaine
        if (showWeeklyPreviewOption) {
            item {
                val todayWeekDays = remember(todayStr) {
                val list = mutableListOf<Date>()
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                for (i in 0..6) {
                    list.add(cal.time)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                list
            }

            val sdfDb = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Aperçu de la semaine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    todayWeekDays.forEach { date ->
                        val dateStr = sdfDb.format(date)
                        val dayLabel = SimpleDateFormat("EEE", Locale.FRANCE).format(date).replaceFirstChar { it.uppercase() }
                        val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
                        val isToday = dateStr == todayStr

                        val tasksCount = tasks.count { it.date == dateStr }
                        val gymCount = gymSessions.count { it.date == dateStr }
                        val milestonesCount = allMilestones.count { it.targetDate == dateStr && !it.completed }

                        OperationsCard(
                            modifier = Modifier
                                .width(88.dp)
                                .clickable {
                                    viewModel.selectCalendarDate(dateStr)
                                    viewModel.setCalendarViewMode("Jour")
                                    onNavigateToPage(2)
                                },
                            borderBrush = if (isToday) GradientTokens.sunsetHorizontal else null,
                            borderWidth = if (isToday) 1.5.dp else 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isToday) LightBeige.copy(alpha = 0.3f) else Color.Transparent)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = dayLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) GoldClassic else MediumGray
                                )
                                Text(
                                    text = dayNum,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Anthracite
                                )

                                Divider(color = LightGrayDivider.copy(alpha = 0.5f))

                                // Density indicators
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    // Tasks Indicator
                                    if (tasksCount > 0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Tâches",
                                                tint = GoldClassic,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "$tasksCount",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Anthracite
                                            )
                                        }
                                    }

                                    // Gym Indicator
                                    if (gymCount > 0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = "GYM",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "$gymCount",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Anthracite
                                            )
                                        }
                                    }

                                    // Milestones Indicator
                                    if (milestonesCount > 0) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Flag,
                                                contentDescription = "Jalons",
                                                tint = Color(0xFFC62828),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Text(
                                                text = "$milestonesCount",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Anthracite
                                            )
                                        }
                                    }

                                    // Placeholder if empty
                                    if (tasksCount == 0 && gymCount == 0 && milestonesCount == 0) {
                                        Text(
                                            text = "Vide",
                                            fontSize = 9.sp,
                                            color = MediumGray.copy(alpha = 0.6f),
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        // MODE JOUR DE PAUSE TOGGLE & BANNER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Rest Day",
                        tint = if (isRestDayActive) GoldClassic else MediumGray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aujourd'hui je souffle 🕊️",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Anthracite
                    )
                }
                Switch(
                    checked = isRestDayActive,
                    onCheckedChange = { viewModel.toggleRestDay(todayStr) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GoldClassic,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    ),
                    modifier = Modifier.testTag("rest_day_switch")
                )
            }
        }

        if (isRestDayActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBeige, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Jour de pause activé — repose-toi, tout reprend normalement demain.",
                        fontSize = 12.sp,
                        color = GoldClassic,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // d) Carte double — Affirmation & Confiance du jour
        if (showAffirmationOption) {
            item {
                OperationsCard(borderBrush = adaptiveGradient, borderWidth = 1.5.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                    // General Affirmation Block
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Affirmation",
                                tint = GoldClassic,
                                modifier = Modifier.size(14.dp)
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
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "« $deterministicAffirmation »",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = Anthracite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Divider(color = LightGrayDivider, thickness = 1.dp)

                    // Confidence Block
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelfImprovement,
                                contentDescription = "Confiance",
                                tint = GoldClassic,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CONFIANCE EN SOI DU JOUR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "« $deterministicConfidence »",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = Anthracite,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }

        // e) Gratitude rapide
        if (showGratitudeOption) {
            item {
                OperationsCard(borderAccent = false) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Gratitude",
                                    tint = GoldClassic,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gratitude du jour ✨",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Anthracite
                                )
                            }
                            
                            if (isGratitudeFilled) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Rempli",
                                    tint = GoldClassic,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (isGratitudeFilled) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AnimatedVisibility(
                                    visible = showGratitudeAnimation || isGratitudeFilled,
                                    enter = fadeIn() + expandVertically()
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(g1, fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Medium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(g2, fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Medium)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(g3, fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                                
                                if (showGratitudeAnimation) {
                                    Text(
                                        text = "Félicitations pour vos gratitudes quotidiennes ! 🎉",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldClassic,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = g1,
                                    onValueChange = { g1 = it },
                                    placeholder = { Text("1. Première gratitude...", fontSize = 11.sp, color = MediumGray) },
                                    modifier = Modifier.fillMaxWidth().testTag("fast_gratitude_1"),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = GoldClassic,
                                        unfocusedIndicatorColor = LightGrayDivider
                                    )
                                )
                                OutlinedTextField(
                                    value = g2,
                                    onValueChange = { g2 = it },
                                    placeholder = { Text("2. Deuxième gratitude...", fontSize = 11.sp, color = MediumGray) },
                                    modifier = Modifier.fillMaxWidth().testTag("fast_gratitude_2"),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = GoldClassic,
                                        unfocusedIndicatorColor = LightGrayDivider
                                    )
                                )
                                OutlinedTextField(
                                    value = g3,
                                    onValueChange = { g3 = it },
                                    placeholder = { Text("3. Troisième gratitude...", fontSize = 11.sp, color = MediumGray) },
                                    modifier = Modifier.fillMaxWidth().testTag("fast_gratitude_3"),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = GoldClassic,
                                        unfocusedIndicatorColor = LightGrayDivider
                                    )
                                )

                                Button(
                                    onClick = {
                                        if (g1.isNotEmpty() && g2.isNotEmpty() && g3.isNotEmpty()) {
                                            viewModel.saveGratitude(todayStr, g1, g2, g3)
                                            showGratitudeAnimation = true
                                        }
                                    },
                                    enabled = g1.isNotEmpty() && g2.isNotEmpty() && g3.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("save_fast_gratitude"),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldClassic, disabledContainerColor = LightGrayDivider),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Enregistrer mes gratitudes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // f) Accès Rapide — Favoris + Toutes les pages
        if (showFavoritesOption) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Favorites header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mes Raccourcis (Appui long pour éditer) 📌",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        
                        IconButton(onClick = {
                            tempFavorites = favoriteIndices
                            showFavoritesDialog = true
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GoldClassic, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Favorites Horizontal Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(favoriteIndices) { pageIdx ->
                            val pageInfo = allPagesList.find { it.index == pageIdx }
                            if (pageInfo != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(68.dp)
                                        .pointerInput(pageIdx) {
                                            detectTapGestures(
                                                onTap = { onNavigateToPage(pageInfo.index) },
                                                onLongPress = {
                                                    tempFavorites = favoriteIndices
                                                    showFavoritesDialog = true
                                                }
                                            )
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(LightBeige, CircleShape)
                                            .border(1.dp, GoldClassic.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = pageInfo.icon,
                                            contentDescription = pageInfo.title,
                                            tint = GoldClassic,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pageInfo.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Anthracite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Toggle Collapsible Section Button
                    TextButton(
                        onClick = { showAllPages = !showAllPages },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = GoldClassic)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (showAllPages) "Masquer toutes les pages (18)" else "Voir toutes les pages (18)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (showAllPages) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Collapsible view
                    AnimatedVisibility(
                        visible = showAllPages,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGrayBg.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Search field
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Rechercher une page...", fontSize = 12.sp, color = MediumGray) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MediumGray, modifier = Modifier.size(18.dp)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = GoldClassic,
                                    unfocusedIndicatorColor = LightGrayDivider
                                )
                            )

                            // Categorized lists
                            val categories = listOf("Santé & Récupération", "Croissance & Esprit", "Organisation", "Professionnel & Système")
                            val filteredPages = allPagesList.filter {
                                searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true)
                            }

                            categories.forEach { cat ->
                                val pagesInCat = filteredPages.filter { it.category == cat }
                                if (pagesInCat.isNotEmpty()) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldClassic,
                                            letterSpacing = 0.5.sp,
                                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                                        )
                                        pagesInCat.chunked(2).forEach { rowItems ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowItems.forEach { page ->
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color.White)
                                                            .border(1.dp, LightGrayDivider, RoundedCornerShape(8.dp))
                                                            .clickable { onNavigateToPage(page.index) }
                                                            .padding(10.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = page.icon,
                                                                contentDescription = page.title,
                                                                tint = GoldClassic,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Text(
                                                                text = page.title,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = Anthracite,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                                if (rowItems.count() < 2) {
                                                    Spacer(modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // g) Grille secondaire d'informations du jour (Tâches restantes, séance GYM, compléments, sommeil)
        if (showSecondaryGridOption) {
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
                            GradientLinearProgressIndicator(
                                progress = progressPct,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                trackColor = LightGrayDivider,
                                shape = RoundedCornerShape(2.dp)
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
                                    PremiumCheckbox(
                                        checked = taken,
                                        onCheckedChange = { viewModel.toggleSupplement(key, it) }
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
                                    GradientLinearProgressIndicator(
                                        progress = pct,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp),
                                        trackColor = LightGrayDivider,
                                        shape = RoundedCornerShape(2.dp)
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

        // --- ENCART VICTOIRE ALÉATOIRE ---
        if (randomWin != null) {
            item {
                OperationsCard(borderAccent = false) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Victoire Récente 🏆",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "« ${randomWin.winText} »",
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Anthracite,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // h) Analyse Gemini du jour
        if (showGeminiAnalysisOption) {
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                SkeletonLoader(height = 18.dp)
                                SkeletonLoader(height = 14.dp)
                                SkeletonLoader(height = 14.dp)
                                SkeletonLoader(height = 14.dp)
                            }
                        } else if (analysisError == "OFFLINE_NO_CACHE") {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "Offline",
                                    tint = MediumGray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "Connecte-toi à internet pour recevoir ta première analyse personnalisée.",
                                    fontSize = 13.sp,
                                    color = MediumGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (!analysisError.isNullOrEmpty()) {
                            Text(
                                text = "Un problème est survenu lors de l'analyse quotidienne. Veuillez réessayer plus tard.",
                                fontSize = 13.sp,
                                color = Color(0xFFC62828)
                            )
                        } else if (geminiAnalysis.isNotEmpty()) {
                            Column {
                                if (isAnalysisOffline) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WifiOff,
                                            contentDescription = "Offline",
                                            tint = MediumGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hors ligne — dernière analyse du $geminiAnalysisDate",
                                            fontSize = 11.sp,
                                            color = MediumGray,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Text(
                                    text = geminiAnalysis,
                                    fontSize = 14.sp,
                                    color = Anthracite,
                                    lineHeight = 20.sp
                                )
                            }
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
        }
    }

    // Favorites Customization Dialog
    if (showFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showFavoritesDialog = false },
            title = { Text("Personnaliser mes raccourcis", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Anthracite) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Sélectionnez jusqu'à 6 pages favorites à épingler en accès rapide :", fontSize = 12.sp, color = MediumGray)
                    allPagesList.forEach { page ->
                        val isSelected = tempFavorites.contains(page.index)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) {
                                        tempFavorites = tempFavorites - page.index
                                    } else {
                                        if (tempFavorites.size < 6) {
                                            tempFavorites = tempFavorites + page.index
                                        }
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = page.icon, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(18.dp))
                                Text(text = page.title, fontSize = 13.sp, color = Anthracite)
                            }
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        tempFavorites = tempFavorites - page.index
                                    } else {
                                        if (tempFavorites.size < 6) {
                                            tempFavorites = tempFavorites + page.index
                                        }
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = GoldClassic)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        favoriteIndices = tempFavorites
                        val savedString = tempFavorites.joinToString(",")
                        sharedPrefs.edit().putString("dashboard_favorite_pages", savedString).apply()
                        showFavoritesDialog = false
                    }
                ) {
                    Text("Valider", fontWeight = FontWeight.Bold, color = GoldClassic)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFavoritesDialog = false }) {
                    Text("Annuler", color = MediumGray)
                }
            }
        )
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
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GradientTokens.sunsetVertical)
                    .clickable { showAddDialog = true }
                    .testTag("add_task_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
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
        PremiumCheckbox(
            checked = task.done,
            onCheckedChange = { onToggle() }
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

    val viewMode by viewModel.calendarViewMode.collectAsState()
    val selectedDayStr by viewModel.selectedCalendarDate.collectAsState()

    val calendar = remember { Calendar.getInstance() }
    var currentWeekStart by remember { mutableStateOf(Date()) }

    // Align currentWeekStart with selectedDayStr
    LaunchedEffect(selectedDayStr) {
        try {
            val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDayStr) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = dateObj
            cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            currentWeekStart = cal.time
        } catch (e: Exception) {}
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
                            .clickable { viewModel.setCalendarViewMode(mode) }
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
                            viewModel.selectCalendarDate(dateStr)
                            viewModel.setCalendarViewMode("Jour")
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
                        val dateStr = sdfDb.format(date)
                        val dayLabel = viewModel.getDayOfWeekLabel(dateStr)
                        val dayNum = SimpleDateFormat("d", Locale.getDefault()).format(date)
                        val isToday = dateStr == viewModel.getTodayDate()
                        val dayEventsCount = tasks.count { it.date == dateStr && it.time != null } + gymSessions.count { it.date == dateStr }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.selectCalendarDate(dateStr)
                                    viewModel.setCalendarViewMode("Jour")
                                }
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        when {
                                            dayEventsCount >= 3 -> GoldClassic
                                            dayEventsCount in 1..2 -> LightGrayDivider
                                            else -> Color.Transparent
                                        }
                                    , CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayEventsCount > 0) {
                                    Text(
                                        text = dayEventsCount.toString(),
                                        fontSize = 8.sp,
                                        fontWeight = if (dayEventsCount >= 3) FontWeight.Bold else FontWeight.Normal,
                                        color = if (dayEventsCount >= 3) Color.White else Anthracite
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(LightGrayDivider, CircleShape)
                                    )
                                }
                            }
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
                        PremiumCheckbox(
                            checked = info.taken,
                            onCheckedChange = { viewModel.toggleSupplement(info.key, it) }
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
    val haptic = LocalHapticFeedback.current
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = MediumGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Aucune séance de sport enregistrée pour le moment.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Planifiez et enregistrez votre premier entraînement !",
                    fontSize = 11.sp,
                    color = MediumGray.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
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
                            
                            // Check if it's a new personal record
                            val previousMax = gymExercises
                                .filter { (editEx == null || it.id != editEx.id) && it.exerciseName.trim().equals(nameEx, ignoreCase = true) }
                                .map { it.weightKg }
                                .maxOrNull() ?: 0f
                            
                            if (previousMax > 0f && weight > previousMax) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }

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

    val activeTabState = viewModel.recoveryActiveTab.collectAsState()
    val activeTab = activeTabState.value

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
                    AnimatedCountText(
                        value = currentStreak,
                        suffix = " jours",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
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
            listOf("Streak", "Kegel", "Respiration", "Stop-Start", "Reconditionnement", "Journal").forEach { tab ->
                val isSel = activeTab == tab
                Box(
                    modifier = Modifier
                        .background(if (isSel) GoldClassic else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setRecoveryActiveTab(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (tab == "Reconditionnement") "Recon. Cérébral" else tab,
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
            "Reconditionnement" -> ReconditionnementCerebralSection(viewModel)
            "Journal" -> JournalSection(viewModel, journalEntries)
        }
    }
}

@Composable
fun StreakSection(currentStreak: Int, pastStreaks: List<RecoveryStreak>, onReset: (String?) -> Unit) {
    var showConfirmReset by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

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

                GradientLinearProgressIndicator(
                    progress = pct,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    trackColor = LightGrayDivider,
                    shape = RoundedCornerShape(4.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MediumGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Aucune réinitialisation enregistrée. Restez fort !",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGray,
                    textAlign = TextAlign.Center
                )
            }
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
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
    val startDate by viewModel.kegelProgramStartDate.collectAsState()
    val startVal = if (startDate.isBlank()) viewModel.getKegelProgramStartDateOrDefault() else startDate
    val (phase, weekNumber) = KegelProgramCalculator.getCurrentPhase(startVal)

    var variantConfig by remember { mutableStateOf("Longues") } // "Longues", "Rapides"
    val repsConfig = if (variantConfig == "Rapides") phase.fastReps else phase.slowReps

    val isRunning by viewModel.kegelIsRunning.collectAsState()
    val currentRep by viewModel.kegelRepCount.collectAsState()
    val isContracting by viewModel.kegelIsContracting.collectAsState()
    val secondsLeft by viewModel.kegelSecondsLeft.collectAsState()

    val scaleValue = remember { Animatable(1f) }

    // Reverse Kegel Timer States
    var activeTimerTab by remember { mutableStateOf("Classic") } // "Classic", "Reverse"
    var reverseIsRunning by remember { mutableStateOf(false) }
    var reverseInhaling by remember { mutableStateOf(true) } // true = inhale (expand), false = exhale (relax/push)
    var reverseSecondsLeft by remember { mutableStateOf(4) }
    var reverseCycleCount by remember { mutableStateOf(0) }

    val reverseScaleValue = remember { Animatable(1f) }

    LaunchedEffect(reverseIsRunning, reverseInhaling) {
        if (reverseIsRunning) {
            while (reverseIsRunning) {
                delay(1000)
                if (reverseSecondsLeft > 1) {
                    reverseSecondsLeft--
                } else {
                    reverseInhaling = !reverseInhaling
                    reverseSecondsLeft = 4
                    if (reverseInhaling) {
                        reverseCycleCount++
                    }
                }
            }
        }
    }

    LaunchedEffect(reverseInhaling, reverseIsRunning) {
        if (reverseIsRunning) {
            if (reverseInhaling) {
                reverseScaleValue.animateTo(
                    targetValue = 1.3f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )
            } else {
                reverseScaleValue.animateTo(
                    targetValue = 0.9f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )
            }
        } else {
            reverseScaleValue.animateTo(1f)
        }
    }

    // Pelvic Tension States & Weekly Checks
    val pelvicChecks by viewModel.pelvicTensionChecks.collectAsState()
    val currentWeekStart = remember {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        sdf.format(cal.time)
    }
    val checkForThisWeek = pelvicChecks.firstOrNull { it.weekStartDate == currentWeekStart }

    val hasTwoWeeksConsecutiveTension = remember(pelvicChecks) {
        if (pelvicChecks.size >= 2) {
            val sorted = pelvicChecks.sortedByDescending { it.weekStartDate }
            sorted.size >= 2 && sorted[0].tensionReported && sorted[1].tensionReported
        } else {
            false
        }
    }

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

    val todayStr = viewModel.getTodayDate()
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val todayKegelLog = kegelLogs.firstOrNull { it.date == todayStr } ?: KegelLog(date = todayStr)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (hasTwoWeeksConsecutiveTension) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF9C4), RoundedCornerShape(12.dp)) // fond ambre clair
                    .border(1.dp, Color(0xFFFBC02D), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⚠️", fontSize = 16.sp)
                    Text(
                        text = "Tension pelvienne signalée plusieurs semaines de suite — réduis temporairement l'intensité des contractions (moins de répétitions, tenues plus courtes) et privilégie le relâchement. Si la gêne persiste, consulte un kinésithérapeute pelvien.",
                        fontSize = 11.sp,
                        color = Color(0xFF5D4037),
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Reverse Kegel Encart Éducatif
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "L'importance du Relâchement Pelvien (Reverse Kegel)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Un plancher pelvien trop tendu chroniquement (hypertonique) peut aggraver l'éjaculation précoce et créer des douleurs pelviennes — l'inverse de l'effet recherché. Le relâchement volontaire est aussi important que le renforcement.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
            }
        }

        // --- HEADER COMPLET DU PROGRAMME PROGRESSIF ---
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Phase ${phase.phaseNumber} — ${phase.phaseName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        AnimatedCountText(
                            value = weekNumber,
                            prefix = "Semaine ",
                            suffix = " / 24",
                            fontSize = 12.sp,
                            color = GoldClassic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(LightBeige, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Surcharge Progressive",
                            fontSize = 9.sp,
                            color = GoldClassic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                val globalProgress = (weekNumber.toFloat() / 24f).coerceIn(0f, 1f)
                GradientLinearProgressIndicator(
                    progress = globalProgress,
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    trackColor = LightGrayDivider,
                    shape = RoundedCornerShape(4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Début : $startVal", fontSize = 10.sp, color = MediumGray)
                    Text("${(globalProgress * 100).toInt()}% complété", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- SPECIFIC ROUTINE DETAILS CARD ---
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Routine du jour — Semaine $weekNumber",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = phase.description,
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightGrayBg, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("CONTRACTION LENTE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${phase.slowHoldSeconds}s / ${phase.slowHoldSeconds}s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Text("${phase.slowReps} répétitions", fontSize = 9.sp, color = MediumGray)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightGrayBg, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("FLICKS RAPIDES", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1s / 1s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Text("${phase.fastReps} répétitions", fontSize = 9.sp, color = MediumGray)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(LightGrayBg, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("FRÉQUENCE J.", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${phase.sessionsPerDay} séance(s) / j", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Text("Matin/Midi/Soir", fontSize = 9.sp, color = MediumGray)
                    }
                }
            }
        }

        // --- CHECKBOX COMPLETION PAR SÉANCE ---
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Suivi des séances quotidiennes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "Cochez vos séances après complétion pour maintenir votre streak de récupération.",
                    fontSize = 11.sp,
                    color = MediumGray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (phase.sessionsPerDay == 3) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "morning") }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.morningDone,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "morning") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Matin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "midday") }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.middayDone,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "midday") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Midi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "evening") }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.eveningDone,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "evening") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Soir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (phase.sessionsPerDay == 2) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "morning") }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.morningDone,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "morning") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Matin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "evening") }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.eveningDone,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "evening") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Soir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleKegelLogSession(todayStr, "general") }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PremiumCheckbox(
                                checked = todayKegelLog.done,
                                onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "general") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Séance de Maintenance", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightGrayBg, RoundedCornerShape(8.dp))
                        .clickable { viewModel.toggleKegelLogSession(todayStr, "reverse") }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PremiumCheckbox(
                        checked = todayKegelLog.reverseDone,
                        onCheckedChange = { viewModel.toggleKegelLogSession(todayStr, "reverse") }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Relâchement Pelvien (Reverse Kegel)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total jours complétés : $totalSessions",
                        fontSize = 11.sp,
                        color = MediumGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (todayKegelLog.done) "Validé aujourd'hui" else "Incomplet",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (todayKegelLog.done) GoldClassic else Color(0xFFC62828)
                    )
                }
            }
        }

        // --- TIMER GUI CARD ---
        OperationsCard(borderAccent = isRunning || reverseIsRunning) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector tabs if neither is running
                if (!isRunning && !reverseIsRunning) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightGrayBg, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Classic" to "Kegel Classique", "Reverse" to "Relâchement (Reverse)").forEach { (tabId, label) ->
                            val isS = activeTimerTab == tabId
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isS) GoldClassic else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { activeTimerTab = tabId }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isS) Color.White else MediumGray
                                )
                            }
                        }
                    }
                }

                if (activeTimerTab == "Reverse") {
                    if (!reverseIsRunning) {
                        Text("Séance guidée de Reverse Kegel (5 minutes)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Cette routine favorise la détente et l'allongement du plancher pelvien. Calquez votre respiration sur les mouvements du cercle : inspirez profondément pour étendre, expirez longuement pour relâcher.",
                            fontSize = 11.sp,
                            color = MediumGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        GoldGradientButton(
                            text = "Commencer le relâchement",
                            onClick = {
                                reverseIsRunning = true
                                reverseInhaling = true
                                reverseSecondsLeft = 4
                                reverseCycleCount = 0
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cycles de respiration : $reverseCycleCount",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic
                            )
                            Text(
                                text = "Temps restant : ${if (reverseCycleCount >= 37) "Fini !" else "${300 - (reverseCycleCount * 8 + (4 - reverseSecondsLeft))}s"}",
                                fontSize = 12.sp,
                                color = MediumGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .graphicsLayer {
                                    scaleX = reverseScaleValue.value
                                    scaleY = reverseScaleValue.value
                                }
                                .background(
                                    color = if (reverseInhaling) GoldClassic.copy(alpha = 0.08f) else Color(0xFFE1F5FE),
                                    shape = CircleShape
                                )
                                .border(2.dp, if (reverseInhaling) GoldClassic else Color(0xFF0288D1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (reverseInhaling) "INSPIRER" else "RELACHER",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reverseInhaling) GoldClassic else Color(0xFF0288D1)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${reverseSecondsLeft}s",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Anthracite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { reverseIsRunning = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Arrêter la séance", color = Color.White)
                        }
                    }
                } else {
                    // Classic Timer UI
                    if (!isRunning) {
                        Text("Lancer une séance guidée", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Type d'exercice :", fontSize = 12.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Longues", "Rapides").forEach { v ->
                                    val isS = variantConfig == v
                                    Box(
                                        modifier = Modifier
                                            .background(if (isS) LightBeige else Color.Transparent, RoundedCornerShape(8.dp))
                                            .border(1.dp, if (isS) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                                            .clickable { variantConfig = v }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (v == "Longues") "Lentes (${phase.slowHoldSeconds}s)" else "Rapides (1s)",
                                            fontSize = 10.sp,
                                            color = if (isS) GoldClassic else MediumGray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cette séance effectuera $repsConfig répétitions de contractions ${variantConfig.lowercase()}.",
                            fontSize = 11.sp,
                            color = MediumGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        GoldGradientButton(
                            text = "Commencer la routine",
                            onClick = { viewModel.startKegelTimer(repsConfig, variantConfig) },
                            modifier = Modifier.fillMaxWidth().testTag("start_kegel_button")
                        )
                    } else {
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
        }

        // --- WEEKLY PELVIC TENSION OVERTRAINING CHECK ---
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bilan hebdomadaire de surcharge (Surentraînement)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Une question simple une fois par semaine pour prévenir l'hypertonie pelvienne et adapter l'intensité.",
                    fontSize = 11.sp,
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (checkForThisWeek != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bilan de la semaine effectué", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MediumGray)
                        Box(
                            modifier = Modifier
                                .background(if (checkForThisWeek.tensionReported) Color(0xFFFFF3E0) else Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (checkForThisWeek.tensionReported) "Tension signalée" else "Pas de tension",
                                fontSize = 10.sp,
                                color = if (checkForThisWeek.tensionReported) Color(0xFFE65100) else Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Cette semaine, as-tu ressenti une tension, gêne ou douleur pelvienne inhabituelle ?",
                        fontSize = 11.sp,
                        color = Anthracite,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.savePelvicTensionCheck(currentWeekStart, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3E0)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Oui", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.savePelvicTensionCheck(currentWeekStart, false) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Non", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notice Médicale & Surcharge Progressive", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "La surcharge progressive est la clé du renforcement musculaire. Comme n'importe quel autre muscle, le muscle pubo-coccygien (PC) s'adapte à l'intensité de la tension qui lui est imposée.\n\n" +
                            "La phase 1 pose les fondations neuromusculaires. La phase 2 améliore l'endurance via des contractions de 5s et l'exercice ascenseur. La phase 3 intègre le contrôle actif en situation debout couplé à la respiration diaphragmatique. La phase 4 consolide le contrôle de maintenance.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun ReconditionnementCerebralSection(viewModel: OperationsViewModel) {
    val todayStr = viewModel.getTodayDate()
    val urgeSurfLogs by viewModel.urgeSurfLogs.collectAsState()
    val delayTrainingLogs by viewModel.delayTrainingLogs.collectAsState()

    var isEducationalExpanded by remember { mutableStateOf(false) }

    var isSurfRunning by remember { mutableStateOf(false) }
    var surfDurationMinutes by remember { mutableStateOf(15) }
    var surfSecondsLeft by remember { mutableStateOf(15 * 60) }
    var surfTotalSeconds by remember { mutableStateOf(15 * 60) }

    val surfScaleValue = remember { Animatable(1f) }

    LaunchedEffect(isSurfRunning) {
        if (!isSurfRunning) {
            surfScaleValue.animateTo(1f)
        }
    }

    LaunchedEffect(isSurfRunning, surfSecondsLeft) {
        if (isSurfRunning && surfSecondsLeft > 0) {
            val progressPercent = (surfTotalSeconds - surfSecondsLeft).toFloat() / surfTotalSeconds.toFloat()
            val targetScale = 1f + 0.15f * kotlin.math.sin((surfSecondsLeft.toFloat() * 2 * Math.PI / 10f)).toFloat()
            surfScaleValue.animateTo(targetScale, animationSpec = tween(1000, easing = LinearEasing))
        }
    }

    LaunchedEffect(isSurfRunning) {
        if (isSurfRunning) {
            while (surfSecondsLeft > 0) {
                delay(1000)
                surfSecondsLeft--
            }
            isSurfRunning = false
            viewModel.insertUrgeSurfLog(surfDurationMinutes)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEducationalExpanded = !isEducationalExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Circuit de la Récompense & Tolérance au Délai",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isEducationalExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = GoldClassic
                    )
                }

                if (isEducationalExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = LightGrayDivider)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "L'exposition répétée à une gratification rapide et intense (stimulation compulsive, pornographie, scrolling) désensibilise le circuit de récompense et réduit la tolérance au délai. Cela contribue directement au réflexe d'éjaculer rapidement plutôt que de prolonger le contrôle.\n\n" +
                                "Ce circuit neurologique se reconditionne progressivement, comme un muscle, via une pratique régulière du délai volontaire. En apprenant à tolérer l'inconfort d'une impulsion sans la satisfaire immédiatement, vous renforcez le contrôle cortical sur vos réflexes physiques.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Outil 1 : Surf de l'Urgence (Urge Surfing)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "Une technique clinique prouvée pour traverser les envies compulsives ou impulsions sans céder. L'envie monte, atteint un pic, puis s'éteint.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                if (!isSurfRunning) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Durée : ${surfDurationMinutes} minutes", fontSize = 12.sp, color = Anthracite)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(15, 20).forEach { mins ->
                                val isSelected = surfDurationMinutes == mins
                                Box(
                                    modifier = Modifier
                                        .background(if (isSelected) LightBeige else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSelected) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                                        .clickable {
                                            surfDurationMinutes = mins
                                            surfSecondsLeft = mins * 60
                                            surfTotalSeconds = mins * 60
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$mins min",
                                        fontSize = 11.sp,
                                        color = if (isSelected) GoldClassic else MediumGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    GoldGradientButton(
                        text = "Démarrer un exercice",
                        onClick = {
                            surfSecondsLeft = surfDurationMinutes * 60
                            surfTotalSeconds = surfDurationMinutes * 60
                            isSurfRunning = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("start_urge_surf_button")
                    )
                } else {
                    val elapsedSeconds = surfTotalSeconds - surfSecondsLeft
                    val progressRatio = elapsedSeconds.toFloat() / surfTotalSeconds.toFloat()

                    val guideMessage = when {
                        progressRatio < 0.2f -> "Observe l'envie sans agir dessus. Elle commence à monter doucement. Respire amplement par le ventre."
                        progressRatio < 0.5f -> "L'envie s'intensifie. C'est normal. Imagine cette envie comme une vague de l'océan sur laquelle tu surfes, sans essayer de la combattre ni de la nourrir."
                        progressRatio < 0.75f -> "L'intensité approche de son maximum. Respire au cœur de la sensation. Elle va culminer et redescendre d'elle-même. Reste simple observateur."
                        else -> "La sensation reflue et perd de sa puissance. Tu as traversé la tempête sans céder. Savoure ce calme retrouvé."
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .graphicsLayer {
                                    scaleX = surfScaleValue.value
                                    scaleY = surfScaleValue.value
                                }
                                .background(
                                    color = GoldClassic.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                                .border(2.dp, GoldClassic, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val mins = surfSecondsLeft / 60
                            val secs = surfSecondsLeft % 60
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", mins, secs),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Anthracite
                            )
                        }

                        Text(
                            text = guideMessage,
                            fontSize = 12.sp,
                            color = Anthracite,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Button(
                            onClick = { isSurfRunning = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Arrêter l'exercice", color = Color.White)
                        }
                    }
                }

                if (urgeSurfLogs.isNotEmpty()) {
                    Divider(color = LightGrayDivider)
                    Text("Historique des vagues surfées :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        urgeSurfLogs.take(5).forEach { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Session de ${log.durationMinutes} min",
                                    fontSize = 11.sp,
                                    color = Anthracite
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = log.date,
                                        fontSize = 11.sp,
                                        color = MediumGray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteUrgeSurfLog(log) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = Color(0xFFC62828).copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        val todayChallenge = DelayTrainingChallenges.getChallengeForDay(todayStr)
        val todayLog = delayTrainingLogs.firstOrNull { it.date == todayStr }
        val isTodayChallengeCompleted = todayLog?.completed == true

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Outil 2 : Entraînement au Délai Quotidien",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "Pratiquez la gratification différée de manière volontaire à travers de petits exercices quotidiens non sexuels pour muscler votre volonté corticale.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightGrayBg, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "DÉFI DU JOUR :",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )
                        Text(
                            text = todayChallenge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite,
                            lineHeight = 16.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Défi complété aujourd'hui",
                        fontSize = 12.sp,
                        color = Anthracite,
                        fontWeight = FontWeight.Bold
                    )
                    PremiumCheckbox(
                        checked = isTodayChallengeCompleted,
                        onCheckedChange = { viewModel.toggleDelayTrainingLog(todayStr, todayChallenge) },
                        modifier = Modifier.testTag("delay_challenge_checkbox")
                    )
                }

                Divider(color = LightGrayDivider)
                Text("Streak de régularité (7 derniers jours)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    last7Days.forEach { date ->
                        val logForDate = delayTrainingLogs.firstOrNull { it.date == date }
                        val isCompleted = logForDate?.completed ?: false
                        val dayLabel = viewModel.getDayOfWeekLabel(date)
                        val isToday = date == todayStr

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isCompleted) GoldClassic.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isToday) GoldClassic else LightGrayDivider,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayLabel,
                                fontSize = 10.sp,
                                color = if (isToday) GoldClassic else MediumGray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Default.Circle,
                                contentDescription = null,
                                tint = if (isCompleted) GoldClassic else MediumGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
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
                "HOLD_HIGH" -> {
                    // Subtle 2-second pulse: up slightly then back to max
                    scaleValue.animateTo(1.43f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
                    scaleValue.animateTo(1.4f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
                }
                "OUT" -> scaleValue.animateTo(0.9f, animationSpec = tween(durationMillis = 7000, easing = LinearEasing))
                "HOLD_LOW" -> {
                    // Stays small for 2 seconds
                    scaleValue.animateTo(0.9f, animationSpec = tween(durationMillis = 2000, easing = LinearEasing))
                }
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
                        "IN" -> "Inspire"
                        "HOLD_HIGH" -> "Retiens"
                        "OUT" -> "Expire"
                        "HOLD_LOW" -> "Retiens"
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
                                    "HOLD_HIGH" -> GoldClassic.copy(alpha = 0.15f)
                                    "OUT" -> Color(0xFFEBEBEB)
                                    "HOLD_LOW" -> Color(0xFFDCDCDC)
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
    var dailyWinText by remember { mutableStateOf("") }
    var stressVal by remember { mutableFloatStateOf(5f) }
    var tensionVal by remember { mutableFloatStateOf(5f) }
    var motivationVal by remember { mutableFloatStateOf(5f) }
    var performanceAnxietyVal by remember { mutableFloatStateOf(1f) }

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

                // Performance Anxiety slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Anxiété de Performance Sexuelle", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                        Text("${performanceAnxietyVal.toInt()}/10", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = performanceAnxietyVal,
                        onValueChange = { performanceAnxietyVal = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                    )
                }

                // Daily Win Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Une victoire aujourd'hui, même petite ? 🏆",
                        fontSize = 11.sp,
                        color = Anthracite,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = dailyWinText,
                        onValueChange = { dailyWinText = it },
                        placeholder = {
                            Text(
                                "Ex: j'ai bien géré une conversation difficile, j'ai fini une tâche importante...",
                                fontSize = 11.sp,
                                color = MediumGray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("daily_win_input")
                    )
                }

                GoldGradientButton(
                    text = "Sauvegarder l'entrée",
                    onClick = {
                        viewModel.saveJournalEntry(
                            journalText,
                            stressVal.toInt(),
                            tensionVal.toInt(),
                            motivationVal.toInt(),
                            performanceAnxietyVal.toInt()
                        )
                        if (dailyWinText.isNotBlank()) {
                            viewModel.saveDailyWin(viewModel.getTodayDate(), dailyWinText)
                        }
                        journalText = ""
                        dailyWinText = ""
                        stressVal = 5f
                        tensionVal = 5f
                        motivationVal = 5f
                        performanceAnxietyVal = 1f
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
                                    Text("Anx. Perf: ${entry.performanceAnxiety}", fontSize = 10.sp, color = MediumGray)
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
    var stretchingDone by remember { mutableStateOf(false) }
    var screensOffBeforeBed by remember { mutableStateOf(false) }

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

    val rituelInsight = remember(sleepLogs) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = java.util.Calendar.getInstance()
        val fourteenDaysAgoList = mutableListOf<String>()
        for (i in 0 until 14) {
            val c = cal.clone() as java.util.Calendar
            c.add(java.util.Calendar.DAY_OF_YEAR, -i)
            fourteenDaysAgoList.add(sdf.format(c.time))
        }

        val logsLast14Days = sleepLogs.filter { it.date in fourteenDaysAgoList }
        val withRitual = logsLast14Days.filter { it.stretchingDone && it.screensOffBeforeBed }
        val withoutRitual = logsLast14Days.filter { !it.stretchingDone || !it.screensOffBeforeBed }

        if (withRitual.size >= 5 && withoutRitual.size >= 5) {
            val avgWith = withRitual.map { it.durationHours }.average()
            val avgWithout = withoutRitual.map { it.durationHours }.average()
            
            val avgQualityWith = withRitual.map { it.quality }.average()
            val avgQualityWithout = withoutRitual.map { it.quality }.average()

            val diffHours = avgWith - avgWithout
            val diffQuality = avgQualityWith - avgQualityWithout

            val comparisonText = buildString {
                append("En analysant les 14 derniers jours, ")
                if (diffHours > 0) {
                    append("votre sommeil dure en moyenne ${String.format("%.1f", diffHours)} h de plus les nuits précédées du rituel complet (étirements + écrans coupés). ")
                } else if (diffHours < 0) {
                    append("votre sommeil est en moyenne ${String.format("%.1f", -diffHours)} h plus court avec le rituel, mais peut-être plus réparateur. ")
                } else {
                    append("la durée de votre sommeil est identique avec ou sans rituel. ")
                }

                if (diffQuality > 0) {
                    append("De plus, la qualité subjective de votre sommeil s'améliore de ${String.format("%.1f", diffQuality)} points / 5. ")
                } else if (diffQuality < 0) {
                    append("Cependant, la qualité subjective est légèrement inférieure de ${String.format("%.1f", -diffQuality)} points / 5. ")
                } else {
                    append("La qualité de sommeil perçue reste stable. ")
                }
                append("Ces observations valident que couper les écrans et s'étirer calment le tonus sympathique nocturne.")
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
                Text("Objectif recommandé : 7.5 heures (5 cycles)", fontSize = 11.sp, color = MediumGray)
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

        if (rituelInsight != null) {
            val (avgWith, avgWithout, text) = rituelInsight
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Insight Rituel",
                            tint = GoldClassic,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Insight Rituel : Étirements & Écrans",
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
                            Text("Avec Rituel", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                            Text("${String.format("%.1f", avgWith)} h", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sans Rituel", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
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

                // Rituel du Soir Checkboxes
                Text("Rituel du Soir", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { stretchingDone = !stretchingDone }
                    ) {
                        PremiumCheckbox(
                            checked = stretchingDone,
                            onCheckedChange = { stretchingDone = it },
                            modifier = Modifier.testTag("stretching_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🧘 Étirements/Souplesse faits ce soir", fontSize = 12.sp, color = Anthracite)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { screensOffBeforeBed = !screensOffBeforeBed }
                    ) {
                        PremiumCheckbox(
                            checked = screensOffBeforeBed,
                            onCheckedChange = { screensOffBeforeBed = it },
                            modifier = Modifier.testTag("screens_off_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🚫 Écrans coupés au moins 30 min avant le coucher", fontSize = 12.sp, color = Anthracite)
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
                            selectedQuality,
                            stretchingDone = stretchingDone,
                            screensOffBeforeBed = screensOffBeforeBed
                        )

                        // Reset checkboxes
                        stretchingDone = false
                        screensOffBeforeBed = false
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
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🧘",
                                            fontSize = 11.sp,
                                            modifier = Modifier.alpha(if (log.stretchingDone) 1f else 0.3f)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Étirements",
                                            fontSize = 9.sp,
                                            color = if (log.stretchingDone) GoldClassic else MediumGray.copy(alpha = 0.5f),
                                            fontWeight = if (log.stretchingDone) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "🚫",
                                            fontSize = 11.sp,
                                            modifier = Modifier.alpha(if (log.screensOffBeforeBed) 1f else 0.3f)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Écrans",
                                            fontSize = 9.sp,
                                            color = if (log.screensOffBeforeBed) GoldClassic else MediumGray.copy(alpha = 0.5f),
                                            fontWeight = if (log.screensOffBeforeBed) FontWeight.Bold else FontWeight.Normal
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
    val goalName by viewModel.goalName.collectAsState()
    val goalTargetDate by viewModel.goalTargetDate.collectAsState()

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

        // --- PROGRAMME KEGEL START DATE CARD ---
        val kegelStartDate by viewModel.kegelProgramStartDate.collectAsState()
        val kegelContext = LocalContext.current
        
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Programme Kegel Progressif", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "Configurez la date de début de votre programme progressif de 24 semaines. Votre phase actuelle sera automatiquement calculée sur cette base.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date de début : $kegelStartDate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )

                    PremiumGradientButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            try {
                                val d = sdf.parse(kegelStartDate)
                                if (d != null) calendar.time = d
                            } catch (e: Exception) {}

                            android.app.DatePickerDialog(
                                kegelContext,
                                { _, year, month, dayOfMonth ->
                                    val newDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                    viewModel.updateKegelProgramStartDate(newDate)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.testTag("select_kegel_start_date"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Modifier", color = WhitePure, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- DASHBOARD CUSTOMIZATION CARD ---
        val showAlerts by viewModel.showAlerts.collectAsState()
        val showCountdown by viewModel.showCountdown.collectAsState()
        val showWeeklyPreview by viewModel.showWeeklyPreview.collectAsState()
        val showAffirmation by viewModel.showAffirmation.collectAsState()
        val showGratitude by viewModel.showGratitude.collectAsState()
        val showFavorites by viewModel.showFavorites.collectAsState()
        val showSecondaryGrid by viewModel.showSecondaryGrid.collectAsState()
        val showGeminiAnalysis by viewModel.showGeminiAnalysis.collectAsState()

        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Personnaliser mon Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "Activez ou désactivez les sections du tableau de bord pour un affichage sur mesure et épuré. La ligne Hero et la bannière rituel restent toujours visibles.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Divider(color = LightGrayDivider)

                val customSections = listOf(
                    Triple("dashboard_show_alerts", "Alertes prioritaires", showAlerts),
                    Triple("dashboard_show_countdown", "Compte à rebours de l'objectif", showCountdown),
                    Triple("dashboard_show_weekly_preview", "Aperçu de la semaine", showWeeklyPreview),
                    Triple("dashboard_show_affirmation", "Affirmation & Confiance", showAffirmation),
                    Triple("dashboard_show_gratitude", "Journal de Gratitude", showGratitude),
                    Triple("dashboard_show_favorites", "Raccourcis Favoris", showFavorites),
                    Triple("dashboard_show_secondary_grid", "Indicateurs secondaires (grille)", showSecondaryGrid),
                    Triple("dashboard_show_gemini_analysis", "Analyse d'intelligence Gemini", showGeminiAnalysis)
                )

                customSections.forEachIndexed { index, (key, title, isVisible) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Anthracite)
                        Switch(
                            checked = isVisible,
                            onCheckedChange = { viewModel.setDashboardSectionVisible(key, it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige),
                            modifier = Modifier.testTag("switch_$key")
                        )
                    }
                    if (index < customSections.lastIndex) {
                        Divider(color = LightGrayDivider.copy(alpha = 0.5f))
                    }
                }
            }
        }

        // --- COUNTDOWN GOAL CONFIG CARD ---
        val goalContext = LocalContext.current
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Objectif et Compte à Rebours", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "Configurez un objectif personnel (ex: mariage, compétition, événement) et sa date cible pour afficher un compte à rebours dynamique sur votre tableau de bord.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = goalName,
                    onValueChange = { viewModel.saveGoalName(it) },
                    label = { Text("Nom de l'objectif", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                    modifier = Modifier.fillMaxWidth().testTag("goal_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (goalTargetDate.isBlank()) "Aucune date configurée" else "Date cible : $goalTargetDate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )

                    PremiumGradientButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            try {
                                val d = sdf.parse(goalTargetDate)
                                if (d != null) calendar.time = d
                            } catch (e: Exception) {}

                            android.app.DatePickerDialog(
                                goalContext,
                                { _, year, month, dayOfMonth ->
                                    val newDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                    viewModel.saveGoalTargetDate(newDate)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.testTag("select_goal_target_date"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Modifier la date", color = WhitePure, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
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
                    PremiumGradientButton(
                        onClick = { exportLauncher.launch("directeur_operations_backup.json") },
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

        // --- CONFIGURATION DU FOYER CARD ---
        val householdSize by viewModel.householdSize.collectAsState()
        var householdSizeInput by remember { mutableStateOf(householdSize.toString()) }
        LaunchedEffect(householdSize) {
            householdSizeInput = householdSize.toString()
        }
        OperationsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Configuration de Survie", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Text(
                    text = "Configurez la taille de votre foyer. Toutes les cibles de planification d'autonomie (eau, céréales, conserves, etc.) de l'onglet 'Survive The Great Reset' s'adapteront automatiquement à cette valeur.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )
                OutlinedTextField(
                    value = householdSizeInput,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all { it.isDigit() }) {
                            householdSizeInput = input
                            val num = input.toIntOrNull()
                            if (num != null && num > 0) {
                                viewModel.updateHouseholdSize(num)
                            }
                        }
                    },
                    label = { Text("Taille du foyer (nombre de personnes)", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                    modifier = Modifier.fillMaxWidth().testTag("household_size_input")
                )
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
    val cardioHealthLogs by viewModel.cardioHealthLogs.collectAsState()
    val morningErectionLogs by viewModel.morningErectionLogs.collectAsState()

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

                    PremiumGradientButton(
                        onClick = { viewModel.logSunExposure(selectedMinutes) },
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

        val indicators = remember(sleepLogs, gymSessions, journalEntries, sunExposureLogs, supplementLogs, cardioHealthLogs, last7Days) {
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

            // 6. Cardio count
            val cardioCount = gymSessions.count { it.date in last7Days && it.muscleGroups.contains("Cardio", ignoreCase = true) }
            val cardioOk = cardioCount >= 3

            // 7. BP avg
            val recentCardios = cardioHealthLogs.filter { it.date in last7Days }
            val avgSyst = if (recentCardios.any { it.systolicBP != null }) recentCardios.mapNotNull { it.systolicBP }.average().toInt() else null
            val avgDiast = if (recentCardios.any { it.diastolicBP != null }) recentCardios.mapNotNull { it.diastolicBP }.average().toInt() else null
            val bpOk = avgSyst == null || (avgSyst < 130 && avgDiast != null && avgDiast < 85)

            // 8. Smoke free days
            val smokeFreeCount = 7 - recentCardios.count { it.tobaccoUsed }
            val tobaccoOk = smokeFreeCount == 7

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
                    label = "Cardio cette semaine",
                    value = "$cardioCount / 4 séances",
                    desc = "Cible: 4 par semaine",
                    isGood = cardioOk,
                    icon = Icons.AutoMirrored.Filled.DirectionsRun
                ),
                IndicatorItem(
                    label = "Tension Artérielle",
                    value = if (avgSyst != null && avgDiast != null) "$avgSyst/$avgDiast mmHg" else "Non mesurée",
                    desc = "Cible: < 120/80 mmHg",
                    isGood = bpOk,
                    icon = Icons.Default.Favorite
                ),
                IndicatorItem(
                    label = "Jours sans Tabac",
                    value = "$smokeFreeCount / 7 jours",
                    desc = "Cible: 7 jours (Sans tabac)",
                    isGood = tobaccoOk,
                    icon = Icons.Default.CheckCircle
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

        // --- SECTION NOUVELLE : SANTÉ CARDIOVASCULAIRE ---
        Text(
            text = "Santé Cardiovasculaire",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        var systolicStr by remember { mutableStateOf("") }
        var diastolicStr by remember { mutableStateOf("") }
        var waistStr by remember { mutableStateOf("") }
        var alcoholUnitsVal by remember { mutableFloatStateOf(0f) }
        var tobaccoUsed by remember { mutableStateOf(false) }

        val todayCardio = cardioHealthLogs.firstOrNull { it.date == todayStr }

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Suivi des biomarqueurs cardiovasculaires",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                
                // Medical Warning label
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBeige, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "⚠️ AVERTISSEMENT MÉDICAL : Ces données sont indicatives et ne remplacent pas un avis médical ou un diagnostic professionnel.",
                        fontSize = 10.sp,
                        color = GoldClassic,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 14.sp
                    )
                }

                if (todayCardio != null) {
                    // Show logged data
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tension Artérielle :", fontSize = 11.sp, color = MediumGray)
                            Text(
                                text = if (todayCardio.systolicBP != null && todayCardio.diastolicBP != null) "${todayCardio.systolicBP}/${todayCardio.diastolicBP} mmHg" else "Non renseignée",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tour de Taille :", fontSize = 11.sp, color = MediumGray)
                            Text(
                                text = if (todayCardio.waistCircumferenceCm != null) "${todayCardio.waistCircumferenceCm} cm" else "Non renseigné",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consommation d'Alcool :", fontSize = 11.sp, color = MediumGray)
                            Text(
                                text = "${todayCardio.alcoholUnits} unités standard",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todayCardio.alcoholUnits > 0) Color(0xFFC62828) else Color(0xFF2E7D32)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consommation de Tabac :", fontSize = 11.sp, color = MediumGray)
                            Text(
                                text = if (todayCardio.tobaccoUsed) "Oui (Fumeur)" else "Non (Optimal)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (todayCardio.tobaccoUsed) Color(0xFFC62828) else Color(0xFF2E7D32)
                            )
                        }
                    }
                } else {
                    // Form fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = systolicStr,
                            onValueChange = { systolicStr = it },
                            label = { Text("Systolique (ex: 120)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = diastolicStr,
                            onValueChange = { diastolicStr = it },
                            label = { Text("Diastolique (ex: 80)", fontSize = 10.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = waistStr,
                        onValueChange = { waistStr = it },
                        label = { Text("Tour de Taille en cm (ex: 94)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Alcool (Unités standard)", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                            Text("${alcoholUnitsVal.toInt()} u", fontSize = 11.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = alcoholUnitsVal,
                            onValueChange = { alcoholUnitsVal = it },
                            valueRange = 0f..15f,
                            steps = 14,
                            colors = SliderDefaults.colors(thumbColor = GoldClassic, activeTrackColor = GoldClassic)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Consommation de tabac aujourd'hui :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Switch(
                            checked = tobaccoUsed,
                            onCheckedChange = { tobaccoUsed = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldClassic, checkedTrackColor = LightBeige)
                        )
                    }

                    GoldGradientButton(
                        text = "Enregistrer la santé cardiovasculaire",
                        onClick = {
                            val syst = systolicStr.toIntOrNull()
                            val diast = diastolicStr.toIntOrNull()
                            val waist = waistStr.toFloatOrNull()
                            viewModel.saveCardioHealthLog(todayStr, syst, diast, waist, alcoholUnitsVal.toInt(), tobaccoUsed)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- SECTION NOUVELLE : ÉRECTIONS MATINALES ---
        Text(
            text = "Suivi des Érections Matinales",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        val todayErectionLog = morningErectionLogs.firstOrNull { it.date == todayStr }

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Évaluation de la qualité au réveil",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "L'érection matinale est un indicateur clé de la santé cardiovasculaire et de l'équilibre hormonal de la testostérone libre.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (todayErectionLog != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aujourd'hui :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MediumGray)
                        Box(
                            modifier = Modifier
                                .background(
                                    when (todayErectionLog.quality) {
                                        "Oui" -> Color(0xFFE8F5E9)
                                        "Partielle" -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFFFEBEE)
                                    },
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = todayErectionLog.quality,
                                fontSize = 11.sp,
                                color = when (todayErectionLog.quality) {
                                    "Oui" -> Color(0xFF2E7D32)
                                    "Partielle" -> Color(0xFFE65100)
                                    else -> Color(0xFFC62828)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Oui", "Partielle", "Non").forEach { q ->
                            Button(
                                onClick = { viewModel.saveMorningErectionLog(todayStr, q) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (q) {
                                        "Oui" -> Color(0xFFE8F5E9)
                                        "Partielle" -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFFFEBEE)
                                    }
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = q,
                                    color = when (q) {
                                        "Oui" -> Color(0xFF2E7D32)
                                        "Partielle" -> Color(0xFFE65100)
                                        else -> Color(0xFFC62828)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = LightGrayDivider)
                Spacer(modifier = Modifier.height(4.dp))

                // 7-day visual history
                Text("Historique visuel des 7 derniers jours :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7Days.forEach { date ->
                        val label = viewModel.getDayOfWeekLabel(date)
                        val log = morningErectionLogs.firstOrNull { it.date == date }
                        
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
                                        when (log?.quality) {
                                            "Oui" -> Color(0xFFE8F5E9)
                                            "Partielle" -> Color(0xFFFFF3E0)
                                            "Non" -> Color(0xFFFFEBEE)
                                            else -> Color(0xFFF5F5F5)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (log?.quality) {
                                        "Oui" -> "✓"
                                        "Partielle" -> "P"
                                        "Non" -> "✗"
                                        else -> "•"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (log?.quality) {
                                        "Oui" -> Color(0xFF2E7D32)
                                        "Partielle" -> Color(0xFFE65100)
                                        "Non" -> Color(0xFFC62828)
                                        else -> MediumGray
                                    }
                                )
                            }
                        }
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

@Composable
fun CommunicationPage(viewModel: OperationsViewModel) {
    val practiceLogs by viewModel.communicationPracticeLogs.collectAsState()
    val todayStr = viewModel.getTodayDate()
    val todaySkill = remember(todayStr) { com.example.data.CommunicationSkillsData.getSkillForDate(todayStr) }
    
    val todayLog = practiceLogs.firstOrNull { it.date == todayStr }
    val isTodayPracticed = todayLog?.practiced == true

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

    val practicedCount7Days = remember(practiceLogs, last7Days) {
        practiceLogs.count { it.date in last7Days && it.practiced }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageHeader(
            title = "Communication",
            subtitle = "Parole, langage corporel et intelligence sociale"
        )

        // --- SKILL OF THE DAY CARD ---
        OperationsCard(borderAccent = true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMPÉTENCE DU JOUR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = GoldClassic.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = todaySkill,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite,
                    lineHeight = 20.sp
                )

                Divider(color = LightGrayDivider)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.markSkillPracticed(todayStr, !isTodayPracticed) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PremiumCheckbox(
                        checked = isTodayPracticed,
                        onCheckedChange = { viewModel.markSkillPracticed(todayStr, it) },
                        modifier = Modifier.testTag("communication_today_checkbox")
                    )
                    Text(
                        text = "Pratiqué aujourd'hui",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Anthracite
                    )
                }
            }
        }

        // --- REGULARITY / 7-DAY HISTORY CARD ---
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Pratique sur 7 jours",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$practicedCount7Days / 7 jours validés",
                    fontSize = 11.sp,
                    color = MediumGray
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7Days.forEach { date ->
                        val label = viewModel.getDayOfWeekLabel(date)
                        val log = practiceLogs.firstOrNull { it.date == date }
                        val isDone = log?.practiced == true
                        val isDayToday = date == todayStr

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                color = if (isDayToday) GoldClassic else MediumGray,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(
                                        if (isDone) GoldClassic else Color(0xFFF0F0F0),
                                        CircleShape
                                    )
                                    .clickable {
                                        viewModel.markSkillPracticed(date, !isDone)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Fait",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
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
            }
        }

        // --- EDUCATIONAL MODULES ACCORDIONS ---
        Text(
            text = "Modules Éducatifs",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Module 1
        var m1Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m1Expanded = !m1Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 1 — Prise de parole",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m1Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m1Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m1Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "La pause stratégique",
                            normalText = "le silence après une idée forte capte l'attention ; ne jamais le combler par un \"euh\" ou un mot de remplissage."
                        )
                        BulletPoint(
                            boldText = "Débit maîtrisé",
                            normalText = "ralentir le débit sur les points importants, accélérer légèrement sur les détails secondaires."
                        )
                    }
                }
            }
        }

        // Module 2
        var m2Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m2Expanded = !m2Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 2 — Modulation vocale",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m2Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m2Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m2Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Ton descendant",
                            normalText = "descendre le ton en fin de phrase affirmative (signal de confiance et d'autorité)."
                        )
                        BulletPoint(
                            boldText = "Éviter l'uptalk",
                            normalText = "éviter de monter le ton en fin de phrase non-interrogative (signal d'incertitude)."
                        )
                        BulletPoint(
                            boldText = "Volume dynamique",
                            normalText = "varier le volume pour souligner les points clés plutôt que parler sur un ton monotone."
                        )
                    }
                }
            }
        }

        // Module 3
        var m3Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m3Expanded = !m3Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 3 — Langage corporel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m3Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m3Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m3Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Posture ouverte",
                            normalText = "mains visibles, éviter les bras croisés et les mains dans les poches."
                        )
                        BulletPoint(
                            boldText = "Contact visuel",
                            normalText = "maintenir un regard stable et naturel, sans fixer de façon insistante."
                        )
                        BulletPoint(
                            boldText = "Gestes congruents",
                            normalText = "aligner les gestes avec le message (gestes ouverts pour un discours confiant)."
                        )
                    }
                }
            }
        }

        // Module 4
        var m4Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m4Expanded = !m4Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 4 — Ne pas mal interpréter les autres",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m4Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m4Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m4Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Fait vs Interprétation",
                            normalText = "distinguer le fait observé de l'interprétation qu'on en fait (ex. \"il n'a pas répondu\" vs \"il m'ignore\")."
                        )
                        BulletPoint(
                            boldText = "Curiosité d'abord",
                            normalText = "poser une question ouverte plutôt que supposer une intention négative de la part de l'autre."
                        )
                    }
                }
            }
        }

        // Module 5
        var m5Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m5Expanded = !m5Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 5 — Intelligence émotionnelle",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m5Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m5Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m5Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Labeling émotionnel",
                            normalText = "nommer l'émotion ressentie avant de réagir pour désamorcer l'impulsivité."
                        )
                        BulletPoint(
                            boldText = "Réponse choisie",
                            normalText = "distinguer une réaction impulsive automatique d'une réponse réfléchie et choisie."
                        )
                        BulletPoint(
                            boldText = "Écoute active",
                            normalText = "développer l'empathie en reformulant précisément ce que l'autre vient de dire avant de répliquer."
                        )
                    }
                }
            }
        }

        // Module 6
        var m6Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { m6Expanded = !m6Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Module 6 — Préparation de couple",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (m6Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (m6Expanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (m6Expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Parler de sa démarche",
                            normalText = "dévoiler son protocole et ses efforts de manière positive et sereine, sans projeter d'anxiété."
                        )
                        BulletPoint(
                            boldText = "Co-construction des règles",
                            normalText = "définir ensemble ce que l'autre accepte ou pas dans l'intimité pour avancer en confiance."
                        )
                        BulletPoint(
                            boldText = "Gestion d'équipe",
                            normalText = "faire du couple une force alliée et complice plutôt qu'un lieu d'évaluation nerveuse ou de jugement mutuel."
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BulletPoint(boldText: String, normalText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(6.dp)
                .background(GoldClassic, CircleShape)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Anthracite)) {
                    append(boldText)
                }
                append(" : ")
                withStyle(style = SpanStyle(color = MediumGray)) {
                    append(normalText)
                }
            },
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurviveGreatResetPage(viewModel: OperationsViewModel) {
    val householdSize by viewModel.householdSize.collectAsState()
    val stockItems by viewModel.survivalStockItems.collectAsState()

    val targets = remember(householdSize) { SurvivalCalculator.calculateTargets(householdSize) }

    val categoryTotals = remember(stockItems) {
        stockItems.groupBy { it.category }.mapValues { entry ->
            entry.value.sumOf { it.quantity.toDouble() }.toFloat()
        }
    }

    val categories = remember {
        listOf(
            "Eau", "Céréales", "Légumineuses", "Conserves", "Graisses",
            "Sucre", "Lait en poudre", "Sel", "Hygiène", "Médical"
        )
    }

    val todayStr = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.format(Date())
    }

    // --- FORM STATE ---
    var isFormExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Céréales") }
    var itemNameInput by remember { mutableStateOf("") }
    var itemQtyInput by remember { mutableStateOf("") }
    var itemUnitInput by remember { mutableStateOf("kg") }
    var purchaseDateInput by remember { mutableStateOf(todayStr) }
    var expiryDateInput by remember { mutableStateOf("") }
    var selectedStorageMethod by remember { mutableStateOf("Sac Mylar + absorbeur O2") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var storageDropdownExpanded by remember { mutableStateOf(false) }

    // Update unit automatically based on category selection
    LaunchedEffect(selectedCategory) {
        itemUnitInput = when (selectedCategory) {
            "Eau", "Graisses" -> "L"
            "Hygiène", "Médical" -> "unités"
            else -> "kg"
        }
    }

    // Helper functions
    fun formatQty(value: Float): String {
        return if (value % 1f == 0f) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }

    fun isExpiringSoon(expiryDateStr: String?): Boolean {
        if (expiryDateStr.isNullOrBlank()) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val expiryDate = sdf.parse(expiryDateStr) ?: return false
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            val diffMillis = expiryDate.time - today.time
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)
            diffDays in 0..59
        } catch (e: Exception) {
            false
        }
    }

    fun getCategoryIcon(category: String): ImageVector {
        return when (category) {
            "Eau" -> Icons.Default.Opacity
            "Céréales" -> Icons.Default.Eco
            "Légumineuses" -> Icons.Default.Spa
            "Conserves" -> Icons.Default.Restaurant
            "Graisses" -> Icons.Default.Opacity
            "Sucre" -> Icons.Default.Cake
            "Lait en poudre" -> Icons.Default.LocalCafe
            "Sel" -> Icons.Default.Science
            "Hygiène" -> Icons.Default.Face
            "Médical" -> Icons.Default.Healing
            else -> Icons.Default.Inventory2
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
            title = "Survive The Great Reset",
            subtitle = "Planification d'autonomie et gestion des stocks de secours"
        )

        // --- ADD ITEM COLLAPSIBLE FORM CARD ---
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isFormExpanded = !isFormExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = GoldClassic
                        )
                        Text(
                            text = "Ajouter un article au stock",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                    }
                    Icon(
                        imageVector = if (isFormExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isFormExpanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isFormExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Category Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Catégorie", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                trailingIcon = {
                                    IconButton(onClick = { categoryDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { categoryDropdownExpanded = true }
                                    .testTag("form_category_input")
                            )
                            DropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Name
                        OutlinedTextField(
                            value = itemNameInput,
                            onValueChange = { itemNameInput = it },
                            label = { Text("Nom de l'article (ex: Riz complet, Bidon d'eau)", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_name_input")
                        )

                        // Quantity & Unit Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = itemQtyInput,
                                onValueChange = { input ->
                                    if (input.isEmpty() || input.all { it.isDigit() || it == '.' }) {
                                        itemQtyInput = input
                                    }
                                },
                                label = { Text("Quantité", fontSize = 12.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_quantity_input")
                            )

                            OutlinedTextField(
                                value = itemUnitInput,
                                onValueChange = { itemUnitInput = it },
                                label = { Text("Unité", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_unit_input")
                            )
                        }

                        // Storage Method Dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedStorageMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Méthode de conservation", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                trailingIcon = {
                                    IconButton(onClick = { storageDropdownExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { storageDropdownExpanded = true }
                                    .testTag("form_storage_input")
                            )
                            DropdownMenu(
                                expanded = storageDropdownExpanded,
                                onDismissRequest = { storageDropdownExpanded = false }
                            ) {
                                listOf(
                                    "Sac Mylar + absorbeur O2", "Seau alimentaire", "Conserve standard",
                                    "Bidon scellé", "Bouteille commerciale", "Autre"
                                ).forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method) },
                                        onClick = {
                                            selectedStorageMethod = method
                                            storageDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Dates Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = purchaseDateInput,
                                onValueChange = { purchaseDateInput = it },
                                label = { Text("Date d'achat (AAAA-MM-JJ)", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_purchase_date_input")
                            )

                            OutlinedTextField(
                                value = expiryDateInput,
                                onValueChange = { expiryDateInput = it },
                                label = { Text("Expiration (optionnel)", fontSize = 11.sp) },
                                placeholder = { Text("AAAA-MM-JJ", fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldClassic),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("form_expiry_date_input")
                            )
                        }

                        // Add Button
                        PremiumGradientButton(
                            onClick = {
                                val qty = itemQtyInput.toFloatOrNull()
                                if (qty != null && itemNameInput.isNotBlank()) {
                                    viewModel.insertSurvivalStockItem(
                                        category = selectedCategory,
                                        name = itemNameInput.trim(),
                                        quantity = qty,
                                        unit = itemUnitInput.trim(),
                                        purchaseDate = purchaseDateInput.trim(),
                                        estimatedExpiryDate = expiryDateInput.trim().ifEmpty { null },
                                        storageMethod = selectedStorageMethod
                                    )
                                    // Reset fields
                                    itemNameInput = ""
                                    itemQtyInput = ""
                                    expiryDateInput = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .testTag("form_submit_button")
                        ) {
                            Text("Ajouter au stock", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- WATER EXPLANATION / PERMANENT WARNING BANNER ---
        val waterTotalLitres = householdSize * 12 * 365
        OperationsCard(borderAccent = true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBeige.copy(alpha = 0.4f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Attention",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Stratégie de l'eau : Stock vs Autonomie",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                }

                Text(
                    text = "Pour un foyer de $householdSize personnes sur 365 jours, la consommation totale (boisson, cuisine, hygiène) dépasse $waterTotalLitres litres — un volume impossible à stocker dans une habitation standard. L'objectif ci-dessous est un stock tampon de 3 semaines. Pour couvrir le reste de l'année, prévois un système de filtration/purification (filtre céramique + charbon, ou osmose inverse) et si possible une captation d'eau de pluie.",
                    fontSize = 12.sp,
                    color = Anthracite,
                    lineHeight = 18.sp
                )
            }
        }

        // --- CATEGORIES OVERVIEW ---
        Text(
            text = "Inventaire par Catégorie",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        categories.forEach { cat ->
            val targetInfo = targets[cat]
            if (targetInfo != null) {
                val actualQty = categoryTotals[cat] ?: 0f
                val targetQty = targetInfo.targetQuantity
                val progressPercent = if (targetQty > 0) (actualQty / targetQty) * 100f else 0f
                val safePercent = progressPercent.coerceIn(0f, 100f)

                val isGoalReached = progressPercent >= 80f
                val badgeColor = if (isGoalReached) GoldClassic else MediumGray
                val badgeText = if (isGoalReached) "Optimal (≥80%)" else "À compléter"

                var isCategoryExpanded by remember { mutableStateOf(false) }
                var showWhyDialog by remember { mutableStateOf(false) }

                // Retrieve and sort items in this category
                val catItems = stockItems.filter { it.category == cat }
                    .sortedWith(compareBy<SurvivalStockItem> { it.estimatedExpiryDate == null }
                        .thenBy { it.estimatedExpiryDate })

                OperationsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = getCategoryIcon(cat),
                                    contentDescription = null,
                                    tint = GoldClassic,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = cat,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Anthracite
                                )
                                IconButton(
                                    onClick = { showWhyDialog = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Pourquoi ?",
                                        tint = MediumGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 9.sp,
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Quantities
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Possédé : ${formatQty(actualQty)} / ${formatQty(targetQty)} ${targetInfo.unit}",
                                fontSize = 12.sp,
                                color = Anthracite,
                                fontWeight = FontWeight.SemiBold
                            )
                            AnimatedCountText(
                                value = progressPercent.toInt(),
                                suffix = "%",
                                fontSize = 12.sp,
                                color = if (isGoalReached) GoldClassic else MediumGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Progress bar
                        GradientLinearProgressIndicator(
                            progress = safePercent / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            trackColor = LightBeige,
                            shape = RoundedCornerShape(3.dp)
                        )

                        // Collapsible Inventaire list
                        Divider(color = LightGrayDivider)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isCategoryExpanded = !isCategoryExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Voir l'inventaire (${catItems.size} articles)",
                                fontSize = 12.sp,
                                color = GoldClassic,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isCategoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = GoldClassic,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        if (isCategoryExpanded) {
                            if (catItems.isEmpty()) {
                                Text(
                                    text = "Aucun article enregistré dans cette catégorie.",
                                    fontSize = 11.sp,
                                    color = MediumGray,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    catItems.forEach { item ->
                                        val expiring = isExpiringSoon(item.estimatedExpiryDate)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (expiring) Color(0xFFFFF8E1) else LightBeige.copy(alpha = 0.2f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .border(0.5.dp, LightGrayDivider, RoundedCornerShape(6.dp))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = item.name,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Anthracite
                                                    )
                                                    if (expiring) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(Color(0xFFFFB300), RoundedCornerShape(3.dp))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = "Exp. bientôt",
                                                                fontSize = 8.sp,
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${formatQty(item.quantity)} ${item.unit} • ${item.storageMethod}",
                                                    fontSize = 11.sp,
                                                    color = MediumGray
                                                )
                                                Text(
                                                    text = buildAnnotatedString {
                                                        append("Acheté le : ${item.purchaseDate}")
                                                        if (item.estimatedExpiryDate != null) {
                                                            append(" • Expire le : ${item.estimatedExpiryDate}")
                                                        }
                                                    },
                                                    fontSize = 10.sp,
                                                    color = MediumGray
                                                )
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteSurvivalStockItem(item) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Supprimer",
                                                    tint = Color(0xFFC62828),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Info "Pourquoi" Dialog
                if (showWhyDialog) {
                    AlertDialog(
                        onDismissRequest = { showWhyDialog = false },
                        title = { Text(text = "Pourquoi stocker : $cat") },
                        text = {
                            Text(
                                text = targetInfo.why,
                                fontSize = 14.sp,
                                color = Anthracite,
                                lineHeight = 20.sp
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showWhyDialog = false }) {
                                Text("Compris", color = GoldClassic, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }

        // --- CONSERVATION EDUCATION SECTION ---
        Text(
            text = "Méthodes de Conservation",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        var isEduExpanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEduExpanded = !isEduExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = GoldClassic
                        )
                        Text(
                            text = "Comment garder vos stocks frais ?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                    }
                    Icon(
                        imageVector = if (isEduExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isEduExpanded) "Réduire" else "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isEduExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BulletPoint(
                            boldText = "Céréales & légumineuses sèches",
                            normalText = "Sacs Mylar de qualité alimentaire fermés à chaud avec un absorbeur d'oxygène (O2), placés dans des seaux alimentaires à couvercle hermétique ou gamma seal. Durée de conservation estimée : 20 à 30 ans si stockés au frais (< 21°C), au sec et à l'obscurité."
                        )

                        BulletPoint(
                            boldText = "Conserves standard",
                            normalText = "Appliquez rigoureusement la rotation FIFO (First In, First Out / Premier entré, premier sorti). Inspectez visuellement chaque boîte avant consommation : rejetez impitoyablement toute conserve bombée, rouillée ou cabossée au niveau des sertis. Durée de conservation utile : 2 à 5 ans."
                        )

                        BulletPoint(
                            boldText = "Aliments lyophilisés",
                            normalText = "Excellente alternative ultra-légère. Se conservent plus de 25 ans sous emballage scellé d'origine. Les conditions idéales restent identiques aux céréales : au frais, au sec et à l'obscurité."
                        )

                        BulletPoint(
                            boldText = "Eau stockée",
                            normalText = "Effectuez une rotation tous les 6 à 12 mois si stockée dans des bouteilles plastiques commerciales d'origine. Pour le stockage d'eau en bidons ou vrac, traitez avec de l'eau de Javel pure non parfumée (2 à 4 gouttes par litre) et conservez dans des contenants opaques de qualité alimentaire à l'abri de la lumière."
                        )

                        BulletPoint(
                            boldText = "Règle générale de préservation",
                            normalText = "Les 5 ennemis mortels du stock de survie sont la Chaleur, l'Humidité, l'Oxygène, la Lumière et les Nuisibles. Visez toujours un stockage frais, sec, sombre, hermétique et hors de portée des rongeurs ou insectes."
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 80.dp,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SkeletonAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .alpha(alpha)
            .background(LightGrayDivider, shape)
    )
}

@Composable
fun AnimatedCountText(
    value: Int,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    fontSize: androidx.compose.ui.unit.TextUnit = 28.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = GoldClassic
) {
    val animatedValue = animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "CounterAnimation"
    )

    Text(
        text = "$prefix${animatedValue.value}$suffix",
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier
    )
}

@Composable
fun PremiumCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "CheckboxScale"
    )
    val color by animateColorAsState(
        targetValue = if (checked) GoldClassic else MediumGray.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 180),
        label = "CheckboxColor"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(22.dp)
            .border(
                width = 1.5.dp,
                color = color,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
            )
            .background(
                color = if (checked) GoldClassic.copy(alpha = 0.08f) else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
            )
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = checked,
            enter = fadeIn(animationSpec = tween(120)) + scaleIn(animationSpec = tween(120)),
            exit = fadeOut(animationSpec = tween(120)) + scaleOut(animationSpec = tween(120))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = GoldClassic,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun GradientLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    brush: Brush = GradientTokens.sunsetHorizontal,
    trackColor: Color = LightGrayDivider,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ProgressAnimation"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(trackColor)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(brush)
            )
        }
    }
}

@Composable
fun PremiumGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = GradientTokens.sunsetVertical,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun PourquoiPage(viewModel: OperationsViewModel) {
    val whyStatement by viewModel.whyStatement.collectAsState()
    var isEditing by remember(whyStatement) { mutableStateOf<Boolean>(whyStatement.isEmpty()) }
    var textVal by remember(whyStatement) { mutableStateOf<String>(whyStatement) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Ma Raison Profonde",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Ancrez votre motivation. Pourquoi suivez-vous ce protocole ? Pour qui ? Pour quelle version de vous-même ?",
                fontSize = 14.sp,
                color = MediumGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            if (whyStatement.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            brush = GradientTokens.sunsetHorizontal,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "« $whyStatement »",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Anthracite,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (!isEditing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBeige, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Vous n'avez pas encore défini votre raison profonde. Prenez un instant pour y réfléchir.",
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = GoldClassic,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isEditing) {
                OutlinedTextField(
                    value = textVal,
                    onValueChange = { textVal = it },
                    placeholder = {
                        Text(
                            "Écrivez ici votre motivation profonde... (ex: Mon mariage à venir, ma vitalité au quotidien, être fier de l'homme que je deviens)",
                            fontSize = 13.sp,
                            color = MediumGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            isEditing = false
                            textVal = whyStatement
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Anthracite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    GoldGradientButton(
                        text = "Enregistrer",
                        onClick = {
                            viewModel.saveWhyStatement(textVal)
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                GoldGradientButton(
                    text = "Modifier ma raison profonde",
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AffirmationsPage(viewModel: OperationsViewModel) {
    val gratitudeLogs by viewModel.gratitudeLogs.collectAsState()
    val todayStr = viewModel.getTodayDate()
    
    // Affirmation du moment state
    var currentAffirmation by remember { mutableStateOf(AffirmationsData.getRandomAffirmation()) }
    
    // Gratitude input states
    val todayLog = remember(gratitudeLogs, todayStr) { gratitudeLogs.find { it.date == todayStr } }
    var g1 by remember(todayLog) { mutableStateOf(todayLog?.gratitude1 ?: "") }
    var g2 by remember(todayLog) { mutableStateOf(todayLog?.gratitude2 ?: "") }
    var g3 by remember(todayLog) { mutableStateOf(todayLog?.gratitude3 ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PageHeader(
                title = "Affirmations & Gratitude",
                subtitle = "Programmez votre esprit pour le succès"
            )

            // SECTION 1: Affirmation du moment
            Text(
                text = "Affirmation du moment 💫",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        brush = GradientTokens.sunsetHorizontal,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "« $currentAffirmation »",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Anthracite,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    GoldGradientButton(
                        text = "Nouvelle affirmation",
                        onClick = { currentAffirmation = AffirmationsData.getRandomAffirmation() },
                        modifier = Modifier.testTag("new_affirmation_button")
                    )
                }
            }

            // SECTION 2: Gratitude du jour
            Text(
                text = "Gratitude du jour 🏆",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBeige, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Aujourd'hui, je suis reconnaissant pour...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )

                    OutlinedTextField(
                        value = g1,
                        onValueChange = { g1 = it },
                        placeholder = { Text("1. Première gratitude...", fontSize = 12.sp, color = MediumGray) },
                        modifier = Modifier.fillMaxWidth().testTag("gratitude_input_1"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = g2,
                        onValueChange = { g2 = it },
                        placeholder = { Text("2. Deuxième gratitude...", fontSize = 12.sp, color = MediumGray) },
                        modifier = Modifier.fillMaxWidth().testTag("gratitude_input_2"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = g3,
                        onValueChange = { g3 = it },
                        placeholder = { Text("3. Troisième gratitude...", fontSize = 12.sp, color = MediumGray) },
                        modifier = Modifier.fillMaxWidth().testTag("gratitude_input_3"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    GoldGradientButton(
                        text = "Enregistrer mes gratitudes",
                        onClick = {
                            viewModel.saveGratitude(todayStr, g1, g2, g3)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_gratitude_button")
                    )
                }
            }

            // Historique des gratitudes
            val historicLogs = remember(gratitudeLogs, todayStr) {
                gratitudeLogs.filter { it.date != todayStr && (it.gratitude1.isNotEmpty() || it.gratitude2.isNotEmpty() || it.gratitude3.isNotEmpty()) }
            }

            if (historicLogs.isNotEmpty()) {
                Text(
                    text = "Historique des gratitudes 📜",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )

                historicLogs.forEach { log ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val displayDate = try {
                                val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(log.date)
                                if (dateObj != null) {
                                    SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).format(dateObj)
                                } else {
                                    log.date
                                }
                            } catch (e: Exception) {
                                log.date
                            }
                            Text(
                                text = displayDate.replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic
                            )
                            if (log.gratitude1.isNotEmpty()) {
                                Text(text = "• ${log.gratitude1}", fontSize = 12.sp, color = Anthracite)
                            }
                            if (log.gratitude2.isNotEmpty()) {
                                Text(text = "• ${log.gratitude2}", fontSize = 12.sp, color = Anthracite)
                            }
                            if (log.gratitude3.isNotEmpty()) {
                                Text(text = "• ${log.gratitude3}", fontSize = 12.sp, color = Anthracite)
                            }
                        }
                    }
                }
            }

            // SECTION 3: Bibliothèque d'affirmations
            Text(
                text = "Bibliothèque d'affirmations 📚",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, LightGrayDivider, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(AffirmationsData.affirmations.size) { index ->
                        val affText = AffirmationsData.affirmations[index]
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "✨",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                            )
                            Text(
                                text = affText,
                                fontSize = 13.sp,
                                color = Anthracite,
                                lineHeight = 18.sp
                            )
                        }
                        if (index < AffirmationsData.affirmations.size - 1) {
                            Divider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = LightGrayDivider.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlobalRespirationController(viewModel: OperationsViewModel, onDismiss: () -> Unit) {
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
                "HOLD_HIGH" -> {
                    scaleValue.animateTo(1.43f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
                    scaleValue.animateTo(1.4f, animationSpec = tween(durationMillis = 1000, easing = LinearEasing))
                }
                "OUT" -> scaleValue.animateTo(0.9f, animationSpec = tween(durationMillis = 7000, easing = LinearEasing))
                "HOLD_LOW" -> {
                    scaleValue.animateTo(0.9f, animationSpec = tween(durationMillis = 2000, easing = LinearEasing))
                }
            }
        } else {
            scaleValue.animateTo(1f)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isRunning) {
            Text(
                text = "Sélectionnez la durée de votre séance de cohérence cardiaque (4-2-7-2) :",
                fontSize = 12.sp,
                color = MediumGray,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(3, 5, 10).forEach { m ->
                    val isS = durationMinutes == m
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .background(if (isS) LightBeige else Color.Transparent, RoundedCornerShape(8.dp))
                            .border(1.dp, if (isS) GoldClassic else LightGrayDivider, RoundedCornerShape(8.dp))
                            .clickable { durationMinutes = m }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("${m} min", fontSize = 12.sp, color = if (isS) GoldClassic else MediumGray, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            GoldGradientButton(
                text = "Commencer la session",
                onClick = { viewModel.startBreathingTimer(durationMinutes) },
                modifier = Modifier.fillMaxWidth().testTag("global_start_breathing")
            )
        } else {
            val currentCycleLabel = when (state) {
                "IN" -> "Inspire"
                "HOLD_HIGH" -> "Retiens"
                "OUT" -> "Expire"
                "HOLD_LOW" -> "Retiens"
                else -> ""
            }

            val formattedElapsed = "${totalElapsed / 60}:${String.format("%02d", totalElapsed % 60)}"
            Text(
                text = "Séance en cours : $formattedElapsed / ${durationMinutes}:00",
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
                            "HOLD_HIGH" -> GoldClassic.copy(alpha = 0.15f)
                            "OUT" -> Color(0xFFEBEBEB)
                            "HOLD_LOW" -> Color(0xFFDCDCDC)
                            else -> Color(0xFFEBEBEB)
                        },
                        shape = CircleShape
                    )
                    .border(2.dp, GoldClassic, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentCycleLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic,
                        textAlign = TextAlign.Center
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
                onClick = { viewModel.stopBreathingTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                modifier = Modifier.fillMaxWidth().testTag("global_stop_breathing"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Arrêter la séance", color = Color.White)
            }
        }
    }
}

data class Expert(
    val name: String,
    val dates: String,
    val domain: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val summary: String,
    val metaphor: String
)

@Composable
fun NeurosciencePage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    var activeTab by remember { mutableStateOf("Comment ça marche") }
    var currentLang by remember { mutableStateOf("FR") }

    val tabs = listOf(
        "Comment ça marche",
        "Pourquoi ça revient",
        "Affirmations",
        "Switch Ideas",
        "6 Experts"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PageHeader(
                title = "Neurosciences",
                subtitle = "Comprendre, reprogrammer et reconditionner ses patterns mentaux"
            )

            // Horizontal Tab Selector styled with sunset indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(LightGrayBg, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEach { tab ->
                    val isSel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .background(
                                brush = if (isSel) GradientTokens.sunsetHorizontal else SolidColor(Color.Transparent),
                                shape = RoundedCornerShape(10.dp)
                            )
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

            // Language switcher row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Langue / اللغة : ",
                    fontSize = 12.sp,
                    color = MediumGray,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier
                        .background(LightGrayBg, RoundedCornerShape(16.dp))
                        .padding(2.dp)
                ) {
                    listOf("FR", "AR").forEach { lang ->
                        val isSel = currentLang == lang
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = if (isSel) GradientTokens.sunsetHorizontal else SolidColor(Color.Transparent),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { currentLang = lang }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = lang,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else MediumGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tab Contents
            when (activeTab) {
                "Comment ça marche" -> CommentCaMarcheTab(currentLang)
                "Pourquoi ça revient" -> PourquoiCaRevientTab(currentLang)
                "Affirmations" -> AffirmationsInstantTab(currentLang, onNavigateToPage, viewModel)
                "Switch Ideas" -> SwitchIdeasTab(currentLang, onNavigateToPage, viewModel)
                "6 Experts" -> SixExpertsTab(currentLang)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun CommentCaMarcheTab(lang: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1
        NeuroscienceCard(
            titleFr = "Les deux systèmes en compétition 🧠",
            titleAr = "النظامان المتنافسان في الدماغ 🧠",
            textFr = "Ton cerveau fonctionne avec deux systèmes en compétition permanente. Le Cortex Préfrontal (juste derrière ton front) analyse, planifie et résiste à l'impulsion — mais il est lent et se fatigue vite. Le Circuit Limbique (plus ancien, plus rapide) contient tes habitudes automatiques et ne réfléchit pas, il réagit. Le neurotransmetteur clé de ce second système est la dopamine.",
            textAr = "يعمل دماغك بنظامين في حالة تنافس مستمر. قشرة الفص الجبهي (خلف جبهتك مباشرة) تحلل وتخطط وتقاوم الاندفاعات — لكنها بطيئة وتتعب بسرعة. أما الجهاز الحوفي (الأقدم والأسرع) فيحتوي على عاداتك التلقائية ولا يفكر بل يتفاعل. الناقل العصبي الأساسي لهذا النظام الثاني هو الدوبامين.",
            lang = lang
        )

        // Section 2
        NeuroscienceCard(
            titleFr = "La dopamine n'est pas le plaisir 🎯",
            titleAr = "الدوبامين ليس إشارة المتعة 🎯",
            textFr = "Découverte du neuroscientifique Wolfram Schultz : la dopamine n'est pas le signal du plaisir, mais celui de la surprise — la différence entre ce que ton cerveau attendait et ce qu'il a réellement reçu. Des années de stimulation intense recalibrent ce système vers des pics très hauts, rendant les petites récompenses normales moins satisfaisantes.",
            textAr = "اكتشاف عالم الأعصاب وولفرام شولتز: الدوبامين ليس إشارة المتعة، بل هو إشارة المفاجأة — الفرق بين ما كان يتوقعه دماغك وما تلقاه بالفعل. سنوات من التحفيز المكثف تعيد ضبط هذا النظام نحو قمم عالية جدًا، مما يجعل المكافآت الصغيرة العادية أقل إرضاءً.",
            lang = lang
        )

        // Section 3
        NeuroscienceCard(
            titleFr = "Le principe de Hebb et la plasticité 🧬",
            titleAr = "مبدأ هيب والمرونة العصبية 🧬",
            textFr = "Les neurones qui s'activent ensemble renforcent leur connexion (Donald Hebb, 1949). À l'inverse, un chemin neuronal non utilisé s'affaiblit progressivement. C'est la base biologique de tout reconditionnement — chaque répétition consciente construit littéralement un nouveau chemin physique dans ton cerveau.",
            textAr = "الخلايا العصبية التي تنشط معًا تقوي اتصالاتها (دونالد هيب، 1949). وعلى العكس، فإن المسار العصبي غير المستخدم يضعف تدريجيًا. هذا هو الأساس البيولوجي لكل إعادة برمجة — كل تكرار واعٍ يبني حرفيًا مسارًا ماديًا جديدًا في دماغك.",
            lang = lang
        )
    }
}

@Composable
fun NeuroscienceCard(
    titleFr: String,
    titleAr: String,
    textFr: String,
    textAr: String,
    lang: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .align(Alignment.CenterVertically)
                    .background(brush = GradientTokens.sunsetHorizontal, shape = RoundedCornerShape(2.dp))
                    .height(60.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (lang == "FR") titleFr else titleAr,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (lang == "FR") textFr else textAr,
                    fontSize = 12.sp,
                    color = Anthracite,
                    lineHeight = 18.sp,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PourquoiCaRevientTab(lang: String) {
    val scope = rememberCoroutineScope()
    val observeProgress = remember { Animatable(0f) }
    val reactProgress = remember { Animatable(0f) }
    var activeChoice by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Intro Text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBeige, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == "FR") "Le mécanisme de l'impulsion ⚡" else "آلية الاندفاع المفاجئ ⚡",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (lang == "FR") {
                        "Voici la séquence exacte de ce qui se passe quand un ancien pattern surgit soudainement : un déclencheur (fatigue, stress, ennui) active un chemin neuronal ancien et bien renforcé. Si ton cortex préfrontal est affaibli à ce moment (fatigue, stress), le circuit automatique prend le dessus avant que tu en aies pleinement conscience. C'est à l'instant où tu observes consciemment ce qui se passe que tu interromps la boucle."
                    } else {
                        "إليك التسلسل الدقيق لما يحدث عندما يظهر نمط قديم فجأة: محفز (تعب، توتر، ملل) ينشط مسارًا عصبيًا قديمًا وقويًا. إذا كانت قشرتك الجبهية الأمامية ضعيفة في تلك اللحظة (بسبب التعب أو التوتر)، فإن الدارة التلقائية تسيطر قبل أن تعي ذلك تمامًا. في اللحظة التي تلاحظ فيها ما يحدث بوعي تام، فإنك تقطع هذه الحلقة التلقائية."
                    },
                    fontSize = 12.sp,
                    color = Anthracite,
                    lineHeight = 18.sp,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Illustration Title
        Text(
            text = if (lang == "FR") "Schéma d'interaction neuronal 🧠" else "مخطط التفاعل العصبي 🧠",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        // Illustration Interactive
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(16.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridStep = 20.dp.toPx()
                for (x in 0 until (size.width / gridStep).toInt()) {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.15f),
                        start = Offset(x * gridStep, 0f),
                        end = Offset(x * gridStep, size.height),
                        strokeWidth = 0.5f
                    )
                }
                for (y in 0 until (size.height / gridStep).toInt()) {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.15f),
                        start = Offset(0f, y * gridStep),
                        end = Offset(size.width, y * gridStep),
                        strokeWidth = 0.5f
                    )
                }

                val width = size.width
                val height = size.height
                val centerX = width / 2
                val centerY = height / 2
                val topY = height * 0.22f
                val bottomY = height * 0.78f

                // Base grey connections
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(centerX, centerY),
                    end = Offset(centerX, topY),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(centerX, centerY),
                    end = Offset(centerX, bottomY),
                    strokeWidth = 4f
                )

                // Draw active flows
                if (activeChoice == "observe" && observeProgress.value > 0f) {
                    val currentY = centerY - (centerY - topY) * observeProgress.value
                    drawLine(
                        brush = GradientTokens.sunsetHorizontal,
                        start = Offset(centerX, centerY),
                        end = Offset(centerX, currentY),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }

                if (activeChoice == "react" && reactProgress.value > 0f) {
                    val currentY = centerY + (bottomY - centerY) * reactProgress.value
                    drawLine(
                        color = Anthracite,
                        start = Offset(centerX, centerY),
                        end = Offset(centerX, currentY),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }

                // Node Circles
                // Prefrontal (Top)
                drawCircle(
                    brush = if (activeChoice == "observe" && observeProgress.value > 0.8f) GradientTokens.sunsetHorizontal else SolidColor(Color.Gray),
                    radius = 28.dp.toPx(),
                    center = Offset(centerX, topY)
                )
                // Center Trigger
                drawCircle(
                    color = Color(0xFFE65100),
                    radius = 14.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                // Limbic (Bottom)
                drawCircle(
                    color = if (activeChoice == "react" && reactProgress.value > 0.8f) Anthracite else Color.Gray,
                    radius = 28.dp.toPx(),
                    center = Offset(centerX, bottomY)
                )
            }

            // Labels over top circle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp)
            ) {
                Text(
                    text = if (lang == "FR") "Cortex Préfrontal 🧠" else "قشرة الفص الجبهي 🧠",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (lang == "FR") "OBSERVATION" else "ملاحظة بوعي",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Label over Center Trigger
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (lang == "FR") "Déclencheur ⚡" else "المحفز ⚡",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = if (lang == "FR") "(Fatigue, Stress, Ennui)" else "(تعب، توتر، ملل)",
                        fontSize = 9.sp,
                        color = MediumGray
                    )
                }
            }

            // Labels over bottom circle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
            ) {
                Text(
                    text = if (lang == "FR") "Circuit Limbique 🔄" else "الجهاز الحوفي 🔄",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (lang == "FR") "RÉACTION AUTOMATIQUE" else "تفاعل تلقائي",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        activeChoice = "observe"
                        reactProgress.snapTo(0f)
                        observeProgress.snapTo(0f)
                        observeProgress.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("button_observe"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic)
            ) {
                Text(
                    text = if (lang == "FR") "J'observe 👁️" else "أنا ألاحظ 👁️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        activeChoice = "react"
                        observeProgress.snapTo(0f)
                        reactProgress.snapTo(0f)
                        reactProgress.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("button_react_auto"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text(
                    text = if (lang == "FR") "Je réagis 🔄" else "أنا أتفاعل تلقائياً 🔄",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
            }
        }

        // Result explanations
        AnimatedVisibility(visible = activeChoice != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(
                        width = 1.5.dp,
                        brush = if (activeChoice == "observe") GradientTokens.sunsetHorizontal else SolidColor(Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (activeChoice == "observe") {
                            if (lang == "FR") "Effet : Court-circuit conscient 🧘" else "الأثر: قطع الدارة بوعي 🧘"
                        } else {
                            if (lang == "FR") "Effet : Renforcement du pattern 🔄" else "الأثر: تعزيز النمط التلقائي 🔄"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeChoice == "observe") GoldClassic else Anthracite,
                        textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = if (activeChoice == "observe") {
                            if (lang == "FR") {
                                "Le circuit d'observation se renforce. L'ancien circuit ne reçoit aucun renforcement cette fois."
                            } else {
                                "مسار المراقبة يتقوى تدريجيًا. المسار العصبي القديم لا يتلقى أي تعزيز هذه المرة مما يساهم في إضعافه."
                            }
                        } else {
                            if (lang == "FR") {
                                "L'ancien circuit reçoit un renforcement. Ce n'est pas un échec définitif — juste une donnée pour la prochaine fois."
                            } else {
                                "يتلقى المسار القديم تعزيزًا إضافيًا. هذا ليس فشلاً نهائيًا أبدًا — بل مجرد معلومة وتجربة للاستفادة منها في المرة القادمة."
                            }
                        },
                        fontSize = 11.sp,
                        color = Anthracite,
                        lineHeight = 16.sp,
                        textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AffirmationsInstantTab(lang: String, onNavigate: (Int) -> Unit, viewModel: OperationsViewModel) {
    val items = listOf(
        Pair(
            "Ceci est un ancien circuit qui s'affaiblit. Je l'observe, je ne le nourris pas.",
            "هذا مسار عصبي قديم يضعف. أنا أراقبه، ولا أغذيه."
        ),
        Pair(
            "Chaque fois que je résiste, je gagne.",
            "في كل مرة أقاوم فيها، أفوز."
        ),
        Pair(
            "Je suis l'observateur, pas la pensée.",
            "أنا المراقب، لست الفكرة."
        ),
        Pair(
            "Mon cerveau se reconstruit chaque jour, avec ou sans mon attention — alors je choisis d'y faire attention.",
            "دماغي يعيد بناء نفسه كل يوم، بانتباهي أو بدونه — لذا أختار أن أنتبه."
        ),
        Pair(
            "Cette envie va monter, puis redescendre. Je n'ai rien à faire sauf attendre.",
            "هذه الرغبة سترتفع ثم تنخفض. لا شيء عليّ فعله سوى الانتظار."
        ),
        Pair(
            "Ce n'est pas moi qui échoue. C'est un vieux chemin qui essaie de survivre.",
            "لست أنا من يفشل. إنه مسار قديم يحاول البقاء."
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (lang == "FR") "Affirmations d'urgence dans l'instant 💫" else "توكيدات فورية لحظة الاندفاع 💫",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        items.forEach { pair ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == "FR") pair.first else pair.second,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Anthracite,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 18.sp,
                        textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = if (lang == "FR") pair.second else pair.first,
                        fontSize = 10.sp,
                        color = MediumGray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = if (lang == "AR") TextAlign.Left else TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        GoldGradientButton(
            text = if (lang == "FR") "Lancer le Surf de l'urgence 🌊" else "بدء ركوب موجة الإلحاح 🌊",
            onClick = {
                viewModel.setRecoveryActiveTab("Reconditionnement")
                onNavigate(5)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("launch_urge_surf")
        )
    }
}

@Composable
fun SwitchIdeasTab(lang: String, onNavigate: (Int) -> Unit, viewModel: OperationsViewModel) {
    val ideas = listOf(
        Triple(
            "Se lever et marcher 2 minutes 🚶",
            "النهوض والمشي لمدة دقيقتين 🚶",
            null
        ),
        Triple(
            "Faire 10 respirations profondes 🌬️",
            "أخذ 10 أنفاس عميقة ومريحة 🌬️",
            "Respiration"
        ),
        Triple(
            "Écrire une phrase dans le Journal de victoires ✍️",
            "كتابة جملة في دفتر الانتصارات اليومي ✍️",
            "Journal"
        ),
        Triple(
            "Faire une série de Kegel / Reverse Kegel 🏋️",
            "القيام بجلسة تمارين كيجل أو كيجل العكسي 🏋️",
            "Kegel"
        ),
        Triple(
            "Appeler/texter quelqu'un de confiance 📞",
            "الاتصال أو مراسلة شخص تثق به 📞",
            null
        ),
        Triple(
            "Sortir prendre l'air 5 minutes 🌲",
            "الخروج لتنشق الهواء النقي لمدة 5 دقائق 🌲",
            null
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == "FR") "Le double effet neurologique du Switch 🔄" else "الأثر العصبي المزدوج لإعادة توجيه الانتباه 🔄",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (lang == "FR") {
                        "Rediriger consciemment ton attention n'est pas de la distraction — c'est un double effet neurologique mesurable. En ne complétant pas l'action, tu prives l'ancien circuit de son renforcement habituel. En même temps, l'acte même de rediriger ton attention renforce le circuit d'observation et de contrôle conscient. C'est une compétition physique entre deux réseaux de neurones, et chaque Switch Idea fait pencher la balance."
                    } else {
                        "إعادة توجيه انتباهك بوعي كامل ليست مجرد إلهء — بل هي عملية ذات أثر عصبي مزدوج وقابل للقياس. عندما تمتنع عن إكمال السلوك القديم، فإنك تحرم تلك الدارة من تعزيزها المعتاد. وفي الوقت نفسه، فإن فعل إعادة توجيه تركيزك يقوي دارات المراقبة والتحكم الواعي. إنها منافسة فيزيائية حقيقية بين شبكاتك العصبية، وكل فكرة بديلة تميل الكفة لصالحك."
                    },
                    fontSize = 12.sp,
                    color = Anthracite,
                    lineHeight = 18.sp,
                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text(
            text = if (lang == "FR") "Idées d'action concrètes (Switch Ideas) 🎯" else "أفكار عملية وتلقائية لإعادة التوجيه (Switch Ideas) 🎯",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ideas.forEach { idea ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBeige.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .border(1.dp, LightGrayDivider, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == "FR") idea.first else idea.second,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite,
                                textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = if (lang == "FR") idea.second else idea.first,
                                fontSize = 10.sp,
                                color = MediumGray,
                                textAlign = if (lang == "AR") TextAlign.Left else TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (idea.third != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.setRecoveryActiveTab(idea.third!!)
                                    onNavigate(5)
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("switch_link_${idea.third}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (lang == "FR") "Lancer" else "ابدأ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Go",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SixExpertsTab(lang: String) {
    var selectedExpert by remember { mutableStateOf<Expert?>(null) }

    val experts = listOf(
        Expert(
            name = "Donald Hebb",
            dates = "22 juillet 1904 – 20 août 1985",
            domain = if (lang == "FR") "Psychologue canadien (Neurosciences)" else "عالم نفس كندي (العلوم العصبية)",
            icon = Icons.Outlined.Psychology,
            summary = "Auteur de 'The Organization of Behavior' (1949), il a posé les bases théoriques de l'apprentissage neuronal moderne — les neurones activés ensemble renforcent leur connexion. Considéré comme le fondateur de la neuroscience computationnelle de l'apprentissage.",
            metaphor = "Un chemin de terre qui devient autoroute à force d'être emprunté — chaque répétition consciente construit littéralement un nouveau chemin physique dans le cerveau."
        ),
        Expert(
            name = "Wolfram Schultz",
            dates = "Professeur à Cambridge",
            domain = if (lang == "FR") "Neuroscientifique de la dopamine" else "عالم أعصاب متخصص في الدوبامين",
            icon = Icons.Outlined.Psychology,
            summary = "A découvert par enregistrement direct de neurones dans les années 1990 que la dopamine code l'erreur de prédiction de récompense (la différence entre attente et réalité), pas le plaisir lui-même. Récompensé par le Brain Prize en 2017.",
            metaphor = "Le cerveau n'est pas une machine à plaisir, c'est une machine à paris et surprises — recalibrer ses attentes recalibre sa satisfaction."
        ),
        Expert(
            name = "Judson Brewer",
            dates = "Né en 1974",
            domain = if (lang == "FR") "Psychiatre et Neuroscientifique" else "طبيب نفسي وعالم أعصاب",
            icon = Icons.Outlined.Psychology,
            summary = "A démontré par IRMf que la méthode RAIN (Reconnaître, Accepter, Investiguer, Ne-pas-s'identifier) désactive directement les circuits de rumination liés au craving. Auteur de 'The Craving Mind' et 'Unwinding Anxiety'.",
            metaphor = "Observer une envie comme une vague qui monte, atteint un pic, puis redescend d'elle-même — sans jamais avoir besoin d'y céder."
        ),
        Expert(
            name = "Anna Lembke",
            dates = "Née le 27 novembre 1967",
            domain = if (lang == "FR") "Psychiatre & Professeure à Stanford" else "طبيبة نفسية وأستاذة في جامعة ستانفورد",
            icon = Icons.Outlined.Psychology,
            summary = "Dans 'Dopamine Nation' (2021), elle démontre que plaisir et douleur sont traités par les mêmes zones cérébrales en balance constante — la sur-stimulation répétée fait pencher durablement cette balance vers le manque, et l'abstinence temporaire volontaire permet de la rééquilibrer.",
            metaphor = "Une balançoire dans le cerveau — chaque pic de plaisir intense fait redescendre fort de l'autre côté juste après."
        ),
        Expert(
            name = "Michael Merzenich",
            dates = "Né en 1942",
            domain = if (lang == "FR") "Neuroscientifique (Neuroplasticité)" else "عالم أعصاب (المرونة العصبية)",
            icon = Icons.Outlined.Psychology,
            summary = "A prouvé expérimentalement dès les années 1980-90 que le cerveau adulte reste physiquement modifiable toute la vie, contredisant le dogme antérieur d'un cerveau adulte figé. Auteur de 'Soft-Wired'.",
            metaphor = "Le cerveau change comme un muscle — mais seulement si l'attention est pleinement engagée pendant la pratique, pas en pilote automatique."
        ),
        Expert(
            name = "Épictète",
            dates = "vers 50 apr. J.-C. – vers 135 apr. J.-C.",
            domain = if (lang == "FR") "Philosophe Stoïcien" else "فيلسوف رواقي",
            icon = Icons.Outlined.AccountBalance,
            summary = "A formulé la 'dichotomie du contrôle' — séparer ce qui dépend de nous (notre réponse) de ce qui n'en dépend pas (l'apparition d'une pensée ou d'une impulsion). Sa discipline de 'l'assentiment', observer une impression avant de lui donner son accord, préfigure de 2000 ans les techniques modernes de pleine conscience.",
            metaphor = "Une pensée qui surgit n'est pas un échec — c'est automatique et hors de ton contrôle. Ta réponse, elle, dépend entièrement de toi."
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = if (lang == "FR") "6 Experts des Neurosciences & Modèles mentaux 📚" else "6 خبراء في العلوم العصبية والنماذج الفكرية 📚",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        if (index < experts.size) {
                            val exp = experts[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, LightGrayDivider, RoundedCornerShape(12.dp))
                                    .clickable { selectedExpert = exp }
                                    .padding(14.dp)
                                    .testTag("expert_card_$index")
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(LightBeige, CircleShape)
                                            .border(1.dp, GoldClassic.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = exp.icon,
                                            contentDescription = exp.name,
                                            tint = GoldClassic,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(
                                            text = exp.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Anthracite,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = exp.dates,
                                            fontSize = 9.sp,
                                            color = MediumGray,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = exp.domain,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = GoldClassic,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedExpert?.let { exp ->
            androidx.compose.ui.window.Dialog(onDismissRequest = { selectedExpert = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(LightBeige, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = exp.icon,
                                        contentDescription = exp.name,
                                        tint = GoldClassic,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(text = exp.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                                    Text(text = exp.dates, fontSize = 9.sp, color = MediumGray)
                                }
                            }
                            IconButton(
                                onClick = { selectedExpert = null },
                                modifier = Modifier.size(28.dp).testTag("close_expert_dialog")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MediumGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (lang == "FR") "Résumé scientifique" else "الملخص العلمي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic,
                                textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = exp.summary,
                                fontSize = 12.sp,
                                color = Anthracite,
                                lineHeight = 18.sp,
                                textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightBeige, RoundedCornerShape(10.dp))
                                .border(1.dp, GoldClassic.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (lang == "FR") "Métaphore / Image simplifiée 💡" else "مفهوم مبسط / تمثيل مجازي 💡",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = exp.metaphor,
                                    fontSize = 11.sp,
                                    color = Anthracite,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 16.sp,
                                    textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (lang == "FR") "🔧 Atelier d'Application Pratique" else "🔧 الورشة التطبيقية للخبير",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic,
                            textAlign = if (lang == "AR") TextAlign.Right else TextAlign.Left,
                            modifier = Modifier.fillMaxWidth()
                        )

                        when (exp.name) {
                            "Donald Hebb" -> HebbSimulator(lang)
                            "Wolfram Schultz" -> SchultzSimulator(lang)
                            "Judson Brewer" -> BrewerSimulator(lang)
                            "Anna Lembke" -> LembkeSimulator(lang)
                            "Michael Merzenich" -> MerzenichSimulator(lang)
                            "Épictète" -> EpictetusSimulator(lang)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HebbSimulator(lang: String) {
    var synapticStrength by remember { mutableStateOf(0.2f) }
    val scope = rememberCoroutineScope()
    val pulseProgress = remember { Animatable(0f) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "🔬 Simulateur de Plasticité Synaptique" else "🔬 محاكي المرونة المشبكية",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )
        
        Text(
            text = if (lang == "FR") {
                "STIMULATION SIMULTANÉE : Activez simultanément les neurones pré et post-synaptiques pour renforcer la gaine de myéline et l'efficacité de la transmission (LTP)."
            } else {
                "التحفيز المتزامن: قم بتنشيط الخلايا العصبية قبل وبعد المشبكية في نفس الوقت لتقوية غمد الميالين وكفاءة النقل العصبي."
            },
            fontSize = 10.sp,
            color = MediumGray,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val neuronARad = 18.dp.toPx()
                val neuronBRad = 18.dp.toPx()
                val centerA = Offset(w * 0.2f, h * 0.5f)
                val centerB = Offset(w * 0.8f, h * 0.5f)
                
                val thickness = 2f + (synapticStrength * 16f)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GoldClassic.copy(alpha = 0.4f), GoldDeep.copy(alpha = synapticStrength))
                    ),
                    start = centerA,
                    end = centerB,
                    strokeWidth = thickness,
                    cap = StrokeCap.Round
                )
                
                drawCircle(
                    color = GoldClassic,
                    radius = neuronARad,
                    center = centerA
                )
                
                drawCircle(
                    color = if (synapticStrength > 0.6f) GoldDeep else Color.Gray,
                    radius = neuronBRad,
                    center = centerB
                )
                
                if (pulseProgress.value > 0f && pulseProgress.value < 1f) {
                    val currentPos = centerA + (centerB - centerA) * pulseProgress.value
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = currentPos
                    )
                    drawCircle(
                        color = GoldDeep.copy(alpha = 0.8f),
                        radius = 8.dp.toPx(),
                        center = currentPos,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "FR") "Neurone A\n(Déclencheur)" else "عصبون أ\n(المثير)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text = if (lang == "FR") "Neurone B\n(Action)" else "عصبون ب\n(السلوك)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lang == "FR") "Force de la connexion :" else "قوة الرابط العصبي :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Anthracite
            )
            Text(
                text = "${(synapticStrength * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldDeep
            )
        }
        
        LinearProgressIndicator(
            progress = { synapticStrength },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = GoldClassic,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        synapticStrength = (synapticStrength + 0.15f).coerceAtMost(1.0f)
                        pulseProgress.snapTo(0f)
                        pulseProgress.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (lang == "FR") "⚡ Activer ensemble" else "⚡ تنشيط مشترك",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Button(
                onClick = {
                    synapticStrength = (synapticStrength - 0.2f).coerceAtLeast(0.1f)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (lang == "FR") "⏳ Laisser dégrader" else "⏳ ترك الرابط يذبل",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = if (lang == "FR") {
                    "💡 CONSEIL DE DONALD HEBB : L'abstinence n'est pas passive. Chaque fois que l'impulsion s'active mais que vous redirigez votre attention vers un autre choix conscient (Switch), vous privez le vieux chemin A-B de renforcement. Sans co-activation, il s'élague naturellement."
                } else {
                    "💡 نصيحة دونالد هيب: الامتناع ليس سلبياً. في كل مرة يتم تنشيط المثير ولكنك تعيد توجيه انتباهك إلى خيار واعٍ آخر (Switch)، فإنك تحرم المسار القديم أ-ب من التعزيز. بدون تفعيل مشترك، فإنه يتقلم طبيعياً."
                },
                fontSize = 9.sp,
                color = Anthracite,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun SchultzSimulator(lang: String) {
    var expectation by remember { mutableStateOf(5f) }
    var actualReward by remember { mutableStateOf(5f) }
    
    val predictionError = actualReward - expectation
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "🎯 Balance d'Erreur de Prédiction (RPE)" else "🎯 ميزان خطأ التنبؤ بالمكافأة",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )
        
        Text(
            text = if (lang == "FR") {
                "DOPAMINE = Récompense Réelle - Attente.\nAjustez les curseurs pour voir comment votre niveau de dopamine réagit biologiquement."
            } else {
                "الدوبامين = المكافأة الفعلية - التوقعات.\nقم بتعديل المؤشرات لترى كيف يتفاعل مستوى الدوبامين لديك عصبياً."
            },
            fontSize = 10.sp,
            color = MediumGray,
            textAlign = TextAlign.Center
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "FR") "Attente / Niveau d'excitation espéré :" else "التوقعات / مستوى الإثارة المتوقع :",
                    fontSize = 10.sp,
                    color = Anthracite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = expectation.toInt().toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )
            }
            Slider(
                value = expectation,
                onValueChange = { expectation = it },
                valueRange = 0f..10f,
                colors = SliderDefaults.colors(
                    activeTrackColor = GoldClassic,
                    thumbColor = GoldClassic
                )
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "FR") "Récompense Réelle obtenue :" else "المكافأة الفعلية التي تم تلقيها :",
                    fontSize = 10.sp,
                    color = Anthracite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = actualReward.toInt().toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
            }
            Slider(
                value = actualReward,
                onValueChange = { actualReward = it },
                valueRange = 0f..10f,
                colors = SliderDefaults.colors(
                    activeTrackColor = Anthracite,
                    thumbColor = Anthracite
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (lang == "FR") "Erreur de Prédiction de la Dopamine (RPE) :" else "خطأ التنبؤ بالدوبامين (RPE) :",
                    fontSize = 10.sp,
                    color = MediumGray
                )
                
                Text(
                    text = if (predictionError > 0.1f) {
                        "+${String.format(Locale.US, "%.1f", predictionError)} (SURPRISE POSITIVE! 🚀)"
                    } else if (predictionError < -0.1f) {
                        "${String.format(Locale.US, "%.1f", predictionError)} (DOPAMINE CRASH! 📉)"
                    } else {
                        "0.0 (NEUTRE / HABITUDE 💤)"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (predictionError > 0.1f) GoldDeep else if (predictionError < -0.1f) Color(0xFFC62828) else Color.Gray
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(LightGrayBg, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = ((predictionError + 10f) / 20f).coerceIn(0f, 1f))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (predictionError > 0.1f) {
                                        listOf(GoldClassic, GoldDeep)
                                    } else if (predictionError < -0.1f) {
                                        listOf(Color(0xFFEF9A9A), Color(0xFFC62828))
                                    } else {
                                        listOf(Color.LightGray, Color.Gray)
                                    }
                                ),
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }

                Text(
                    text = if (predictionError > 0.1f) {
                        if (lang == "FR") {
                            "🔥 Le cerveau libère une énorme décharge de dopamine car l'événement dépasse l'attente. C'est le moteur de l'addiction : la recherche constante de ce pic inattendu."
                        } else {
                            "🔥 يطلق الدماغ دفقة ضخمة من الدوبامين لأن الحدث تجاوز التوقعات. هذا هو محرك الإدمان الأساسي: البحث المستمر عن تلك المفاجأة غير المتوقعة."
                        }
                    } else if (predictionError < -0.1f) {
                        if (lang == "FR") {
                            "📉 Catastrophe dopaminergique ! Vous attendiez beaucoup d'excitation mais la réalité est décevante. Cela crée un vide insoutenable (craving, frustration) qui vous pousse à consommer à nouveau pour compenser."
                        } else {
                            "📉 انهيار الدوبامين! كنت تتوقع إثارة عالية لكن الواقع كان مخيباً للآمال. يخلق هذا فجوة حادة (الرغبة الشديدة، الإحباط) تدفعك للتكرار للتعويض."
                        }
                    } else {
                        if (lang == "FR") {
                            "💤 Routine. Le cerveau a parfaitement anticipé la récompense. Aucune libération surprise de dopamine. L'activité perd sa saveur excitante."
                        } else {
                            "💤 رتابة. لقد توقع الدماغ المكافأة تمامًا. لا إفراز مفاجئ للدوبامين. النشاط يفقد بريقه المثير تدريجياً."
                        }
                    },
                    fontSize = 10.sp,
                    color = Anthracite,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BrewerSimulator(lang: String) {
    var step by remember { mutableStateOf(1) }
    val check1 = remember { mutableStateOf(false) }
    val check2 = remember { mutableStateOf(false) }
    val check3 = remember { mutableStateOf(false) }
    val check4 = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "🌊 Guide de Pleine Conscience RAIN" else "🌊 دليل اليقظة الذهنية والركوب على الموجة (RAIN)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("R", "A", "I", "N").forEachIndexed { index, name ->
                val active = step > index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(
                            color = if (active) GoldClassic else Color.LightGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            when (step) {
                1 -> Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == "FR") "R - RECONNAÎTRE (Recognize) 👀" else "R - التعرف والإدراك (Recognize) 👀",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                    Text(
                        text = if (lang == "FR") {
                            "Dès que l'impulsion surgit, nommez-la consciemment. Dites-vous : 'L'envie est là.' ou 'Ah, voilà un pattern de craving.' Le simple fait de nommer l'émotion désengage l'automatisme inconscient."
                        } else {
                            "بمجرد ظهور الاندفاع، حدده وسمّه بوعي. قل لنفسك: 'الرغبة موجودة الآن.' أو 'آه، هذا نمط رغبة يرتفع.' مجرد التسمية يفصلك عن التلقائية اللاشعورية."
                        },
                        fontSize = 10.sp,
                        color = Anthracite,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(if (lang == "FR") "Reconnu 👁️" else "تم التعرف 👁️", fontSize = 10.sp, color = Color.White)
                    }
                }
                2 -> Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == "FR") "A - ACCEPTER (Allow) 🧘" else "A - القبول والسماح (Allow) 🧘",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                    Text(
                        text = if (lang == "FR") {
                            "Laissez cette sensation exister. Ne la combattez pas (ce qui l'alimenterait en stress) et ne cédez pas non plus. C'est juste un signal électrique temporaire dans votre tête."
                        } else {
                            "اسمح لهذا الشعور بالوجود. لا تحاربه (مما يغذيه بالتوتر) ولا تستسلم له كذلك. إنه مجرد إشارة كهربائية مؤقتة في دماغك."
                        },
                        fontSize = 10.sp,
                        color = Anthracite,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { step = 3 },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(if (lang == "FR") "J'accepte et je respire 🌬️" else "أقبل وأتنفس بعمق 🌬️", fontSize = 10.sp, color = Color.White)
                    }
                }
                3 -> Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == "FR") "I - INVESTIGUER (Investigate) 🔎" else "I - الاستكشاف والتقصي (Investigate) 🔎",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = if (lang == "FR") {
                            "Explorez votre corps avec curiosité scientifique. Où ressentez-vous l'envie ? Cochez les sensations présentes :"
                        } else {
                            "استكشف جسدك بفضول علمي. أين تشعر بالرغبة بالضبط؟ حدد الأحاسيس الجسدية الحالية:"
                        },
                        fontSize = 10.sp,
                        color = Anthracite,
                        lineHeight = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                    
                    listOf(
                        Pair(check1, if (lang == "FR") "Serrage ou boule dans la gorge/poitrine" else "ضيق أو غصة في الحلق/الصدر"),
                        Pair(check2, if (lang == "FR") "Tension ou agitation dans les mains/jambes" else "توتر أو تململ في اليدين/الرجلين"),
                        Pair(check3, if (lang == "FR") "Accélération du rythme cardiaque" else "تسارع نبضات القلب"),
                        Pair(check4, if (lang == "FR") "Pensée répétitive et obsédante en boucle" else "تفكير متكرر وملح يدور في حلقة مفرغة")
                    ).forEach { pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { pair.first.value = !pair.first.value }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .border(1.dp, GoldClassic, RoundedCornerShape(4.dp))
                                    .background(
                                        if (pair.first.value) GoldClassic else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (pair.first.value) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "checked",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = pair.second, fontSize = 9.sp, color = Anthracite)
                        }
                    }

                    Button(
                        onClick = { step = 4 },
                        enabled = check1.value || check2.value || check3.value || check4.value,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .align(Alignment.CenterHorizontally),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(if (lang == "FR") "Exploré avec curiosité 🔎" else "تم الاستكشاف بفضول 🔎", fontSize = 10.sp, color = Color.White)
                    }
                }
                4 -> Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (lang == "FR") "N - NE PAS S'IDENTIFIER (Non-Identify) 🌊" else "N - عدم التماهي / التحرر (Non-Identify) 🌊",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                    Text(
                        text = if (lang == "FR") {
                            "Réalisez que vous êtes l'observateur de l'envie, pas l'envie elle-même. Répétez-vous : 'Je ressens une impulsion, mais je ne suis pas cette impulsion.' Regardez-la redescendre et s'évaporer lentement."
                        } else {
                            "أدرك تماماً أنك المراقب لهذه الرغبة، ولست الرغبة نفسها. قل لنفسك: 'أنا أشعر بالاندفاع، لكني لست هذا الاندفاع.' شاهدها وهي تنخفض وتتلاشى ببطء."
                        },
                        fontSize = 10.sp,
                        color = Anthracite,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            step = 1
                            check1.value = false
                            check2.value = false
                            check3.value = false
                            check4.value = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldDeep),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(if (lang == "FR") "Libéré, recommencer 🔄" else "حر ومستعد، إعادة المحاولة 🔄", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun LembkeSimulator(lang: String) {
    var stateType by remember { mutableStateOf("neutral") }
    
    val rotationAngle by animateFloatAsState(
        targetValue = when (stateType) {
            "pleasure" -> -15f
            "gremlin" -> 18f
            "pain_effort" -> 12f
            else -> 0f
        },
        animationSpec = tween(1200, easing = FastOutSlowInEasing), label = "SeeSawRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "⚖️ Balance Plaisir-Douleur" else "⚖️ ميزان اللذة والألم",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )
        
        Text(
            text = if (lang == "FR") {
                "Plaisir et douleur sont traités dans la même zone cérébrale. Cliquez pour ajouter des stimuli et voir comment la balance se réajuste biologiquement."
            } else {
                "تتم معالجة اللذة والألم في نفس منطقة الدماغ. انقر لإضافة محفزات وشاهد كيف يعيد الميزان ضبط نفسه بيولوجياً."
            },
            fontSize = 10.sp,
            color = MediumGray,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val pivotX = w / 2f
                val pivotY = h * 0.75f
                
                val pivotPath = Path().apply {
                    moveTo(pivotX, pivotY)
                    lineTo(pivotX - 15.dp.toPx(), h * 0.95f)
                    lineTo(pivotX + 15.dp.toPx(), h * 0.95f)
                    close()
                }
                drawPath(pivotPath, color = Color.Gray)
                
                val boardLength = w * 0.75f
                
                rotate(degrees = rotationAngle, pivot = Offset(pivotX, pivotY)) {
                    drawLine(
                        color = Anthracite,
                        start = Offset(pivotX - boardLength / 2f, pivotY),
                        end = Offset(pivotX + boardLength / 2f, pivotY),
                        strokeWidth = 6f
                    )
                    
                    drawCircle(
                        color = GoldClassic,
                        radius = 8.dp.toPx(),
                        center = Offset(pivotX - boardLength / 2f, pivotY - 10.dp.toPx())
                    )
                    
                    drawCircle(
                        color = Color.DarkGray,
                        radius = 8.dp.toPx(),
                        center = Offset(pivotX + boardLength / 2f, pivotY - 10.dp.toPx())
                    )
                    
                    if (stateType == "gremlin") {
                        drawCircle(
                            color = Color(0xFFC62828),
                            radius = 12.dp.toPx(),
                            center = Offset(pivotX + boardLength / 2f, pivotY - 26.dp.toPx())
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "FR") "Plaisir\n(Dopamine)" else "اللذة\n(دوبامين)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (lang == "FR") "Douleur\n(Manque/Effort)" else "الألم\n(نقص/جهد)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    stateType = "pleasure"
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (lang == "FR") "🍬 Plaisir Facile" else "🍬 متعة سهلة",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    stateType = "gremlin"
                },
                enabled = stateType == "pleasure",
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (lang == "FR") "👾 Neuro-Gremlin" else "👾 جني التوازن",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    stateType = "pain_effort"
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (lang == "FR") "🏃 Douleur Saine" else "🏃 ألم صحي (جهد)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(1.dp, LightGrayDivider, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Text(
                text = when (stateType) {
                    "pleasure" -> if (lang == "FR") {
                        "🍬 PLAISIR DIRECT : Ajouter une dose de plaisir artificiel intense (ex: écrans, pornographie, sucre) fait immédiatement basculer la balance du côté du Plaisir. Le soulagement est instantané, mais de courte durée..."
                    } else {
                        "🍬 لذة مباشرة وسريعة: إدخال متعة اصطناعية مكثفة (مثل الشاشات، السلوكيات الإدمانية، السكر) يميل الكفة فوراً نحو اللذة. الارتياح سريع ولحظي، لكنه قصير الأجل..."
                    }
                    "gremlin" -> if (lang == "FR") {
                        "👾 LE GREMLIN DE L'HOMÉOSTASIE : Pour restaurer l'équilibre, votre cerveau envoie des 'gremlins' (mécanismes d'adaptation opposés) sur le côté de la Douleur pour faire pencher la balance de l'autre côté. C'est le crash dopaminergique post-plaisir, créant le manque, l'agitation et l'envie de recommencer."
                    } else {
                        "👾 جني التوازن العصبي: لاستعادة التوازن الطبيعي للدماغ، يرسل دماغك 'آليات تعويضية عصبية' (جنود تعويضية) لتقف في جانب الألم لتميل الكفة للجهة المقابلة بقوة. هذا هو الانهيار الدوباميني اللاحق للمتعة السريعة، مسبباً الرغبة الملحة والتململ."
                    }
                    "pain_effort" -> if (lang == "FR") {
                        "🏃 DOULEUR SAINE & EFFORT : En faisant un effort volontaire (méditation, sport, douche froide, lecture, abstinence), vous faites pencher la balance vers la Douleur/Effort. En réponse, le cerveau compense en ajoutant du plaisir naturel pour restaurer l'équilibre, augmentant durablement votre niveau de dopamine de base !"
                    } else {
                        "🏃 الألم الصحي والجهد الاختياري: عند بذل جهد واعٍ (تمارين كيجل، تأمل، رياضة، حمام بارد، امتناع)، يميل ميزانك نحو الألم/الجهد. كاستجابة تعويضية، يقوم الدماغ بإضافة اللذة الطبيعية ليعيد ضبط التوازن، مما يرفع مستوى الدوبامين الأساسي لديك لفترة طويلة وبشكل مستقر!"
                    }
                    else -> if (lang == "FR") {
                        "⚖️ ÉTAT NEUTRE : La balance est en équilibre instable. Cliquez sur 'Plaisir Facile' pour simuler une décharge rapide, ou sur 'Douleur Saine' pour voir comment un effort conscient reconstruit votre bien-être."
                    } else {
                        "⚖️ الحالة الطبيعية المتوازنة: الميزان في حالة تعادل مستقر. انقر على 'متعة سهلة' لمحاكاة دفق سريع، أو على 'ألم صحي' لترى كيف يعيد الجهد الواعي بناء صحتك العصبية."
                    }
                },
                fontSize = 10.sp,
                color = Anthracite,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun MerzenichSimulator(lang: String) {
    var randomNumber by remember { mutableStateOf((1..9).random()) }
    var score by remember { mutableStateOf(0) }
    var totalAttempts by remember { mutableStateOf(0) }
    var gameFeedback by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(900)
            randomNumber = (1..9).random()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "⚡ Entraîneur d'Attention Active" else "⚡ ممرن الانتباه الواعي الفعال",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )
        
        Text(
            text = if (lang == "FR") {
                "SANS ATTENTION, PAS DE MERVEILLE : Merzenich a prouvé que la neuroplasticité ne s'active QUE si vous êtes pleinement engagé. Cliquez sur le bouton d'action UNIQUEMENT lorsque le chiffre 7 apparaît !"
            } else {
                "بدون انتباه، لا توجد مرونة عصبية: أثبت ميرزنيخ أن المرونة لا تنشط إلا إذا كنت منتبهاً بالكامل. اضغط على الزر فقط عندما يظهر الرقم 7 على الشاشة!"
            },
            fontSize = 10.sp,
            color = MediumGray,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(70.dp)
                .background(Color.White, CircleShape)
                .border(2.dp, if (randomNumber == 7) GoldClassic else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = randomNumber.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (randomNumber == 7) GoldClassic else Anthracite
            )
        }

        Button(
            onClick = {
                totalAttempts++
                if (randomNumber == 7) {
                    score++
                    gameFeedback = if (lang == "FR") {
                        "🔥 ATTENTION ACTIVE ! Acétylcholine libérée. Les synapses s'ajustent physiquement !"
                    } else {
                        "🔥 انتباه نشط! تم إطلاق الأستيل كولين. الروابط العصبية تتعدل فيزيائياً!"
                    }
                } else {
                    gameFeedback = if (lang == "FR") {
                        "❌ PILOTE AUTOMATIQUE. Pas d'attention focalisée. Aucune plasticité générée."
                    } else {
                        "❌ القيادة التلقائية. لا يوجد تركيز بؤري. لم يتم تحفيز أي مرونة عصبية."
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Text(
                text = if (lang == "FR") "⚡ Capturer l'Instant (Cliquer sur 7) !" else "⚡ اقتنص اللحظة (اضغط عند ظهور 7) !",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (lang == "FR") "Synapses Reprogrammées :" else "الروابط المشبكية المعاد برمجتها :",
                fontSize = 10.sp,
                color = Anthracite
            )
            Text(
                text = "$score / $totalAttempts",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldClassic
            )
        }

        if (gameFeedback.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = gameFeedback,
                    fontSize = 9.sp,
                    color = if (gameFeedback.startsWith("🔥")) GoldDeep else Color(0xFFC62828),
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EpictetusSimulator(lang: String) {
    val items = listOf(
        Triple(
            "L'apparition soudaine d'une pensée ou d'une envie d'agir.",
            "الظهور المفاجئ لفكرة ملحة أو رغبة شديدة في التصرف.",
            false
        ),
        Triple(
            "Ma décision de respirer profondément et de faire un 'Switch'.",
            "قراري بأخذ نفس عميق والقيام بعملية تحويل الانتباه (Switch).",
            true
        ),
        Triple(
            "Le fait d'avoir rechuté ou cédé par le passé.",
            "وقوعي في الانتكاسة أو استسلامي للرغبة في الماضي.",
            false
        ),
        Triple(
            "Donner mon assentiment ou dire 'Oui' à la pensée intrusive.",
            "موافقتي العقلية أو قول 'نعم' للفكرة المقتحمة والاستسلام لها.",
            true
        ),
        Triple(
            "La fatigue physique ou le stress généré par ma journée.",
            "التعب الجسدي أو الإرهاق المتراكم من ساعات عملي.",
            false
        )
    )

    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Boolean?>(null) }
    var explanationText by remember { mutableStateOf("") }
    
    val currentItem = items[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBg, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (lang == "FR") "🏛️ Le Filtre de la Dichotomie du Contrôle" else "🏛️ فلتر ثنائية التحكم",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = GoldClassic
        )
        
        Text(
            text = if (lang == "FR") currentItem.first else currentItem.second,
            fontSize = 12.sp,
            color = Anthracite,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    selectedAnswer = true
                    explanationText = if (lang == "FR") {
                        if (currentItem.third) "✅ Correct ! C'est votre choix conscient, pleinement sous votre contrôle."
                        else "❌ Incorrect. Ceci est un événement externe ou une pensée automatique involontaire, hors de votre contrôle direct."
                    } else {
                        if (currentItem.third) "✅ صحيح! هذا اختيارك الواعي، تحت سيطرتك الكاملة."
                        else "❌ غير صحيح. هذا حدث خارجي أو فكرة تلقائية لا إرادية، خارجة عن سيطرتك المباشرة."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == true) GoldClassic else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldClassic),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Text(
                    text = if (lang == "FR") "Sous mon contrôle" else "تحت سيطرتي",
                    color = if (selectedAnswer == true) Color.White else GoldClassic,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    selectedAnswer = false
                    explanationText = if (lang == "FR") {
                        if (!currentItem.third) "✅ Correct ! Ceci est hors de votre contrôle direct. Vous ne devez pas gaspiller d'énergie dessus."
                        else "❌ Incorrect. C'est votre décision consciente, elle est pleinement sous votre contrôle."
                    } else {
                        if (!currentItem.third) "✅ صحيح! هذا خارج عن سيطرتك المباشرة. لا يجب أن تهدر طاقتك عليه."
                        else "❌ غير صحيح. هذا قرارك الواعي، وهو تحت سيطرتك الكاملة."
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == false) GoldClassic else Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldClassic),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Text(
                    text = if (lang == "FR") "Hors contrôle" else "خارج سيطرتي",
                    color = if (selectedAnswer == false) Color.White else GoldClassic,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (explanationText.isNotEmpty()) {
            Text(
                text = explanationText,
                fontSize = 10.sp,
                color = MediumGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Button(
                onClick = {
                    selectedAnswer = null
                    explanationText = ""
                    currentIndex = (currentIndex + 1) % items.size
                },
                colors = ButtonDefaults.buttonColors(containerColor = Anthracite),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text(
                    text = if (lang == "FR") "Suivant" else "التالي",
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}

data class LeadershipDiagnosticQuestion(
    val title: String,
    val scenario: String,
    val options: List<String>,
    val feedbacks: List<String>,
    val profiles: List<String> // "NICE", "CHEF", "ABSENT", "GOOD"
)

data class LeadershipSbiTemplate(
    val title: String,
    val situation: String,
    val behavior: String,
    val impact: String,
    val request: String
)

data class ChantierScenario(
    val title: String,
    val situation: String,
    val options: List<String>,
    val scores: List<Pair<Int, Int>>, // Pair(Warmth, Competence)
    val explanation: String
)

@Composable
fun LeadershipPage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    var activeTab by remember { mutableStateOf("Good Man vs Nice Man") }

    val tabs = listOf(
        "Good Man vs Nice Man",
        "Aura & Respect",
        "Gestion de Chantier",
        "MOSSAD LEVEL"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PageHeader(
                title = "Leadership",
                subtitle = "Management d'équipe professionnel et gestion de chantiers"
            )

            // Horizontal Tab Selector styled with sunset indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(LightGrayBg, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tabs.forEach { tab ->
                    val isSel = activeTab == tab
                    Box(
                        modifier = Modifier
                            .background(
                                brush = if (isSel) GradientTokens.sunsetHorizontal else SolidColor(Color.Transparent),
                                shape = RoundedCornerShape(10.dp)
                            )
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
                "Good Man vs Nice Man" -> TabGoodManVsNiceMan()
                "Aura & Respect" -> TabAuraAndRespect(onNavigateToPage)
                "Gestion de Chantier" -> TabGestionDeChantier()
                "MOSSAD LEVEL" -> TabMossadLevel()
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TabGoodManVsNiceMan() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1: Accordéon "Le modèle Chaleur/Compétence"
        var isChaleurExpanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isChaleurExpanded = !isChaleurExpanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Le modèle Chaleur/Compétence (Science Sociale)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (isChaleurExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (isChaleurExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Selon les travaux scientifiques de Susan Fiske et Amy Cuddy (Université Harvard), tout individu face à une figure d'autorité pose inconsciemment deux questions pour évaluer son statut :\n" +
                                "1. Quelles sont ses intentions à mon égard ? (Chaleur / Warmth - Intention)\n" +
                                "2. Est-il capable d'exécuter ses intentions ? (Compétence / Competence - Capacité)\n\n" +
                                "La combinaison optimale est la double haute valeur (Chaleur + Compétence), qui suscite l'admiration, la loyauté absolue et la confiance active des équipes.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BulletPoint(
                            boldText = "Petit Chef (Compétence haute / Chaleur basse)",
                            normalText = "Suscitation de peur et d'évitement. Le personnel fait le strict minimum syndical."
                        )
                        BulletPoint(
                            boldText = "Nice Man (Chaleur haute / Compétence basse)",
                            normalText = "Suscitation de sympathie mais mépris professionnel. Les consignes de sécurité et de délais sont ignorées car le leader veut plaire."
                        )
                        BulletPoint(
                            boldText = "Leader Absent (Basse Chaleur / Basse Compétence)",
                            normalText = "Indifférence et débandade de l'équipe."
                        )
                        BulletPoint(
                            boldText = "Good Man (Haute Chaleur / Haute Compétence) ✨",
                            normalText = "Respect absolu, autorité saine et engagement infaillible de l'équipe."
                        )
                    }
                }
            }
        }

        // Section 2: Questionnaire Diagnostic Interactif
        Text(
            text = "Test de Leadership : Êtes-vous Nice Man, Petit Chef ou Good Man ?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val questions = remember {
            listOf<LeadershipDiagnosticQuestion>(
                LeadershipDiagnosticQuestion(
                    title = "1. Un technicien qualifié fait une erreur répétée",
                    scenario = "Un maçon très expérimenté oublie pour la deuxième fois de vibrer le béton sur une poutre critique.",
                    options = listOf(
                        "Vous ne dites rien pour éviter de créer de la tension ou de le vexer.",
                        "Vous l'engueulez devant tout le monde sur le chantier : « Tu fais de la merde ! ».",
                        "Vous ignorez la situation, le béton finira bien par sécher.",
                        "Vous l'appelez de côté, lui montrez la poutre en expliquant le risque d'Eurocode 2, puis déterminez ensemble comment éviter cet oubli."
                    ),
                    feedbacks = listOf(
                        "Nice Man : Vous privilégiez le confort immédiat au détriment de la qualité technique.",
                        "Petit Chef : Vous cherchez à l'humilier, brisant sa motivation et l'esprit d'équipe.",
                        "Absent : Vous fuyez vos responsabilités de contrôle technique.",
                        "Good Man : Recadrage factuel et constructif, préservant le respect mutuel."
                    ),
                    profiles = listOf("NICE", "CHEF", "ABSENT", "GOOD")
                ),
                LeadershipDiagnosticQuestion(
                    title = "2. Livraison majeure retardée",
                    scenario = "Le fournisseur de béton annonce 1h30 de retard, menaçant de faire déborder les horaires de coulage.",
                    options = listOf(
                        "Vous vous excusez platement auprès des hommes en leur demandant s'ils acceptent gentiment de rester.",
                        "Vous leur criez dessus : « Personne ne rentre chez soi tant que ce n'est pas coulé, c'est comme ça ! ».",
                        "Vous fermez votre bureau de chantier à 17h et les laissez gérer le camion arrivant.",
                        "Vous réunissez l'équipe, expliquez l'impact technique du retard, restez avec eux sur le terrain et assurez les heures sup et primes."
                    ),
                    feedbacks = listOf(
                        "Nice Man : Vous suppliez au lieu de diriger.",
                        "Petit Chef : Autoritarisme aveugle sans considération humaine.",
                        "Absent : Désertion totale face à la crise.",
                        "Good Man : Management solidaire et responsable. Exemplarité absolue."
                    ),
                    profiles = listOf("NICE", "CHEF", "ABSENT", "GOOD")
                ),
                LeadershipDiagnosticQuestion(
                    title = "3. Un sous-traitant conteste une non-conformité",
                    scenario = "Le plombier refuse de reprendre un tuyau mal posé et affirme que vous chipotez.",
                    options = listOf(
                        "Vous acceptez son travail bâclé pour ne pas retarder le reste du chantier.",
                        "Vous menacez de le frapper ou de le chasser immédiatement du chantier physique.",
                        "Vous attendez que le contrôleur passe dans 2 semaines pour trancher à votre place.",
                        "Vous sortez calmement le cahier des charges et la norme DTU, prenez des mesures précises devant lui et suspendez ses acomptes."
                    ),
                    feedbacks = listOf(
                        "Nice Man : Soumission à l'agressivité d'un tiers.",
                        "Petit Chef : Violence inutile qui décrédibilise votre professionnalisme.",
                        "Absent : Manque d'assurance et fuite du conflit.",
                        "Good Man : Autorité calme basée sur les faits et les normes opposables."
                    ),
                    profiles = listOf("NICE", "CHEF", "ABSENT", "GOOD")
                ),
                LeadershipDiagnosticQuestion(
                    title = "4. Attitude lors d'un briefing sécurité (Pre-start)",
                    scenario = "L'équipe est distraite et rigole pendant la lecture des consignes.",
                    options = listOf(
                        "Vous riez avec eux et laissez tomber les consignes de sécurité.",
                        "Vous les menacez de sanctions disciplinaires immédiates sans chercher de dialogue.",
                        "Vous lisez le papier dans votre coin le nez baissé sans vous soucier de leur écoute.",
                        "Vous vous arrêtez de parler, fixez l'élément perturbateur en silence, puis reprenez sur un cas d'accident réel lié au sujet."
                    ),
                    feedbacks = listOf(
                        "Nice Man : La sécurité est sacrifiée pour être aimé de tous.",
                        "Petit Chef : Réaction impulsive basée sur la peur.",
                        "Absent : Animation fantôme sans présence réelle.",
                        "Good Man : Utilisation du silence lourd et recadrage ciblé basé sur la valeur humaine de la sécurité."
                    ),
                    profiles = listOf("NICE", "CHEF", "ABSENT", "GOOD")
                ),
                LeadershipDiagnosticQuestion(
                    title = "5. Un collaborateur performant mais arrogant",
                    scenario = "Votre meilleur conducteur d'engins refuse de remplir ses rapports journaliers d'activité.",
                    options = listOf(
                        "Vous l'en excusez car c'est un as du levage.",
                        "Vous le menacez de licenciement immédiat sans considération pour sa rareté sur le marché.",
                        "Vous remplissez vous-même ses rapports le soir en cachette.",
                        "Vous lui expliquez calmement que la précision administrative valide son exploit sur le terrain et que la règle est uniforme."
                    ),
                    feedbacks = listOf(
                        "Nice Man : Vous tolérez l'insubordination par peur de le perdre.",
                        "Petit Chef : Vous risquez de casser l'outil de production par orgueil personnel.",
                        "Absent : Vous travaillez à sa place au lieu de le manager.",
                        "Good Man : Traitement équitable, refus du traitement de faveur, communication valorisante."
                    ),
                    profiles = listOf("NICE", "CHEF", "ABSENT", "GOOD")
                )
            )
        }

        var currentDiagIndex by remember { mutableStateOf(0) }
        var selectedDiagOption by remember { mutableStateOf(-1) }
        val diagScores = remember { mutableStateListOf<String>() } // To track choices
        var showDiagResults by remember { mutableStateOf(false) }

        if (!showDiagResults) {
            val q = questions[currentDiagIndex]
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = q.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )
                        Text(
                            text = "Question ${currentDiagIndex + 1}/${questions.size}",
                            fontSize = 11.sp,
                            color = MediumGray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = q.scenario,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Anthracite,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    q.options.forEachIndexed { idx, option ->
                        val isSelected = selectedDiagOption == idx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    if (isSelected) LightBeige else LightGrayBg,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) GoldClassic else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedDiagOption = idx }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedDiagOption = idx },
                                colors = RadioButtonDefaults.colors(selectedColor = GoldClassic)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                color = Anthracite,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (selectedDiagOption != -1) {
                                    diagScores.add(q.profiles[selectedDiagOption])
                                    if (currentDiagIndex < questions.size - 1) {
                                        currentDiagIndex++
                                        selectedDiagOption = -1
                                    } else {
                                        showDiagResults = true
                                    }
                                }
                            },
                            enabled = selectedDiagOption != -1,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldClassic)
                        ) {
                            Text(
                                text = if (currentDiagIndex == questions.size - 1) "Terminer" else "Suivant",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Compute scores
            val goodCount = diagScores.count { it == "GOOD" }
            val niceCount = diagScores.count { it == "NICE" }
            val chefCount = diagScores.count { it == "CHEF" }
            val absentCount = diagScores.count { it == "ABSENT" }

            val dominantProfile = when {
                goodCount >= 3 -> "GOOD"
                niceCount >= 2 && niceCount >= chefCount -> "NICE"
                chefCount >= 2 && chefCount >= niceCount -> "CHEF"
                else -> "ABSENT"
            }

            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "VOTRE DIAGNOSTIC LEADERSHIP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )

                    val profileTitle = when (dominantProfile) {
                        "GOOD" -> "✨ GOOD MAN (Le Leader Naturel d'Aura)"
                        "NICE" -> "🥺 NICE MAN (Le Manager Trop Gentil)"
                        "CHEF" -> "😡 PETIT CHEF (L'Autoritaire Réactif)"
                        else -> "🌫️ LEADER ABSENT (L'Éviteur de Conflits)"
                    }

                    val profileDesc = when (dominantProfile) {
                        "GOOD" -> "Félicitations. Vous appliquez la science du leadership moderne : combinant fermeté incontournable et respect inaltérable des hommes. Vos équipes travaillent dur parce qu'elles vous respectent, pas parce qu'elles vous craignent."
                        "NICE" -> "Vous fuyez le conflit par désir d'être aimé. Conséquence : vos équipes vous apprécient mais ignorent vos consignes de sécurité ou de délais car elles savent que vous ne sévirez pas. Vous devez apprendre la fermeté tranquille."
                        "CHEF" -> "Vous gérez par la peur et la domination verbale. C'est inefficace à moyen terme : vos hommes sabotent le travail en douce ou quittent le chantier. Vous manquez de Chaleur, l'axe clé de la fidélisation."
                        else -> "Vous subissez le chantier au lieu de le mener. Vous fuyez les problèmes en espérant qu'ils se règlent seuls. Il est urgent d'adopter une posture engagée."
                    }

                    Text(
                        text = profileTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )

                    Text(
                        text = profileDesc,
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )

                    // Diagnostic Breakdown Charts
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Détails de vos réponses :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        ProgressBarDiagnostic("Good Man (Autorité / Respect)", goodCount, 5)
                        ProgressBarDiagnostic("Nice Man (Gentillesse / Évitement)", niceCount, 5)
                        ProgressBarDiagnostic("Petit Chef (Domination / Colère)", chefCount, 5)
                        ProgressBarDiagnostic("Leader Absent (Négligence)", absentCount, 5)
                    }

                    Button(
                        onClick = {
                            currentDiagIndex = 0
                            selectedDiagOption = -1
                            diagScores.clear()
                            showDiagResults = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Anthracite),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Recommencer le Test", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        // Section 3: SBI-R Generator
        Text(
            text = "Générateur de Script SBI-R (Situation-Comportement-Impact-Requête)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val sbiScenarios = listOf(
            LeadershipSbiTemplate(
                title = "Retard récurrent au briefing matinal",
                situation = "Ce matin à 8h pile, lors du rassemblement d'équipe dans l'algeco,",
                behavior = "tu es arrivé avec 15 minutes de retard pour la troisième fois de la semaine,",
                impact = "l'impact est que nous avons dû retarder la distribution des consignes de sécurité et le démarrage de la grue, pénalisant tout le reste de l'équipe qui attendait sous la pluie.",
                request = "Je te demande d'être présent à 7h55 pour qu'on démarre à l'heure exacte. Si tu as un problème de transport, appelle-moi avant 7h45. Sommes-nous d'accord ?"
            ),
            LeadershipSbiTemplate(
                title = "Non-port du harnais de sécurité",
                situation = "Il y a dix minutes sur le derrick du coffrage de rive,",
                behavior = "tu travaillais à 4 mètres du vide sans ton harnais de sécurité attaché à la ligne de vie,",
                impact = "l'impact est que tu risques une chute mortelle instantanée. De plus, les apprentis te voient et imitent cette infraction grave, ce qui m'expose pénalement et menace ta propre vie.",
                request = "Enfile et connecte ton harnais immédiatement. Au prochain manquement de sécurité de cette nature, je t'exclus définitivement de ce chantier. C'est non négociable."
            ),
            LeadershipSbiTemplate(
                title = "Travail de soudure mal nettoyé",
                situation = "Lors de mon inspection de fin de poste sur les structures d'acier,",
                behavior = "tu as laissé les scories et les éclats de soudure sans aucun meulage de finition,",
                impact = "l'impact est que le contrôleur technique va refuser le raccordement demain matin, entraînant une amende de retard de 4000 € et endommageant notre relation commerciale.",
                request = "Je te demande de reprendre la meuleuse maintenant et de nettoyer l'ensemble des assemblages avant de quitter le chantier ce soir. Je viens valider à 17h30."
            ),
            LeadershipSbiTemplate(
                title = "Arrogance et contestation publique",
                situation = "Tout à l'heure à midi devant l'ensemble des sous-traitants dans la zone de pause,",
                behavior = "tu as haussé le ton en criant que mes plans d'exécution étaient stupides et impossibles,",
                impact = "l'impact est que tu sapes la confiance des équipes et l'autorité globale de la maîtrise d'œuvre, propageant un climat d'insubordination collective délétère.",
                request = "Je respecte ton expérience technique. Si tu vois une anomalie, tu viens m'en parler calmement en tête-à-tête dans mon bureau de chantier. Les éclats publics sont exclus."
            )
        )

        var selectedSbiIndex by remember { mutableStateOf(0) }
        val sbi = sbiScenarios[selectedSbiIndex]

        OperationsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Choisissez une situation typique de chantier :",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )

                // Selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sbiScenarios.forEachIndexed { idx, item ->
                        val isSel = selectedSbiIndex == idx
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSel) GoldClassic else LightGrayBg,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedSbiIndex = idx }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else Anthracite
                            )
                        }
                    }
                }

                Divider(color = LightGrayDivider)

                Text(
                    text = "SCRIPT COMPLET SÉCURISÉ & INDESTRUCTIBLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )

                Column(
                    modifier = Modifier
                        .background(LightBeige, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SbiSegment("SITUATION (Faits de lieu & temps incontestables)", sbi.situation, GoldClassic)
                    SbiSegment("COMPORTEMENT (Observation physique uniquement, zéro jugement)", sbi.behavior, Anthracite)
                    SbiSegment("IMPACT (Conséquences réelles sur la sécurité ou le pognon)", sbi.impact, Color.Red.copy(alpha = 0.8f))
                    SbiSegment("REQUÊTE (Consigne nette, mesurable et non négociable)", sbi.request, Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Composable
fun ProgressBarDiagnostic(label: String, count: Int, total: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 10.sp, color = Anthracite)
            Text(text = "$count/$total", fontSize = 10.sp, color = MediumGray, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = count.toFloat() / total,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = if (label.startsWith("Good")) GoldClassic else MediumGray,
            trackColor = LightGrayBg
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}

@Composable
fun SbiSegment(label: String, text: String, color: Color) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = Anthracite,
            lineHeight = 15.sp,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun TabAuraAndRespect(onNavigateToPage: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1: Fondements scientifiques de l'Aura
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "La Science de l'Aura : Executive Presence",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "L'aura n'est pas un don inné magique. C'est l'application rigoureuse de la théorie de l'esprit (Premack & Woodruff) et du phénomène biologique de la contagion émotionnelle (Sigal Barsade, Université de Wharton). " +
                            "Les neurones miroirs de votre équipe scannent continuellement votre physiologie pour calquer leur niveau de panique ou de confiance sur le vôtre.\n\n" +
                            "Si vous êtes tendu, agité ou parlez vite, vous provoquez un piratage de l'amygdale chez les autres. Si vous êtes physiologiquement calme et parlez lentement, l'équipe s'aligne et se calme.",
                    fontSize = 12.sp,
                    color = MediumGray,
                    lineHeight = 17.sp
                )
            }
        }

        // Section 2: Les Protocoles Physiques
        Text(
            text = "Les 4 Protocoles Physiques Incontestables",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val protocols = listOf(
            Triple(
                "Le Protocole du Silence (La règle des 2 secondes)",
                "Ne répondez jamais instantanément à une question agressive ou complexe. Marquez un arrêt total de 2 secondes en maintenant un regard neutre. Ce silence signale que vous contrôlez vos émotions, fait monter le statut perçu, et force l'autre à écouter votre réponse.",
                "⏳ Calme l'amygdale"
            ),
            Triple(
                "La Resonante Diaphragmatique (Acoustique)",
                "Parlez depuis votre ventre (diaphragme), jamais depuis la gorge. Évitez absolument le ton interrogatif à la fin des phrases affirmatives (uptalk). Chaque phrase doit se terminer sur une note basse descendante pour sceller l'autorité.",
                "🗣️ Voix de poitrine"
            ),
            Triple(
                "L'Oculaire Triangulaire (Winston Churchill)",
                "Ne fuyez jamais le regard (soumission) et ne fixez pas l'autre continuellement (défi de combat). Adoptez le regard triangulaire de pouvoir : fixez l'œil gauche de l'interlocuteur pendant 3 secondes, puis l'œil droit, puis le centre de son front. C'est l'ancrage de la domination tranquille.",
                "👁️ Focus Churchill"
            ),
            Triple(
                "La Stabilité Posturale (Zéro Gesticulation)",
                "Gardez vos pieds ancrés parallèlement à largeur d'épaules, les mains ouvertes et calmes. Éliminez tous les micro-mouvements de décharge de stress (se toucher le visage, trifouiller un stylo, tapoter le pied). Moins vous bougez de manière désordonnée, plus votre autorité perçue est grande.",
                "🛡️ Ancrage au sol"
            )
        )

        protocols.forEach { (title, desc, tag) ->
            var isExp by remember { mutableStateOf(false) }
            OperationsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExp = !isExp }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .background(LightBeige, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = tag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        }
                    }
                    if (isExp) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = MediumGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section 3: Checklist interactive d'Aura quotidienne
        Text(
            text = "Audit Quotidien d'Aura (Auto-évaluation physique)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val checklistItems = remember {
            mutableStateListOf(
                Pair("J'ai marqué 2 secondes de silence avant chaque prise de décision difficile.", false),
                Pair("J'ai maintenu ma voix basse en évitant de monter dans les aigus.", false),
                Pair("J'ai ancré mon regard sans fuir devant les regards hostiles ou sceptiques.", false),
                Pair("Mes épaules et mon buste étaient ouverts et mes pieds bien campés au sol.", false),
                Pair("J'ai éliminé tout tic de manipulation (stylo, frottement de mains, portable).", false),
                Pair("J'ai dit « non » calmement et sans me confondre en justifications excessives.", false),
                Pair("J'ai absorbé la panique ambiante en affichant un visage neutre et stable.", false),
                Pair("J'ai formulé un feed-back SBI-R précis sans aucune attaque sur la personne.", false)
            )
        }

        val checkedCount = checklistItems.count { it.second }

        OperationsCard(borderAccent = true) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "AURA DE LEADERSHIP : $checkedCount/${checklistItems.size} PILIERS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldClassic
                )

                LinearProgressIndicator(
                    progress = checkedCount.toFloat() / checklistItems.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = GoldClassic,
                    trackColor = LightGrayBg
                )

                Spacer(modifier = Modifier.height(4.dp))

                checklistItems.forEachIndexed { index, (label, checked) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                checklistItems[index] = Pair(label, !checked)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                checklistItems[index] = Pair(label, !checked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = GoldClassic)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            color = if (checked) Anthracite else MediumGray,
                            fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabGestionDeChantier() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section 1: Hersey-Blanchard Situational Calculator
        OperationsCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Calculateur de Délégation Situationnelle",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite
                )
                Text(
                    text = "Selon le modèle de Paul Hersey & Ken Blanchard, le micro-management détruit les experts, tandis que l'absence de directives noie les débutants. Choisissez le profil de votre ouvrier pour obtenir la méthode d'animation optimale.",
                    fontSize = 11.sp,
                    color = MediumGray,
                    lineHeight = 15.sp
                )

                var workerExp by remember { mutableStateOf("Débutant") } // Débutant, Intermédiaire, Expérimenté, Expert
                var taskComp by remember { mutableStateOf("Simple") } // Simple, Complexe

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Expérience ouvrier :", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Spacer(modifier = Modifier.height(4.dp))
                        listOf("Débutant", "Intermédiaire", "Expérimenté", "Expert").forEach { level ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(if (workerExp == level) LightBeige else LightGrayBg, RoundedCornerShape(4.dp))
                                    .clickable { workerExp = level }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = workerExp == level, onClick = { workerExp = level }, colors = RadioButtonDefaults.colors(selectedColor = GoldClassic))
                                Text(level, fontSize = 10.sp, color = Anthracite)
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Complexité tâche :", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Spacer(modifier = Modifier.height(4.dp))
                        listOf("Simple", "Complexe").forEach { comp ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .background(if (taskComp == comp) LightBeige else LightGrayBg, RoundedCornerShape(4.dp))
                                    .clickable { taskComp = comp }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = taskComp == comp, onClick = { taskComp = comp }, colors = RadioButtonDefaults.colors(selectedColor = GoldClassic))
                                Text(comp, fontSize = 10.sp, color = Anthracite)
                            }
                        }
                    }
                }

                Divider(color = LightGrayDivider)

                // Computed Output
                val styleName = when {
                    workerExp == "Débutant" -> "S1 : DIRECTIF (Directives & Contrôle fort)"
                    workerExp == "Intermédiaire" && taskComp == "Complexe" -> "S2 : ENTRAÎNEUR (Explications & Soutien)"
                    workerExp == "Intermédiaire" && taskComp == "Simple" -> "S3 : PARTICIPATIF (Échanges & Validation)"
                    workerExp == "Expérimenté" && taskComp == "Complexe" -> "S3 : PARTICIPATIF (Conseil d'égal à égal)"
                    workerExp == "Expérimenté" && taskComp == "Simple" -> "S4 : DÉLÉGATIF (Confiance & Autonomie)"
                    else -> "S4 : DÉLÉGATIF TOTAL (Validation du résultat de fin)"
                }

                val styleDesc = when {
                    workerExp == "Débutant" -> "Donnez des consignes précises : quoi faire, comment le faire, pour quand. Ne demandez pas son avis sur la méthode, montrez le geste et contrôlez l'avancement toutes les heures."
                    workerExp.startsWith("Inter") -> "Expliquez le POURQUOI technique de la consigne. Demandez-lui comment il compte s'y prendre et guidez-le de manière bienveillante sans imposer brutalement votre méthode."
                    workerExp.startsWith("Expér") -> "Raisonnez en termes d'objectifs globaux de production ou d'ordonnancement. Laissez-le décider du COMMENT, mais fixez un jalon de contrôle à mi-parcours."
                    else -> "Délégation totale de la tâche. Établissez une relation de confiance. Le piège absolu serait de micro-manager cet homme, ce qui provoquerait son départ ou son désengagement total."
                }

                Text(text = "STYLE CONSEILLÉ : $styleName", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                Text(text = styleDesc, fontSize = 11.sp, color = Anthracite, lineHeight = 15.sp)
            }
        }

        // Section 2: Simulator - Jeu de rôle interactif de crise sur le chantier
        Text(
            text = "Simulateur de Crise de Chantier (Interactif)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val crisisScenarios = remember {
            listOf(
                ChantierScenario(
                    title = "Crise 1 : L'infraction de sécurité majeure",
                    situation = "Un grutier de la boîte sous-traitante lève un fardeau de coffrage métallique lourd au-dessus de la tête d'autres ouvriers sans zone de balisage.",
                    options = listOf(
                        "Vous fermez les yeux car le planning est extrêmement serré aujourd'hui.",
                        "Vous montez au derrick et engueulez le grutier par talkie-walkie en l'insultant.",
                        "Vous sonnez immédiatement l'arrêt de la manœuvre. Vous rassemblez le chef d'équipe sous-traitant et le grutier, justifiez par le risque de mort écrasé, et faites poser un balisage physique sous 5 minutes."
                    ),
                    scores = listOf(Pair(-2, -2), Pair(-1, 2), Pair(2, 2)),
                    explanation = "L'option 3 est la seule attitude de Leader (Good Man) : la sécurité de la vie humaine est non négociable. L'option 1 vous expose au pénal. L'option 2 détruit votre autorité par perte de sang-froid."
                ),
                ChantierScenario(
                    title = "Crise 2 : La pression exorbitante du client",
                    situation = "Le maître d'ouvrage (le client) exige que vous couliez une dalle de béton immédiatement, alors que l'armature métallique n'a pas reçu la validation écrite obligatoire du bureau de contrôle.",
                    options = listOf(
                        "Vous coulez pour faire plaisir au client et éviter qu'il résilie votre contrat.",
                        "Vous claquez la porte au nez du client en lui hurlant qu'il ne connaît rien à la construction.",
                        "Vous refusez de couler fermement. Vous lui expliquez le risque d'effondrement et l'annulation automatique de l'assurance décennale. Vous appelez le contrôleur avec lui pour accélérer la validation."
                    ),
                    scores = listOf(Pair(2, -2), Pair(-2, 1), Pair(2, 2)),
                    explanation = "L'option 3 démontre une haute compétence et un leadership calme. Couler sans validation légale est une faute professionnelle lourde (Nice Man lâche). Agresser le client est stérile."
                ),
                ChantierScenario(
                    title = "Crise 3 : L'Insubordination ouverte",
                    situation = "Un de vos chefs d'équipe, très ancien dans la maison, refuse de nettoyer sa zone de bétonnage en fin de poste : « C'est pas à moi de balayer, embauche des manœuvres ! ».",
                    options = listOf(
                        "Vous nettoyez vous-même en douce le soir pour éviter l'esclandre.",
                        "Vous le virez sur-le-champ de l'entreprise devant ses hommes.",
                        "Vous arrêtez le travail. Vous lui dites : « Je respecte ton ancienneté mais la propreté est garante de la sécurité anti-chute de tes propres gars. Prends le balai maintenant, la règle est uniforme pour préserver le groupe. »"
                    ),
                    scores = listOf(Pair(1, -2), Pair(-2, 1), Pair(2, 2)),
                    explanation = "L'option 3 fixe des limites fermes de groupe tout en valorisant son expérience de maçon. L'option 1 crée un précédent catastrophique. L'option 2 est une réaction d'orgueil disproportionnée."
                )
            )
        }

        var simStep by remember { mutableStateOf(0) }
        var chosenOptionIndex by remember { mutableStateOf(-1) }
        var warmthPoints by remember { mutableStateOf(0) }
        var compPoints by remember { mutableStateOf(0) }
        var simEnded by remember { mutableStateOf(false) }

        if (!simEnded) {
            val scenario = crisisScenarios[simStep]
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = scenario.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )
                    Text(
                        text = scenario.situation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Anthracite,
                        lineHeight = 17.sp
                    )

                    Divider(color = LightGrayDivider)

                    scenario.options.forEachIndexed { idx, opt ->
                        val isSelected = chosenOptionIndex == idx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(if (isSelected) LightBeige else LightGrayBg, RoundedCornerShape(8.dp))
                                .border(width = 1.dp, color = if (isSelected) GoldClassic else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                .clickable { chosenOptionIndex = idx }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = { chosenOptionIndex = idx }, colors = RadioButtonDefaults.colors(selectedColor = GoldClassic))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = opt, fontSize = 11.sp, color = Anthracite, lineHeight = 15.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (chosenOptionIndex != -1) {
                                    val (w, c) = scenario.scores[chosenOptionIndex]
                                    warmthPoints += w
                                    compPoints += c

                                    if (simStep < crisisScenarios.size - 1) {
                                        simStep++
                                        chosenOptionIndex = -1
                                    } else {
                                        simEnded = true
                                    }
                                }
                            },
                            enabled = chosenOptionIndex != -1,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldClassic)
                        ) {
                            Text("Valider la Décision", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        } else {
            OperationsCard(borderAccent = true) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "RÉSULTAT DES SCÉNARIOS DE CRISE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldClassic
                    )

                    Text(
                        text = "Score d'Équilibre Leadership :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Chaleur / Respect", fontSize = 10.sp, color = MediumGray)
                            Text("$warmthPoints/6", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Compétence / Décision", fontSize = 10.sp, color = MediumGray)
                            Text("$compPoints/6", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        }
                    }

                    val simResultDesc = if (warmthPoints >= 4 && compPoints >= 4) {
                        "✨ EXCELLENT (Good Man) : Vous possédez un sang-froid et une intelligence situationnelle exceptionnels. Vos décisions concilient parfaitement impératifs techniques rigoureux et préservation de la vie humaine."
                    } else if (compPoints >= 4) {
                        "😡 PETIT CHEF : Vos choix sont techniquement logiques mais votre communication agressive sème la discorde et brise le moral de vos troupes."
                    } else {
                        "🥺 NICE MAN / LAXISTE : Vous privilégiez le confort immédiat au détriment de la sécurité et des obligations juridiques. Le chantier court à la catastrophe."
                    }

                    Text(
                        text = simResultDesc,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = {
                            simStep = 0
                            chosenOptionIndex = -1
                            warmthPoints = 0
                            compPoints = 0
                            simEnded = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Anthracite),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Recommencer la Simulation", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TabMossadLevel() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Warning Banner
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
                    text = "DÉBULLONAGE SCIENTIFIQUE : Le décodage corporel populaire (micro-expressions, regard fuyant) est scientifiquement faux (précision de 54%, identique au pur hasard). Le Mossad et les services secrets de pointe utilisent l'approche cognitive SUE (Strategic Use of Evidence), validée par la science (75% de réussite). Elle repose sur la saturation de la charge mentale.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
            }
        }

        // Section: Les 3 piliers de la méthode cognitive
        Text(
            text = "Les 3 Piliers de la Détection Cognitive (Granhag, 2014)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        val mossadPillars = listOf(
            Triple(
                "1. L'Établissement de la Baseline",
                "Ne commencez jamais par accuser ou interroger sur le sujet chaud. Posez d'abord 5 questions de routine neutres (météo, déroulement de la matinée, trajet). Observez son rythme de parole habituel, ses expressions et sa vitesse de réponse naturelle. C'est votre niveau de référence unique.",
                "📊 Création de référence"
            ),
            Triple(
                "2. La Surcharge Cognitive Spontanée",
                "Mentir demande un effort intellectuel complexe (garder l'histoire cohérente). Augmentez cet effort en demandant de raconter les faits dans l'ordre inverse (de la fin de journée au matin), ou de décrire des détails sensoriels superflus (odeurs, bruits d'ambiance). Un menteur s'effondrera sous l'effort.",
                "🧠 Saturation mentale"
            ),
            Triple(
                "3. L'Utilisation Stratégique des Preuves (SUE - Strategic Use of Evidence)",
                "Si vous possédez une preuve (photo, bon de livraison erroné), NE la montrez JAMAIS au début. Laissez le suspect formuler des explications libres. Posez des questions larges, puis resserrez le piège. Lorsqu'il a menti de manière incontestable, abattez la preuve physique. La contradiction scelle sa culpabilité.",
                "🧩 Méthode SUE (Strategic Use of Evidence)"
            )
        )

        mossadPillars.forEach { (title, desc, badge) ->
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
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )
                        Box(
                            modifier = Modifier
                                .background(LightBeige, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldClassic)
                        }
                    }
                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = MediumGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section: Simulateur Interactif "L'enquête du Matériel Disparu"
        Text(
            text = "Simulateur Interactif : L'Enquête du Matériel Disparu",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            modifier = Modifier.padding(top = 8.dp)
        )

        var step by remember { mutableStateOf(1) } // 1, 2, 3, 4 (end)
        var scoreInvestigation by remember { mutableStateOf(0) }
        var investigativeFeedbacks = remember { mutableStateListOf<String>() }

        OperationsCard(borderAccent = true) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "L'enquête : Un de vos conducteurs de travaux est suspecté de détourner des sacs de ciment spéciaux du stock.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp
                )

                Divider(color = LightGrayDivider)

                when (step) {
                    1 -> {
                        Text(
                            text = "PHASE 1 : Établir la Baseline",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )
                        Text(
                            text = "Vous recevez le suspect dans votre bureau de chantier. Quelle est votre première question ?",
                            fontSize = 11.sp,
                            color = Anthracite
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    scoreInvestigation += 2
                                    investigativeFeedbacks.add("Félicitations. Vous avez posé une question neutre sur la route qui permet d'évaluer sa vitesse de parole habituelle et son aisance d'expression.")
                                    step = 2
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Salut Pierre. Comment s'est passé ton trajet ce matin ? Pas trop de bouchons à l'entrée ouest ? »", color = Anthracite, fontSize = 10.sp, textAlign = TextAlign.Start)
                            }
                            Button(
                                onClick = {
                                    scoreInvestigation -= 2
                                    investigativeFeedbacks.add("Erreur stratégique : Lancer l'accusation d'emblée détruit toute chance d'observer sa baseline de parole habituelle. Il se referme immédiatement.")
                                    step = 2
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Est-ce que c'est toi qui voles le ciment du stock de la dalle sud ? Réponds-moi honnêtement ! »", color = Anthracite, fontSize = 10.sp, textAlign = TextAlign.Start)
                            }
                        }
                    }
                    2 -> {
                        Text(
                            text = "PHASE 2 : La Surcharge Cognitive",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )
                        Text(
                            text = "Il affirme qu'il était absent de la zone hier entre 14h et 16h car il inspectait l'étanchéité du bâtiment B. Comment saturez-vous sa mémoire ?",
                            fontSize = 11.sp,
                            color = Anthracite
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    scoreInvestigation += 3
                                    investigativeFeedbacks.add("Excellent. Le forcer à raconter son inspection à l'envers ou demander des détails sensoriels précis sature l'effort mental. Un mensonge se fissure face à cet exercice.")
                                    step = 3
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Raconte-moi ton inspection en partant de ta descente du bâtiment B à 16h jusqu'à ton entrée à 14h. Quel temps faisait-il ? Quels bruits as-tu entendus ? »", color = Anthracite, fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    scoreInvestigation -= 1
                                    investigativeFeedbacks.add("Insuffisant : Cette question trop directe et linéaire n'exige aucun effort cognitif de construction mentale. Il récite son mensonge préparé sans forcer.")
                                    step = 3
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Est-ce que tu as fini l'étanchéité ? Le travail était-il difficile ? »", color = Anthracite, fontSize = 10.sp)
                            }
                        }
                    }
                    3 -> {
                        Text(
                            text = "PHASE 3 : Utilisation Stratégique des Preuves (SUE - Strategic Use of Evidence)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )
                        Text(
                            text = "Vous possédez une photo du badge d'accès de son utilitaire montrant qu'il est passé à 14h12 par la zone de stockage sud. Que faites-vous ?",
                            fontSize = 11.sp,
                            color = Anthracite
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    scoreInvestigation += 3
                                    investigativeFeedbacks.add("Brillant ! Vous le laissez s'enfermer dans son déni avant d'abattre la preuve physique. Il ne peut plus s'échapper ou inventer une parade légitime tardive.")
                                    step = 4
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Tu es sûr de n'avoir pas approché le stockage sud ? Aucun passage de ton utilitaire là-bas entre 14h et 16h ? » (Puis vous montrez la photo du badge après sa confirmation écrite)", color = Anthracite, fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    scoreInvestigation -= 2
                                    investigativeFeedbacks.add("Mauvais timing : Poser la preuve sur la table dès le début lui permet d'inventer instantanément une parade plausible : « Ah oui, j'ai oublié, je suis passé chercher un câble... ».")
                                    step = 4
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightGrayBg),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("« Tiens, regarde cette photo ! Ton utilitaire était bien au stockage sud à 14h12. Tu m'expliques ce mensonge ?! »", color = Anthracite, fontSize = 10.sp)
                            }
                        }
                    }
                    4 -> {
                        Text(
                            text = "RÉSULTAT DE VOTRE ENQUÊTE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldClassic
                        )

                        Text(
                            text = "Score Cognitif : $scoreInvestigation/8",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Anthracite
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            investigativeFeedbacks.forEach { feedback ->
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(5.dp)
                                            .background(GoldClassic, CircleShape)
                                    )
                                    Text(text = feedback, fontSize = 11.sp, color = MediumGray, lineHeight = 15.sp)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                step = 1
                                scoreInvestigation = 0
                                investigativeFeedbacks.clear()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Anthracite),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Recommencer l'Enquête", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Point 1
        var c1Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { c1Expanded = !c1Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. Établir le baseline de parole habituelle",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (c1Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (c1Expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Observe comment une personne se comporte normalement (son « baseline » : rythme de parole, tics de langage, contact visuel habituel) lors de sujets neutres et simples avant de chercher des écarts significatifs lors d'une question importante. Il n'existe pas de signal universel isolé du mensonge, seulement des variations par rapport au comportement habituel de l'individu.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Point 2
        var c2Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { c2Expanded = !c2Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Raconter les événements dans l'ordre inverse",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (c2Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (c2Expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Mentir demande déjà un effort cognitif important car il faut construire et mémoriser un récit faux. Ajouter une charge mentale supplémentaire — comme demander de raconter l'histoire depuis la fin jusqu'au début ou d'expliquer un détail précis à reculons — fait s'effondrer le récit fabriqué d'un menteur alors qu'un récit vécu résistera sans peine.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Point 3
        var c3Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { c3Expanded = !c3Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. Demander des détails sensoriels inattendus",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (c3Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (c3Expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "« Que sentait-on dans la pièce ? », « Quel bruit y avait-il en arrière-plan ? », « Quel temps faisait-il exactement quand tu es descendu de voiture ? ». Les récits inventés sont construits de manière logique et chronologique mais manquent typiquement de détails sensoriels secondaires inattendus.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Point 4
        var p4_Expanded by remember { mutableStateOf(false) }
        OperationsCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { p4_Expanded = !p4_Expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "4. Analyser la cohérence verbale",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite
                    )
                    Icon(
                        imageVector = if (p4_Expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Développer",
                        tint = GoldClassic,
                        modifier = Modifier.size(16.dp)
                    )
                }
                if (p4_Expanded) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Analyse la structure des mots plutôt que le visage : un récit authentique contient fréquemment des auto-corrections spontanées (« ah non, c'était plutôt mardi, attends, oui mardi ») et des détails superflus non pertinents. Un récit mensonger est presque toujours trop propre, trop linéaire, sans la moindre hésitation sur la chronologie logique globale.",
                        fontSize = 12.sp,
                        color = MediumGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Ethical Reminder Banner
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
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Éthique",
                    tint = GoldClassic,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Ces techniques donnent des indices probabilistes, jamais une certitude. Toujours vérifier par des faits concrets avant de tirer une conclusion définitive sur quelqu'un — particulièrement important dans un contexte de management où les décisions affectent le travail et la réputation des autres.",
                    fontSize = 11.sp,
                    color = Anthracite,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChantiersPage(viewModel: OperationsViewModel) {
    val chantiers by viewModel.chantiers.collectAsState()
    var selectedChantierId by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (selectedChantierId != null) {
        ChantierDetailPage(
            viewModel = viewModel,
            chantierId = selectedChantierId!!,
            onBack = { selectedChantierId = null }
        )
    } else {
        Scaffold(
            containerColor = WhitePure,
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GradientTokens.sunsetVertical)
                        .clickable { showAddDialog = true }
                        .testTag("add_chantier_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un chantier", tint = Color.White)
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    PageHeader(
                        title = "Gestion des Chantiers",
                        subtitle = "Suivi professionnel et budgétaire de vos opérations (Max 5 actifs)"
                    )
                }

                if (chantiers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightBeige, RoundedCornerShape(12.dp))
                                .border(width = 0.5.dp, color = LightGrayDivider, shape = RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Construction,
                                    contentDescription = null,
                                    tint = GoldClassic,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Aucun chantier enregistré",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Anthracite
                                )
                                Text(
                                    text = "Ajoutez votre premier chantier professionnel pour suivre les jalons, budgets et incidents.",
                                    fontSize = 12.sp,
                                    color = MediumGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Créer un chantier", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(chantiers) { chantier ->
                        ChantierCard(
                            chantier = chantier,
                            onClick = { selectedChantierId = chantier.id }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddChantierDialog(
            viewModel = viewModel,
            activeCount = chantiers.count { it.status != "Terminé" },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
fun ChantierCard(
    chantier: Chantier,
    onClick: () -> Unit
) {
    val daysLeft = calculateDaysDifference(chantier.targetEndDate)
    val isOverdue = daysLeft < 0 && chantier.status != "Terminé"

    OperationsCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chantier_card_${chantier.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chantier.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Anthracite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (chantier.location.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MediumGray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = chantier.location,
                                fontSize = 12.sp,
                                color = MediumGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = chantier.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Avancement", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Medium)
                Text("${chantier.progressPercent}%", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            CustomLinearProgressBar(progress = chantier.progressPercent / 100f)

            Spacer(modifier = Modifier.height(14.dp))

            // Budget bar
            val hasBudget = chantier.budgetTotal > 0f
            val budgetProgress = if (hasBudget) chantier.budgetSpent / chantier.budgetTotal else 0f
            val budgetOverspent = chantier.budgetSpent > chantier.budgetTotal && hasBudget

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Budget consommé", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Medium)
                Text(
                    text = if (hasBudget) {
                        "${chantier.budgetSpent.toInt()} € / ${chantier.budgetTotal.toInt()} €"
                    } else {
                        "Non défini"
                    },
                    fontSize = 11.sp,
                    color = if (budgetOverspent) Color(0xFFC1666B) else Anthracite,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (hasBudget) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(LightGrayDivider)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = budgetProgress.coerceAtMost(1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (budgetOverspent) Color(0xFFC1666B) else GoldClassic)
                    )
                }
                if (budgetOverspent) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠ Dépassement budgétaire !",
                        fontSize = 10.sp,
                        color = Color(0xFFC1666B),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text("Aucun budget total spécifié", fontSize = 10.sp, color = MediumGray, fontStyle = FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dates & Due Date warning
            Divider(color = LightGrayDivider, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MediumGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Échéance : ${formatFrenchDate(chantier.targetEndDate)}",
                        fontSize = 11.sp,
                        color = Anthracite
                    )
                }

                if (chantier.status == "Terminé") {
                    Box(
                        modifier = Modifier
                            .background(LightGrayBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Complété", fontSize = 9.sp, color = MediumGray, fontWeight = FontWeight.Bold)
                    }
                } else if (isOverdue) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFF0F0), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color(0xFFC1666B), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Retard de ${-daysLeft} j", fontSize = 9.sp, color = Color(0xFFC1666B), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(GoldPale, RoundedCornerShape(4.dp))
                            .border(0.5.dp, GoldClassic, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Jours restants : $daysLeft j", fontSize = 9.sp, color = GoldClassic, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "Terminé" -> Triple(LightGrayBg, MediumGray, "Terminé")
        "En retard" -> Triple(Color(0xFFFFF5F5), Color(0xFFC1666B), "En retard")
        "En pause" -> Triple(Color(0xFFF7F7F7), Color(0xFF8A8A8A), "En pause")
        else -> Triple(GoldPale, GoldClassic, "En cours")
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(0.5.dp, textColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomLinearProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(LightGrayDivider)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(GradientTokens.sunsetHorizontal)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChantierDialog(
    viewModel: OperationsViewModel,
    activeCount: Int,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(viewModel.getTodayDate()) }
    var targetEndDate by remember { mutableStateOf("") }
    var progressPercent by remember { mutableStateOf("0") }
    var budgetTotal by remember { mutableStateOf("") }
    var budgetSpent by remember { mutableStateOf("0") }
    var status by remember { mutableStateOf("En cours") }
    var notes by remember { mutableStateOf("") }

    var showErrorLimit by remember { mutableStateOf(activeCount >= 5) }

    // Auto-calculate end date to 30 days from now if left empty
    LaunchedEffect(Unit) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 30)
            targetEndDate = sdf.format(calendar.time)
        } catch (e: Exception) {
            targetEndDate = "2026-08-11"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = WhitePure,
        title = {
            Text(
                text = "Créer un nouveau chantier",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )
        },
        text = {
            if (showErrorLimit) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFFC1666B), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Limitation : Vous avez atteint la limite de 5 chantiers actifs simultanément. Veuillez marquer un autre chantier comme \"Terminé\" avant de pouvoir en ajouter un nouveau.",
                            fontSize = 12.sp,
                            color = Color(0xFFC1666B),
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nom du chantier*", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("add_chantier_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Localisation", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Début (AAAA-MM-JJ)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetEndDate,
                            onValueChange = { targetEndDate = it },
                            label = { Text("Échéance (AAAA-MM-JJ)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = budgetTotal,
                            onValueChange = { budgetTotal = it },
                            label = { Text("Budget Total (€)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = budgetSpent,
                            onValueChange = { budgetSpent = it },
                            label = { Text("Budget Consommé (€)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = progressPercent,
                        onValueChange = { progressPercent = it },
                        label = { Text("Avancement (0-100 %)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes initiales", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            if (!showErrorLimit) {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val prog = progressPercent.toIntOrNull() ?: 0
                            val bt = budgetTotal.toFloatOrNull() ?: 0f
                            val bs = budgetSpent.toFloatOrNull() ?: 0f
                            viewModel.addChantier(
                                name = name,
                                location = location,
                                startDate = startDate,
                                targetEndDate = targetEndDate,
                                progressPercent = prog.coerceIn(0, 100),
                                budgetTotal = bt,
                                budgetSpent = bs,
                                status = status,
                                notes = notes
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                    enabled = name.isNotBlank()
                ) {
                    Text("Ajouter", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", color = MediumGray)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChantierDetailPage(
    viewModel: OperationsViewModel,
    chantierId: Long,
    onBack: () -> Unit
) {
    val chantiers by viewModel.chantiers.collectAsState()
    val chantier = chantiers.find { it.id == chantierId }

    if (chantier == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Chantier introuvable", color = MediumGray)
        }
        BackHandler { onBack() }
        return
    }

    val milestones by viewModel.getMilestonesForChantierFlow(chantierId).collectAsState(initial = emptyList())
    val incidents by viewModel.getIncidentsForChantierFlow(chantierId).collectAsState(initial = emptyList())

    var showEditInfoDialog by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf(chantier.notes) }

    var milestoneName by remember { mutableStateOf("") }
    var milestoneDate by remember { mutableStateOf(viewModel.getTodayDate()) }

    var incidentDescription by remember { mutableStateOf("") }
    var incidentSeverity by remember { mutableStateOf("Mineur") }

    BackHandler {
        onBack()
    }

    Scaffold(
        containerColor = WhitePure,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Retour", tint = GoldClassic)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = chantier.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showEditInfoDialog = true }, modifier = Modifier.testTag("edit_chantier_button")) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier", tint = GoldClassic)
                }
                IconButton(onClick = {
                    viewModel.deleteChantierById(chantierId)
                    onBack()
                }, modifier = Modifier.testTag("delete_chantier_button")) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = Color(0xFFC1666B))
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                OperationsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Informations Générales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                            StatusBadge(status = chantier.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (chantier.location.isNotEmpty()) {
                            DetailRow(label = "Localisation", value = chantier.location, icon = Icons.Default.Place)
                        }
                        DetailRow(label = "Date de début", value = formatFrenchDate(chantier.startDate), icon = Icons.Default.CalendarToday)
                        DetailRow(label = "Date d'échéance", value = formatFrenchDate(chantier.targetEndDate), icon = Icons.Default.CalendarToday)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Avancement global", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Medium)
                            Text("${chantier.progressPercent}%", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        CustomLinearProgressBar(progress = chantier.progressPercent / 100f)

                        Spacer(modifier = Modifier.height(14.dp))

                        val hasBudget = chantier.budgetTotal > 0f
                        val budgetProgress = if (hasBudget) chantier.budgetSpent / chantier.budgetTotal else 0f
                        val budgetOverspent = chantier.budgetSpent > chantier.budgetTotal && hasBudget

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Budget consommé", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Medium)
                            Text(
                                text = if (hasBudget) {
                                    "${chantier.budgetSpent.toInt()} € / ${chantier.budgetTotal.toInt()} €"
                                } else {
                                    "Non défini"
                                },
                                fontSize = 11.sp,
                                color = if (budgetOverspent) Color(0xFFC1666B) else Anthracite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (hasBudget) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(LightGrayDivider)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = budgetProgress.coerceAtMost(1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (budgetOverspent) Color(0xFFC1666B) else GoldClassic)
                                )
                            }
                            if (budgetOverspent) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠ Le budget consommé dépasse le budget total prévu !",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC1666B),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                OperationsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Jalons & Livrables", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = milestoneName,
                                onValueChange = { milestoneName = it },
                                label = { Text("Nouveau jalon", fontSize = 10.sp) },
                                modifier = Modifier.weight(1.5f).testTag("milestone_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldClassic,
                                    unfocusedBorderColor = LightGrayDivider
                                ),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = milestoneDate,
                                onValueChange = { milestoneDate = it },
                                label = { Text("Date (AAAA-MM-JJ)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("milestone_date_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldClassic,
                                    unfocusedBorderColor = LightGrayDivider
                                ),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (milestoneName.isNotBlank() && milestoneDate.isNotBlank()) {
                                        viewModel.addMilestone(chantierId, milestoneName, milestoneDate)
                                        milestoneName = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(GoldClassic, RoundedCornerShape(8.dp))
                                    .testTag("add_milestone_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (milestones.isEmpty()) {
                            Text(
                                "Aucun jalon défini pour le moment.",
                                fontSize = 11.sp,
                                color = MediumGray,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                milestones.forEach { milestone ->
                                    val isMilestoneOverdue = !milestone.completed && calculateDaysDifference(milestone.targetDate) < 0
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(LightGrayBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = milestone.completed,
                                            onCheckedChange = { isChecked ->
                                                viewModel.updateMilestone(milestone.copy(completed = isChecked))
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = GoldClassic),
                                            modifier = Modifier.testTag("milestone_check_${milestone.id}")
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = milestone.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (milestone.completed) MediumGray else Anthracite,
                                                textDecoration = if (milestone.completed) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Prévu le : ${formatFrenchDate(milestone.targetDate)}",
                                                    fontSize = 10.sp,
                                                    color = MediumGray
                                                )
                                                if (isMilestoneOverdue) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "⚠ Retard !",
                                                        fontSize = 10.sp,
                                                        color = Color(0xFFC1666B),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        IconButton(onClick = { viewModel.deleteMilestoneById(milestone.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer jalon",
                                                tint = MediumGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                OperationsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Incidents & Blocages", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGrayBg, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = incidentDescription,
                                onValueChange = { incidentDescription = it },
                                label = { Text("Signaler un incident (matériel, météo...)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("incident_desc_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldClassic,
                                    unfocusedBorderColor = LightGrayDivider
                                ),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Gravité :", fontSize = 11.sp, color = Anthracite, fontWeight = FontWeight.Bold)
                                    listOf("Mineur", "Majeur", "Critique").forEach { sev ->
                                        val isSelected = incidentSeverity == sev
                                        val chipColor = when (sev) {
                                            "Critique" -> if (isSelected) Color(0xFFC1666B) else Color(0x1AC1666B)
                                            "Majeur" -> if (isSelected) SunsetAmber else Color(0x1AE8A33D)
                                            else -> if (isSelected) GoldClassic else Color(0x1AD4AF37)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(chipColor)
                                                .clickable { incidentSeverity = sev }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = sev,
                                                color = if (isSelected) Color.White else Anthracite,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (incidentDescription.isNotBlank()) {
                                            viewModel.addIncident(
                                                chantierId = chantierId,
                                                date = viewModel.getTodayDate(),
                                                description = incidentDescription,
                                                severity = incidentSeverity
                                            )
                                            incidentDescription = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("add_incident_button"),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Signaler", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (incidents.isEmpty()) {
                            Text(
                                "Aucun incident signalé.",
                                fontSize = 11.sp,
                                color = MediumGray,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                incidents.forEach { incident ->
                                    val sevColor = when (incident.severity) {
                                        "Critique" -> Color(0xFFC1666B)
                                        "Majeur" -> SunsetAmber
                                        else -> GoldClassic
                                    }
                                    val sevBg = when (incident.severity) {
                                        "Critique" -> Color(0xFFFFF0F0)
                                        "Majeur" -> Color(0xFFFFF9E6)
                                        else -> GoldPale
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(0.5.dp, sevColor, RoundedCornerShape(8.dp))
                                            .background(sevBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(sevColor, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        incident.severity.uppercase(),
                                                        color = Color.White,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Text(
                                                    text = formatFrenchDate(incident.date),
                                                    fontSize = 10.sp,
                                                    color = MediumGray
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = incident.description,
                                                fontSize = 12.sp,
                                                color = Anthracite,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        IconButton(onClick = { viewModel.deleteIncidentById(incident.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer incident",
                                                tint = MediumGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                OperationsCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Notes & Observations", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("Rédigez vos remarques ici...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("notes_input_text"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldClassic,
                                unfocusedBorderColor = LightGrayDivider
                            ),
                            maxLines = 8,
                            minLines = 4
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateChantier(chantier.copy(notes = notesText))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("save_notes_button")
                            ) {
                                Text("Enregistrer les notes", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditInfoDialog) {
        EditChantierDialog(
            viewModel = viewModel,
            chantier = chantier,
            onDismiss = { showEditInfoDialog = false }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = GoldClassic, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label : ", fontSize = 12.sp, color = MediumGray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = Anthracite, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChantierDialog(
    viewModel: OperationsViewModel,
    chantier: Chantier,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(chantier.name) }
    var location by remember { mutableStateOf(chantier.location) }
    var startDate by remember { mutableStateOf(chantier.startDate) }
    var targetEndDate by remember { mutableStateOf(chantier.targetEndDate) }
    var progressPercent by remember { mutableStateOf(chantier.progressPercent.toString()) }
    var budgetTotal by remember { mutableStateOf(chantier.budgetTotal.toString()) }
    var budgetSpent by remember { mutableStateOf(chantier.budgetSpent.toString()) }
    var status by remember { mutableStateOf(chantier.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        containerColor = WhitePure,
        title = {
            Text(
                text = "Modifier le chantier",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Anthracite
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du chantier", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("edit_chantier_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Localisation", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Date de début", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = targetEndDate,
                        onValueChange = { targetEndDate = it },
                        label = { Text("Date d'échéance", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = budgetTotal,
                        onValueChange = { budgetTotal = it },
                        label = { Text("Budget Total (€)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = budgetSpent,
                        onValueChange = { budgetSpent = it },
                        label = { Text("Budget Consommé (€)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldClassic,
                            unfocusedBorderColor = LightGrayDivider
                        ),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = progressPercent,
                    onValueChange = { progressPercent = it },
                    label = { Text("Avancement (0-100 %)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldClassic,
                        unfocusedBorderColor = LightGrayDivider
                    ),
                    singleLine = true
                )

                Text("Statut :", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Anthracite)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("En cours", "En retard", "En pause", "Terminé").forEach { st ->
                        val isSelected = status == st
                        val btnColor = if (isSelected) GoldClassic else LightGrayBg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(btnColor)
                                .clickable { status = st }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = st,
                                color = if (isSelected) Color.White else Anthracite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val prog = progressPercent.toIntOrNull() ?: 0
                        val bt = budgetTotal.toFloatOrNull() ?: 0f
                        val bs = budgetSpent.toFloatOrNull() ?: 0f
                        viewModel.updateChantier(
                            chantier.copy(
                                name = name,
                                location = location,
                                startDate = startDate,
                                targetEndDate = targetEndDate,
                                progressPercent = prog.coerceIn(0, 100),
                                budgetTotal = bt,
                                budgetSpent = bs,
                                status = status
                            )
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                enabled = name.isNotBlank()
            ) {
                Text("Enregistrer", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = MediumGray)
            }
        }
    )
}

fun formatFrenchDate(dateStr: String): String {
    return try {
        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdfInput.parse(dateStr) ?: return dateStr
        val sdfOutput = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)
        sdfOutput.format(date)
    } catch (e: Exception) {
        dateStr
    }
}

fun calculateDaysDifference(targetDateStr: String): Int {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val targetDate = sdf.parse(targetDateStr) ?: return 0
        
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val targetCal = Calendar.getInstance().apply {
            time = targetDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val diffMs = targetCal.timeInMillis - todayCal.timeInMillis
        (diffMs / (1000 * 60 * 60 * 24)).toInt()
    } catch (e: Exception) {
        0
    }
}

@Composable
fun RitualPage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    val todayStr = viewModel.getTodayDate()
    
    // Collect states to trigger recomposition on any updates
    val tasks by viewModel.tasks.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val breathingSessions by viewModel.breathingSessions.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val sunExposureLogs by viewModel.sunExposureLogs.collectAsState()
    val communicationPracticeLogs by viewModel.communicationPracticeLogs.collectAsState()
    val delayTrainingLogs by viewModel.delayTrainingLogs.collectAsState()
    val cardioHealthLogs by viewModel.cardioHealthLogs.collectAsState()
    val morningErectionLogs by viewModel.morningErectionLogs.collectAsState()
    val dailyWins by viewModel.dailyWins.collectAsState()
    val gratitudeLogs by viewModel.gratitudeLogs.collectAsState()
    val dailyAffirmation by viewModel.dailyAffirmation.collectAsState()
    val restDays by viewModel.restDays.collectAsState()

    val isRestDayActive = restDays.any { it.date == todayStr && it.active }

    // Aggregate the ritual plan
    val ritualPlan = remember(
        todayStr, tasks, gymSessions, supplementLogs, kegelLogs, breathingSessions,
        journalEntries, sleepLogs, sunExposureLogs, communicationPracticeLogs,
        delayTrainingLogs, cardioHealthLogs, morningErectionLogs, dailyWins,
        gratitudeLogs, dailyAffirmation
    ) {
        com.example.data.DailyRitualAggregator.buildTodayRitual(viewModel, todayStr)
    }

    val formattedDate = remember(todayStr) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(todayStr) ?: java.util.Date()
            SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(date).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            todayStr
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                PageHeader(
                    title = "Rituel du Jour",
                    subtitle = formattedDate
                )
            }

            if (isRestDayActive) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightBeige, RoundedCornerShape(12.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "Rest Day",
                                tint = GoldClassic,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Jour de pause activé — repose-toi, tout reprend normalement demain.",
                                fontSize = 14.sp,
                                color = GoldClassic,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            } else {
                // Progress Bar
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightGrayBg, RoundedCornerShape(16.dp))
                            .border(1.dp, LightGrayDivider, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progression Globale",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            Text(
                                text = "${ritualPlan.completionPercent}%",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldClassic
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ritualPlan.completionPercent / 100f)
                                    .background(GradientTokens.sunsetHorizontal, CircleShape)
                            )
                        }
                    }
                }

                // If all completed, show elegant empty state
                if (ritualPlan.completionPercent == 100) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Rituel Complété",
                                tint = GoldClassic,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Rituel du jour terminé !",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bien joué. 🕊️",
                                fontSize = 14.sp,
                                color = GoldClassic,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // MATIN Section
                if (ritualPlan.morningItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "MATIN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                    val morningSorted = ritualPlan.morningItems.sortedBy { it.isCompleted }
                    items(morningSorted.size) { index ->
                        val item = morningSorted[index]
                        RitualItemRow(item = item, onNavigateToPage = onNavigateToPage)
                    }
                }

                // JOURNÉE Section
                if (ritualPlan.dayItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "JOURNÉE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                    val daySorted = ritualPlan.dayItems.sortedBy { it.isCompleted }
                    items(daySorted.size) { index ->
                        val item = daySorted[index]
                        RitualItemRow(item = item, onNavigateToPage = onNavigateToPage)
                    }
                }

                // SOIR Section
                if (ritualPlan.eveningItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "SOIR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldClassic,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                    val eveningSorted = ritualPlan.eveningItems.sortedBy { it.isCompleted }
                    items(eveningSorted.size) { index ->
                        val item = eveningSorted[index]
                        RitualItemRow(item = item, onNavigateToPage = onNavigateToPage)
                    }
                }
            }
        }
    }
}

@Composable
fun RitualItemRow(
    item: com.example.data.RitualItem,
    onNavigateToPage: (Int) -> Unit
) {
    val isActionable = item.quickToggleAction != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (isActionable && item.quickToggleAction != null) {
                    item.quickToggleAction.invoke()
                } else {
                    onNavigateToPage(item.sourcePageIndex)
                }
            }
            .background(if (item.isCompleted) Color.Transparent else LightGrayBg.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isActionable) {
                PremiumCheckbox(
                    checked = item.isCompleted,
                    onCheckedChange = { _ -> item.quickToggleAction?.invoke() },
                    modifier = Modifier.testTag("ritual_checkbox_${item.label.lowercase().replace(" ", "_")}")
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Action complexe requise",
                    tint = GoldClassic.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.label,
                fontSize = 13.sp,
                color = if (item.isCompleted) MediumGray else Anthracite,
                textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
        
        if (!isActionable) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Naviguer pour compléter",
                tint = GoldClassic,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun StreaksOverviewPage(viewModel: OperationsViewModel, onNavigateToPage: (Int) -> Unit) {
    val todayStr = viewModel.getTodayDate()
    
    // Collect all states to trigger recomposition on any updates
    val tasks by viewModel.tasks.collectAsState()
    val gymSessions by viewModel.gymSessions.collectAsState()
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val breathingSessions by viewModel.breathingSessions.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val sunExposureLogs by viewModel.sunExposureLogs.collectAsState()
    val communicationPracticeLogs by viewModel.communicationPracticeLogs.collectAsState()
    val delayTrainingLogs by viewModel.delayTrainingLogs.collectAsState()
    val cardioHealthLogs by viewModel.cardioHealthLogs.collectAsState()
    val morningErectionLogs by viewModel.morningErectionLogs.collectAsState()
    val dailyWins by viewModel.dailyWins.collectAsState()
    val gratitudeLogs by viewModel.gratitudeLogs.collectAsState()
    val dailyAffirmation by viewModel.dailyAffirmation.collectAsState()
    val restDays by viewModel.restDays.collectAsState()
    val recoveryStreaks by viewModel.recoveryStreaks.collectAsState()

    var sortOrder by remember { mutableStateOf("longest") }

    val activeStreaks = remember(
        todayStr, tasks, gymSessions, supplementLogs, kegelLogs, breathingSessions,
        journalEntries, sleepLogs, sunExposureLogs, communicationPracticeLogs,
        delayTrainingLogs, cardioHealthLogs, morningErectionLogs, dailyWins,
        gratitudeLogs, dailyAffirmation, restDays, recoveryStreaks
    ) {
        StreaksOverview.getAllActiveStreaks(viewModel, todayStr)
    }

    val activeCount = activeStreaks.count { it.currentCount > 0 }
    val strongestStreak = activeStreaks.maxByOrNull { it.currentCount }
    val synthesisText = if (strongestStreak != null && strongestStreak.currentCount > 0) {
        "Tu as actuellement $activeCount ${if (activeCount > 1) "streaks actifs" else "streak actif"}. Ton plus fort est ${strongestStreak.label} avec ${strongestStreak.currentCount} ${if (strongestStreak.currentCount > 1) "jours" else "jour"} ! 🔥"
    } else {
        "Aucun streak actif pour le moment. C'est le moment idéal pour démarrer une nouvelle habitude saine ! 🚀"
    }

    val sortedStreaks = remember(activeStreaks, sortOrder) {
        when (sortOrder) {
            "longest" -> activeStreaks.sortedByDescending { it.currentCount }
            "name" -> activeStreaks.sortedBy { it.label }
            else -> activeStreaks
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            item {
                PageHeader(
                    title = "Mes Streaks",
                    subtitle = "Vue d'ensemble de votre régularité"
                )
            }

            // Synthesis card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GradientTokens.sunsetVertical)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streaks",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aperçu de vos efforts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = synthesisText,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Sort Selector Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trier par :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MediumGray
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { sortOrder = "longest" },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (sortOrder == "longest") LightBeige else Color.Transparent,
                                contentColor = if (sortOrder == "longest") GoldClassic else MediumGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp).testTag("sort_streaks_longest")
                        ) {
                            Text("Plus fort d'abord", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = { sortOrder = "name" },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (sortOrder == "name") LightBeige else Color.Transparent,
                                contentColor = if (sortOrder == "name") GoldClassic else MediumGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp).testTag("sort_streaks_name")
                        ) {
                            Text("A-Z", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Grid of streaks
            val chunkedStreaks = sortedStreaks.chunked(2)
            items(chunkedStreaks.size) { index ->
                val rowItems = chunkedStreaks[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StreakCard(
                        item = rowItems[0],
                        onNavigate = onNavigateToPage,
                        modifier = Modifier.weight(1f)
                    )
                    if (rowItems.size > 1) {
                        StreakCard(
                            item = rowItems[1],
                            onNavigate = onNavigateToPage,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StreakCard(
    item: StreakSummary,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (item.iconName) {
        "FlashOn" -> Icons.Filled.FlashOn
        "Favorite" -> Icons.Filled.Favorite
        "SmokeFree" -> Icons.Filled.SmokeFree
        "Forum" -> Icons.Filled.Forum
        "Psychology" -> Icons.Filled.Psychology
        "WbSunny" -> Icons.Filled.WbSunny
        else -> Icons.Filled.TrendingUp
    }

    val progress = if (item.bestCount > 0) {
        item.currentCount.toFloat() / item.bestCount.toFloat()
    } else {
        0f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onNavigate(item.sourcePageIndex) }
            .testTag("streak_card_${item.label.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = LightGrayBg.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, LightGrayDivider)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.label,
                    tint = GoldClassic,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = item.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Anthracite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimatedCountText(
                    value = item.currentCount,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Anthracite
                )
                Text(
                    text = if (item.currentCount > 1) "jours" else "jour",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MediumGray,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Record : ${item.bestCount} j",
                    fontSize = 11.sp,
                    color = GoldClassic,
                    fontWeight = FontWeight.Medium
                )
                
                // Discret sunset progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .background(GradientTokens.sunsetHorizontal, CircleShape)
                    )
                }
            }
        }
    }
}









