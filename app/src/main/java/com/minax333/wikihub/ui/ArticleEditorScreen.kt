package com.minax333.wikihub.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleBlock

@Composable
fun ArticleEditorScreen(
    paddingValues: PaddingValues,
    wikiId: String,
    article: Article?,
    existingArticles: List<Article>,
    onSave: (title: String, blocks: List<ArticleBlock>) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var title by remember(article?.id) {
        mutableStateOf(article?.title ?: "")
    }

    var blocks by remember(article?.id) {
        mutableStateOf(
            article?.blocks
                ?: listOf(ArticleBlock.Text(""))
        )
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var showLinkDialog by remember {
        mutableStateOf(false)
    }

    fun persistUri(uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }
    }

    fun addBlock(block: ArticleBlock) {
        blocks = blocks + block
    }

    fun updateBlock(
        index: Int,
        block: ArticleBlock
    ) {
        blocks = blocks.toMutableList().also {
            it[index] = block
        }
    }

    fun deleteBlock(index: Int) {
        blocks = blocks.toMutableList().also {
            it.removeAt(index)
        }

        if (blocks.isEmpty()) {
            blocks = listOf(ArticleBlock.Text(""))
        }
    }

    val imageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                persistUri(it)

                addBlock(
                    ArticleBlock.Image(
                        it.toString()
                    )
                )
            }
        }

    val videoLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                persistUri(it)

                addBlock(
                    ArticleBlock.Video(
                        it.toString()
                    )
                )
            }
        }

    val audioLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            uri?.let {
                persistUri(it)

                addBlock(
                    ArticleBlock.Audio(
                        it.toString()
                    )
                )
            }
        }

    val linkableArticles = existingArticles
        .filter { it.id != article?.id }

    if (showLinkDialog) {
        AlertDialog(
            onDismissRequest = {
                showLinkDialog = false
            },
            title = {
                Text("内部リンクを追加")
            },
            text = {
                if (linkableArticles.isEmpty()) {
                    Text("リンクできる記事がありません。")
                } else {
                    LazyColumn {
                        items(
                            items = linkableArticles,
                            key = { it.id }
                        ) { targetArticle ->

                            TextButton(
                                onClick = {
                                    addBlock(
                                        ArticleBlock.InternalLink(
                                            articleId = targetArticle.id,
                                            label = targetArticle.title
                                        )
                                    )

                                    showLinkDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = targetArticle.title,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLinkDialog = false
                    }
                ) {
                    Text("閉じる")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = null
            },
            label = {
                Text("記事タイトル")
            },
            placeholder = {
                Text("記事のタイトルを入力")
            },
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(
                    minHeight = 56.dp
                ),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Text("ブロックを追加")

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        addBlock(
                            ArticleBlock.Text("")
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("テキスト")
                }

                OutlinedButton(
                    onClick = {
                        imageLauncher.launch(
                            arrayOf("image/*")
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("画像")
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        videoLauncher.launch(
                            arrayOf("video/*")
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("動画")
                }

                OutlinedButton(
                    onClick = {
                        audioLauncher.launch(
                            arrayOf("audio/*")
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("音声")
                }

                OutlinedButton(
                    onClick = {
                        showLinkDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("内部リンク")
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            blocks.forEachIndexed { index, block ->

                when (block) {

                    is ArticleBlock.Text -> {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("テキスト")

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                OutlinedTextField(
                                    value = block.text,
                                    onValueChange = {
                                        updateBlock(
                                            index,
                                            ArticleBlock.Text(it)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 4
                                )

                                TextButton(
                                    onClick = {
                                        deleteBlock(index)
                                    }
                                ) {
                                    Text("このブロックを削除")
                                }
                            }
                        }
                    }

                    is ArticleBlock.Image -> {
                        MediaBlockCard(
                            title = "画像",
                            uri = block.uri,
                            onDelete = {
                                deleteBlock(index)
                            }
                        )
                    }

                    is ArticleBlock.Video -> {
                        MediaBlockCard(
                            title = "動画",
                            uri = block.uri,
                            onDelete = {
                                deleteBlock(index)
                            }
                        )
                    }

                    is ArticleBlock.Audio -> {
                        MediaBlockCard(
                            title = "音声",
                            uri = block.uri,
                            onDelete = {
                                deleteBlock(index)
                            }
                        )
                    }

                    is ArticleBlock.InternalLink -> {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text("内部リンク")

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    "リンク先: ${block.label}"
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    "Article ID: ${block.articleId}"
                                )

                                TextButton(
                                    onClick = {
                                        deleteBlock(index)
                                    }
                                ) {
                                    Text("このブロックを削除")
                                }
                            }
                        }
                    }
                }

                if (index < blocks.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = errorMessage!!
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("キャンセル")
                }

                Button(
                    onClick = {
                        if (title.trim().isEmpty()) {
                            errorMessage =
                                "記事タイトルを入力してください。"
                            return@Button
                        }

                        errorMessage = null

                        onSave(
                            title.trim(),
                            blocks
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("保存")
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun MediaBlockCard(
    title: String,
    uri: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(title)

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = uri,
                maxLines = 2
            )

            TextButton(
                onClick = onDelete
            ) {
                Text("このブロックを削除")
            }
        }
    }
}