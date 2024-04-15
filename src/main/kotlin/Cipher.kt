interface Cipher {

    val name: String

    fun encipher(plainText: String): String

    fun decipher(cipherText: String): String

    companion object {

        val noop: Cipher = wordedTextCipher(
            name = "NOOP",
            encipher = { plainWords: List<String> ->
                plainWords
            },
            decipher = { cipherWords: List<String> ->
                cipherWords
            }
        )

        val caesar = shift(-3)

        // Note that this could also be a PermutedAlphabet
        fun shift(shift: Int): Cipher = affine(factor = 1, shift = shift)

        fun affine(factor: Int, shift: Int): Cipher = substitution(
            name = "Affine with formula `$factor * x + $shift mod ${plaintextAlphabet.length()}`",
            encipherChar = { plainChar: Char, _: Int ->
                plainChar.transform(factor, shift)
            },
            decipherChar = { cipherChar: Char, _: Int ->
                val inverseFactor: Int = multiplicativeInverse(factor)
                    ?: throw IllegalArgumentException("$factor must be coprime with ${plaintextAlphabet.length()}")
                cipherChar.invertTransformation(inverseFactor, shift)
            }
        )

        fun vigenere(keyword: String): Cipher = substitution(
            name = "Vigenère with keyword $keyword",
            encipherChar = { plainChar: Char, index: Int ->
                plainChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])
            },
            decipherChar = { cipherChar: Char, index: Int ->
                cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])
            }
        )

        fun simpleSubstitution(keyword: String): Cipher = permutedAlphabet(
            name = "Simple Substitution with keyword $keyword",
            permutedAlphabet = plaintextAlphabet.permuteWithPrefix(keyword.removeDuplicates())
        )

        val atbash: Cipher = permutedAlphabet(
            name = "Atbash",
            permutedAlphabet = plaintextAlphabet.reversed()
        )

        fun autoKey(keyword: String): Cipher {
            fun autoKeyEncipher(
                keyword: String,
                plainText: String,
                index: Int,
                runningCipherText: String
            ): String =
                if (index >= plainText.length) {
                    runningCipherText
                } else {
                    autoKeyEncipher(
                        keyword = keyword,
                        plainText = plainText,
                        index = index + 1,
                        runningCipherText = runningCipherText + TabulaRecta.encode(
                            key = plainText[index],
                            char = plainText[index - keyword.length]
                        )
                    )
                }

            fun autoKeyDecipher(
                keyword: String,
                cipherText: String,
                index: Int,
                runningKeyword: String
            ): String =
                if (index >= cipherText.length) {
                    runningKeyword.removePrefix(keyword)
                } else {
                    autoKeyDecipher(
                        keyword = keyword,
                        cipherText = cipherText,
                        index = index + 1,
                        runningKeyword = runningKeyword + TabulaRecta.decode(
                            key = runningKeyword[index],
                            char = cipherText[index]
                        )
                    )
                }

            return despacedCipher(
                name = "Autokey with keyword $keyword",
                encipher = { despacedPlainText: String ->
                    autoKeyEncipher(
                        keyword = keyword,
                        plainText = keyword + despacedPlainText,
                        index = keyword.length,
                        runningCipherText = ""
                    )
                },
                decipher = { despacedCipherText: String ->
                    autoKeyDecipher(
                        keyword = keyword,
                        cipherText = despacedCipherText,
                        index = 0,
                        runningKeyword = keyword
                    )
                }
            )
        }

    }

}

// Constructors

fun rawTextCipher(
    name: String,
    encipher: (rawPlainText: String) -> String,
    decipher: (rawCipherText: String) -> String
): Cipher = object : Cipher {

    override val name: String = name

    override fun encipher(plainText: String): String = encipher(plainText)

    override fun decipher(cipherText: String): String = decipher(cipherText)

}

fun wordedTextCipher(
    name: String,
    encipher: (plainWords: List<String>) -> List<String>,
    decipher: (cipherWords: List<String>) -> List<String>
): Cipher = rawTextCipher(
    name,
    encipher = { rawPlainText: String ->
        val plainWords: List<String> = rawPlainText.split(' ')
        val cipherWords: List<String> = encipher(plainWords)
        cipherWords.joinToString(separator = " ")
    },
    decipher = { rawCipherText: String ->
        val cipherWords: List<String> = rawCipherText.split(' ')
        val plainWords: List<String> = decipher(cipherWords)
        plainWords.joinToString(separator = " ")
    }
)

fun despacedCipher(
    name: String,
    encipher: (despacedPlainText: String) -> String,
    decipher: (despacedCipherText: String) -> String
): Cipher = rawTextCipher(
    name,
    encipher = { rawPlainText: String ->
        val despacedCipherText: String = encipher(rawPlainText.replace(" ", ""))
        despacedCipherText.reInjectSpacesFrom(rawPlainText)
    },
    decipher = { rawCipherText: String ->
        val despacedPlainText: String = decipher(rawCipherText.replace(" ", ""))
        despacedPlainText.reInjectSpacesFrom(rawCipherText)
    }
)

fun substitution(
    name: String,
    encipherChar: (plainChar: Char, index: Int) -> Char,
    decipherChar: (cipherChar: Char, index: Int) -> Char
): Cipher {
    fun applySubstitutions(
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

    return wordedTextCipher(
        name,
        encipher = { plainWords: List<String> ->
            applySubstitutions(plainWords, encipherChar)
        },
        decipher = { cipherWords: List<String> ->
            applySubstitutions(cipherWords, decipherChar)
        }
    )
}

fun permutedAlphabet(
    name: String,
    permutedAlphabet: Alphabet
): Cipher = substitution(
    name,
    encipherChar = { plainChar: Char, index: Int ->
        permutedAlphabet[plaintextAlphabet.indexOf(plainChar)]
    },
    decipherChar = { cipherChar: Char, index: Int ->
        plaintextAlphabet[permutedAlphabet.indexOf(cipherChar)]
    }
)
