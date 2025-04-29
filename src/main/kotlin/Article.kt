import java.time.LocalDateTime

data class Article(
    val title: String,
    val url: String,
    val createdAt: LocalDateTime,
    val description: String = "",
) : Comparable<Article> {
    override fun compareTo(other: Article): Int =
        compareByDescending(Article::createdAt)
            .thenBy(Article::title)
            .compare(this, other)
}
