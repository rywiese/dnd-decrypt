import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.Row1
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe

class DecryptionStrategyTests : FreeSpec() {
    init {
        "Tests" - {
            forAll(
                Row3(
                    "MY NAME IS JEFF",
                    DecryptionStrategy.Noop,
                    "MY NAME IS JEFF"
                ),
                Row3(
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD",
                    DecryptionStrategy.Caesar('D'),
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    DecryptionStrategy.ReverseCaesar('D'),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD",
                    DecryptionStrategy.Caesar(3),
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    DecryptionStrategy.ReverseCaesar(3),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "OVNLQBPVT HZNZOUZ",
                    DecryptionStrategy.Vigenere("OCULORHINOLARINGOLOGY"),
                    "ATTACKING TONIGHT"
                ),
                Row3(
                    "ATTACKING TONIGHT",
                    DecryptionStrategy.ReverseVigenere("OCULORHINOLARINGOLOGY"),
                    "OVNLQBPVT HZNZOUZ"
                ),
            ) { cipherText: String, decryptionStrategy: DecryptionStrategy, expectedPlaintext: String ->
                "${decryptionStrategy.name} should decrypt ciphertext: $cipherText" {
                    val plaintext: String = decryptionStrategy.decrypt(cipherText)
                    plaintext shouldBe expectedPlaintext
                }
            }
        }
    }
}