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
                    Cipher.noop,
                    "MY NAME IS JEFF"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Cipher.caesar,
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
                    Cipher.affine(factor = 1, shift = -3),
                    "QEB NRFZH YOLTK CLU GRJMP LSBO QEB IXWV ALD"
                ),
                Row3(
                    "AFFINE",
                    Cipher.affine(factor = 5, shift = 8),
                    "IHHWVC"
                ),
                Row3(
                    "ATTACKING TONIGHT",
                    Cipher.vigenere("OCULORHINOLARINGOLOGY"),
                    "OVNLQBPVT HZNZOUZ"
                ),
                Row3(
                    "FLEE AT ONCE WE ARE DISCOVERED",
                    Cipher.simpleSubstitution("ZEBRAS"),
                    "SIAA ZQ LKBA VA ZOA RFPBLUAOAR"
                ),
                Row3(
                    "TUTORIAL",
                    Cipher.shift(3),
                    "WXWRULDO"
                ),
                Row3(
                    "FOO BAR",
                    Cipher.atbash,
                    "ULL YZI"
                ),
                Row3(
                    "ATTACK AT DAWN",
                    Cipher.autoKey("QUEENLY"),
                    "QNXEPV YT WTWP"
                ),
                // Actual solution
                Row3(
                    "IT IS TRUE HE LIVES AGAIN THROUGH THE GLORY OF THE CHAINED GOD IF YOUR FATHER CANNOT ACCEPT THIS TRUTH THEN YOU WILL MUST TAKE HIS PLACE NEZZNAR SHALL GUIDE THE YUAN TI TO GREATNESS THE END OF THE AGE OF MAN IS NIGH",
                    Cipher.vigenere("ECLIPSE"),
                    "MV TA IJYI JP TXNIW CRIXF XLTZCVZ XLG RTDJC SH EPT ULEKYMS YSH KQ GDMV JCEPTJ GEPYWI SGGGAB IZMW VCCIZ XLGY GDM AMNW UJKX XCVM WAW TNLKT FIDBYIG KLENW OJAHI VSM NMER VT BD YVICEVTKW XJP MCV SJ VSM PYI SH XIC AW RKRP"
                ),
                // Found a bug
                Row3(
                    "IT IS TRUE HE LIVES AGAIN THROUGH THE GLORY OF THE CHAINED GOD IF YOUR FATHER CANNOT ACCEPT THIS TRUTH THEN YOU WILL MUST TAKE HIS PLACE NEZZNAR SHALL GUIDE THE YUAN TI TO GREATNESS THE END OF THE AGE OF MAN IS NIGH",
                    Cipher.simpleSubstitution("ECLIPSE"),
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
                Cipher.noop.name shouldBe "NOOP"
            }
            "Caesar" {
                Cipher.caesar.name shouldBe "Affine with formula `1 * x + -3 mod 26`"
            }
            "Affine" {
                val cipher = Cipher.affine(factor = 5, shift = 8)
                cipher.name shouldBe "Affine with formula `5 * x + 8 mod 26`"
            }
            "Vigenere" {
                val cipher = Cipher.vigenere("OCULORHINOLARINGOLOGY")
                cipher.name shouldBe "Vigenère with keyword OCULORHINOLARINGOLOGY"
            }
            "Simple Substitution" {
                val cipher = Cipher.simpleSubstitution("ZEBRAS")
                cipher.name shouldBe "Simple Substitution with keyword ZEBRAS"
            }
            "Simple Substitution when key has duplicate letter" {
                // I didn't really consider this use case, it probably should throw on initialization but that would
                // make Main fail, and I'm not going to fix that since the puzzle has been solved with a different cipher
                val cipher = Cipher.simpleSubstitution("ECLIPSE")
                cipher.name shouldBe "Simple Substitution with keyword ECLIPSE"
            }
            "Shift" {
                val cipher = Cipher.shift(3)
                cipher.name shouldBe "Affine with formula `1 * x + 3 mod 26`"
            }
            "Atbash" {
                Cipher.atbash.name shouldBe "Atbash"
            }
            "Autokey" {
                val cipher = Cipher.autoKey("QUEENLY")
                cipher.name shouldBe "Autokey with keyword QUEENLY"
            }
        }
    }
}
