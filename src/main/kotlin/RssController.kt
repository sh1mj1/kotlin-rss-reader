import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RssController(
    val feedSources: FeedSources,
) {
    fun run() =
        runBlocking {
            feedSources.articlesResultState.collectLatest { articlesResult ->
                when (articlesResult) {
                    is FeedSources.ArticlesResult.Loading -> {
                        showLoading()
                    }

                    is FeedSources.ArticlesResult.Success -> {
                        val keyword = withContext(Dispatchers.IO) { readUpdatedKeyword() }

                        showArticles(articlesResult, keyword)
                    }

                    is FeedSources.ArticlesResult.Failure -> {
                        showErrorMessage(articlesResult)
                    }
                }
            }
        }

    private fun showArticles(
        articlesResult: FeedSources.ArticlesResult.Success,
        keyword: String,
    ) {
        val articles = articlesResult.articles

        if (keyword.isBlank()) {
            articles
                .sorted()
                .take(10)
                .let(::showArticles)
        } else {
            articles
                .filter { article ->
                    article.title.contains(keyword)
                }
                .sorted()
                .take(10)
                .let(::showArticles)
        }
    }

    private fun showErrorMessage(articlesResult: FeedSources.ArticlesResult.Failure) {
        when (articlesResult) {
            is FeedSources.ArticlesResult.Failure.DateParsingError -> {
                showDateParsingError()
            }

            is FeedSources.ArticlesResult.Failure.IoError -> {
                showIoError()
            }

            is FeedSources.ArticlesResult.Failure.MissingDataError -> {
                showMissingDataError()
            }

            is FeedSources.ArticlesResult.Failure.NetworkError -> {
                showNetworkError()
            }

            is FeedSources.ArticlesResult.Failure.XmlParsingError -> {
                showXmlParsingError()
            }

            is FeedSources.ArticlesResult.Failure.UnknownError -> {
                showUnknowError()
            }
        }
    }
}
