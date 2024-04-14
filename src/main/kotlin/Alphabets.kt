val plainTextAlphabet: String = ('A'..'Z').joinToString("")

fun cipherTextAlphabetFrom(keyword: String) = keyword + plainTextAlphabet.filterNot { char: Char ->
    char in keyword
}