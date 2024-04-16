package cipher

import TabulaRecta
import transformDespaced

class Autokey(val keyword: String) : Cipher {

    override val name: String = "Autokey with keyword $keyword"

    override fun encipher(plainText: String): String = plainText
        .transformDespaced { despacedPlainText: String ->
            encipher(
                plainText = keyword + despacedPlainText,
                index = keyword.length,
                runningCipherText = ""
            )
        }

    private fun encipher(plainText: String, index: Int, runningCipherText: String): String =
        if (index >= plainText.length) {
            runningCipherText
        } else {
            encipher(
                plainText = plainText,
                index = index + 1,
                runningCipherText = runningCipherText + TabulaRecta.encipher(
                    key = plainText[index],
                    plainChar = plainText[index - keyword.length]
                )
            )
        }

    override fun decipher(cipherText: String): String = cipherText
        .transformDespaced { despacedCipherText: String ->
            decipher(
                cipherText = despacedCipherText,
                index = 0,
                runningKeyword = keyword
            )
        }

    private fun decipher(cipherText: String, index: Int, runningKeyword: String): String =
        if (index >= cipherText.length) {
            runningKeyword.removePrefix(keyword)
        } else {
            decipher(
                cipherText = cipherText,
                index = index + 1,
                runningKeyword = runningKeyword + TabulaRecta.decipher(
                    key = runningKeyword[index],
                    cipherChar = cipherText[index]
                )
            )
        }

}
