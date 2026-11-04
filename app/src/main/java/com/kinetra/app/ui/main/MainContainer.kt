package com.kinetra.app.ui.main

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
import com.kinetra.app.ui.discover.DiscoverScreen
import com.kinetra.app.ui.discover.DiscoverViewModel
import com.kinetra.app.ui.home.HomeScreen
import com.kinetra.app.ui.home.HomeViewModel
import com.kinetra.app.ui.profile.*
import com.kinetra.app.ui.race.*
import com.kinetra.app.ui.theme.*
import com.kinetra.app.ui.track.TrackScreen
import com.kinetra.app.ui.track.TrackViewModel

sealed class Screen {
    object Welcome : Screen()
    object SignIn : Screen()
    object PasswordRecovery : Screen()
    object MainContent : Screen( )
    object RaceDay : Screen()
    object RaceEntry : Screen()
    object RaceConfirmed : Screen()
    object AiIntelligence : Screen()
    object RaceLog : Screen()
    object Notifications : Screen()
    object Settings : Screen()
    object OrganizerDashboard : Screen()
}

enum class BottomTab(val title: String) {
    HOME("Home"),
    DISCOVER("Discover"),
    TRACK("Track"),
    PROFILE("Profile")
}

@Composable
fun MainContainer() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainContent) }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    val authViewModel = remember { AuthViewModel() }
    val homeViewModel = remember { HomeViewModel() }
    val discoverViewModel = remember { DiscoverViewModel() }
    val trackViewModel = remember { TrackViewModel() }
    val profileViewModel = remember { ProfileViewModel() }
    val raceViewModel = remember { RaceViewModel() }
    val aiViewModel = remember { AiIntelligenceViewModel() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content based on current screen
            when (currentScreen) {
                is Screen.Welcome -> WelcomeScreen(
                    onCreateAccountClick = { currentScreen = Screen.SignIn },
                    onSignInClick = { currentScreen = Screen.SignIn },
                    onGoogleSignInClick = { currentScreen = Screen.MainContent },
                    onDemoSkip = { currentScreen = Screen.MainContent }
                )
                is Screen.SignIn -> SignInScreen(
                    viewModel = authViewModel,
                    onSignInSuccess = { currentScreen = Screen.MainContent; selectedTab = BottomTab.HOME },
                    onForgotPasswordClick = { currentScreen = Screen.PasswordRecovery },
                    onCreateAccountClick = { currentScreen = Screen.SignIn },
                    onBack = { currentScreen = Screen.Welcome }
                )
                is Screen.PasswordRecovery -> PasswordRecoveryScreen(
                    viewModel = authViewModel,
                    onBackToSignIn = { currentScreen = Screen.SignIn }
                )
                is Screen.RaceDay -> RaceDayScreen(
                    viewModel = raceViewModel,
                    onRegisterClick = { currentScreen = Screen.RaceEntry },
                    onBack = { currentScreen = Screen.MainContent }
                )
                is Screen.RaceEntry -> RaceEntryScreen(
                    viewModel = raceViewModel,
                    onSubmitSuccess = { currentScreen = Screen.RaceConfirmed },
                    onBack = { currentScreen = Screen.RaceDay }
                )
                is Screen.RaceConfirmed -> ConfirmedScreen(
                    viewModel = raceViewModel,
                    onReturnHome = { currentScreen = Screen.MainContent; selectedTab = BottomTab.HOME }
                )
                is Screen.AiIntelligence -> AiIntelligenceScreen(
                    viewModel = aiViewModel,
                    onBack = { currentScreen = Screen.MainContent }
                )
                is Screen.RaceLog -> RaceLogScreen(
                    onBack = { currentScreen = Screen.MainContent; selectedTab = BottomTab.PROFILE }
                )
                is Screen.Notifications -> NotificationsScreen(
                    onBack = { currentScreen = Screen.MainContent; selectedTab = BottomTab.PROFILE }
                )
                is Screen.Settings -> SettingsScreen(
                    onBack = { currentScreen = Screen.MainContent; selectedTab = BottomTab.PROFILE },
                    onSignOut = { currentScreen = Screen.Welcome }
                )
                is Screen.OrganizerDashboard -> OrganizerDashboardScreen(
                    onBack = { currentScreen = Screen.MainContent; selectedTab = BottomTab.PROFILE }
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
                                        onClick = { selectedTab = tab },
                                        icon = {
                                            // Simple indicator or initial letter
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
                                    onStartRunClick = { selectedTab = BottomTab.TRACK },
                                    onRaceDetailsClick = { currentScreen = Screen.RaceDay },
                                    onAiIntelligenceClick = { currentScreen = Screen.AiIntelligence }
                                )
                                BottomTab.DISCOVER -> DiscoverScreen(
                                    viewModel = discoverViewModel,
                                    onEventClick = { currentScreen = Screen.RaceDay }
                                )
                                BottomTab.TRACK -> TrackScreen(
                                    viewModel = trackViewModel,
                                    onViewAnalysis = { currentScreen = Screen.AiIntelligence }
                                )
                                BottomTab.PROFILE -> ProfileScreen(
                                    viewModel = profileViewModel,
                                    onHistoryClick = { currentScreen = Screen.RaceLog },
                                    onSettingsClick = { currentScreen = Screen.Settings },
                                    onOrganizerClick = { currentScreen = Screen.OrganizerDashboard },
                                    onNotificationsClick = { currentScreen = Screen.Notifications }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
