/**
 * Like [Char.code], but
 * 1. 'A' maps to 0 instead of 65 and
 * 2. 'Z' maps to 25 instead of 90
 *
 * @throws [AssertionError] if [this] is not a capital letter
 */
fun Char.zeroIndexedCode(): Int {
    assert(this in 'A'..'Z') {
        "$this must be a capital letter"
    }
    return this.code - 'A'.code
}

/**
 * The inverse of [zeroIndexedCode]. Like [Int.toChar], but
 * 1. 0 maps to 'A' instead of 65 mapping to 'A' and
 * 2. 25 maps to 'Z' instead of 90 mapping to 'Z'
 *
 * @throws [AssertionError] if [this] is not the zero indexed code for a capital letter
 */
fun Int.toCharFromZeroIndexedCode(): Char {
    assert(this in 'A'.zeroIndexedCode()..'Z'.zeroIndexedCode()) {
        "$this must be the zero indexed code for a capital letter"
    }
    return (this + 'A'.code).toChar()
}

fun Char.shiftBy(shift: Int): Char = this.zeroIndexedCode()
    .plus(shift)
    .mod(26)
    .toCharFromZeroIndexedCode()

fun Char.shiftBackBy(shift: Int): Char = this.shiftBy(shift = -shift)

fun Char.shiftBy(shiftChar: Char): Char = shiftBy(shiftChar.zeroIndexedCode())

fun Char.shiftBackBy(shiftChar: Char): Char = shiftBackBy(shiftChar.zeroIndexedCode())