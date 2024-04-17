package words

/**
 * @return a [String] resulting from splitting [this] [String] into words, applying [transform] to the words,
 * and joining the words back into a single [String]
 */
fun String.transformWords(transform: (List<String>) -> List<String>): String =
    transform(split(' ')).joinToString(" ")

/**
 * @return a [String] resulting from removing all spaces in [this] [String], applying [transform] to the despaced
 * [String], and reinserting spaces back in the same positions
 */
fun String.transformDespaced(transform: (String) -> String): String =
    transform(removeSpaces()).reInsertSpacesFrom(this)

fun String.removeSpaces(): String = replace(oldValue = " ", newValue = "")

/**
 * @param other is assumed to have an equal number of non-space [Char]s as [this]
 * @return [this] [String] but with spaces inserted such that the result has spaces at the same
 * indices as the original
 */
fun String.reInsertSpacesFrom(other: String): String =
    if (other.isNotEmpty()) {
        val (space: String, otherTail: String) = if (other[0] == ' ') {
            // drop the space and the corresponding char
            " " to other.drop(2)
        } else {
            // only drop the corresponding char
            "" to other.drop(1)
        }
        val head: Char = get(0)
        val tail: String = drop(1)
        space + head + tail.reInsertSpacesFrom(otherTail)
    } else ""

fun String.removeDuplicates(): String = toCharArray().distinct().joinToString(separator = "")

/**
 * @return the [String] resulting from applying [substitute] to each [Char]
 * @param offset to be added to each index before plugging into [substitute],
 * for use when [this] is not the first word in a cipher
 */
fun String.applySubstitutions(
    substitute: (Char, Int) -> Char,
    offset: Int = 0
): String = this
    .mapIndexed { index: Int, char: Char ->
        substitute(char, offset + index)
    }
    .joinToString(separator = "")

/**
 * @return the [List<String>] resulting from applying [substitute] to each [Char]
 * in each word in [this]. It is assumed that the words in [this] are in order, such that
 * `this[0][0]` has index 0 and `this[n][0]` has index `this[n-1].last() + 1`
 */
fun List<String>.applySubstitutions(
    substitute: (Char, Int) -> Char
): List<String> = this
    .zipWithOffsets()
    .map { (word: String, offset: Int) ->
        word.applySubstitutions(substitute, offset)
    }

/**
 * @return a [List] of [Pair]s where, for each [Pair], the first element is the corresponding word in [this] and the
 * second element is the sum of the sizes of all previous words in [this]
 */
fun List<String>.zipWithOffsets(): List<Pair<String, Int>> = this
    .runningFoldIndexed(0) { index: Int, offset: Int, _: String ->
        offset + (this.getOrNull(index - 1)?.length ?: 0)
    }
    .drop(1)
    .let(this::zip)
