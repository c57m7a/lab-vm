import java.io.InputStream
import java.util.*

class VMLangParser(private val inputStream: InputStream) {
    fun parse(): ArrayList<Command> {
        val multiLineComment = """(\/\*(?s).*?\*\/)"""
        val singleLineComment = """(\/\/.*)"""
        val delimiter = "($multiLineComment|$singleLineComment| |\\t|\\r|\\n)+"

        val scanner = Scanner(inputStream).useDelimiter(delimiter.toPattern())
        val iterator = scanner.iterator()

        val result = ArrayList<Command>()
        var opIndex = 0
        val labels = HashMap<String, Int>()
        iterator.forEach { s ->
            if (s.endsWith(':')) {
                labels.put(s, opIndex)
                return@forEach
            }
            result.add(when (s) {
                "halt" -> Command.Halt()
                "read" -> Command.Read()
                "write" -> Command.Write()
                "load" -> Command.Load(readValue(iterator.next()))
                "store" -> Command.Store(readValue(iterator.next()) as? Value.Ref ?: throw IllegalArgumentException("Reference expected"))
                "neg" -> Command.Neg()
                "lshift" -> Command.LShift(readValue(iterator.next()))
                "rshift" -> Command.RShift(readValue(iterator.next()))
                "add" -> Command.Add(readValue(iterator.next()))
                "jmp" -> Command.Jump(labels[iterator.next()]!!)
                "jg" -> Command.Jg(labels[iterator.next()]!!)
                else -> throw IllegalArgumentException("Unknown symbol $s")
            })
            ++opIndex
        }
        return result
    }

    private fun readValue(s: String): Value {
        if (s[0] == '[' && s.last() == ']') {
            return Value.Ref(readValue(s.substring(1..s.length - 1)))
        }
        val intValue = s.toIntOrNull() ?: throw NumberFormatException()
        return Value.Number(intValue)
    }
}