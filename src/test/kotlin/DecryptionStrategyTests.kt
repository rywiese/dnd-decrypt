import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.Row1
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe

class DecryptionStrategyTests : FreeSpec() {
    init {
        "Test decipher logic" - {
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
                Row3(
                    "ATTACKING TONIGHT",
                    DecryptionStrategy.ReverseVigenere("OCULORHINOLARINGOLOGY"),
                    "OVNLQBPVT HZNZOUZ"
                ),
                Row3(
                    "SIAA ZQ LKBA VA ZOA RFPBLUAOAR",
                    DecryptionStrategy.SimpleSubstitution("ZEBRAS"),
                    "FLEE AT ONCE WE ARE DISCOVERED"
                ),
                Row3(
                    "FLEE AT ONCE WE ARE DISCOVERED",
                    DecryptionStrategy.ReverseSimpleSubstitution("ZEBRAS"),
                    "SIAA ZQ LKBA VA ZOA RFPBLUAOAR"
                ),
            ) { cipherText: String, decryptionStrategy: DecryptionStrategy, expectedPlaintext: String ->
                "${decryptionStrategy.name} should decrypt ciphertext: $cipherText" {
                    val plaintext: String = decryptionStrategy.decipher(cipherText)
                    plaintext shouldBe expectedPlaintext
                }
            }
        }
        "Test class properties" - {
            "NOOP" {
                DecryptionStrategy.Noop.name shouldBe "NOOP"
            }
            "Caesar" {
                forAll(
                    Row1(DecryptionStrategy.Caesar('D')),
                    Row1(DecryptionStrategy.Caesar(3))
                ) { decryptionStrategy: DecryptionStrategy.Caesar ->
                    decryptionStrategy.name shouldBe "Caesar with shift 3"
                    decryptionStrategy.shift shouldBe 3
                }
            }
            "Reverse Caesar" {
                forAll(
                    Row1(DecryptionStrategy.ReverseCaesar('D')),
                    Row1(DecryptionStrategy.ReverseCaesar(3))
                ) { decryptionStrategy: DecryptionStrategy.ReverseCaesar ->
                    decryptionStrategy.name shouldBe "Reverse Caesar with shift 3"
                    decryptionStrategy.shift shouldBe 3
                }
            }
            "Vigenere" {
                val decryptionStrategy = DecryptionStrategy.Vigenere("OCULORHINOLARINGOLOGY")
                decryptionStrategy.name shouldBe "Vigenère with keyword OCULORHINOLARINGOLOGY"
                decryptionStrategy.keyword shouldBe "OCULORHINOLARINGOLOGY"
            }
            "Reverse Vigenere" {
                val decryptionStrategy = DecryptionStrategy.ReverseVigenere("OCULORHINOLARINGOLOGY")
                decryptionStrategy.name shouldBe "Reverse Vigenère with keyword OCULORHINOLARINGOLOGY"
                decryptionStrategy.keyword shouldBe "OCULORHINOLARINGOLOGY"
            }
            "Simple Substitution" {
                val decryptionStrategy = DecryptionStrategy.SimpleSubstitution("ZEBRAS")
                decryptionStrategy.name shouldBe "Simple Substitution with keyword ZEBRAS"
                decryptionStrategy.keyword shouldBe "ZEBRAS"
                decryptionStrategy.cipherTextAlphabet shouldBe "ZEBRASCDFGHIJKLMNOPQTUVWXY"
            }
            "Reverse Simple Substitution" {
                val decryptionStrategy = DecryptionStrategy.ReverseSimpleSubstitution("ZEBRAS")
                decryptionStrategy.name shouldBe "Reverse Simple Substitution with keyword ZEBRAS"
                decryptionStrategy.keyword shouldBe "ZEBRAS"
                decryptionStrategy.cipherTextAlphabet shouldBe "ZEBRASCDFGHIJKLMNOPQTUVWXY"
            }
        }
    }
}
