import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.Row3
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe

class CipherTests : FreeSpec() {
    init {
        "Test cipher behavior" - {
            forAll(
                Row3(
                    "MY NAME IS JEFF",
                    Cipher.Noop,
                    "MY NAME IS JEFF"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Cipher.Caesar('D'),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Cipher.Affine(factor = 1, shift = -3),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "AFFINE",
                    Cipher.Affine(factor = 5, shift = 8),
                    "IHHWVC"
                ),
                Row3(
                    "ATTACKING TONIGHT",
                    Cipher.Vigenere("OCULORHINOLARINGOLOGY"),
                    "OVNLQBPVT HZNZOUZ"
                ),
                Row3(
                    "FLEE AT ONCE WE ARE DISCOVERED",
                    Cipher.SimpleSubstitution("ZEBRAS"),
                    "SIAA ZQ LKBA VA ZOA RFPBLUAOAR"
                ),
                Row3(
                    "TUTORIAL",
                    Cipher.Shift(3),
                    "WXWRULDO"
                ),
            ) { plainText: String, cipher: Cipher, cipherText: String ->
                "${cipher.name} should encipher plaintext: $plainText" {
                    val actualCipherText: String = cipher.encipher(plainText)
                    actualCipherText shouldBe cipherText
                }
                "${cipher.name} should decipher ciphertext: $cipherText" {
                    val actualPlaintext: String = cipher.decipher(cipherText)
                    actualPlaintext shouldBe plainText
                }
            }
        }
        "Test cipher properties" - {
            "NOOP" {
                Cipher.Noop.name shouldBe "NOOP"
            }
            "Caesar" {
                val cipher = Cipher.Caesar('D')
                cipher.name shouldBe "Caesar with shift D"
                cipher.shift shouldBe 'D'
            }
            "Affine" {
                val cipher = Cipher.Affine(factor = 5, shift = 8)
                cipher.name shouldBe "Affine with formula `5 * x + 8 mod 26`"
                cipher.factor shouldBe 5
                cipher.shift shouldBe 8
                cipher.inverseFactor shouldBe 21
            }
            "Vigenere" {
                val cipher = Cipher.Vigenere("OCULORHINOLARINGOLOGY")
                cipher.name shouldBe "Vigenère with keyword OCULORHINOLARINGOLOGY"
                cipher.keyword shouldBe "OCULORHINOLARINGOLOGY"
            }
            "Simple Substitution" {
                val cipher = Cipher.SimpleSubstitution("ZEBRAS")
                cipher.name shouldBe "Simple Substitution with keyword ZEBRAS"
                cipher.keyword shouldBe "ZEBRAS"
                cipher.cipherTextAlphabet shouldBe "ZEBRASCDFGHIJKLMNOPQTUVWXY"
            }
            "Shift" {
                val cipher = Cipher.Shift(3)
                cipher.name shouldBe "Shift with shift 3"
                cipher.shift shouldBe 3
                cipher.cipherTextAlphabet shouldBe "DEFGHIJKLMNOPQRSTUVWXYZABC"
            }
        }
    }
}
