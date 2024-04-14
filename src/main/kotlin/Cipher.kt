interface Cipher {

    val name: String

    fun encipher(plaintext: String): String {
        val plainWords: List<String> = plaintext.split(' ')
        val cipherWords: List<String> = encipher(plainWords)
        return cipherWords.joinToString(separator = " ")
    }

    fun decipher(cipherText: String): String {
        val cipherWords: List<String> = cipherText.split(' ')
        val plainWords: List<String> = decipher(cipherWords)
        return plainWords.joinToString(separator = " ")
    }

    fun encipher(plainWords: List<String>): List<String>

    fun decipher(cipherWords: List<String>): List<String>

    object Noop : Cipher {

        override val name: String = "NOOP"

        override fun encipher(plainWords: List<String>): List<String> = plainWords

        override fun decipher(cipherWords: List<String>): List<String> = cipherWords

    }

    interface Substitution : Cipher {

        override fun encipher(plainWords: List<String>): List<String> =
            applySubstitutions(plainWords, ::encipherSubstitution)

        override fun decipher(cipherWords: List<String>): List<String> =
            applySubstitutions(cipherWords, ::decipherSubstitution)

        fun encipherSubstitution(cipherChar: Char, index: Int): Char

        fun decipherSubstitution(cipherChar: Char, index: Int): Char

        private fun applySubstitutions(
            words: List<String>,
            substitute: (Char, Int) -> Char
        ): List<String> =
            words.mapIndexed { wordIndex: Int, word: String ->
                val numPreviousChars: Int = words.take(wordIndex)
                    .fold(0) { totalChars: Int, previousWord: String ->
                        totalChars + previousWord.length
                    }
                word
                    .mapIndexed { charIndex: Int, char: Char ->
                        val index: Int = numPreviousChars + charIndex
                        substitute(char, index)
                    }
                    .joinToString(separator = "")
            }

    }

    class Caesar(val shift: Char) : Substitution by Affine(
        factor = 1,
        shift = -shift.alphabetIndex()
    ) {

        override val name: String = "Caesar with shift $shift"

    }

    class Affine(val factor: Int, val shift: Int) : Substitution {

        override val name: String = "Affine with formula `$factor * x + $shift mod ${alphabet.length}`"

        val inverseFactor: Int = multiplicativeInverse(factor)
            ?: throw IllegalArgumentException("$factor must be coprime with ${alphabet.length}")

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.transform(factor, shift)

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.invertTransformation(inverseFactor, shift)

    }

    class Vigenere(val keyword: String) : Substitution {

        override val name: String = "Vigenère with keyword $keyword"

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    interface WithCipherTextAlphabet : Substitution {

        val cipherTextAlphabet: String

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherTextAlphabet[cipherChar.alphabetIndex()]

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            alphabet[cipherTextAlphabet.indexOf(cipherChar)]

    }

    class SimpleSubstitution(val keyword: String) : WithCipherTextAlphabet {

        override val name: String = "Simple Substitution with keyword $keyword"

        override val cipherTextAlphabet: String = permuteAlphabetWithPrefix(keyword)

    }

    class Shift(val shift: Int) : WithCipherTextAlphabet {

        override val name: String = "Shift with shift $shift"

        override val cipherTextAlphabet: String = shiftAlphabetBackBy(shift)

    }

}
