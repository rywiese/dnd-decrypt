package cipher

import words.applySubstitutions
import words.mapWords

/**
 * A [Cipher] where each [Char] in the plaintext is transformed to a new [Char] using only knowledge of the [Char]
 * itself and its index in the plaintext.
 *
 * Implementations need only implement the logic to [encipherChar] and [decipherChar] based on those parameters.
 */
interface Substitution : Cipher {

    override fun encipher(plainText: String): String = plainText
        .mapWords { plainWords: List<String> ->
            plainWords.applySubstitutions(::encipherChar)
        }

    override fun decipher(cipherText: String): String = cipherText
        .mapWords { cipherWords: List<String> ->
            cipherWords.applySubstitutions(::decipherChar)
        }

    fun encipherChar(plainChar: Char, index: Int): Char

    fun decipherChar(cipherChar: Char, index: Int): Char

}
