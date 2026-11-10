package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdvisorScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FarmManagerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KnowledgeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.AgriIaTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ThemePreference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AgriApplication.ensureFirebase(applicationContext)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val themePref by mainViewModel.themePreference.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themePref) {
                ThemePreference.SYSTEM -> systemDark
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }

            AgriIaTheme(darkTheme = isDark) {
                AppGate(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun AppGate(viewModel: MainViewModel) {
    var currentUser by remember {
        mutableStateOf(
            try {
                Firebase.auth.currentUser
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Erreur lecture currentUser: ${e.message}", e)
                null
            }
        )
    }

    DisposableEffect(Unit) {
        val listener = try {
            val l = FirebaseAuth.AuthStateListener { auth ->
                currentUser = auth.currentUser
                if (auth.currentUser != null) {
                    viewModel.onUserAuthenticated()
                }
            }
            Firebase.auth.addAuthStateListener(l)
            l
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Erreur addAuthStateListener: ${e.message}", e)
            null
        }

        onDispose {
            listener?.let {
                try {
                    Firebase.auth.removeAuthStateListener(it)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
                viewModel.onUserAuthenticated()
            }
        )
    } else {
        val uid = currentUser!!.uid
        key(uid) {
            MainAppScaffold(
                viewModel = viewModel,
                onSignedOut = {
                    currentUser = null
                }
            )
        }
    }
}

@Composable
fun MainAppScaffold(
    viewModel: MainViewModel,
    onSignedOut: () -> Unit
) {
    var currentTab by remember { mutableIntStateOf(0) }
    var initialAdvisorPrompt by remember { mutableStateOf<String?>(null) }
    var knowledgeInitialTab by remember { mutableIntStateOf(0) }

    // Handle Android system back button: return to Home screen if on another tab
    BackHandler(enabled = currentTab != 0) {
        currentTab = 0
    }

    val navBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Accueil"
                        )
                    },
                    label = { Text("Accueil", fontSize = 11.sp, fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = {
                        initialAdvisorPrompt = null
                        currentTab = 1
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Assistant"
                        )
                    },
                    label = { Text("Assistant", fontSize = 11.sp, fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = "Exploitation"
                        )
                    },
                    label = { Text("Mon Champ", fontSize = 11.sp, fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = {
                        knowledgeInitialTab = 0
                        currentTab = 3
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Savoir"
                        )
                    },
                    label = { Text("Savoir", fontSize = 11.sp, fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    selected = currentTab == 5,
                    onClick = { currentTab = 5 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    },
                    label = { Text("Profil", fontSize = 11.sp, fontWeight = if (currentTab == 5) FontWeight.Bold else FontWeight.Normal) },
                    colors = navBarItemColors
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAdvisor = { prompt ->
                        initialAdvisorPrompt = prompt
                        currentTab = 1
                    },
                    onNavigateToVision = { currentTab = 4 },
                    onNavigateToFarm = { currentTab = 2 },
                    onNavigateToKnowledge = { tabIndex ->
                        knowledgeInitialTab = tabIndex
                        currentTab = 3
                    }
                )
                1 -> AdvisorScreen(
                    viewModel = viewModel,
                    initialPrompt = initialAdvisorPrompt
                )
                2 -> FarmManagerScreen(viewModel = viewModel)
                3 -> KnowledgeScreen(
                    viewModel = viewModel,
                    initialTab = knowledgeInitialTab,
                    onAskAiAboutCrop = { question ->
                        initialAdvisorPrompt = question
                        currentTab = 1
                    }
                )
                4 -> VisionScreen(viewModel = viewModel)
                5 -> ProfileScreen(
                    viewModel = viewModel,
                    onSignedOut = onSignedOut
                )
            }
        }
    }
}
