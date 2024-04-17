package cipher

import words.applySubstitutions
import words.mapWords

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
