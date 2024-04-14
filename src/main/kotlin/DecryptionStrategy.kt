interface DecryptionStrategy {

    val name: String

    fun decipher(cipherText: String): String {
        val cipherWords: List<String> = cipherText.split(' ')
        val plaintextWords: List<String> = decipher(cipherWords)
        return plaintextWords.joinToString(separator = " ")
    }

    fun decipher(cipherWords: List<String>): List<String>

    object Noop : DecryptionStrategy {

        override val name: String = "NOOP"

        override fun decipher(cipherWords: List<String>): List<String> = cipherWords

    }

    interface Substitution : DecryptionStrategy {

        override fun decipher(cipherWords: List<String>): List<String> = cipherWords
            .mapIndexed { cipherWordIndex: Int, cipherWord: String ->
                val numPreviousChars: Int = cipherWords.take(cipherWordIndex)
                    .fold(0) { totalChars: Int, previousWord: String ->
                        totalChars + previousWord.length
                    }
                cipherWord
                    .mapIndexed { indexInWord: Int, cipherChar: Char ->
                        val index: Int = numPreviousChars + indexInWord
                        substitute(cipherChar, index)
                    }
                    .joinToString(separator = "")
            }

        fun substitute(cipherChar: Char, index: Int): Char

    }

    class Caesar(val shift: Int) : Substitution {

        constructor(
            shiftChar: Char
        ) : this(
            shift = shiftChar.code - 'A'.code
        )

        override val name: String = "Caesar with shift $shift"

        override fun substitute(cipherChar: Char, index: Int): Char = cipherChar.shiftBy(shift)

    }

    class ReverseCaesar(val shift: Int) : Substitution by Caesar(-shift) {

        constructor(
            shiftChar: Char
        ) : this(
            shift = shiftChar.code - 'A'.code
        )

        override val name: String = "Reverse Caesar with shift $shift"

    }

    class Vigenere(val keyword: String) : Substitution {

        override val name: String = "Vigenère with keyword $keyword"

        override fun substitute(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    class ReverseVigenere(val keyword: String) : Substitution {

        override val name: String = "Reverse Vigenère with keyword $keyword"

        override fun substitute(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    class SimpleSubstitution(val keyword: String) : Substitution {

        override val name: String = "Simple Substitution with keyword $keyword"

        val cipherTextAlphabet: String = cipherTextAlphabetFrom(keyword)

        override fun substitute(cipherChar: Char, index: Int): Char =
            plainTextAlphabet[cipherTextAlphabet.indexOf(cipherChar)]

    }

    class ReverseSimpleSubstitution(val keyword: String) : Substitution {

        override val name: String = "Reverse Simple Substitution with keyword $keyword"

        val cipherTextAlphabet: String = cipherTextAlphabetFrom(keyword)

        override fun substitute(cipherChar: Char, index: Int): Char =
            // cipherTextAlphabet[plainTextAlphabet.indexOf(cipherChar)]
            // Optimization
            cipherTextAlphabet[cipherChar.zeroIndexedCode()]

    }

}