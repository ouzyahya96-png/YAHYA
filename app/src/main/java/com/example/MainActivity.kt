package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.OperationsRepository
import com.example.ui.*
import com.example.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.combinedClickable
import kotlinx.coroutines.delay

class MainActivity : FragmentActivity() {
    private val isUnlockedState = mutableStateOf(false)
    private lateinit var viewModel: OperationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize DB and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = OperationsRepository(database)

        // 2. Initialize ViewModel
        val factory = OperationsViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[OperationsViewModel::class.java]

        // 3. Request permissions for notifications on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // 4. Schedule positive affirmations and weekly reports
        com.example.data.AffirmationScheduler.scheduleAll(this)
        com.example.data.WeeklyReportScheduler.schedule(this)
        com.example.data.SunExposureScheduler.schedule(this)
        com.example.data.DigestScheduler.scheduleAll(this)
        com.example.data.SupplementCheckScheduler.scheduleAll(this)
        com.example.data.CommunicationSkillScheduler.schedule(this)

        setContent {
            MyApplicationTheme {
                val appLockEnabled by viewModel.appLockEnabled.collectAsState()
                val isUnlocked by isUnlockedState

                if (appLockEnabled && !isUnlocked) {
                    LockScreen(
                        activity = this,
                        viewModel = viewModel,
                        onUnlockSuccess = {
                            isUnlockedState.value = true
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = WhitePure
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            MainAppLayout(viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            val appLockEnabled = viewModel.appLockEnabled.value
            if (appLockEnabled) {
                isUnlockedState.value = false
            }
        }
    }
}

@Composable
fun LockScreen(
    activity: FragmentActivity,
    viewModel: OperationsViewModel,
    onUnlockSuccess: () -> Unit
) {
    val fallbackPinHash by viewModel.fallbackPinHash.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isBiometricAvailable = remember { com.example.data.BiometricAuthManager.isBiometricAvailable(activity) }

    // Auto trigger biometric prompt on start
    LaunchedEffect(Unit) {
        if (isBiometricAvailable) {
            com.example.data.BiometricAuthManager.showBiometricPrompt(
                activity = activity,
                onSuccess = { onUnlockSuccess() },
                onError = { err ->
                    errorMessage = err
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhitePure)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gold Logo Icon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock Logo",
            tint = GoldClassic,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DIRECTEUR DES OPÉRATIONS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Anthracite,
            letterSpacing = 2.sp
        )
        Text(
            text = "Sécurité active",
            fontSize = 12.sp,
            color = MediumGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isBiometricAvailable) {
            Button(
                onClick = {
                    com.example.data.BiometricAuthManager.showBiometricPrompt(
                        activity = activity,
                        onSuccess = { onUnlockSuccess() },
                        onError = { err -> errorMessage = err }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldClassic),
                modifier = Modifier.testTag("biometric_unlock_button")
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = WhitePure)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Utiliser l'empreinte", color = WhitePure)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("OU", fontSize = 11.sp, color = MediumGray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // PIN code entry
        Text(
            text = if (fallbackPinHash.isEmpty()) "Définir un code PIN de secours" else "Saisir le code PIN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Anthracite
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN dot indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            for (i in 1..4) {
                val active = enteredPin.length >= i
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = if (active) GoldClassic else Color.Transparent,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = GoldClassic,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage!!, color = Color(0xFFC62828), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Custom elegant keypad
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫")
            )

            for (row in keys) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    for (key in row) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = Color(0xFFF9F9F9),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .clickable {
                                    when (key) {
                                        "C" -> enteredPin = ""
                                        "⌫" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                        else -> {
                                            if (enteredPin.length < 4) {
                                                enteredPin += key
                                                if (enteredPin.length == 4) {
                                                    // Verify PIN
                                                    if (fallbackPinHash.isEmpty()) {
                                                        // Register PIN
                                                        viewModel.setFallbackPin(enteredPin)
                                                        onUnlockSuccess()
                                                    } else {
                                                        val hash = com.example.data.BiometricAuthManager.hashPin(enteredPin)
                                                        if (hash == fallbackPinHash) {
                                                            onUnlockSuccess()
                                                        } else {
                                                            errorMessage = "Code PIN incorrect"
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontSize = 20.sp,
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

data class NavigationItem(
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val pageIndex: Int
)

fun hasAttentionNeeded(pageIndex: Int, viewModel: OperationsViewModel): Boolean {
    val todayStr = viewModel.getTodayDate()
    return when (pageIndex) {
        3 -> { // Compléments
            val log = viewModel.supplementLogs.value.firstOrNull { it.date == todayStr }
            log == null || !(log.creatine && log.omega3 && log.magnesium && log.ashwagandha && 
                             log.tongkatAli && log.vitaminD3 && log.zinc && log.lTheanine && 
                             log.boron && log.lCitrulline)
        }
        5 -> { // Récupération
            val log = viewModel.kegelLogs.value.firstOrNull { it.date == todayStr }
            log == null || !log.done
        }
        7 -> { // Communication
            val log = viewModel.communicationPracticeLogs.value.firstOrNull { it.date == todayStr }
            log == null || !log.practiced
        }
        else -> false
    }
}

@Composable
fun MainAppLayout(viewModel: OperationsViewModel) {
    var selectedPageIndex by remember { mutableIntStateOf(0) }
    var isSidebarOpen by remember { mutableStateOf(true) }
    var showGlobalBreathing by remember { mutableStateOf(false) }

    // State flows collected reactively for attention indicators
    val supplementLogs by viewModel.supplementLogs.collectAsState()
    val kegelLogs by viewModel.kegelLogs.collectAsState()
    val communicationPracticeLogs by viewModel.communicationPracticeLogs.collectAsState()

    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarOpen) 190.dp else 64.dp,
        animationSpec = tween(
            durationMillis = MotionTokens.DURATION_STANDARD,
            easing = MotionTokens.EasingStandard
        ),
        label = "SidebarWidth"
    )

    val navGroups = remember {
        listOf(
            "OPÉRATIONS" to listOf(
                NavigationItem("Dashboard", Icons.Filled.GridView, Icons.Outlined.GridView, 0),
                NavigationItem("To-Do List", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, 1),
                NavigationItem("Calendrier", Icons.Filled.DateRange, Icons.Outlined.DateRange, 2),
                NavigationItem("Rituel", Icons.Filled.Checklist, Icons.Outlined.Checklist, 16),
                NavigationItem("Chantiers", Icons.Filled.Construction, Icons.Outlined.Construction, 15)
            ),
            "SANTÉ" to listOf(
                NavigationItem("Compléments", Icons.Filled.LocalPharmacy, Icons.Outlined.LocalPharmacy, 3),
                NavigationItem("GYM", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, 4),
                NavigationItem("Récupération", Icons.Filled.FlashOn, Icons.Outlined.FlashOn, 5),
                NavigationItem("Testostérone", Icons.Filled.WbSunny, Icons.Outlined.WbSunny, 6),
                NavigationItem("Sommeil", Icons.Filled.NightsStay, Icons.Outlined.NightsStay, 8)
            ),
            "ESPRIT" to listOf(
                NavigationItem("Neurosciences", Icons.Filled.Psychology, Icons.Outlined.Psychology, 13),
                NavigationItem("Affirmations", Icons.Filled.SelfImprovement, Icons.Outlined.SelfImprovement, 12),
                NavigationItem("Pourquoi", Icons.Filled.Favorite, Icons.Outlined.Favorite, 11),
                NavigationItem("Communication", Icons.Filled.Forum, Icons.Outlined.Forum, 7),
                NavigationItem("Leadership", Icons.Filled.Groups, Icons.Outlined.Groups, 14)
            ),
            "SYSTÈME" to listOf(
                NavigationItem("Great Reset", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, 9),
                NavigationItem("Mes Streaks", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment, 17)
            )
        )
    }

    val settingsItem = remember {
        NavigationItem("Paramètres", Icons.Filled.Settings, Icons.Outlined.Settings, 10)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            // --- SIDEBAR NAVIGATION (LEFT) ---
            Column(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .background(WhitePure)
            ) {
                // Persistent Brand Indicator & Toggle button in header
                Spacer(modifier = Modifier.height(16.dp))
                if (isSidebarOpen) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(GradientTokens.sunsetHorizontal, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DIRECTEUR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldClassic,
                                letterSpacing = 1.5.sp
                            )
                        }

                        IconButton(
                            onClick = { isSidebarOpen = !isSidebarOpen },
                            modifier = Modifier.size(24.dp).testTag("sidebar_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Toggle Sidebar",
                                tint = GoldClassic,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GradientTokens.sunsetHorizontal, CircleShape)
                        )
                        IconButton(
                            onClick = { isSidebarOpen = !isSidebarOpen },
                            modifier = Modifier.size(28.dp).testTag("sidebar_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Toggle Sidebar",
                                tint = GoldClassic,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Navigation menu items grouped by categories
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    navGroups.forEachIndexed { groupIdx, (categoryName, items) ->
                        if (isSidebarOpen) {
                            Text(
                                text = categoryName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MediumGray,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(start = 16.dp, top = if (groupIdx > 0) 12.dp else 4.dp, bottom = 4.dp)
                            )
                        } else if (groupIdx > 0) {
                            Divider(
                                color = LightGrayDivider,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        items.forEach { item ->
                            SidebarItem(
                                item = item,
                                isActive = selectedPageIndex == item.pageIndex,
                                isSidebarOpen = isSidebarOpen,
                                hasAttention = hasAttentionNeeded(item.pageIndex, viewModel),
                                onClick = { 
                                    selectedPageIndex = item.pageIndex 
                                    isSidebarOpen = false
                                }
                            )
                        }
                    }
                }

                // Push settings to the bottom
                Divider(color = LightGrayDivider, modifier = Modifier.padding(horizontal = 8.dp))

                Spacer(modifier = Modifier.height(8.dp))

                SidebarItem(
                    item = settingsItem,
                    isActive = selectedPageIndex == settingsItem.pageIndex,
                    isSidebarOpen = isSidebarOpen,
                    hasAttention = false,
                    onClick = { 
                        selectedPageIndex = settingsItem.pageIndex 
                        isSidebarOpen = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Elegant single file border / divider on right edge
            Box(
                modifier = Modifier
                    .width(0.5.dp)
                    .fillMaxHeight()
                    .background(LightGrayDivider)
            )

            // --- MAIN SCREEN PANELS (RIGHT) ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                AnimatedContent(
                    targetState = selectedPageIndex,
                    transitionSpec = {
                        val isForward = targetState >= initialState
                        if (isForward) {
                            val enterTransition = fadeIn(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) + slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) { width -> width / 10 } + scaleIn(
                                initialScale = 0.95f,
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            )
                            
                            val exitTransition = fadeOut(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) + slideOutHorizontally(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) { width -> -width / 10 } + scaleOut(
                                targetScale = 0.97f,
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            )
                            enterTransition togetherWith exitTransition
                        } else {
                            val enterTransition = fadeIn(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) + slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) { width -> -width / 10 } + scaleIn(
                                initialScale = 0.95f,
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            )
                            
                            val exitTransition = fadeOut(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) + slideOutHorizontally(
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            ) { width -> width / 10 } + scaleOut(
                                targetScale = 0.97f,
                                animationSpec = tween(
                                    durationMillis = MotionTokens.DURATION_EMPHASIZED,
                                    easing = MotionTokens.EasingEmphasized
                                )
                            )
                            enterTransition togetherWith exitTransition
                        }
                    },
                    label = "PageTransition"
                ) { targetIndex ->
                    when (targetIndex) {
                        16 -> RitualPage(viewModel, onNavigateToPage = {
                            selectedPageIndex = it
                            isSidebarOpen = false
                        })
                        17 -> StreaksOverviewPage(viewModel, onNavigateToPage = {
                            selectedPageIndex = it
                            isSidebarOpen = false
                        })
                        0 -> DashboardPage(viewModel, onNavigateToPage = { 
                            selectedPageIndex = it 
                            isSidebarOpen = false
                        })
                        11 -> PourquoiPage(viewModel)
                        12 -> AffirmationsPage(viewModel)
                        1 -> TodoListPage(viewModel)
                        2 -> CalendrierPage(viewModel)
                        15 -> ChantiersPage(viewModel)
                        3 -> ComplementsPage(viewModel)
                        4 -> GymPage(viewModel)
                        5 -> RecoveryPage(viewModel)
                        13 -> NeurosciencePage(viewModel, onNavigateToPage = {
                            selectedPageIndex = it
                            isSidebarOpen = false
                        })
                        6 -> TestosteronePage(viewModel)
                        7 -> CommunicationPage(viewModel)
                        14 -> LeadershipPage(viewModel, onNavigateToPage = {
                            selectedPageIndex = it
                            isSidebarOpen = false
                        })
                        8 -> SommeilPage(viewModel)
                        9 -> SurviveGreatResetPage(viewModel)
                        10 -> SettingsPage(viewModel)
                        else -> DashboardPage(viewModel, onNavigateToPage = { 
                            selectedPageIndex = it 
                            isSidebarOpen = false
                        })
                    }
                }
            }
        }

        // --- GLOBAL FLOATING BUTTON FOR BREATHING ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            FloatingActionButton(
                onClick = { showGlobalBreathing = true },
                containerColor = Color.White,
                contentColor = GoldClassic,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, GradientTokens.sunsetHorizontal, CircleShape)
                    .testTag("global_respiration_fab")
            ) {
                Icon(
                    imageVector = Icons.Filled.SelfImprovement,
                    contentDescription = "Respiration Parfaite",
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // --- GLOBAL DIALOG OVERLAY ---
        if (showGlobalBreathing) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showGlobalBreathing = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Respiration Parfaite 🌬️",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Anthracite
                            )
                            IconButton(
                                onClick = { showGlobalBreathing = false },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer",
                                    tint = MediumGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        GlobalRespirationController(viewModel = viewModel, onDismiss = { showGlobalBreathing = false })
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarItem(
    item: NavigationItem,
    isActive: Boolean,
    isSidebarOpen: Boolean,
    hasAttention: Boolean = false,
    onClick: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    // Auto dismiss after 1.5 seconds
    LaunchedEffect(showTooltip) {
        if (showTooltip) {
            delay(1500)
            showTooltip = false
        }
    }

    val density = androidx.compose.ui.platform.LocalDensity.current
    val offsetX = with(density) { 60.dp.roundToPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .combinedPressClickEffect(
                onClick = { onClick() },
                onLongClick = {
                    if (!isSidebarOpen) {
                        showTooltip = true
                    }
                }
            )
            .background(if (isActive) LightBeige else Color.Transparent),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isSidebarOpen) 16.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isSidebarOpen) Arrangement.Start else Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = if (isActive) item.activeIcon else item.inactiveIcon,
                    contentDescription = item.label,
                    tint = if (isActive) GoldClassic else MediumGray,
                    modifier = Modifier.size(20.dp)
                )
                if (hasAttention) {
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(6.dp)
                            .background(GoldClassic, CircleShape)
                    )
                }
            }

            if (isSidebarOpen) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.label,
                    fontSize = 12.5.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActive) GoldClassic else Anthracite,
                    maxLines = 1
                )
            }
        }

        // Left-aligned gold vertical stripe indicator
        if (isActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.5.dp)
                    .fillMaxHeight()
                    .background(GradientTokens.sunsetVertical)
            )
        }

        // Tooltip popup
        if (showTooltip && !isSidebarOpen) {
            androidx.compose.ui.window.Popup(
                alignment = Alignment.CenterStart,
                offset = androidx.compose.ui.unit.IntOffset(x = offsetX, y = 0),
                onDismissRequest = { showTooltip = false }
            ) {
                Box(
                    modifier = Modifier
                        .background(Anthracite, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item.label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun Modifier.combinedPressClickEffect(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "PressScale"
    )
    return this
        .scale(scale)
        .combinedClickable(
            interactionSource = interactionSource,
            indication = androidx.compose.foundation.LocalIndication.current,
            onClick = onClick,
            onLongClick = onLongClick
        )
}
