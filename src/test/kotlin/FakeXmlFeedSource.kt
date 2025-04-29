import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class FakeXmlFeedSource(
    private var _fakeXml: String,
) : FeedSource {
    val fakeXml get() = _fakeXml

    override suspend fun fetchedDocument(): Document = documentBuilder.parse(ByteArrayInputStream(fakeXml.toByteArray()))

    fun updateFakeXml(newXml: String) {
        _fakeXml = newXml
    }

    companion object {
        private val documentBuilder: DocumentBuilder =
            DocumentBuilderFactory.newInstance().newDocumentBuilder()
    }
}
