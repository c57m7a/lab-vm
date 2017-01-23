import java.io.DataOutputStream
import java.io.OutputStream

class BytecodeOutputStream(private val dataOutputStream: DataOutputStream) {
    constructor(outputStream: OutputStream) : this(DataOutputStream(outputStream))

    fun write(command: Command) = with(dataOutputStream) {
        when (command) {
            is Command.Halt -> writeByte(0)
            is Command.Read -> writeByte(1)
            is Command.Write -> writeByte(2)
            is Command.Load -> {
                writeByte(3)
                writeValue(command.value)
            }
            is Command.Store -> {
                writeByte(4)
                writeValue(command.ref)
            }
            is Command.Neg -> writeByte(5)
            is Command.LShift -> {
                writeByte(6)
                writeValue(command.value)
            }
            is Command.RShift -> {
                writeByte(7)
                writeValue(command.value)
            }
            is Command.Add -> {
                writeByte(8)
                writeValue(command.value)
            }
            is Command.Jg -> {
                writeByte(9)
                writeInt(command.distance!!)
            }
            is Command.Jump -> {
                writeByte(10)
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
        writeByte(i)
        writeInt(ref.intValue)
    }
}