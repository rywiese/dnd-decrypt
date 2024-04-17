package cipher

import alphabet.Alphabet
import alphabet.plaintextAlphabet
import words.removeDuplicates

class SimpleSubstitution(val keyword: String) : PermutedAlphabet {

    override val name: String = "Simple Substitution with keyword $keyword"

    override val permutedAlphabet: Alphabet = plaintextAlphabet.permuteWithPrefix(keyword.removeDuplicates())

}
