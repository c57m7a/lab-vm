import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess

private const val singleLineComment = """(\/\/.*)"""
private const val wordDelimiter = "($singleLineComment| |\\t|\\r|\\n)+"

class VMLangParser(inputStream: InputStream) {
    val currentPosition = Position("", 0, 0)
    internal var opIndex = 0
        private set

    data class Position(var line: String, var lineIndex: Int, var charIndex: Int) {

        override fun toString() = buildString {
            appendln("line $lineIndex:")
            appendln(line)
            append(CharArray(charIndex) { ' ' })
            append('^')
        }
    }

    private data class Jump(val jump: Command.Jump, val opIndex: Int, val label: String, val position: Position)

    private val notInitializedJumps = LinkedList<Jump>()

    val words = buildIterator {
        Scanner(inputStream).useDelimiter("\n").forEach { line ->
            ++(currentPosition.lineIndex)
            currentPosition.line = line
            val wordsScanner = Scanner(line).useDelimiter(wordDelimiter)
            wordsScanner.forEach { word ->
                currentPosition.charIndex = wordsScanner.match().end()
                yield(word.toLowerCase())
            }
        }
    }

    val commands: List<Command> by lazy {
        val result = ArrayList<Command>()
        val labels = HashMap<String, Int>()
        for (word in words) {
            if (word.endsWith(':')) {
                labels.put(word.dropLast(1), opIndex)
                continue
            }
            result.add(when (word) {
                "halt" -> Command.Halt()
                "read" -> Command.Read()
                "write" -> Command.Write()
                "load" -> Command.Load(readValue())
                "store" -> Command.Store(readValue() as? Value.Ref ?: parseException("Reference expected"))
                "neg" -> Command.Neg()
                "lshift" -> Command.LShift(readValue())
                "rshift" -> Command.RShift(readValue())
                "add" -> Command.Add(readValue())
                "jg" -> initJump(Command.Jg(), labels)
                "jump" -> initJump(Command.Jump(), labels)
                else -> parseException("Unknown symbol $word")
            })
            ++opIndex
        }

        checkJumps(labels)
        return@lazy result
    }

    private fun readValue(): Value {
        if (!words.hasNext()) {
            parseException("Value expected")
        }
        return parseValue(words.next())
    }

    private fun initJump(jump: Command.Jump, labels: HashMap<String, Int>): Command.Jump {
        val label = words.next()
        val targetLineIndex = labels[label]
        if (targetLineIndex != null) {
            jump.distance = targetLineIndex - opIndex - 1
        } else {
            notInitializedJumps.add(Jump(jump, opIndex, label, currentPosition.copy()))
        }
        return jump
    }

    private fun checkJumps(labels: HashMap<String, Int>) {
        val filtered = notInitializedJumps.filter { (jump, opIndex, label) ->
            val targetLineIndex = labels[label]
            if (targetLineIndex != null) {
                jump.distance = targetLineIndex - opIndex - 1
            }
            return@filter targetLineIndex == null
        }
        if (filtered.isNotEmpty()) {
            System.err.println(filtered.joinToString(prefix = "Unknown labels:\n", separator = "\n") {
                it.position.toString()
            })
            exitProcess(-1)
        }
    }

    private fun parseValue(s: String): Value {
        if (s.isEmpty())
            parseException("Value expected")
        if (s[0] == '[' && s.last() == ']') {
            ++(currentPosition.charIndex)
            val substring = s.substring(1..s.length - 2)
            return Value.Ref(parseValue(substring))
        }
        val intValue = s.toIntOrNull() ?: parseException("Number expected")
        return Value.Number(intValue)
    }

    private fun parseException(msg: String): Nothing {
        System.err.println(msg)
        System.err.println(currentPosition)
        exitProcess(-1)
    }
}