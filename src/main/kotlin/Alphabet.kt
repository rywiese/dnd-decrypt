/**
 * All [Char]s that are valid for plaintext/ciphertext. Defines the "standard" mapping of [Char]s and [Int]s
 * ('A' to 0, 'Z' to 25, etc.) and the basis for all other permutations.
 */
val plaintextAlphabet: Alphabet = Alphabet(('A'..'Z').joinToString(""))

/**
 * Represents all [Char]s used in a given cipher.
 *
 * Defines the mapping from [Char] to [Int], such as `A` to 0, `Z` to 25, etc. These mappings can be obtained easily
 * using the [indexOf] and [get] functions. For example, assuming the standard alphabet:
 * ```
 * alphabet.indexOf('A') == 0
 * alphabet.indexOf('Z') == 25
 * alphabet[0] == 'A'
 * alphabet[25] == 'Z'
 * ```
 *
 * Also provides functionality for permuting into other [Alphabet]s.
 */
@JvmInline
value class Alphabet(private val alphabet: String) {

    // TODO: This would be nice, but init doesn't work well with value class
//    init {
//        require(!alphabet.containsDuplicateChars())
//    }

    /**
     * @return whether this [Alphabet] contains [char]
     */
    operator fun contains(char: Char): Boolean = char in alphabet

    /**
     * @return the index of [char] [Char] in the [alphabet]
     * @throws [IllegalArgumentException] if [char] is not in the alphabet
     *
     * Ex. alphabet.indexOf('A') == 0
     */
    fun indexOf(char: Char): Int = alphabet
        .indexOf(char)
        .takeIf { index: Int ->
            index != -1
        }
        ?: throw IllegalArgumentException("$this is not in the alphabet")

    /**
     * @return the [Char] in the [alphabet] with [index],
     * or [index] mod this [Alphabet] size if [index] is outside that range
     *
     * This is the inverse of [indexOf] restricted to the domain of [Char]s in this [Alphabet]
     *
     * Ex. alphabet['0'] == 'A'
     */
    operator fun get(index: Int): Char = alphabet[index.mod(alphabet.length)]

    fun length(): Int = alphabet.length

    fun indices(): IntRange = alphabet.indices

    /**
     * @return a permutation of this [Alphabet] that is reversed
     */
    fun reversed(): Alphabet = Alphabet(alphabet.reversed())

    /**
     * @return a permutation of this [Alphabet] shifted back by [shift], such that the first [shift] [Char]s in the
     * [Alphabet] have been removed from the front and appended to the back.
     */
    fun shiftBackBy(shift: Int): Alphabet = memoizedShiftedAlphabets
        .getOrPut(this to shift) {
            val firstN: String = alphabet.take(shift)
            val lastN: String = alphabet.drop(shift)
            Alphabet("$lastN$firstN")
        }

    /**
     * @return a permutation of this [Alphabet] with [keyword] as the prefix
     * and the remaining [Char]s (in the original order) as the suffix.
     *
     * Any duplicate [Char] (after first occurrence) are removed from [keyword].
     */
    fun permuteWithPrefix(keyword: String): Alphabet = Alphabet(
        keyword.removeDuplicates() + alphabet.filterNot { char: Char ->
            char in keyword
        }
    )

    companion object {

        /**
         * Internal mutable "cache" indexed by [Alphabet] and shift count so that we don't have to recompute/store
         * multiple of the same shifted [Alphabet]s if they are requested multiple times.
         */
        private val memoizedShiftedAlphabets: MutableMap<Pair<Alphabet, Int>, Alphabet> = mutableMapOf()

    }

}

object TabulaRecta {

    /**
     * @return the alphabet represented by the row in the tabula recta keyed by [key]
     */
    fun alphabetFor(key: Char): Alphabet = plaintextAlphabet.shiftBackBy(plaintextAlphabet.indexOf(key))

    /**
     * @return the [Char] in the tabula recta where the row is given by the [key] and the column is given by the [char]
     */
    fun encode(key: Char, char: Char): Char = alphabetFor(key)[plaintextAlphabet.indexOf(char)]

    /**
     * @return the [Char] in the "top" row of the tabula recta ([plaintextAlphabet]) where the index is the same as the
     * index of [char] in the row keyed by [key]
     */
    fun decode(key: Char, char: Char): Char = plaintextAlphabet[alphabetFor(key).indexOf(char)]

}
