package alphabet

/**
 * @return the result of applying the affine transformation `[factor] * x + [shift]` where `x` is the
 * index of [this] in [plaintextAlphabet]
 */
fun Char.transform(factor: Int, shift: Int): Char =
    plaintextAlphabet[factor * plaintextAlphabet.indexOf(this) + shift]

/**
 * @param inverseFactor the [multiplicativeInverse] of `factor` in the original affine transformation
 * @return the result of inverting the affine transformation `factor * x + [shift]` where `x` is the
 * index of [this] in [plaintextAlphabet]
 */
fun Char.invertTransformation(inverseFactor: Int, shift: Int): Char =
    plaintextAlphabet[inverseFactor * (plaintextAlphabet.indexOf(this) - shift)]

/**
 * @return the multiplicative inverse of [factor] mod the [plaintextAlphabet] size, or null if none exists
 *
 * By definition, `factor * multiplicativeInverse(factor) === 1 (mod plaintextAlphabet.length)`.
 * For example, `multiplicativeInverse(3) == 9` because `3 * 9 === 27 === 1 (mod 26).
 *
 * This is the modulo analog of division in the real number system, where, for example, `1/3` is the multiplicative
 * inverse of `3` because `3 * 1/3 == 1`.
 */
fun multiplicativeInverse(factor: Int): Int? = plaintextAlphabet.indices()
    .firstOrNull { inverse: Int ->
        inverse.times(factor).mod(plaintextAlphabet.length()) == 1
    }

/**
 * @return whether [this] has a multiplicative inverse mod the [plaintextAlphabet] size
 */
fun Int.isCoprime(): Boolean = multiplicativeInverse(this) != null

fun Char.shiftBy(shift: Int): Char = transform(factor = 1, shift)

fun Char.shiftBackBy(shift: Int): Char = this.shiftBy(shift = -shift)

fun Char.shiftBy(shiftChar: Char): Char = shiftBy(plaintextAlphabet.indexOf(shiftChar))

fun Char.shiftBackBy(shiftChar: Char): Char = shiftBackBy(plaintextAlphabet.indexOf(shiftChar))
