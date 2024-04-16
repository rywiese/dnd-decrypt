package cipher

import invertTransformation
import multiplicativeInverse
import plaintextAlphabet
import transform

class Affine(val factor: Int, val shift: Int) : Substitution {

    override val name: String = "Affine with formula `$factor * x + $shift mod ${plaintextAlphabet.length()}`"

    val inverseFactor: Int = multiplicativeInverse(factor)
        ?: throw IllegalArgumentException("$factor must be coprime with ${plaintextAlphabet.length()}")

    override fun encipherChar(plainChar: Char, index: Int): Char =
        plainChar.transform(factor, shift)

    override fun decipherChar(cipherChar: Char, index: Int): Char =
        cipherChar.invertTransformation(inverseFactor, shift)

}
