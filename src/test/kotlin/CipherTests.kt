import alphabet.Alphabet
import cipher.Affine
import cipher.Atbash
import cipher.Autokey
import cipher.Caesar
import cipher.Cipher
import cipher.Noop
import cipher.Shift
import cipher.SimpleSubstitution
import cipher.Vigenere
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
                    Noop,
                    "MY NAME IS JEFF"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Caesar,
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Affine(factor = 1, shift = -3),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "AFFINE",
                    Affine(factor = 5, shift = 8),
                    "IHHWVC"
                ),
                Row3(
                    "ATTACKING TONIGHT",
                    Vigenere("OCULORHINOLARINGOLOGY"),
                    "OVNLQBPVT HZNZOUZ"
                ),
                Row3(
                    "FLEE AT ONCE WE ARE DISCOVERED",
                    SimpleSubstitution("ZEBRAS"),
                    "SIAA ZQ LKBA VA ZOA RFPBLUAOAR"
                ),
                Row3(
                    "TUTORIAL",
                    Shift(3),
                    "WXWRULDO"
                ),
                Row3(
                    "FOO BAR",
                    Atbash,
                    "ULL YZI"
                ),
                Row3(
                    "ATTACK AT DAWN",
                    Autokey("QUEENLY"),
                    "QNXEPV YT WTWP"
                ),
                // Actual solution
                Row3(
                    "IT IS TRUE HE LIVES AGAIN THROUGH THE GLORY OF THE CHAINED GOD IF YOUR FATHER CANNOT ACCEPT THIS TRUTH THEN YOU WILL MUST TAKE HIS PLACE NEZZNAR SHALL GUIDE THE YUAN TI TO GREATNESS THE END OF THE AGE OF MAN IS NIGH",
                    Vigenere("ECLIPSE"),
                    "MV TA IJYI JP TXNIW CRIXF XLTZCVZ XLG RTDJC SH EPT ULEKYMS YSH KQ GDMV JCEPTJ GEPYWI SGGGAB IZMW VCCIZ XLGY GDM AMNW UJKX XCVM WAW TNLKT FIDBYIG KLENW OJAHI VSM NMER VT BD YVICEVTKW XJP MCV SJ VSM PYI SH XIC AW RKRP"
                ),
                // Found a bug
                Row3(
                    "IT IS TRUE HE LIVES AGAIN THROUGH THE GLORY OF THE CHAINED GOD IF YOUR FATHER CANNOT ACCEPT THIS TRUTH THEN YOU WILL MUST TAKE HIS PLACE NEZZNAR SHALL GUIDE THE YUAN TI TO GREATNESS THE END OF THE AGE OF MAN IS NIGH",
                    SimpleSubstitution("ECLIPSE"),
                    "DT DR TQUP BP HDVPR EAEDK TBQMUAB TBP AHMQY MS TBP LBEDKPI AMI DS YMUQ SETBPQ LEKKMT ELLPNT TBDR TQUTB TBPK YMU WDHH JURT TEGP BDR NHELP KPZZKEQ RBEHH AUDIP TBP YUEK TD TM AQPETKPRR TBP PKI MS TBP EAP MS JEK DR KDAB"
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
                Noop.name shouldBe "NOOP"
            }
            "Caesar" {
                val cipher = Caesar
                cipher.name shouldBe "Caesar"
            }
            "Affine" {
                val cipher = Affine(factor = 5, shift = 8)
                cipher.name shouldBe "Affine with formula `5 * x + 8 mod 26`"
                cipher.factor shouldBe 5
                cipher.shift shouldBe 8
                cipher.inverseFactor shouldBe 21
            }
            "Vigenere" {
                val cipher = Vigenere("OCULORHINOLARINGOLOGY")
                cipher.name shouldBe "Vigenère with keyword OCULORHINOLARINGOLOGY"
                cipher.keyword shouldBe "OCULORHINOLARINGOLOGY"
            }
            "Simple Substitution" {
                val cipher = SimpleSubstitution("ZEBRAS")
                cipher.name shouldBe "Simple Substitution with keyword ZEBRAS"
                cipher.keyword shouldBe "ZEBRAS"
                cipher.permutedAlphabet shouldBe Alphabet("ZEBRASCDFGHIJKLMNOPQTUVWXY")
            }
            "Simple Substitution when key has duplicate letter" {
                // I didn't really consider this use case, it probably should throw on initialization but that would
                // make Main fail, and I'm not going to fix that since the puzzle has been solved with a different cipher
                val cipher = SimpleSubstitution("ECLIPSE")
                cipher.name shouldBe "Simple Substitution with keyword ECLIPSE"
                cipher.keyword shouldBe "ECLIPSE"
                cipher.permutedAlphabet shouldBe Alphabet("ECLIPSABDFGHJKMNOQRTUVWXYZ")
            }
            "Shift" {
                val cipher = Shift(3)
                cipher.name shouldBe "Shift with shift 3"
                cipher.shift shouldBe 3
            }
            "Atbash" {
                Atbash.name shouldBe "Atbash"
                Atbash.permutedAlphabet shouldBe Alphabet("ZYXWVUTSRQPONMLKJIHGFEDCBA")
            }
            "Autokey" {
                val cipher = Autokey("QUEENLY")
                cipher.name shouldBe "Autokey with keyword QUEENLY"
                cipher.keyword shouldBe "QUEENLY"
            }
        }
    }
}
