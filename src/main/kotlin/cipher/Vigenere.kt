package cipher

import alphabet.shiftBackBy
import alphabet.shiftBy

class Vigenere(val keyword: String) : Substitution {

    override val name: String = "Vigenère with keyword $keyword"

    override fun encipherChar(plainChar: Char, index: Int): Char =
        plainChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

    override fun decipherChar(cipherChar: Char, index: Int): Char =
        cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

}
