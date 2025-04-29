import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArticleParsingTest : BehaviorSpec({

    Given("XML 문서 파싱 기능이 주어졌을 때") {
        val fakeXml =
            """
            <rss>
                <channel>
                    <item>
                        <title>Article One</title>
                        <link>https://example.com/article/one</link>
                        <pubDate>Wed, 17 Apr 2024 10:00:00 GMT</pubDate>
                        <description>Description for article one.</description>
                    </item>
                    <item>
                        <title>Article Two</title>
                        <link>https://example.com/article/two</link>
                        <pubDate>Wed, 17 Apr 2024 11:00:00 GMT</pubDate>
                        <description>Description for article two.</description>
                    </item>
                    <item>
                        <title>Article Three</title>
                        <link>https://example.com/article/three</link>
                        <pubDate>Wed, 17 Apr 2024 09:00:00 GMT</pubDate>
                        <!-- Description 태그 없음 -->
                    </item>
                </channel>
            </rss>
            """.trimIndent()
        val fakeFeedSource = FakeXmlFeedSource(fakeXml)
        val dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME

        When("초기 XML 문서를 Article 목록으로 파싱할 때") {
            val document = fakeFeedSource.fetchedDocument()

            val articles: List<Article> = document.parsedArticles(dateTimeFormatter)

            Then("올바른 Article 객체 목록이 생성되어야 한다") {
                articles[0] shouldBe
                    Article(
                        title = "Article One",
                        url = "https://example.com/article/one",
                        createdAt = LocalDateTime.of(2024, 4, 17, 10, 0, 0),
                        description = "Description for article one.",
                    )

                articles[1] shouldBe
                    Article(
                        title = "Article Two",
                        url = "https://example.com/article/two",
                        createdAt = LocalDateTime.of(2024, 4, 17, 11, 0, 0),
                        description = "Description for article two.",
                    )

                articles[2] shouldBe
                    Article(
                        title = "Article Three",
                        url = "https://example.com/article/three",
                        createdAt = LocalDateTime.of(2024, 4, 17, 9, 0, 0),
                        description = "",
                    )
            }
        }

        When("fakeXml 값을 업데이트한 후 문서를 다시 Article 목록으로 파싱할 때") {
            val updatedFakeXml =
                """
                <rss>
                    <channel>
                        <item>
                            <title>업데이트된 네 번째 기사</title>
                            <link>https://example.com/article/four</link>
                            <pubDate>Wed, 17 Apr 2024 12:00:00 GMT</pubDate>
                            <description>네 번째 기사에 대한 설명입니다.</description>
                        </item>
                        <item>
                            <title>업데이트된 다섯 번째 기사</title>
                            <link>https://example.com/article/five</link>
                            <pubDate>Wed, 17 Apr 2024 13:00:00 GMT</pubDate>
                            <!-- Description 태그 없음 -->
                        </item>
                    </channel>
                </rss>
                """.trimIndent()

            fakeFeedSource.updateFakeXml(updatedFakeXml)

            val updatedDocument = fakeFeedSource.fetchedDocument()
            val updatedArticles = updatedDocument.parsedArticles(dateTimeFormatter)

            Then("업데이트된 내용으로 올바른 Article 객체 목록이 생성되어야 한다") {
                updatedArticles.size shouldBe 2

                updatedArticles[0] shouldBe
                    Article(
                        title = "업데이트된 네 번째 기사",
                        url = "https://example.com/article/four",
                        createdAt = LocalDateTime.of(2024, 4, 17, 12, 0, 0),
                        description = "네 번째 기사에 대한 설명입니다.",
                    )

                updatedArticles[1] shouldBe
                    Article(
                        title = "업데이트된 다섯 번째 기사",
                        url = "https://example.com/article/five",
                        createdAt = LocalDateTime.of(2024, 4, 17, 13, 0, 0),
                        description = "",
                    )
            }
        }
    }
})
