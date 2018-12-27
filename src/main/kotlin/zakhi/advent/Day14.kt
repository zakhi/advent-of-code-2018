package zakhi.advent


fun main() {
    val iterations = 633601
    val firstBoard = RecipeBoard().makeRecipesWhile { it.recipes.size < iterations + 10 }

    val scores = firstBoard.recipes.slice(iterations until iterations + 10)
    println("Part 1: ${scores.joinToString(separator = "")}")

    val iterationDigits = iterations.digits()
    val secondBoard = RecipeBoard().makeRecipesWhile { !it.contains(iterationDigits) }
    val recipesBefore = secondBoard.recipes.joinToString(separator = "").substringBefore(iterations.toString())
    println("Part 2: ${recipesBefore.length}")
}


class RecipeBoard {

    val recipes = mutableListOf(3, 7)

    private var firstElfPosition = 0
    private var secondElfPosition = 1

    fun makeRecipesWhile(condition: (RecipeBoard) -> Boolean): RecipeBoard {
        while (condition(this)) {
            val sum = recipes[firstElfPosition] + recipes[secondElfPosition]
            sum.digits().forEach { recipes.add(it) }

            firstElfPosition = advance(firstElfPosition)
            secondElfPosition = advance(secondElfPosition)
        }

        return this
    }

    fun contains(other: List<Int>): Boolean = when {
        recipes.size < other.size + 1 -> false
        else ->
            recipes.subList(recipes.size - other.size, recipes.size) == other ||
            recipes.subList(recipes.size - other.size - 1, recipes.size - 1) == other
    }

    private fun advance(position: Int): Int = (position + recipes[position] + 1) % recipes.size
}

private fun Int.digits(): List<Int> = toString().map { it.toString().toInt() }
