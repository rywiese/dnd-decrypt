package cipher

import Alphabet
import plaintextAlphabet

object Atbash : PermutedAlphabet {

    override val name: String = "Atbash"

    override val permutedAlphabet: Alphabet = plaintextAlphabet.reversed()

}
