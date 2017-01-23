import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess

private const val singleLineComment = """(\/\/.*)"""
private const val wordDelimiter = "($singleLineComment| |\\t|\\r|\\n)+"

class VMLangParser(inputStream: InputStream) {
    val currentPosition = Position("", 0, 0)
    var opIndex = 0

    data class Position(var line: String, var lineIndex: Int, var charIndex: Int) {

        override fun toString() = buildString {
            appendln("line $lineIndex:")
            appendln(line)
            append(CharArray(charIndex) { ' ' })
            append('^')
        }
    }

    private val notInitializedJumps = LinkedList<Jump>()

    private data class Jump(val jump: Command.Jump, val opIndex: Int, val label: String, val position: Position)

    val words = buildIterator {
        Scanner(inputStream).useDelimiter("\n").forEach { rawLine ->
            ++(currentPosition.lineIndex)
            currentPosition.line = rawLine.takeWhile { it != '\'' }
            val wordsScanner = Scanner(currentPosition.line).useDelimiter(wordDelimiter)
            wordsScanner.forEach { s ->
                currentPosition.charIndex = wordsScanner.match().end()
                yield(s)
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
                "jump" -> initJump(Command.Jump(), labels)
                "jg" -> initJump(Command.Jg(), labels)
                else -> parseException("Unknown symbol $word")
            })
            ++opIndex
        }

        checkJumps(labels)
        return@lazy result
    }

    private fun checkJumps(labels: HashMap<String, Int>) {
        val filtered = notInitializedJumps.filter { (jump, opIndex, label) ->
            val targetLineIndex = labels[label]
            if (targetLineIndex != null) {
                jump.distance = targetLineIndex - opIndex - 1
            }
            targetLineIndex == null
        }
        if (filtered.isNotEmpty()) {
            System.err.println(filtered.joinToString(prefix = "Unknown labels:\n", separator = "\n") {
                it.position.toString()
            })
            exitProcess(-1)
        }
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
        if (targetLineIndex == null) {
            notInitializedJumps.add(Jump(jump, opIndex, label, currentPosition.copy()))
        } else {
            jump.distance = targetLineIndex - opIndex - 1
        }
        return jump
    }

    private fun parseException(msg: String): Nothing {
        with(System.err) {
            println(msg)
            println(currentPosition)
        }
        exitProcess(-1)
    }

    private fun parseValue(s: String): Value {
        if (s[0] == '[' && s.last() == ']') {
            ++(currentPosition.charIndex)
            val substring = s.substring(1..s.length - 2)
            return Value.Ref(parseValue(substring))
        }
        val intValue = s.toIntOrNull() ?: parseException("Number expected")
        return Value.Number(intValue)
    }
}