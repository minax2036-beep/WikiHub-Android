package com.minax333.wikihub.ui

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleBlock
import android.media.MediaPlayer

@Composable
fun ArticleViewScreen(
    paddingValues: PaddingValues,
    article: Article,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onOpenArticle: (String) -> Unit,
    onBack: () -> Unit
) {

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

            Text(
                text = article.title
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        items(
            items = article.blocks,
            key = {
                when (it) {
                    is ArticleBlock.Text ->
                        "text_${it.hashCode()}"

                    is ArticleBlock.Image ->
                        "image_${it.uri}"

                    is ArticleBlock.Video ->
                        "video_${it.uri}"

                    is ArticleBlock.Audio ->
                        "audio_${it.uri}"

                    is ArticleBlock.InternalLink ->
                        "link_${it.articleId}_${it.label}"
                }
            }
        ) { block ->

            when (block) {

                is ArticleBlock.Text -> {

                    Text(
                        text = block.text
                    )
                }

                is ArticleBlock.Image -> {

                    ArticleImagePreview(
                        uri = block.uri
                    )
                }

                is ArticleBlock.Video -> {

                    ArticleVideoPreview(
                        uri = block.uri
                    )
                }

                is ArticleBlock.Audio -> {

                    ArticleAudioPreview(
                        uri = block.uri
                    )
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

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("記事を編集")
            }

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("記事を削除")
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wikiに戻る")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun ArticleImagePreview(
    uri: String
) {
    AndroidView(
        factory = { context ->

            ImageView(context).apply {

                adjustViewBounds = true

                scaleType =
                    ImageView.ScaleType.FIT_CENTER

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            }
        },
        update = { imageView ->

            imageView.setImageURI(
                Uri.parse(uri)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ArticleVideoPreview(
    uri: String
) {

    val context = LocalContext.current

    AndroidView(
        factory = {

            VideoView(context).apply {

                setVideoURI(
                    Uri.parse(uri)
                )

                setMediaController(
                    MediaController(context)
                )

                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        240
                    )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ArticleAudioPreview(
    uri: String
) {

    val context = LocalContext.current

    var playing by remember(uri) {
        mutableStateOf(false)
    }

    val player = remember(uri) {

        MediaPlayer.create(
            context,
            Uri.parse(uri)
        )
    }

    DisposableEffect(player) {

        player?.setOnCompletionListener {
            playing = false
        }

        onDispose {
            player?.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text("音声")

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {

                if (player == null) {

                    Toast.makeText(
                        context,
                        "この音声を再生できません",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                if (player.isPlaying) {

                    player.pause()

                    playing = false

                } else {

                    player.start()

                    playing = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (playing) {
                    "一時停止"
                } else {
                    "▶ 音声を再生"
                }
            )
        }
    }
}
