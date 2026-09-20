package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Wiki

@Composable
fun WikiListScreen(
    paddingValues: PaddingValues,
    wikis: List<Wiki>,
    onCreateWiki: () -> Unit,
    onOpenWiki: (Wiki) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Button(
            onClick = onCreateWiki,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("＋ 新しいWikiを作成")
        }

        if (wikis.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("まだWikiがありません。")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = wikis,
                    key = { it.id }
                ) { wiki ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onOpenWiki(wiki)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = wiki.name
                            )

                            if (wiki.description.isNotBlank()) {
                                Text(
                                    text = wiki.description,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
