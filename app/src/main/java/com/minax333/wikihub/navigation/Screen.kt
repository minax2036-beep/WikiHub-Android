package com.minax333.wikihub.navigation

enum class Screen(
    val title: String
) {
    HOME("ホーム"),
    WIKI_LIST("Wiki一覧"),
    RECENT_ARTICLES("最近の記事"),
    STATISTICS("統計"),
    SETTINGS("設定"),

    WIKI_CREATE("Wiki作成"),
    WIKI_HOME("Wiki"),
    WIKI_SETTINGS("Wiki設定")
}
