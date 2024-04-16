package cipher

interface Cipher {

    val name: String

    fun encipher(plainText: String): String

    fun decipher(cipherText: String): String

}
