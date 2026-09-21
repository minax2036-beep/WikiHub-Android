package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.Wiki

@Composable
fun WikiHomeScreen(
    wiki: Wiki,
    articles: List<Article>,
    onCreateArticle: () -> Unit,
    onOpenArticle: (String) -> Unit,
    onSettings: () -> Unit,
    onBackToList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = wiki.name
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (wiki.description.isNotBlank()) {
            Text(
                text = wiki.description
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }

        Button(
            onClick = onCreateArticle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("記事を作成")
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "記事一覧"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (articles.isEmpty()) {
            Text("まだ記事がありません。")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = articles,
                    key = { it.id }
                ) { article ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onOpenArticle(article.id)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(article.title)

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                "${article.blocks.size} ブロック"
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wiki設定")
        }

        TextButton(
            onClick = onBackToList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wiki一覧に戻る")
        }
    }
}
