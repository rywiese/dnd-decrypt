import java.io.File

fun main(args: Array<String>) {
    val cipherFilePath: String = args[0]
    val keyword: String? = args.getOrNull(1)

    val cipherFile = File(cipherFilePath)
    val cipherText: String = cipherFile.readText().replace("\n", "")
    println("Ciphertext:\n$cipherText\n")
    assert(cipherText.all { char -> char == ' ' || char in alphabet }) {
        "Ciphertext must only contain spaces and letters in the alphabet"
    }

    val ciphers: List<Cipher> = mutableListOf<Cipher>().apply {
        if (keyword == null) {
            add(Cipher.Noop)
            add(Cipher.Atbash)
            add(Cipher.Caesar)
            val alphabetIndexes: List<Int> = alphabet.map { char: Char ->
                char.alphabetIndex()
            }
            alphabetIndexes.forEach { shift: Int ->
                add(Cipher.Shift(shift))
                alphabetIndexes
                    .filter { factor: Int ->
                        factor.isCoprime()
                    }
                    .forEach { factor: Int ->
                        add(Cipher.Affine(factor, shift))
                    }
            }
            add(Cipher.Vigenere("JEFF"))
        } else {
            // We were given the hint that this does require a keyword
            // We were also told that "ABOVE" is not the key...
            add(Cipher.Vigenere(keyword))
            add(Cipher.SimpleSubstitution(keyword))
            add(Cipher.Autokey(keyword))
        }
    }

    ciphers.forEach { cipher: Cipher ->
        // Decipher ciphertext
        val plaintext: String = cipher.decipher(cipherText)
        println("${cipher.name} plaintext:\n$plaintext\n")

        // Just for fun, encipher plaintext
        val recipherText: String = cipher.encipher(cipherText)
        println("${cipher.name} reciphertext:\n$recipherText\n")
    }

}
