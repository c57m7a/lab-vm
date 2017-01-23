open class RAM(protected val map: MutableMap<Int, Int>) : MutableMap<Int, Int> by map {
    constructor() : this(HashMap())

    override operator fun get(key: Int) = map[key] ?: throw NoSuchElementException()
}
