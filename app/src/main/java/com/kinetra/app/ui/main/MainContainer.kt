package com.kinetra.app.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.data.AuthSessionState
import com.kinetra.app.data.SessionManager
import com.kinetra.app.tracking.repository.WorkoutRepository
import com.kinetra.app.ui.ai.AiIntelligenceScreen
import com.kinetra.app.ui.ai.AiIntelligenceViewModel
import com.kinetra.app.ui.auth.*
import com.kinetra.app.ui.components.KinetraAppBadge
import com.kinetra.app.ui.devices.ConnectedDevicesScreen
import com.kinetra.app.ui.devices.ConnectedDevicesViewModel
import com.kinetra.app.ui.discover.DiscoverScreen
import com.kinetra.app.ui.discover.DiscoverViewModel
import com.kinetra.app.ui.drawer.*
import com.kinetra.app.ui.home.HomeScreen
import com.kinetra.app.ui.home.HomeViewModel
import com.kinetra.app.ui.profile.*
import com.kinetra.app.ui.race.*
import com.kinetra.app.ui.theme.*
import com.kinetra.app.ui.track.TrackScreen
import com.kinetra.app.ui.track.TrackViewModel
import com.kinetra.app.ui.tracking.LiveTrackingScreen
import com.kinetra.app.ui.tracking.LiveTrackingViewModel
import kotlinx.coroutines.launch

sealed class Screen {
    object Welcome : Screen()
    object SignIn : Screen()
    object SignUp : Screen()
    object PasswordRecovery : Screen()
    object MainContent : Screen()
    object RaceDay : Screen()
    object RaceEntry : Screen()
    object RaceConfirmed : Screen()
    object AiIntelligence : Screen()
    object RaceLog : Screen()
    object Notifications : Screen()
    object EditProfile : Screen()
    object Settings : Screen()
    object OrganizerDashboard : Screen()
    object LiveTracking : Screen()
    object ConnectedDevices : Screen()
    object WorkoutHistory : Screen()
    object MyGoals : Screen()
    object Challenges : Screen()
    object Achievements : Screen()
    object HelpSupport : Screen()
}

private fun Screen.isAuthScreen(): Boolean =
    this is Screen.Welcome || this is Screen.SignIn || this is Screen.SignUp || this is Screen.PasswordRecovery

enum class BottomTab(val title: String) {
    HOME("Home"),
    DISCOVER("Discover"),
    TRACK("Track"),
    PROFILE("Profile")
}

data class NavDestination(
    val screen: Screen,
    val tab: BottomTab = BottomTab.HOME
)

@Composable
fun MainContainer() {
    val context = LocalContext.current
    val authState by SessionManager.authState.collectAsState()

    // Initialize navigation root strictly from device authentication state (never default to Home unauthenticated)
    val backStack = remember {
        mutableStateListOf(
            if (SessionManager.authState.value is AuthSessionState.Authenticated) {
                NavDestination(Screen.MainContent, BottomTab.HOME)
            } else {
                NavDestination(Screen.Welcome)
            }
        )
    }

    val currentDestination = backStack.lastOrNull() ?: if (authState is AuthSessionState.Authenticated) {
        NavDestination(Screen.MainContent, BottomTab.HOME)
    } else {
        NavDestination(Screen.Welcome)
    }
    val currentScreen = currentDestination.screen
    val selectedTab = currentDestination.tab

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val authViewModel = remember { AuthViewModel() }
    val homeViewModel = remember { HomeViewModel() }
    val discoverViewModel = remember { DiscoverViewModel() }
    val trackViewModel = remember { TrackViewModel() }
    val profileViewModel = remember { ProfileViewModel() }
    val notificationsViewModel = remember { NotificationsViewModel() }
    val raceViewModel = remember { RaceViewModel() }
    val aiViewModel = remember { AiIntelligenceViewModel() }
    val liveTrackingViewModel = remember { LiveTrackingViewModel() }
    val devicesViewModel = remember { ConnectedDevicesViewModel() }

    // Drive startup session validation & strict multi-user state isolation
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthSessionState.Checking -> {
                SessionManager.validatePersistedSession(context)
            }
            is AuthSessionState.Authenticated -> {
                WorkoutRepository.setActiveUser(state.user.id)
                profileViewModel.loadProfile()
                notificationsViewModel.loadNotifications()

                if (backStack.lastOrNull()?.screen?.isAuthScreen() != false) {
                    backStack.clear()
                    backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
                }
            }
            is AuthSessionState.Unauthenticated -> {
                WorkoutRepository.clearActiveUserCache()
                profileViewModel.clearUserData()
                notificationsViewModel.clearUserData()
                homeViewModel.clearUserData()

                val current = backStack.lastOrNull()?.screen
                if (current == null || !current.isAuthScreen()) {
                    backStack.clear()
                    backStack.add(NavDestination(Screen.Welcome))
                }
            }
        }
    }

    fun navigateTo(screen: Screen, tab: BottomTab = selectedTab) {
        val dest = NavDestination(screen, tab)
        if (backStack.lastOrNull() != dest) {
            backStack.add(dest)
        }
    }

    fun navigateToTab(tab: BottomTab) {
        if (currentScreen == Screen.MainContent && selectedTab == tab) return

        if (tab == BottomTab.HOME) {
            backStack.clear()
            backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
        } else {
            if (backStack.isEmpty() || backStack.first().screen != Screen.MainContent || backStack.first().tab != BottomTab.HOME) {
                backStack.clear()
                backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
            }
            backStack.removeAll { it.screen == Screen.MainContent && it.tab == tab }
            backStack.add(NavDestination(Screen.MainContent, tab))
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        } else if (backStack.size == 1 &&
            backStack.first().screen == Screen.MainContent &&
            backStack.first().tab != BottomTab.HOME
        ) {
            backStack.clear()
            backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
        }
    }

    fun resetToRoot() {
        if (SessionManager.isAuthenticated()) {
            backStack.clear()
            backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
        } else {
            backStack.clear()
            backStack.add(NavDestination(Screen.Welcome))
        }
    }

    fun performLogout() {
        coroutineScope.launch {
            if (drawerState.isOpen) {
                drawerState.close()
            }
        }
        authViewModel.logout(context) {
            WorkoutRepository.clearActiveUserCache()
            profileViewModel.clearUserData()
            notificationsViewModel.clearUserData()
            homeViewModel.clearUserData()
            backStack.clear()
            backStack.add(NavDestination(Screen.Welcome))
        }
    }

    // While verifying a persisted token, show a clean loading screen — never flash another user's Home
    if (authState is AuthSessionState.Checking) {
        SessionCheckingScreen()
        return
    }

    val isAuthenticated = authState is AuthSessionState.Authenticated

    val canGoBack = drawerState.isOpen || backStack.size > 1 ||
        (backStack.size == 1 && backStack.first().screen == Screen.MainContent && backStack.first().tab != BottomTab.HOME)

    BackHandler(enabled = canGoBack) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAuthenticated && currentScreen == Screen.MainContent,
        drawerContent = {
            if (isAuthenticated) {
                KinetraDrawerContent(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        coroutineScope.launch { drawerState.close() }
                        if (screen == Screen.MainContent) {
                            navigateToTab(BottomTab.HOME)
                        } else {
                            navigateTo(screen)
                        }
                    },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Enforce auth boundary: unauthenticated devices can ONLY access auth screens
                val effectiveScreen = if (!isAuthenticated && !currentScreen.isAuthScreen()) {
                    Screen.Welcome
                } else {
                    currentScreen
                }

                when (effectiveScreen) {
                    is Screen.Welcome -> WelcomeScreen(
                        onCreateAccountClick = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignUp)
                        },
                        onSignInClick = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignIn)
                        },
                        onGoogleSignInClick = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignIn)
                        },
                        onDemoSkip = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignIn)
                        }
                    )
                    is Screen.SignIn -> SignInScreen(
                        viewModel = authViewModel,
                        onSignInSuccess = { resetToRoot() },
                        onForgotPasswordClick = { navigateTo(Screen.PasswordRecovery) },
                        onCreateAccountClick = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignUp)
                        },
                        onBack = { navigateBack() }
                    )
                    is Screen.SignUp -> SignUpScreen(
                        viewModel = authViewModel,
                        onSignUpSuccess = { resetToRoot() },
                        onSignInClick = {
                            authViewModel.clearForm()
                            navigateTo(Screen.SignIn)
                        },
                        onBack = { navigateBack() }
                    )
                    is Screen.PasswordRecovery -> PasswordRecoveryScreen(
                        viewModel = authViewModel,
                        onBackToSignIn = { navigateBack() }
                    )
                    is Screen.RaceDay -> RaceDayScreen(
                        viewModel = raceViewModel,
                        onRegisterClick = { navigateTo(Screen.RaceEntry) },
                        onBack = { navigateBack() }
                    )
                    is Screen.RaceEntry -> RaceEntryScreen(
                        viewModel = raceViewModel,
                        onSubmitSuccess = { navigateTo(Screen.RaceConfirmed) },
                        onBack = { navigateBack() }
                    )
                    is Screen.RaceConfirmed -> ConfirmedScreen(
                        viewModel = raceViewModel,
                        onReturnHome = { resetToRoot() }
                    )
                    is Screen.AiIntelligence -> AiIntelligenceScreen(
                        viewModel = aiViewModel,
                        onBack = { navigateBack() }
                    )
                    is Screen.RaceLog -> RaceLogScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.Notifications -> NotificationsScreen(
                        viewModel = notificationsViewModel,
                        onBack = { navigateBack() }
                    )
                    is Screen.EditProfile -> EditProfileScreen(
                        viewModel = profileViewModel,
                        onBack = { navigateBack() },
                        onSaveSuccess = { navigateBack() }
                    )
                    is Screen.Settings -> SettingsScreen(
                        onBack = { navigateBack() },
                        onSignOut = { performLogout() }
                    )
                    is Screen.OrganizerDashboard -> OrganizerDashboardScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.LiveTracking -> LiveTrackingScreen(
                        viewModel = liveTrackingViewModel,
                        onBack = { navigateBack() },
                        onDevicesClick = { navigateTo(Screen.ConnectedDevices) }
                    )
                    is Screen.ConnectedDevices -> ConnectedDevicesScreen(
                        viewModel = devicesViewModel,
                        onBack = { navigateBack() }
                    )
                    is Screen.WorkoutHistory -> WorkoutHistoryScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.MyGoals -> MyGoalsScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.Challenges -> ChallengesScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.Achievements -> AchievementsScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.HelpSupport -> HelpSupportScreen(
                        onBack = { navigateBack() }
                    )
                    is Screen.MainContent -> {
                        Scaffold(
                            bottomBar = {
                                Column {
                                    HorizontalDivider(thickness = 1.dp, color = CardBorder)
                                    NavigationBar(
                                        containerColor = NavigationBarBg,
                                        tonalElevation = 0.dp
                                    ) {
                                        BottomTab.values().forEach { tab ->
                                            val isSelected = selectedTab == tab
                                            val iconVector: ImageVector = when (tab) {
                                                BottomTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                                                BottomTab.DISCOVER -> if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore
                                                BottomTab.TRACK -> if (isSelected) Icons.Filled.DirectionsRun else Icons.Outlined.DirectionsRun
                                                BottomTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                                            }
                                            NavigationBarItem(
                                                selected = isSelected,
                                                onClick = { navigateToTab(tab) },
                                                icon = {
                                                    Icon(
                                                        imageVector = iconVector,
                                                        contentDescription = tab.title,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = tab.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = BrandAccentText,
                                                    selectedTextColor = BrandAccentText,
                                                    unselectedIconColor = TextGray,
                                                    unselectedTextColor = TextGray,
                                                    indicatorColor = AccentTintSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            containerColor = BackgroundDark
                        ) { innerPadding ->
                            Box(modifier = Modifier.padding(innerPadding)) {
                                when (selectedTab) {
                                    BottomTab.HOME -> HomeScreen(
                                        viewModel = homeViewModel,
                                        onStartRunClick = { navigateTo(Screen.LiveTracking) },
                                        onRaceDetailsClick = { navigateTo(Screen.RaceDay) },
                                        onAiIntelligenceClick = { navigateTo(Screen.AiIntelligence) },
                                        onNotificationsClick = { navigateTo(Screen.Notifications) },
                                        unreadNotificationsCount = notificationsViewModel.uiState.collectAsState().value.unreadCount,
                                        onMenuClick = { coroutineScope.launch { drawerState.open() } }
                                    )
                                    BottomTab.DISCOVER -> DiscoverScreen(
                                        viewModel = discoverViewModel,
                                        onEventClick = { navigateTo(Screen.RaceDay) }
                                    )
                                    BottomTab.TRACK -> TrackScreen(
                                        viewModel = trackViewModel,
                                        onViewAnalysis = { navigateTo(Screen.AiIntelligence) }
                                    )
                                    BottomTab.PROFILE -> ProfileScreen(
                                        viewModel = profileViewModel,
                                        onHistoryClick = { navigateTo(Screen.RaceLog) },
                                        onSettingsClick = { navigateTo(Screen.Settings) },
                                        onOrganizerClick = { navigateTo(Screen.OrganizerDashboard) },
                                        onNotificationsClick = { navigateTo(Screen.Notifications) },
                                        onEditProfileClick = { navigateTo(Screen.EditProfile) }
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
private fun SessionCheckingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraAppBadge(size = 56.dp, cornerRadius = 14.dp)
            Text(
                text = "KINETRA",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 5.sp
            )
            CircularProgressIndicator(
                color = BrandPrimary,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Verifying your session...",
                color = TextGray,
                fontSize = 13.sp
            )
        }
    }
}
