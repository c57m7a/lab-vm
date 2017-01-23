sealed class Value {
    abstract fun get(ram: RAM): Int

    class Number(val intValue: Int) : Value() {
        override fun get(ram: RAM) = intValue
        override fun toString() = intValue.toString()
    }

    class Ref(val v: Value) : Value() {
        constructor(intValue: Int) : this(Number(intValue))

        override fun get(ram: RAM) = ram[v.get(ram)]
        override fun toString() = "[$v]"
    }
}