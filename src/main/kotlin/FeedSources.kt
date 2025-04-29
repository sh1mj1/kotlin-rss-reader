import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import org.w3c.dom.Document
import org.xml.sax.SAXParseException
import java.io.IOException
import java.net.ConnectException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.time.Duration

class FeedSources(
    private val sources: List<FeedSource>,
    private val dateFormatter: DateTimeFormatter,
    private val checkUpdateTimeInterval: Duration,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) {
    val documentsState: StateFlow<List<Document>> =
        flow {
            while (currentCoroutineContext().isActive) {
                val newDocuments: List<Document> = sources.map { it.fetchedDocument() }

                emit(newDocuments)
                delay(checkUpdateTimeInterval)
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    val articlesResultState: StateFlow<ArticlesResult> =
        documentsState
            .map { documents ->
                val articles = documents.parsedArticles(dateFormatter)
                ArticlesResult.Success(articles)
            }.catch { cause ->
                when (cause) {
                    is ConnectException -> ArticlesResult.Failure.NetworkError
                    is IOException -> ArticlesResult.Failure.IoError
                    is SAXParseException -> ArticlesResult.Failure.XmlParsingError
                    is DateTimeParseException -> ArticlesResult.Failure.DateParsingError
                    is NullPointerException -> ArticlesResult.Failure.MissingDataError
                    else -> ArticlesResult.Failure.UnknownError(cause)
                }
            }.stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = ArticlesResult.Loading,
            )

    sealed class ArticlesResult {
        object Loading : ArticlesResult()

        data class Success(val articles: List<Article>) : ArticlesResult()

        sealed class Failure : ArticlesResult() {
            object NetworkError : Failure()

            object IoError : Failure()

            object XmlParsingError : Failure()

            object DateParsingError : Failure()

            object MissingDataError : Failure()

            data class UnknownError(val cause: Throwable? = null) : Failure()
        }
    }
}

fun List<Document>.parsedArticles(dateTimeFormatter: DateTimeFormatter): List<Article> =
    this.flatMap {
        it.parsedArticles(dateTimeFormatter)
    }
