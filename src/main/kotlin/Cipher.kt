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

    class Affine(val factor: Int, val shift: Int) : Substitution {

        override val name: String = "Affine with formula `$factor * x + $shift mod 26`"

        // TODO: Find a better way?
        val inverseFactor: Int = (1..26)
            .firstOrNull { inverse: Int ->
                inverse.times(factor).mod(26) == 1
            }
            ?: throw IllegalArgumentException("$factor must be coprime with 26")

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.transform(factor, shift)

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char = cipherChar.zeroIndexedCode()
            .minus(shift)
            .times(inverseFactor)
            .mod(26)
            .toCharFromZeroIndexedCode()

    }

    class Caesar(val shift: Char) : Substitution by Affine(
        factor = 1,
        shift = -shift.zeroIndexedCode()
    ) {

        override val name: String = "Caesar with shift $shift"

    }

    class Vigenere(val keyword: String) : Substitution {

        override val name: String = "Vigenère with keyword $keyword"

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    class SimpleSubstitution(val keyword: String) : Substitution {

        override val name: String = "Simple Substitution with keyword $keyword"

        val cipherTextAlphabet: String = cipherTextAlphabetFrom(keyword)

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            cipherTextAlphabet[cipherChar.zeroIndexedCode()]

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            plainTextAlphabet[cipherTextAlphabet.indexOf(cipherChar)]

    }

}