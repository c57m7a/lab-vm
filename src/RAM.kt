class RAM(val map: MutableMap<Int, Int>) : MutableMap<Int, Int> by map {
    constructor() : this(HashMap())

    override operator fun get(key: Int) = map[key] ?: throw NoSuchElementException()

    override fun put(key: Int, value: Int): Int? {
        /*print(kvToString(key, value))
        if (containsKey(key))
            print("\told: ${this[key]}")*/
        return map.put(key, value)
    }

}

fun kvToString(key: Int, value: Int) = String.format("%2d -> %-2d", key, value)