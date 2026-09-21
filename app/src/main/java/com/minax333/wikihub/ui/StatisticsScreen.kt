package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsScreen(
    wikiCount: Int,
    articleCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("統計")

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text("Wiki数: $wikiCount")

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text("記事数: $articleCount")
    }
}
