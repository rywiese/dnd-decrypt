package cipher

object Noop : Cipher {

    override val name: String = "NOOP"

    override fun encipher(plainText: String): String = plainText

    override fun decipher(cipherText: String): String = cipherText

}
