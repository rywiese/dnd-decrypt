import java.io.File

fun main(args: Array<String>) {
    val cipherFilePath: String = args[0]
    val vegenereKeyword: String? = args.getOrNull(1)

    val cipherFile = File(cipherFilePath)
    val cipherText: String = cipherFile.readText().replace("\n", "")
    println("Ciphertext:\n$cipherText\n")
    assert(cipherText.all { char -> char == ' ' || char in 'A'..'Z' }) {
        "Ciphertext must only contain spaces and capital letters."
    }

    val decryptionStrategies: List<DecryptionStrategy> = mutableListOf<DecryptionStrategy>().apply {
        add(DecryptionStrategy.Noop)
        ('A'..'Z').forEach { shiftChar: Char ->
            add(DecryptionStrategy.Caesar(shiftChar))
            add(DecryptionStrategy.ReverseCaesar(shiftChar))
        }
        add(DecryptionStrategy.Vigenere("JEFF"))
        add(DecryptionStrategy.ReverseVigenere("JEFF"))
        if (vegenereKeyword != null) {
            add(DecryptionStrategy.Vigenere(vegenereKeyword))
            add(DecryptionStrategy.ReverseVigenere(vegenereKeyword))
        }
    }

    decryptionStrategies.forEach { decryptionStrategy: DecryptionStrategy ->
        val plaintext: String = decryptionStrategy.decrypt(cipherText)
        println("${decryptionStrategy.name} decryption strategy plaintext:\n$plaintext\n")
    }

}
