import org.w3c.dom.Document
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
