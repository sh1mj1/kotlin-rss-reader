import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.w3c.dom.Document

class FakeXmlFeedSourceTest : BehaviorSpec({
    Given("FakeXmlFeedSource") {
        val initialXml =
            """
            <root>
                <item>Initial</item>
            </root>
            """.trimIndent()
        val fakeFeedSource = FakeXmlFeedSource(initialXml)

        When("fetchedDocument is called") {
            val initialDocument: Document = fakeFeedSource.fetchedDocument()

            Then("it should parse the initial XML") {
                initialDocument.getElementsByTagName("item").item(0).textContent shouldBe "Initial"
            }

            And("the fakeXml is updated") {
                fakeFeedSource.updateFakeXml("<root><item>Updated</item></root>")

                Then("fetchedDocument should reflect the updated XML") {
                    fakeFeedSource.fetchedDocument().getElementsByTagName("item")
                        .item(0).textContent shouldBe "Updated"
                }
            }
        }
    }
})
