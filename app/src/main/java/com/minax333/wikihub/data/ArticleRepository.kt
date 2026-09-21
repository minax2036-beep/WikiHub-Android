package com.minax333.wikihub.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ArticleRepository(
    context: Context
) {

    companion object {
        private const val PREFS_NAME = "wikihub_storage"
        private const val KEY_ARTICLES = "articles"
    }

    private val preferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getAll(): List<Article> {
        val json = preferences.getString(KEY_ARTICLES, "[]") ?: "[]"
        val array = JSONArray(json)

        val result = mutableListOf<Article>()

        for (i in 0 until array.length()) {
            result.add(articleFromJson(array.getJSONObject(i)))
        }

        return result.sortedByDescending { it.updatedAt }
    }

    fun getByWikiId(wikiId: String): List<Article> {
        return getAll()
            .filter { it.wikiId == wikiId }
            .sortedByDescending { it.updatedAt }
    }

    fun getById(id: String): Article? {
        return getAll().firstOrNull { it.id == id }
    }

    fun create(
        wikiId: String,
        title: String,
        blocks: List<ArticleBlock>
    ): Article {

        val now = System.currentTimeMillis()

        val article = Article(
            id = UUID.randomUUID().toString(),
            wikiId = wikiId,
            title = title,
            blocks = blocks,
            createdAt = now,
            updatedAt = now
        )

        val articles = getAll().toMutableList()
        articles.add(article)
        saveAll(articles)

        return article
    }

    fun update(
        id: String,
        title: String,
        blocks: List<ArticleBlock>
    ): Article? {

        val articles = getAll().toMutableList()
        val index = articles.indexOfFirst { it.id == id }

        if (index == -1) {
            return null
        }

        val oldArticle = articles[index]

        val updatedArticle = oldArticle.copy(
            title = title,
            blocks = blocks,
            updatedAt = System.currentTimeMillis()
        )

        articles[index] = updatedArticle
        saveAll(articles)

        return updatedArticle
    }

    fun delete(id: String) {
        val articles = getAll()
            .filterNot { it.id == id }

        saveAll(articles)
    }

    fun deleteByWikiId(wikiId: String) {
        val articles = getAll()
            .filterNot { it.wikiId == wikiId }

        saveAll(articles)
    }

    private fun saveAll(articles: List<Article>) {
        val array = JSONArray()

        articles.forEach { article ->
            array.put(articleToJson(article))
        }

        preferences.edit()
            .putString(KEY_ARTICLES, array.toString())
            .apply()
    }

    private fun articleToJson(article: Article): JSONObject {
        val blocks = JSONArray()

        article.blocks.forEach { block ->
            blocks.put(blockToJson(block))
        }

        return JSONObject().apply {
            put("id", article.id)
            put("wikiId", article.wikiId)
            put("title", article.title)
            put("createdAt", article.createdAt)
            put("updatedAt", article.updatedAt)
            put("blocks", blocks)
        }
    }

    private fun articleFromJson(json: JSONObject): Article {
        val blocksJson = json.optJSONArray("blocks") ?: JSONArray()
        val blocks = mutableListOf<ArticleBlock>()

        for (i in 0 until blocksJson.length()) {
            val block = blockFromJson(blocksJson.getJSONObject(i))

            if (block != null) {
                blocks.add(block)
            }
        }

        return Article(
            id = json.getString("id"),
            wikiId = json.getString("wikiId"),
            title = json.getString("title"),
            blocks = blocks,
            createdAt = json.getLong("createdAt"),
            updatedAt = json.getLong("updatedAt")
        )
    }

    private fun blockToJson(block: ArticleBlock): JSONObject {
        return when (block) {

            is ArticleBlock.Text -> JSONObject().apply {
                put("type", "text")
                put("text", block.text)
            }

            is ArticleBlock.Image -> JSONObject().apply {
                put("type", "image")
                put("uri", block.uri)
            }

            is ArticleBlock.Video -> JSONObject().apply {
                put("type", "video")
                put("uri", block.uri)
            }

            is ArticleBlock.Audio -> JSONObject().apply {
                put("type", "audio")
                put("uri", block.uri)
            }

            is ArticleBlock.InternalLink -> JSONObject().apply {
                put("type", "internal_link")
                put("articleId", block.articleId)
                put("label", block.label)
            }
        }
    }

    private fun blockFromJson(json: JSONObject): ArticleBlock? {
        return when (json.optString("type")) {

            "text" -> ArticleBlock.Text(
                text = json.optString("text")
            )

            "image" -> ArticleBlock.Image(
                uri = json.optString("uri")
            )

            "video" -> ArticleBlock.Video(
                uri = json.optString("uri")
            )

            "audio" -> ArticleBlock.Audio(
                uri = json.optString("uri")
            )

            "internal_link" -> ArticleBlock.InternalLink(
                articleId = json.optString("articleId"),
                label = json.optString("label")
            )

            else -> null
        }
    }
}
