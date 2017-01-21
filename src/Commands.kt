sealed class Command {
    abstract fun run(vm: VM)

    class Halt : Command() {
        override fun run(vm: VM) = with(vm) {
            interrupted = true
        }
    }

    class Read : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = userInputStream.read()
        }
    }

    class Write : Command() {
        override fun run(vm: VM) = with(vm) {
            outputStream.write(ram[0])
        }
    }

    class Load(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = value.get(ram)
        }
    }

    class Store(val ref: Value.Ref) : Command() {
        override fun run(vm: VM) = with(vm) {
            val index = ref.v.get(ram)
            ram[index] = ram[0]
        }
    }

    class Neg : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = -ram[0]
        }
    }

    class LShift(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] shl value.get(ram)
        }
    }

    class RShift(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] shr value.get(ram)
        }
    }

    class Add(val value: Value) : Command() {
        override fun run(vm: VM) = with(vm) {
            ram[0] = ram[0] + value.get(ram)
        }
    }

    class Jump(val distance: Int) : Command() {
        override fun run(vm: VM) = with(vm) {
            currentOpIndex += distance
        }
    }

    class Jg(val distance: Int) : Command() {
        override fun run(vm: VM) = with(vm) {
            if (ram[0] > 0) {
                currentOpIndex += distance
            }
        }
    }
}