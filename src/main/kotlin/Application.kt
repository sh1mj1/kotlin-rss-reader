import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.minutes

fun main() =
    runBlocking {
        RssController(
            FeedSources(
                sources =
                    listOf(
                        UrlFeedSource("https://techblog.woowahan.com/feed"),
                        UrlFeedSource("https://v2.velog.io/rss/sh1mj1"),
                    ),
                dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME,
                checkUpdateTimeInterval = 10.minutes,
                scope = this,
            ),
        ).run()
    }
