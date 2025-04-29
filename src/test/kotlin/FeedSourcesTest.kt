import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.w3c.dom.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class FeedSourcesTest : BehaviorSpec({
    val dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
    val checkUpdateTimeInterval = 5.seconds

    Given("FakeXmlFeedSource를 사용하는 FeedSources 객체가 주어졌을 때") {
        val initialXml =
            """
            <rss><channel>
                <item>
                    <title>Initial Article One</title>
                    <link>https://example.com/initial/one</link>
                    <pubDate>Wed, 17 Apr 2024 10:00:00 GMT</pubDate>
                    <description>Initial description one.</description>
                </item>
                <item>
                    <title>Initial Article Two</title>
                    <link>https://example.com/initial/two</link>
                    <pubDate>Wed, 17 Apr 2024 11:00:00 GMT</pubDate>
                    <description>Initial description two.</description>
                </item>
            </channel></rss>
            """.trimIndent()
        val fakeFeedSource = FakeXmlFeedSource(initialXml)
        val feedSources =
            FeedSources(
                sources = listOf(fakeFeedSource),
                dateFormatter = dateFormatter,
                checkUpdateTimeInterval = checkUpdateTimeInterval,
            )

        Then("초기 XML에서 파싱된 Document 목록을 방출해야 한다") {
            runTest {
                val firstTitle =
                    feedSources.documentsState
                        .first(List<Document>::isNotEmpty)
                        .first()
                        .getElementsByTagName("title")
                        .item(0).textContent

                firstTitle shouldBe "Initial Article One"
            }
        }

        When("FakeXmlFeedSource가 업데이트되고 시간이 경과할 때") {
            val updatedXml =
                """
                <rss><channel>
                    <item>
                        <title>Updated Article Three</title>
                        <link>https://example.com/updated/three</link>
                        <pubDate>Wed, 17 Apr 2024 12:00:00 GMT</pubDate>
                        <description>Updated description three.</description>
                    </item>
                </channel></rss>
                """.trimIndent()

            Then("업데이트된 XML에서 파싱된 Document 목록을 방출해야 한다") {
                runTest {
                    fakeFeedSource.updateFakeXml(updatedXml)

                    val title =
                        feedSources.documentsState.first {
                            it.first().getElementsByTagName("title")
                                .item(0).textContent == "Updated Article Three"
                        }
                            .first()
                            .getElementsByTagName("title")
                            .item(0).textContent

                    title shouldBe "Updated Article Three"
                }
            }
        }
    }
    Given("articlesResultState Flow가 초기 수집될 때") {
        val initialXml =
            """
            <rss><channel>
                <item>
                    <title>Initial Article One</title>
                    <link>https://example.com/initial/one</link>
                    <pubDate>Wed, 17 Apr 2024 10:00:00 GMT</pubDate>
                    <description>Initial description one.</description>
                </item>
                <item>
                    <title>Initial Article Two</title>
                    <link>https://example.com/initial/two</link>
                    <pubDate>Wed, 17 Apr 2024 11:00:00 GMT</pubDate>
                    <description>Initial description two.</description>
                </item>
            </channel></rss>
            """.trimIndent()
        val fakeFeedSource = FakeXmlFeedSource(initialXml)
        val feedSources =
            FeedSources(
                sources = listOf(fakeFeedSource),
                dateFormatter = dateFormatter,
                checkUpdateTimeInterval = checkUpdateTimeInterval,
            )

        Then("초기 XML에서 파싱된 Article 목록을 포함하는 ArticlesResult.Success를 방출해야 한다") {
            runTest {
                val articlesResult =
                    feedSources.articlesResultState.first {
                        it is FeedSources.ArticlesResult.Success
                    } as FeedSources.ArticlesResult

                val successResult = articlesResult as FeedSources.ArticlesResult.Success
                val articles = successResult.articles

                articles.size shouldBe 2
                articles[0].title shouldBe "Initial Article One"
                articles[0].createdAt shouldBe LocalDateTime.of(2024, 4, 17, 10, 0, 0)

                articles[1].title shouldBe "Initial Article Two"
                articles[1].createdAt shouldBe LocalDateTime.of(2024, 4, 17, 11, 0, 0)
            }
        }

        When("FakeXmlFeedSource가 업데이트되고 시간이 경과하여 Flow가 다시 방출될 때") {
            val updatedXml =
                """
                <rss><channel>
                    <item>
                        <title>Updated Article Three</title>
                        <link>https://example.com/updated/three</link>
                        <pubDate>Wed, 17 Apr 2024 12:00:00 GMT</pubDate>
                        <description>Updated description three.</description>
                    </item>
                </channel></rss>
                """.trimIndent()
            Then("업데이트된 XML에서 파싱된 Article을 포함하는 ArticlesResult.Success를 방출해야 한다") {
                runTest {
                    fakeFeedSource.updateFakeXml(updatedXml)

                    val articlesResultSuccess =
                        feedSources.articlesResultState.first {
                            it is FeedSources.ArticlesResult.Success &&
                                it.articles.first().title == "Updated Article Three"
                        } as FeedSources.ArticlesResult.Success

                    articlesResultSuccess.articles.first().title shouldBe "Updated Article Three"
                }
            }
        }
    }
})
