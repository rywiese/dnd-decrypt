fun String.splitIntoSameSizeWordsAs(others: List<String>): List<String> =
    if (others.isEmpty()) {
        emptyList()
    } else {
        val otherFirst: String = others.first()
        val othersRest: List<String> = others.drop(1)
        val thisFirst: String = take(otherFirst.length)
        val thisRest: String = drop(otherFirst.length)
        listOf(thisFirst) + thisRest.splitIntoSameSizeWordsAs(othersRest)
    }

fun String.removeDuplicateChars(): String = toCharArray().distinct().joinToString(separator = "")
