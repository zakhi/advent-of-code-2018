package zakhi.advent.helpers


infix fun <F, S> Iterable<F>.cartesianProduct(other: Iterable<S>): Sequence<Pair<F, S>> =
    asSequence().flatMap { first -> other.asSequence().map { second -> first to second } }
