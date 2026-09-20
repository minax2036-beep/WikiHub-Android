package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.minax333.wikihub.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiHubApp() {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                WikiHubDrawer(
                    currentScreen = currentScreen,
                    onScreenSelected = { screen ->
                        currentScreen = screen

                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                WikiHubTopBar(
                    title = currentScreen.title,
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { paddingValues ->

            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.WIKI_LIST -> {
                    WikiListScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.RECENT_ARTICLES -> {
                    RecentArticlesScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.STATISTICS -> {
                    StatisticsScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}
