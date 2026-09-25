package com.minax333.wikihub.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleRepository

@Composable
fun ArticleEditScreen(
    paddingValues: PaddingValues,
    article: Article,
    articles: List<Article>,
    articleRepository: ArticleRepository,
    onSaved: (String) -> Unit,
    onCancel: () -> Unit
) {
    ArticleEditorScreen(
        paddingValues = paddingValues,
        wikiId = article.wikiId,
        article = article,
        existingArticles = articles,
        onSave = { title, blocks ->

            val updated = articleRepository.update(
                id = article.id,
                title = title,
                blocks = blocks
            )

            if (updated != null) {
                onSaved(updated.id)
            }
        },
        onCancel = onCancel
    )
}