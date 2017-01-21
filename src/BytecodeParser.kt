import java.io.EOFException
import java.io.InputStream

class BytecodeParser(private val inputStream: InputStream) {
    fun parse(): List<Command> {
        val commands = generateSequence(this::readCommand).toList()
        return commands
    }

    private fun readCommand(): Command? = try {
        val opcode = inputStream.read()
        when (opcode) {
            0 -> Command.Halt()
            1 -> Command.Read()
            2 -> Command.Write()
            3 -> Command.Load(readValue())
            4 -> Command.Store(readValue() as? Value.Ref ?: throw IllegalArgumentException("Reference expected"))
            5 -> Command.Neg()
            6 -> Command.LShift(readValue())
            7 -> Command.RShift(readValue())
            8 -> Command.Add(readValue())
            9 -> Command.Jump(inputStream.read())
            10 -> Command.Jg(inputStream.read())
            else -> throw IllegalArgumentException("Unknown opcode $opcode")
        }
    } catch (e: EOFException) {
        null
    }

    private fun readValue(): Value {
        val type = inputStream.read()
        val number = Value.Number(inputStream.read())
        return when (type) {
            0 -> number
            1 -> Value.Ref(number)
            2 -> Value.Ref(Value.Ref(number))
            else -> throw IllegalArgumentException("Invalid param type")
        }
    }
}