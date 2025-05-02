fun readUpdatedKeyword(): String {
    println("아티클이 업데이트되었습니다.")
    println("검색할 키워드를 입력하세요")
    println("키워드를 입력하지 않으면, 모든 아티클을 검색합니다:")
    return readln()
}

fun showLoading() {
    println("Loading...")
}

fun showArticles(articles: List<Article>) {
    println("articles 개수: ${articles.size}")
    articles.forEachIndexed { index, article ->
        println(
            """
            ${index + 1} 번째 글
            제목: ${article.title}
            링크: ${article.url}
            작성일: ${article.createdAt}
            내용: ${article.description}
            ===================================
            
            """.trimIndent(),
        )
    }
}

fun showUnknowError() {
    println("알 수 없는 오류 발생, 관리자에게 문의하세요.")
}

fun showXmlParsingError() {
    println("XML 파싱 오류 발생")
}

fun showNetworkError() {
    println("네트워크 오류 발생")
}

fun showMissingDataError() {
    println("필수 데이터 누락 오류 발생")
}

fun showIoError() {
    println("입출력 오류 발생")
}

fun showDateParsingError() {
    println("날짜 형식 파싱 오류 발생")
}
