package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WikiHubへようこそ"
        )

        Button(
            onClick = {
                // Wiki作成機能は後で実装
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Wikiを作成")
        }

        Button(
            onClick = {
                // Wiki一覧への移動処理は後で追加
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Wiki一覧を見る")
        }
    }
}
