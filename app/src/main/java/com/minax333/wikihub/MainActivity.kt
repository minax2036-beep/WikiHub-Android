@file:OptIn(ExperimentalMaterial3Api::class)

package com.minax333.wikihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WikiHubApp()
        }
    }
}

enum class Screen {
    HOME,
    WIKI_LIST,
    RECENT_ARTICLES,
    STATISTICS,
    SETTINGS
}

@Composable
fun WikiHubApp() {
    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    fun navigateTo(screen: Screen) {
        currentScreen = screen

        scope.launch {
            drawerState.close()
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    WikiHubDrawer(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            navigateTo(screen)
                        }
                    )
                }
            ) {
                when (currentScreen) {
                    Screen.HOME -> {
                        HomeScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onWikiListClick = {
                                navigateTo(Screen.WIKI_LIST)
                            },
                            onSettingsClick = {
                                navigateTo(Screen.SETTINGS)
                            }
                        )
                    }

                    Screen.WIKI_LIST -> {
                        WikiListScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }

                    Screen.RECENT_ARTICLES -> {
                        RecentArticlesScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }

                    Screen.STATISTICS -> {
                        StatisticsScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }

                    Screen.SETTINGS -> {
                        SettingsScreen(
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WikiHubDrawer(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    ModalDrawerSheet {

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "WikiHub",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        NavigationDrawerItem(
            label = {
                Text("ホーム")
            },
            selected = currentScreen == Screen.HOME,
            onClick = {
                onNavigate(Screen.HOME)
            },
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )

        NavigationDrawerItem(
            label = {
                Text("Wiki一覧")
            },
            selected = currentScreen == Screen.WIKI_LIST,
            onClick = {
                onNavigate(Screen.WIKI_LIST)
            },
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )

        NavigationDrawerItem(
            label = {
                Text("最近の記事")
            },
            selected = currentScreen == Screen.RECENT_ARTICLES,
            onClick = {
                onNavigate(Screen.RECENT_ARTICLES)
            },
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )

        NavigationDrawerItem(
            label = {
                Text("統計")
            },
            selected = currentScreen == Screen.STATISTICS,
            onClick = {
                onNavigate(Screen.STATISTICS)
            },
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        NavigationDrawerItem(
            label = {
                Text("設定")
            },
            selected = currentScreen == Screen.SETTINGS,
            onClick = {
                onNavigate(Screen.SETTINGS)
            },
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        )
    }
}

@Composable
fun WikiHubTopBar(
    title: String,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            TextButton(
                onClick = onMenuClick
            ) {
                Text(
                    text = "☰",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    )
}

@Composable
fun HomeScreen(
    onMenuClick: () -> Unit,
    onWikiListClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            WikiHubTopBar(
                title = "WikiHub",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "WikiHubへようこそ",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "ローカルWikiをまとめて管理できます。",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onWikiListClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wiki一覧")
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    // Wiki作成機能は後で実装
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("＋ Wikiを作成")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "最近のWiki",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "まだWikiがありません。",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("設定を開く")
            }
        }
    }
}

@Composable
fun WikiListScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            WikiHubTopBar(
                title = "Wiki一覧",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "Wiki一覧",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "作成したWikiがここに表示されます。"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("まだWikiがありません。")
                }
            }
        }
    }
}

@Composable
fun RecentArticlesScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            WikiHubTopBar(
                title = "最近の記事",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "最近の記事",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "最近編集した記事がここに表示されます。"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("まだ記事がありません。")
                }
            }
        }
    }
}

@Composable
fun StatisticsScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            WikiHubTopBar(
                title = "統計",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "WikiHub統計",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("Wiki数")
                    Text("0")

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text("記事数")
                    Text("0")
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            WikiHubTopBar(
                title = "設定",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "WikiHub設定",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("設定項目")

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text("これから実装します。")
                }
            }
        }
    }
}
