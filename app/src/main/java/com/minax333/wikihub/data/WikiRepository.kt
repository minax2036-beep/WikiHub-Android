package com.minax333.wikihub.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class WikiRepository(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "wikihub_storage",
        Context.MODE_PRIVATE
    )

    private val wikiKey = "wikis"

    fun getAll(): List<Wiki> {
        val json = preferences.getString(wikiKey, "[]") ?: "[]"

        return try {
            val array = JSONArray(json)
            val result = mutableListOf<Wiki>()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                result.add(
                    Wiki(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        description = item.getString("description"),
                        createdAt = item.getLong("createdAt")
                    )
                )
            }

            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getById(id: String): Wiki? {
        return getAll().firstOrNull {
            it.id == id
        }
    }

    fun create(
        name: String,
        description: String
    ): Wiki {
        val wiki = Wiki(
            id = System.currentTimeMillis().toString(),
            name = name,
            description = description,
            createdAt = System.currentTimeMillis()
        )

        val wikis = getAll().toMutableList()
        wikis.add(wiki)

        saveAll(wikis)

        return wiki
    }

    fun update(
        id: String,
        name: String,
        description: String
    ): Wiki? {
        val wikis = getAll().toMutableList()

        val index = wikis.indexOfFirst {
            it.id == id
        }

        if (index == -1) {
            return null
        }

        val oldWiki = wikis[index]

        val updatedWiki = oldWiki.copy(
            name = name,
            description = description
        )

        wikis[index] = updatedWiki

        saveAll(wikis)

        return updatedWiki
    }

    fun delete(id: String) {
        val wikis = getAll()
            .filterNot {
                it.id == id
            }

        saveAll(wikis)
    }

    private fun saveAll(wikis: List<Wiki>) {
        val array = JSONArray()

        for (wiki in wikis) {
            val objectJson = JSONObject()

            objectJson.put("id", wiki.id)
            objectJson.put("name", wiki.name)
            objectJson.put("description", wiki.description)
            objectJson.put("createdAt", wiki.createdAt)

            array.put(objectJson)
        }

        preferences.edit()
            .putString(wikiKey, array.toString())
            .apply()
    }
}
