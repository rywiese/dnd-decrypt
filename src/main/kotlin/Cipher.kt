interface Cipher {

    val name: String

    fun encipher(plainText: String): String {
        val plainWords: List<String> = plainText.split(' ')
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

    object Caesar : Substitution by Shift(-3)  {

        override val name: String = "Caesar"

    }

    // Note that this could also be a PermutedAlphabet
    class Shift(val shift: Int) : Substitution by Affine(
        factor = 1,
        shift = shift
    ) {

        override val name: String = "Shift with shift $shift"

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

    interface PermutedAlphabet : Substitution {

        val permutedAlphabet: String

        override fun encipherSubstitution(cipherChar: Char, index: Int): Char =
            permutedAlphabet[cipherChar.alphabetIndex()]

        override fun decipherSubstitution(cipherChar: Char, index: Int): Char =
            alphabet[permutedAlphabet.indexOf(cipherChar)]

    }

    class SimpleSubstitution(val keyword: String) : PermutedAlphabet {

        override val name: String = "Simple Substitution with keyword $keyword"

        override val permutedAlphabet: String = permuteAlphabetWithPrefix(keyword)

    }

    object Atbash : PermutedAlphabet {

        override val name: String = "Atbash"

        override val permutedAlphabet: String = alphabet.reversed()

    }

    class Autokey(val keyword: String) : Cipher {

        override val name: String = "Autokey with keyword $keyword"

        override fun encipher(plainWords: List<String>): List<String> {
            // TODO: These gymnastics suggest that the hooks defined in the base interface may not be flexible enough...
            val plainText: String = plainWords.joinToString(separator = "")
            val cipherText: String = encipher(
                plainText = keyword + plainText,
                index = keyword.length,
                runningCipherText = ""
            )
            return cipherText.splitIntoSameSizeWordsAs(plainWords)
        }

        override fun decipher(cipherWords: List<String>): List<String> {
            // TODO: These gymnastics suggest that the hooks defined in the base interface may not be flexible enough...
            val cipherText: String = cipherWords.joinToString(separator = "")
            val plainText: String = decipher(
                cipherText = cipherText.replace(" ", ""),
                index = 0,
                runningKeyword = keyword
            )
            return plainText.splitIntoSameSizeWordsAs(cipherWords)
        }

        private fun encipher(plainText: String, index: Int, runningCipherText: String): String =
            if (index >= plainText.length) {
                runningCipherText
            } else {
                encipher(
                    plainText = plainText,
                    index = index + 1,
                    runningCipherText = runningCipherText + TabulaRecta.encode(
                        key = plainText[index],
                        char = plainText[index - keyword.length]
                    )
                )
            }

        private fun decipher(cipherText: String, index: Int, runningKeyword: String): String =
            if (index >= cipherText.length) {
                runningKeyword.removePrefix(keyword)
            } else {
                decipher(
                    cipherText = cipherText,
                    index = index + 1,
                    runningKeyword = runningKeyword + TabulaRecta.decode(
                        key = runningKeyword[index],
                        char = cipherText[index]
                    )
                )
            }

    }

}
