package com.minax333.wikihub.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
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
            ?.let { wikiId ->
                articleRepository.getByWikiId(wikiId)
            }
            ?: emptyList()
    }

    LaunchedEffect(Unit) {
        reloadWikis()
    }

    LaunchedEffect(selectedWikiId) {
        reloadArticles()
    }

    val selectedWiki = selectedWikiId?.let { id ->
        wikis.firstOrNull { it.id == id }
    }

    val selectedArticle = selectedArticleId?.let { id ->
        articles.firstOrNull { it.id == id }
    }

    val title = when (currentScreen) {

        Screen.WIKI_HOME,
        Screen.WIKI_SETTINGS -> {
            selectedWiki?.name ?: currentScreen.title
        }

        Screen.ARTICLE_VIEW,
        Screen.ARTICLE_EDIT -> {
            selectedArticle?.title ?: currentScreen.title
        }

        else -> {
            currentScreen.title
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                WikiHubDrawer(
                    currentScreen = currentScreen,
                    onScreenSelected = { screen ->

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
                        paddingValues = paddingValues,
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
                        paddingValues = paddingValues,
                        wikis = wikis,
                        onCreateWiki = {
                            currentScreen = Screen.WIKI_CREATE
                        },
                        onOpenWiki = { wiki ->

                            selectedWikiId = wiki.id
                            selectedArticleId = null

                            currentScreen = Screen.WIKI_HOME
                        }
                    )
                }

                Screen.WIKI_CREATE -> {
                    WikiCreateScreen(
                        paddingValues = paddingValues,
                        repository = wikiRepository,
                        onCreated = { wikiId ->

                            reloadWikis()

                            selectedWikiId = wikiId
                            selectedArticleId = null

                            currentScreen = Screen.WIKI_HOME
                        },
                        onBack = {
                            currentScreen = Screen.WIKI_LIST
                        }
                    )
                }

                Screen.WIKI_HOME -> {

                    if (selectedWiki != null) {
                        WikiHomeScreen(
                            wiki = selectedWiki,
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
                                selectedArticleId = null
                                currentScreen = Screen.WIKI_LIST
                            }
                        )
                    }
                }

                Screen.WIKI_SETTINGS -> {

                    if (selectedWiki != null) {
                        WikiSettingsScreen(
                            wiki = selectedWiki,
                            paddingValues = paddingValues,
                            repository = wikiRepository,
                            onSaved = {
                                reloadWikis()
                            },
                            onDeleted = {

                                articleRepository.deleteByWikiId(
                                    selectedWiki.id
                                )

                                reloadWikis()

                                selectedWikiId = null
                                selectedArticleId = null

                                currentScreen = Screen.WIKI_LIST
                            },
                            onBack = {
                                currentScreen = Screen.WIKI_HOME
                            }
                        )
                    }
                }

                Screen.ARTICLE_CREATE -> {

                    if (selectedWiki != null) {
                        ArticleCreateScreen(
                            wikiId = selectedWiki.id,
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

                    if (selectedArticle != null) {
                        ArticleViewScreen(
                            article = selectedArticle,

                            onEdit = {
                                currentScreen = Screen.ARTICLE_EDIT
                            },

                            onDelete = {

                                articleRepository.delete(
                                    selectedArticle.id
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

                    if (selectedArticle != null) {
                        ArticleEditScreen(
                            article = selectedArticle,
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
                    RecentArticlesScreen(
                        paddingValues = paddingValues
                    )
                }

                Screen.STATISTICS -> {
                    StatisticsScreen(
                        paddingValues = paddingValues,
                        wikiCount = wikis.size,
                        articleCount = articleRepository.getAll().size
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}
