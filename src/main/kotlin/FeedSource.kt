import org.w3c.dom.Document
import org.w3c.dom.Element
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

interface FeedSource {
    suspend fun fetchedDocument(): Document
}

class UrlFeedSource(private val url: String) : FeedSource {
    override suspend fun fetchedDocument(): Document = documentBuilder.parse(url)

    companion object {
        private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    }
}

@JvmName("parsedArticlesFromDocument")
fun Document.parsedArticles(dateTimeFormatter: DateTimeFormatter): List<Article> {
    val items = getElementsByTagName("item")

    return (0 until items.length).map { i ->
        val item = items.item(i) as Element
        val title = item.getElementsByTagName("title").item(0).textContent
        val link = item.getElementsByTagName("link").item(0).textContent
        val pubDate = item.getElementsByTagName("pubDate").item(0).textContent
        val publishedDate =
            ZonedDateTime.parse(pubDate, dateTimeFormatter).toLocalDateTime()
        val description = item.getElementsByTagName("description").item(0)?.textContent ?: ""

        Article(title, link, publishedDate, description)
    }
}
