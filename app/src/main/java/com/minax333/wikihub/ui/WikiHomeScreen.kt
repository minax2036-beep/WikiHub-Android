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
import com.minax333.wikihub.data.Wiki

@Composable
fun WikiHomeScreen(
    paddingValues: PaddingValues,
    wiki: Wiki,
    onSettings: () -> Unit,
    onBack: () -> Unit
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
            text = wiki.name
        )

        if (wiki.description.isNotBlank()) {
            Text(
                text = wiki.description,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                // 記事作成機能は次の段階
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("記事を作成")
        }

        Button(
            onClick = onSettings,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Wiki設定")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Wiki一覧に戻る")
        }
    }
}
