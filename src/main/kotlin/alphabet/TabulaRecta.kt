package alphabet

object TabulaRecta {

    /**
     * @return the alphabet represented by the row in the tabula recta keyed by [key]
     */
    fun alphabetFor(key: Char): Alphabet = plaintextAlphabet.shiftBackBy(plaintextAlphabet.indexOf(key))

    /**
     * @return the [Char] in the tabula recta where the row is given by the [key] and the column is given by the [plainChar]
     */
    fun encipher(key: Char, plainChar: Char): Char = alphabetFor(key)[plaintextAlphabet.indexOf(plainChar)]

    /**
     * @return the [Char] in the "top" row of the tabula recta ([plaintextAlphabet]) where the index is the same as the
     * index of [cipherChar] in the row keyed by [key]
     */
    fun decipher(key: Char, cipherChar: Char): Char = plaintextAlphabet[alphabetFor(key).indexOf(cipherChar)]

}