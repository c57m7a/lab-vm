sealed class Command {
    abstract fun run(vm: VM)

    class Halt : Command() {
        override fun run(vm: VM) = with(vm) {
            interrupted = true
        }

        override fun toString() = "Halt"
    }

    class Read : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = userInputStream.read()
        }

        override fun toString() = "Read"
    }

    class Write : Command() {
        override fun run(vm: VM) = with(vm) {
            outputStream.write(ram[0])
        }

        override fun toString() = "Write"
    }

    class Load(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = value.get(ram)
        }

        override fun toString() = "Load $value"
    }

    class Store(val ref: Value.Ref) : Command() {
        override fun run(vm: VM) = with(vm) {
            val index = ref.v.get(ram)
            ram[index] = ram[0]
        }

        override fun toString() = "Store $ref"
    }

    class Neg : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = -ram[0]
        }

        override fun toString() = "Neg"
    }

    class LShift(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] shl value.get(ram)
        }

        override fun toString() = "LShift $value"
    }

    class RShift(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] shr value.get(ram)
        }

        override fun toString() = "RShift $value"
    }

    class Add(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] + value.get(ram)
        }

        override fun toString() = "Add $value"
    }

    class Jg(distance: Int? = null) : Jump(distance) {
        override fun run(vm: VM) {
            if (vm.ram[0] > 0) {
                super.run(vm)
            }
        }

        override fun toString() = "Jg $distance"
    }

    open class Jump(open var distance: Int? = null) : Command() {
        override fun run(vm: VM) = with(vm) {
            currentOpIndex += distance ?: throw RuntimeException("jump distance is null")
        }

        override fun toString() = "Jump $distance"
    }
}