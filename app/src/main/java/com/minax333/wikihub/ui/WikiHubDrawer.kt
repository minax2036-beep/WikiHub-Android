package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.navigation.Screen

@Composable
fun WikiHubDrawer(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "WikiHub"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        NavigationDrawerItem(
            label = {
                Text("ホーム")
            },
            selected = currentScreen == Screen.HOME,
            onClick = {
                onScreenSelected(Screen.HOME)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("Wiki一覧")
            },
            selected = currentScreen == Screen.WIKI_LIST,
            onClick = {
                onScreenSelected(Screen.WIKI_LIST)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("最近の記事")
            },
            selected = currentScreen == Screen.RECENT_ARTICLES,
            onClick = {
                onScreenSelected(Screen.RECENT_ARTICLES)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("統計")
            },
            selected = currentScreen == Screen.STATISTICS,
            onClick = {
                onScreenSelected(Screen.STATISTICS)
            }
        )

        NavigationDrawerItem(
            label = {
                Text("設定")
            },
            selected = currentScreen == Screen.SETTINGS,
            onClick = {
                onScreenSelected(Screen.SETTINGS)
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        HorizontalDivider()
    }
}
