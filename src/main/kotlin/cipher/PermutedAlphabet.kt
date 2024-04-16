package cipher

import Alphabet
import plaintextAlphabet

interface PermutedAlphabet : Substitution {

    val permutedAlphabet: Alphabet

    override fun encipherChar(plainChar: Char, index: Int): Char =
        permutedAlphabet[plaintextAlphabet.indexOf(plainChar)]

    override fun decipherChar(cipherChar: Char, index: Int): Char =
        plaintextAlphabet[permutedAlphabet.indexOf(cipherChar)]

}
