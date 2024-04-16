interface Cipher {

    val name: String

    fun encipher(plainText: String): String

    fun decipher(cipherText: String): String

    interface Substitution : Cipher {

        override fun encipher(plainText: String): String = plainText.mapWords { plainWords: List<String> ->
            plainWords.applySubstitutions(::encipherChar)
        }

        override fun decipher(cipherText: String): String = cipherText.mapWords { cipherWords: List<String> ->
            cipherWords.applySubstitutions(::decipherChar)
        }

        fun encipherChar(plainChar: Char, index: Int): Char

        fun decipherChar(cipherChar: Char, index: Int): Char

    }

    interface PermutedAlphabet : Substitution {

        val permutedAlphabet: Alphabet

        override fun encipherChar(plainChar: Char, index: Int): Char =
            permutedAlphabet[plaintextAlphabet.indexOf(plainChar)]

        override fun decipherChar(cipherChar: Char, index: Int): Char =
            plaintextAlphabet[permutedAlphabet.indexOf(cipherChar)]

    }

    object Noop : Cipher {

        override val name: String = "NOOP"

        override fun encipher(plainText: String): String = plainText

        override fun decipher(cipherText: String): String = cipherText

    }

    object Caesar : Substitution by Shift(-3) {

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

        override val name: String = "Affine with formula `$factor * x + $shift mod ${plaintextAlphabet.length()}`"

        val inverseFactor: Int = multiplicativeInverse(factor)
            ?: throw IllegalArgumentException("$factor must be coprime with ${plaintextAlphabet.length()}")

        override fun encipherChar(plainChar: Char, index: Int): Char =
            plainChar.transform(factor, shift)

        override fun decipherChar(cipherChar: Char, index: Int): Char =
            cipherChar.invertTransformation(inverseFactor, shift)

    }

    class Vigenere(val keyword: String) : Substitution {

        override val name: String = "Vigenère with keyword $keyword"

        override fun encipherChar(plainChar: Char, index: Int): Char =
            plainChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

        override fun decipherChar(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    class SimpleSubstitution(val keyword: String) : PermutedAlphabet {

        override val name: String = "Simple Substitution with keyword $keyword"

        override val permutedAlphabet: Alphabet = plaintextAlphabet.permuteWithPrefix(keyword.removeDuplicates())

    }

    object Atbash : PermutedAlphabet {

        override val name: String = "Atbash"

        override val permutedAlphabet: Alphabet = plaintextAlphabet.reversed()

    }

    class Autokey(val keyword: String) : Cipher {

        override val name: String = "Autokey with keyword $keyword"

        override fun encipher(plainText: String): String = plainText
            .transformDespaced { despacedPlainText: String ->
                encipher(
                    plainText = keyword + despacedPlainText,
                    index = keyword.length,
                    runningCipherText = ""
                )
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

        override fun decipher(cipherText: String): String = cipherText
            .transformDespaced { despacedCipherText: String ->
                decipher(
                    cipherText = despacedCipherText,
                    index = 0,
                    runningKeyword = keyword
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
