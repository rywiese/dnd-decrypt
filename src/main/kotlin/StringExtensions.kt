fun String.splitIntoSameSizeWordsAs(others: List<String>): List<String> =
    if (others.isEmpty()) {
        emptyList()
    } else {
        val otherFirst: String = others.first()
        val othersRemaining: List<String> = others.drop(1)
        val thisFirst: String = take(otherFirst.length)
        val thisRemaining: String = drop(otherFirst.length)
        listOf(thisFirst) + thisRemaining.splitIntoSameSizeWordsAs(othersRemaining)
    }