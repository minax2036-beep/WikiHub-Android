package com.minax333.wikihub.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleBlock
import androidx.compose.ui.platform.LocalContext

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
            blocks = listOf(
                ArticleBlock.Text("")
            )
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
        .filter {
            it.id != article?.id
        }

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
                                    text = targetArticle.title
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

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

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
                    Text("記事のタイトル")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text("本文")

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        items(
            items = blocks.indices.toList(),
            key = { it }
        ) { index ->

            when (val block = blocks[index]) {

                is ArticleBlock.Text -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            value = block.text,
                            onValueChange = {
                                updateBlock(
                                    index,
                                    ArticleBlock.Text(it)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("本文を入力")
                            },
                            minLines = 5
                        )

                        TextButton(
                            onClick = {
                                deleteBlock(index)
                            }
                        ) {
                            Text("この文章を削除")
                        }
                    }
                }

                is ArticleBlock.Image -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("画像")

                        Text(
                            text = block.uri,
                            maxLines = 1
                        )

                        TextButton(
                            onClick = {
                                deleteBlock(index)
                            }
                        ) {
                            Text("画像を削除")
                        }
                    }
                }

                is ArticleBlock.Video -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("動画")

                        Text(
                            text = block.uri,
                            maxLines = 1
                        )

                        TextButton(
                            onClick = {
                                deleteBlock(index)
                            }
                        ) {
                            Text("動画を削除")
                        }
                    }
                }

                is ArticleBlock.Audio -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("音声")

                        Text(
                            text = block.uri,
                            maxLines = 1
                        )

                        TextButton(
                            onClick = {
                                deleteBlock(index)
                            }
                        ) {
                            Text("音声を削除")
                        }
                    }
                }

                is ArticleBlock.InternalLink -> {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "内部リンク: ${block.label}"
                        )

                        TextButton(
                            onClick = {
                                deleteBlock(index)
                            }
                        ) {
                            Text("リンクを削除")
                        }
                    }
                }
            }

            HorizontalDivider()
        }

        item {

            Text("追加")

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
                    Text("文章")
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
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = {
                    showLinkDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("内部リンク")
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (errorMessage != null) {

                Text(
                    text = errorMessage!!
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

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
                modifier = Modifier.height(24.dp)
            )
        }
    }
}
