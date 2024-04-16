package cipher

// Note that this could also be a PermutedAlphabet
class Shift(val shift: Int) : Substitution by Affine(
    factor = 1,
    shift = shift
) {

    override val name: String = "Shift with shift $shift"

}
