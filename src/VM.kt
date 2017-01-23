import java.io.InputStream
import java.io.OutputStream

class VM(val commands: List<Command>,
         val userInputStream: InputStream,
         val outputStream: OutputStream) : Runnable {

    var currentOpIndex = 0; internal set
    var interrupted = false; internal set
    val ram = RAM()

    override fun run() {
        do {
            val nextCommand = getCommand(currentOpIndex)
            printDebug(nextCommand)
            nextCommand.run(vm = this)
            ++currentOpIndex
        } while (!interrupted)
    }

    private fun getCommand(index: Int) = commands.getOrNull(index) ?: throw Exception("Can't get command #$index")

    private fun printDebug(nextCommand: Command) {
        System.out.printf("\n#%2d %-14s", currentOpIndex + 1, nextCommand.toString())
    }
}