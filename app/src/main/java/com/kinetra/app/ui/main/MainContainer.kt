package com.kinetra.app.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.ai.AiIntelligenceScreen
import com.kinetra.app.ui.ai.AiIntelligenceViewModel
import com.kinetra.app.ui.auth.*
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
    // Navigation back-stack preserving complete navigation history
    val backStack = remember {
        mutableStateListOf(NavDestination(Screen.MainContent, BottomTab.HOME))
    }

    val currentDestination = backStack.lastOrNull() ?: NavDestination(Screen.MainContent, BottomTab.HOME)
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

    fun navigateTo(screen: Screen, tab: BottomTab = selectedTab) {
        val dest = NavDestination(screen, tab)
        if (backStack.lastOrNull() != dest) {
            backStack.add(dest)
        }
    }

    fun navigateToTab(tab: BottomTab) {
        if (currentScreen == Screen.MainContent && selectedTab == tab) return

        if (tab == BottomTab.HOME) {
            // Return to root Home tab: clear secondary tabs and sub-screens
            backStack.clear()
            backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
        } else {
            // Ensure Home is base of stack
            if (backStack.isEmpty() || backStack.first().screen != Screen.MainContent || backStack.first().tab != BottomTab.HOME) {
                backStack.clear()
                backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
            }
            // Remove previous instances of this tab to prevent redundant duplicates
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
            // If on a secondary tab and it's the only entry, return to root Home
            backStack.clear()
            backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
        }
    }

    fun resetToRoot() {
        backStack.clear()
        backStack.add(NavDestination(Screen.MainContent, BottomTab.HOME))
    }

    fun resetToWelcome() {
        backStack.clear()
        backStack.add(NavDestination(Screen.Welcome))
    }

    // Only allow BackHandler when not at the root screen or when drawer is open
    val canGoBack = drawerState.isOpen || backStack.size > 1 ||
        (backStack.size == 1 && backStack.first().screen == Screen.MainContent && backStack.first().tab != BottomTab.HOME)

    // Intercept hardware Back button and gesture navigation
    BackHandler(enabled = canGoBack) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentScreen == Screen.MainContent,
        drawerContent = {
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
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundDark
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main content based on current screen
                when (currentScreen) {
                    is Screen.Welcome -> WelcomeScreen(
                        onCreateAccountClick = { navigateTo(Screen.SignIn) },
                        onSignInClick = { navigateTo(Screen.SignIn) },
                        onGoogleSignInClick = { resetToRoot() },
                        onDemoSkip = { resetToRoot() }
                    )
                    is Screen.SignIn -> SignInScreen(
                        viewModel = authViewModel,
                        onSignInSuccess = { resetToRoot() },
                        onForgotPasswordClick = { navigateTo(Screen.PasswordRecovery) },
                        onCreateAccountClick = { navigateTo(Screen.SignIn) },
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
                        onSignOut = { resetToWelcome() }
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
                                NavigationBar(
                                    containerColor = ButtonBackground,
                                    tonalElevation = 8.dp
                                ) {
                                    BottomTab.values().forEach { tab ->
                                        NavigationBarItem(
                                            selected = selectedTab == tab,
                                            onClick = { navigateToTab(tab) },
                                            icon = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            if (selectedTab == tab) GlowBlueBright else TextGray,
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = tab.title,
                                                    color = if (selectedTab == tab) TextWhite else TextGray,
                                                    fontSize = 11.sp
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                indicatorColor = ButtonBorder
                                            )
                                        )
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
