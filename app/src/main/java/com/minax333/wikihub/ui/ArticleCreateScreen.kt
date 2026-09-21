package com.minax333.wikihub.ui

import androidx.compose.runtime.Composable
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleBlock
import com.minax333.wikihub.data.ArticleRepository

@Composable
fun ArticleCreateScreen(
    wikiId: String,
    articles: List<Article>,
    articleRepository: ArticleRepository,
    onCreated: (String) -> Unit,
    onCancel: () -> Unit
) {
    ArticleEditorScreen(
        wikiId = wikiId,
        article = null,
        existingArticles = articles,
        onSave = { title, blocks ->

            val article = articleRepository.create(
                wikiId = wikiId,
                title = title,
                blocks = blocks
            )

            onCreated(article.id)
        },
        onCancel = onCancel
    )
}
