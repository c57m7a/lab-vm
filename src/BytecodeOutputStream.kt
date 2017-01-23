import java.io.DataOutputStream
import java.io.OutputStream

class BytecodeOutputStream(private val dataOutputStream: DataOutputStream) {
    constructor(outputStream: OutputStream) : this(DataOutputStream(outputStream))

    fun write(command: Command) = with(dataOutputStream) {
        when (command) {
            is Command.Halt -> writeInt(0)
            is Command.Read -> writeInt(1)
            is Command.Write -> writeInt(2)
            is Command.Load -> {
                writeInt(3)
                writeValue(command.value)
            }
            is Command.Store -> {
                writeInt(4)
                writeValue(command.ref)
            }
            is Command.Neg -> writeInt(5)
            is Command.LShift -> {
                writeInt(6)
                writeValue(command.value)
            }
            is Command.RShift -> {
                writeInt(7)
                writeValue(command.value)
            }
            is Command.Add -> {
                writeInt(8)
                writeValue(command.value)
            }
            is Command.Jg -> {
                writeInt(9)
                writeInt(command.distance!!)
            }
            is Command.Jump -> {
                writeInt(10)
                writeInt(command.distance!!)
            }
        }
    }

    private fun writeValue(v: Value) = with(dataOutputStream) {
        var ref = v
        var i = 0
        while (ref is Value.Ref) {
            ++i
            ref = ref.v
        }
        ref as Value.Number
        writeInt(i)
        writeInt(ref.intValue)
    }
}