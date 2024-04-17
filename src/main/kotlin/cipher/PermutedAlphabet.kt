package cipher

import alphabet.Alphabet
import alphabet.plaintextAlphabet

/**
 * A [Substitution] [Cipher] where each [Char] in the plaintext maps to the [Char] in some permuted [Alphabet] with the
 * same index.
 *
 * Implementations need only define the [permutedAlphabet].
 */
interface PermutedAlphabet : Substitution {

    val permutedAlphabet: Alphabet

    override fun encipherChar(plainChar: Char, index: Int): Char =
        permutedAlphabet[plaintextAlphabet.indexOf(plainChar)]

    override fun decipherChar(cipherChar: Char, index: Int): Char =
        plaintextAlphabet[permutedAlphabet.indexOf(cipherChar)]

}
