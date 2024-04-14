/**
 * All [Char]s that are valid for plaintext/ciphertext
 */
val alphabet: String = ('A'..'Z').joinToString("")

/**
 * @return the index of [this] [Char] in the [alphabet]
 * @throws [IllegalArgumentException] if [this] is not in the alphabet
 *
 * Like [Char.code], but
 * 1. 'A' maps to 0 instead of 65 and
 * 2. 'Z' maps to 25 instead of 90
 */
fun Char.alphabetIndex(): Int = alphabet
    .indexOf(char = this)
    .takeIf { index: Int ->
        index != -1
    }
    ?: throw IllegalArgumentException("$this is not in the alphabet")

/**
 * @return the [Char] in the [alphabet] with this [this] index,
 * or [this] mod the [alphabet] size if [this] is outside that range
 *
 * This is the inverse of [alphabetIndex] restricted to the domain of [Char]s in the [alphabet]
 *
 * Like [Int.toChar], but
 * 1. 0 maps to 'A' instead of 65 mapping to 'A' and
 * 2. 25 maps to 'Z' instead of 90 mapping to 'Z'
 */
fun Int.toAlphabetChar(): Char = alphabet[this.mod(alphabet.length)]

/**
 * @return a permutation of [alphabet] with [keyword] as the prefix
 * and the remaining [Char]s (in the original order) as the suffix
 */
fun permuteAlphabetWithPrefix(keyword: String): String = keyword + alphabet.filterNot { char: Char ->
    char in keyword
}

fun shiftAlphabetBackBy(shift: Int): String {
    val firstN: String = alphabet.take(shift)
    val lastN: String = alphabet.drop(shift)
    return "$lastN$firstN"
}
