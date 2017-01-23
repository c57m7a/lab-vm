import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream

class BytecodeInputStream(private val dataInputStream: DataInputStream) {
    constructor(inputStream: InputStream) : this(DataInputStream(inputStream))

    val commands = generateSequence(this::readCommand).toList()

    private fun readCommand(): Command? = try {
        val opcode = dataInputStream.readByte().toInt()
        val command = when (opcode) {
            -1 -> null
            0 -> Command.Halt()
            1 -> Command.Read()
            2 -> Command.Write()
            3 -> Command.Load(readValue())
            4 -> Command.Store(readValue() as? Value.Ref ?: throw IllegalArgumentException("Reference expected"))
            5 -> Command.Neg()
            6 -> Command.LShift(readValue())
            7 -> Command.RShift(readValue())
            8 -> Command.Add(readValue())
            9 -> Command.Jg(dataInputStream.readInt())
            10 -> Command.Jump(dataInputStream.readInt())
            else -> throw IllegalArgumentException("Unknown opcode $opcode")
        }
        command
    } catch (e: EOFException) {
        null
    }

    private fun readValue(): Value {
        val type = dataInputStream.readByte().toInt()
        val number = Value.Number(dataInputStream.readInt())
        return when (type) {
            0 -> number
            1 -> Value.Ref(number)
            2 -> Value.Ref(Value.Ref(number))
            else -> throw IllegalArgumentException("Invalid param type $type")
        }
    }
}