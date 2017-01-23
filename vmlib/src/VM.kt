import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream

open class VM(val commands: List<Command>,
              val userInputStream: InputStream,
              val outputStream: OutputStream,
              val debugOutputStream: PrintStream? = null) : Runnable {

    var currentOpIndex = 0; internal set
    var interrupted = false; internal set

    internal val ram: RAM = if (debugOutputStream != null) DebugRAM() else RAM()

    override fun run() {
        do {
            val nextCommand = getCommand(currentOpIndex)
            printDebug(nextCommand)
            nextCommand.run(vm = this)
            ++currentOpIndex
        } while (!interrupted)
    }

    private fun getCommand(index: Int) = commands.getOrNull(index) ?: throw Exception("Can't get command #$index")

    private fun printDebug(command: Command) {
        debugOutputStream?.format("\n#%2d %-14s", currentOpIndex + 1, command.toString())
    }

    private inner class DebugRAM : RAM() {
        override fun put(key: Int, value: Int): Int? {
            debugOutputStream!!.format("%2d -> %-2d", key, value)
            if (containsKey(key))
                debugOutputStream.print("\told: ${this[key]}")
            return map.put(key, value)
        }
    }
}