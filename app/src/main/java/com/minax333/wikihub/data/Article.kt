package com.minax333.wikihub.data

sealed interface ArticleBlock {

    data class Text(
        val text: String
    ) : ArticleBlock

    data class Image(
        val uri: String
    ) : ArticleBlock

    data class Video(
        val uri: String
    ) : ArticleBlock

    data class Audio(
        val uri: String
    ) : ArticleBlock

    data class InternalLink(
        val articleId: String,
        val label: String
    ) : ArticleBlock
}

data class Article(
    val id: String,
    val wikiId: String,
    val title: String,
    val blocks: List<ArticleBlock>,
    val createdAt: Long,
    val updatedAt: Long
)
