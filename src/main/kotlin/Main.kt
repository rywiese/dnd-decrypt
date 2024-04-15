import java.io.File

fun main(args: Array<String>) {
    val cipherFilePath: String = args[0]
    val keyword: String? = args.getOrNull(1)

    val cipherFile = File(cipherFilePath)
    val cipherText: String = cipherFile.readText().replace("\n", " ")
    println("Ciphertext:\n$cipherText\n")
    assert(cipherText.all { char -> char == ' ' || char in plaintextAlphabet }) {
        "Ciphertext must only contain spaces and letters in the alphabet"
    }

    val ciphers: List<Cipher> = mutableListOf<Cipher>().apply {
        if (keyword == null) {
            add(Cipher.Noop)
            add(Cipher.Atbash)
            add(Cipher.Caesar)
            plaintextAlphabet.indices().forEach { shift: Int ->
                add(Cipher.Shift(shift))
                plaintextAlphabet.indices()
                    .filter { factor: Int ->
                        factor.isCoprime()
                    }
                    .forEach { factor: Int ->
                        add(Cipher.Affine(factor, shift))
                    }
            }
            add(Cipher.Vigenere("JEFF"))
        } else {
            add(Cipher.Vigenere(keyword))
            add(Cipher.SimpleSubstitution(keyword))
            add(Cipher.Autokey(keyword))
        }
    }

    ciphers.forEach { cipher: Cipher ->
        // Decipher ciphertext
        val plaintext: String = cipher.decipher(cipherText)
        println("${cipher.name} plaintext:\n$plaintext\n")

        // Just for fun, re-encipher plaintext
        val recipherText: String = cipher.encipher(cipherText)
        println("${cipher.name} reciphertext:\n$recipherText\n")
    }

    // We were given the following hints:
    // 1. Cipher requires a key (hence why the non-key ciphers above are flagged off)
    // 2. The key is not "ABOVE"
    // 3. The plaintext for the 6th word (ciphertext "CRIXF") is "AGAIN"

    // I am going to try and reverse engineer the key.
    // Initial assumptions:
    // 1. vigenere
    // 2. key is 15 chars long and therefor we can get the first 5 letters of the key easily
    val cipherChunk = "CRIXF"
    val plainChunk = "AGAIN"
    val keyChunk: String = cipherChunk
        .mapIndexed { index: Int, cipherChar: Char ->
            plaintextAlphabet.indexOf(cipherChar) - plaintextAlphabet.indexOf(plainChunk[index])
        }
        .map { keyIndex: Int ->
            plaintextAlphabet[keyIndex]
        }
        .toString()
    println("My guess at the first chunk of the key is $keyChunk")
    // This was right, keyChunk is CLIPSE and the full key is ECLIPSE

}
