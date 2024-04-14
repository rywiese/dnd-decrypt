/**
 * @return the result of applying the affine transformation `[factor] * x + [shift]` where `x` is the
 * [alphabetIndex] of [this]
 */
fun Char.transform(factor: Int, shift: Int): Char = this
    .alphabetIndex()
    .times(factor)
    .plus(shift)
    .mod(alphabet.length)
    .toAlphabetChar()

/**
 * @param inverseFactor the [multiplicativeInverse] of `factor` in the original affine transformation
 * @return the result of inverting the affine transformation `factor * x + [shift]` where `x` is the
 * [alphabetIndex] of [this]
 */
fun Char.invertTransformation(inverseFactor: Int, shift: Int): Char = this
    .alphabetIndex()
    .minus(shift)
    .times(inverseFactor)
    .toAlphabetChar()

/**
 * @return the multiplicative inverse of [factor] mod the [alphabet] size, or null if none exists
 */
fun multiplicativeInverse(factor: Int): Int? = alphabet
    .map { char: Char ->
        char.alphabetIndex()
    }
    .firstOrNull { inverse: Int ->
        inverse.times(factor).mod(alphabet.length) == 1
    }

fun Char.shiftBy(shift: Int): Char = transform(factor = 1, shift)

fun Char.shiftBackBy(shift: Int): Char = this.shiftBy(shift = -shift)

fun Char.shiftBy(shiftChar: Char): Char = shiftBy(shiftChar.alphabetIndex())

fun Char.shiftBackBy(shiftChar: Char): Char = shiftBackBy(shiftChar.alphabetIndex())
