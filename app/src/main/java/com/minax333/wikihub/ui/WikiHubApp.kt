package com.minax333.wikihub.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.minax333.wikihub.data.Article
import com.minax333.wikihub.data.ArticleRepository
import com.minax333.wikihub.data.WikiRepository
import com.minax333.wikihub.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiHubApp() {

    val context = LocalContext.current

    val wikiRepository = remember {
        WikiRepository(context)
    }

    val articleRepository = remember {
        ArticleRepository(context)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    var currentScreen by remember {
        mutableStateOf(Screen.HOME)
    }

    var selectedWikiId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedArticleId by remember {
        mutableStateOf<String?>(null)
    }

    var wikis by remember {
        mutableStateOf(wikiRepository.getAll())
    }

    var articles by remember {
        mutableStateOf<List<Article>>(emptyList())
    }

    fun reloadWikis() {
        wikis = wikiRepository.getAll()
    }

    fun reloadArticles() {
        articles = selectedWikiId
            ?.let { articleRepository.getByWikiId(it) }
            ?: emptyList()
    }

    LaunchedEffect(Unit) {
        reloadWikis()
    }

    LaunchedEffect(selectedWikiId) {
        reloadArticles()
    }

    val selectedWiki = selectedWikiId
        ?.let { id ->
            wikis.firstOrNull { it.id == id }
        }

    val selectedArticle = selectedArticleId
        ?.let { id ->
            articles.firstOrNull { it.id == id }
        }

    val title = when (currentScreen) {

        Screen.WIKI_HOME,
        Screen.WIKI_SETTINGS -> {
            selectedWiki?.name ?: currentScreen.title
        }

        Screen.ARTICLE_VIEW -> {
            selectedArticle?.title ?: "記事"
        }

        Screen.ARTICLE_EDIT -> {
            selectedArticle?.title ?: "記事編集"
        }

        else -> currentScreen.title
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                WikiHubDrawer(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        currentScreen = screen

                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                WikiHubTopBar(
                    title = title,
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { paddingValues ->

            when (currentScreen) {

                Screen.HOME -> {
                    HomeScreen(
                        wikiCount = wikis.size,
                        onCreateWiki = {
                            currentScreen = Screen.WIKI_CREATE
                        },
                        onOpenWikiList = {
                            currentScreen = Screen.WIKI_LIST
                        }
                    )
                }

                Screen.WIKI_LIST -> {
                    WikiListScreen(
                        wikis = wikis,
                        onCreateWiki = {
                            currentScreen = Screen.WIKI_CREATE
                        },
                        onOpenWiki = { wikiId ->
                            selectedWikiId = wikiId
                            selectedArticleId = null
                            currentScreen = Screen.WIKI_HOME
                        }
                    )
                }

                Screen.WIKI_CREATE -> {
                    WikiCreateScreen(
                        wikiRepository = wikiRepository,
                        onCreated = { wikiId ->
                            reloadWikis()

                            selectedWikiId = wikiId
                            selectedArticleId = null
                            currentScreen = Screen.WIKI_HOME
                        },
                        onCancel = {
                            currentScreen = Screen.WIKI_LIST
                        }
                    )
                }

                Screen.WIKI_HOME -> {
                    selectedWiki?.let { wiki ->

                        WikiHomeScreen(
                            wiki = wiki,
                            articles = articles,
                            onCreateArticle = {
                                selectedArticleId = null
                                currentScreen = Screen.ARTICLE_CREATE
                            },
                            onOpenArticle = { articleId ->
                                selectedArticleId = articleId
                                currentScreen = Screen.ARTICLE_VIEW
                            },
                            onSettings = {
                                currentScreen = Screen.WIKI_SETTINGS
                            },
                            onBackToList = {
                                currentScreen = Screen.WIKI_LIST
                            }
                        )
                    }
                }

                Screen.WIKI_SETTINGS -> {
                    selectedWiki?.let { wiki ->

                        WikiSettingsScreen(
                            wiki = wiki,
                            wikiRepository = wikiRepository,
                            onSaved = {
                                reloadWikis()
                            },
                            onDeleted = {

                                articleRepository.deleteByWikiId(
                                    wiki.id
                                )

                                reloadWikis()

                                selectedWikiId = null
                                selectedArticleId = null

                                currentScreen = Screen.WIKI_LIST
                            },
                            onCancel = {
                                currentScreen = Screen.WIKI_HOME
                            }
                        )
                    }
                }

                Screen.ARTICLE_CREATE -> {
                    selectedWiki?.let { wiki ->

                        ArticleCreateScreen(
                            wikiId = wiki.id,
                            articles = articles,
                            articleRepository = articleRepository,
                            onCreated = { articleId ->

                                reloadArticles()

                                selectedArticleId = articleId
                                currentScreen = Screen.ARTICLE_VIEW
                            },
                            onCancel = {
                                currentScreen = Screen.WIKI_HOME
                            }
                        )
                    }
                }

                Screen.ARTICLE_VIEW -> {
                    selectedArticle?.let { article ->

                        ArticleViewScreen(
                            article = article,
                            onEdit = {
                                currentScreen = Screen.ARTICLE_EDIT
                            },
                            onDelete = {

                                articleRepository.delete(
                                    article.id
                                )

                                reloadArticles()

                                selectedArticleId = null
                                currentScreen = Screen.WIKI_HOME
                            },
                            onOpenArticle = { articleId ->
                                selectedArticleId = articleId
                            },
                            onBack = {
                                selectedArticleId = null
                                currentScreen = Screen.WIKI_HOME
                            }
                        )
                    }
                }

                Screen.ARTICLE_EDIT -> {
                    selectedArticle?.let { article ->

                        ArticleEditScreen(
                            article = article,
                            articles = articles,
                            articleRepository = articleRepository,
                            onSaved = { articleId ->

                                reloadArticles()

                                selectedArticleId = articleId
                                currentScreen = Screen.ARTICLE_VIEW
                            },
                            onCancel = {
                                currentScreen = Screen.ARTICLE_VIEW
                            }
                        )
                    }
                }

                Screen.RECENT_ARTICLES -> {
                    RecentArticlesScreen()
                }

                Screen.STATISTICS -> {
                    StatisticsScreen(
                        wikiCount = wikis.size,
                        articleCount = articleRepository.getAll().size
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen()
                }
            }
        }
    }
}
