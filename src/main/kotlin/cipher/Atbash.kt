package cipher

import alphabet.Alphabet
import alphabet.plaintextAlphabet

object Atbash : PermutedAlphabet {

    override val name: String = "Atbash"

    override val permutedAlphabet: Alphabet = plaintextAlphabet.reversed()

}
