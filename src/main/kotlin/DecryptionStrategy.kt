interface DecryptionStrategy {

    val name: String

    fun decrypt(cipherText: String): String {
        val cipherWords: List<String> = cipherText.split(' ')
        val plaintextWords: List<String> = decrypt(cipherWords)
        return plaintextWords.joinToString(separator = " ")
    }

    fun decrypt(cipherWords: List<String>): List<String>

    object Noop : DecryptionStrategy {

        override val name: String = "NOOP"

        override fun decrypt(cipherWords: List<String>): List<String> = cipherWords

    }

    interface Historical : DecryptionStrategy {

        override fun decrypt(cipherWords: List<String>): List<String> = cipherWords
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

    class Caesar(val shift: Int) : Historical {

        constructor(
            shiftChar: Char
        ) : this(
            shift = shiftChar.code - 'A'.code
        )

        override val name: String = "Caesar with shift $shift"

        override fun substitute(cipherChar: Char, index: Int): Char = cipherChar.shiftBy(shift)

    }

    class ReverseCaesar(val shift: Int) : Historical by Caesar(-shift) {

        constructor(
            shiftChar: Char
        ) : this(
            shift = shiftChar.code - 'A'.code
        )

        override val name: String = "Reverse Caesar with shift $shift"

    }

    class Vigenere(val keyword: String) : Historical {

        override val name: String = "Vigenère with keyword $keyword"

        override fun substitute(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBackBy(shiftChar = keyword[index.mod(keyword.length)])

    }

    class ReverseVigenere(val keyword: String) : Historical {

        override val name: String = "Reverse Vigenère with keyword $keyword"

        override fun substitute(cipherChar: Char, index: Int): Char =
            cipherChar.shiftBy(shiftChar = keyword[index.mod(keyword.length)])

    }

}