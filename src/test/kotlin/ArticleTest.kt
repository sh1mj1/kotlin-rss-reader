import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ArticleTest : BehaviorSpec({
    Given("a list of articles") {
        val article1 =
            Article(
                title = "Title C",
                url = "fakeUrl1",
                createdAt = LocalDateTime.now().minusDays(1),
            )
        val article2 =
            Article(
                title = "Title D",
                url = "fakeUrl1",
                createdAt = LocalDateTime.now().minusDays(2),
            )

        val article3: Article =
            Article(
                title = "Title B",
                url = "fakeUrl1",
                createdAt = LocalDateTime.now().minusDays(4),
            )

        val article4 =
            Article(
                title = "Title A",
                url = "fakeUrl1",
                createdAt = LocalDateTime.now().minusDays(2),
            )
        val articles = listOf(article1, article2, article3, article4)
        When("sorting by createdAt and then title") {
            val sortedArticles = articles.sorted()
            Then("the list should be sorted correctly") {
                sortedArticles shouldBe listOf(article1, article4, article2, article3)
            }
        }
    }
})
