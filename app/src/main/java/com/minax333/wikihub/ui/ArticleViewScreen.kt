package com.minax333.wikihub.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleBlock

@Composable
fun ArticleViewScreen(
    article: Article,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenArticle: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    fun openMedia(
        uriString: String,
        mimeType: String
    ) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uriString)
        ).apply {
            type = mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "このファイルを開けるアプリがありません",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = article.title
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = article.blocks
            ) { block ->

                when (block) {

                    is ArticleBlock.Text -> {
                        Text(
                            text = block.text
                        )
                    }

                    is ArticleBlock.Image -> {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("画像")

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Button(
                                    onClick = {
                                        openMedia(
                                            block.uri,
                                            "image/*"
                                        )
                                    }
                                ) {
                                    Text("画像を開く")
                                }
                            }
                        }
                    }

                    is ArticleBlock.Video -> {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("動画")

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Button(
                                    onClick = {
                                        openMedia(
                                            block.uri,
                                            "video/*"
                                        )
                                    }
                                ) {
                                    Text("動画を開く")
                                }
                            }
                        }
                    }

                    is ArticleBlock.Audio -> {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("音声")

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Button(
                                    onClick = {
                                        openMedia(
                                            block.uri,
                                            "audio/*"
                                        )
                                    }
                                ) {
                                    Text("音声を開く")
                                }
                            }
                        }
                    }

                    is ArticleBlock.InternalLink -> {
                        TextButton(
                            onClick = {
                                onOpenArticle(
                                    block.articleId
                                )
                            }
                        ) {
                            Text(
                                text = "→ ${block.label}"
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("記事を編集")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("記事を削除")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wikiに戻る")
        }
    }
}
