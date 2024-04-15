/**
 * All [Char]s that are valid for plaintext/ciphertext
 *
 * Note that [String] is an [Array] of [Char]s, and that the index of each [Char] in the [String] is its [Int]
 * representation in the transformations used throughout. For example, `A` is represented by 0, `Z` by 25, etc.
 * This is true for other permutations of the [alphabet] used later. [alphabetIndex] and [toAlphabetChar] can be used to
 * map each [Char] to its [Int] representation and vice versa (respectively), with validation and respecting mods.
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

private val memoizedShiftedAlphabets: Map<Int, String> = mutableMapOf()

fun shiftAlphabetBackBy(shift: Int): String = memoizedShiftedAlphabets
    .getOrElse(shift) {
        val firstN: String = alphabet.take(shift)
        val lastN: String = alphabet.drop(shift)
        "$lastN$firstN"
    }

object TabulaRecta {

    fun alphabetFor(key: Char): String = shiftAlphabetBackBy(key.alphabetIndex())

    fun decode(key: Char, char: Char): Char = alphabet[alphabetFor(key).indexOf(char)]

    fun encode(key: Char, char: Char): Char = alphabetFor(key)[char.alphabetIndex()]

}
