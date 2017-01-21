import java.io.InputStream
import java.io.OutputStream

class VM(val commands: List<Command>,
         val userInputStream: InputStream,
         val outputStream: OutputStream) : Runnable {

    var currentOpIndex = 0; internal set
    var interrupted = false; internal set
    val ram = RAM()

    override fun run() = try {
        do {
            val nextCommand = commands.getOrNull(currentOpIndex) ?: throw Exception("End of stream (line #$currentOpIndex)")
            //printDebug(nextCommand)
            nextCommand.run(vm = this)
            ++currentOpIndex
        } while (!interrupted)
    } finally {
        userInputStream.close()
        outputStream.close()
    }

    private fun printDebug(nextCommand: Command) {
        val clazz = nextCommand::class.java
        System.out.printf("\n#%2d %-8s", currentOpIndex + 1, clazz.simpleName)
        val params = clazz.declaredFields.joinToString {
            it.isAccessible = true
            val field = it.get(nextCommand)
            field.toString()
        }
        if (params.isNotEmpty()) {
            System.out.printf("%-7s", params)
        }
    }

}