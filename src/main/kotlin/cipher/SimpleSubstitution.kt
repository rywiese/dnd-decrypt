package cipher

import Alphabet
import plaintextAlphabet
import removeDuplicates

class SimpleSubstitution(val keyword: String) : PermutedAlphabet {

    override val name: String = "Simple Substitution with keyword $keyword"

    override val permutedAlphabet: Alphabet = plaintextAlphabet.permuteWithPrefix(keyword.removeDuplicates())

}
