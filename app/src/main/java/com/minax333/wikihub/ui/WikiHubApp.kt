package com.minax333.wikihub.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.minax333.wikihub.data.Wiki
import com.minax333.wikihub.data.WikiRepository
import com.minax333.wikihub.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiHubApp() {
    val context = LocalContext.current

    val repository = remember {
        WikiRepository(context)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    var selectedWikiId by remember {
        mutableStateOf<String?>(null)
    }

    var wikis by remember {
        mutableStateOf(emptyList<Wiki>())
    }

    fun reloadWikis() {
        wikis = repository.getAll()
    }

    LaunchedEffect(Unit) {
        reloadWikis()
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
                    title = when (currentScreen) {
                        Screen.WIKI_HOME,
                        Screen.WIKI_SETTINGS -> {
                            val wiki = selectedWikiId?.let {
                                repository.getById(it)
                            }

                            wiki?.name ?: currentScreen.title
                        }

                        else -> currentScreen.title
                    },
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
                        paddingValues = paddingValues,
                        wikiCount = wikis.size,
                        onCreateWiki = {
                            currentScreen = Screen.WIKI_CREATE
                        },
                        onOpenWikiList = {
                            currentScreen = Screen.WIKI_LIST
                        }
                    )
                }

                Screen.WIKI_LIST -> {
                    WikiListScreen(
                        paddingValues = paddingValues,
                        wikis = wikis,
                        onCreateWiki = {
                            currentScreen = Screen.WIKI_CREATE
                        },
                        onOpenWiki = { wiki ->
                            selectedWikiId = wiki.id
                            currentScreen = Screen.WIKI_HOME
                        }
                    )
                }

                Screen.WIKI_CREATE -> {
                    WikiCreateScreen(
                        paddingValues = paddingValues,
                        onBack = {
                            currentScreen = Screen.WIKI_LIST
                        },
                        onCreated = { wiki ->
                            reloadWikis()
                            selectedWikiId = wiki.id
                            currentScreen = Screen.WIKI_HOME
                        },
                        repository = repository
                    )
                }

                Screen.WIKI_HOME -> {
                    val wiki = selectedWikiId?.let {
                        repository.getById(it)
                    }

                    if (wiki != null) {
                        WikiHomeScreen(
                            paddingValues = paddingValues,
                            wiki = wiki,
                            onSettings = {
                                currentScreen = Screen.WIKI_SETTINGS
                            },
                            onBack = {
                                currentScreen = Screen.WIKI_LIST
                            }
                        )
                    } else {
                        currentScreen = Screen.WIKI_LIST
                    }
                }

                Screen.WIKI_SETTINGS -> {
                    val wiki = selectedWikiId?.let {
                        repository.getById(it)
                    }

                    if (wiki != null) {
                        WikiSettingsScreen(
                            paddingValues = paddingValues,
                            wiki = wiki,
                            repository = repository,
                            onSaved = {
                                reloadWikis()
                                currentScreen = Screen.WIKI_HOME
                            },
                            onDeleted = {
                                reloadWikis()
                                selectedWikiId = null
                                currentScreen = Screen.WIKI_LIST
                            },
                            onBack = {
                                currentScreen = Screen.WIKI_HOME
                            }
                        )
                    } else {
                        currentScreen = Screen.WIKI_LIST
                    }
                }

                Screen.RECENT_ARTICLES -> {
                    RecentArticlesScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.STATISTICS -> {
                    StatisticsScreen(
                        paddingValues = paddingValues,
                        wikiCount = wikis.size
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
