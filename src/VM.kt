import java.io.DataInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val debugOutputStream = when {
        args.size == 1 -> null
        args.size == 2 && args[1] == "--debug" -> System.out
        else -> usageError()
    }
    val fileName = args[0]

    println("Running '$fileName'")
    val fileInputStream = FileInputStream(fileName)
    val dataInputStream = DataInputStream(fileInputStream)
    val commands = BytecodeInputStream(dataInputStream).commands

    val vm = VM(commands, userInputStream, userOutputStream, debugOutputStream)
    vm.run()
    println("\nProgram exit (line ${vm.currentOpIndex})")
}

val userInputStream = object : InputStream() {
    val scanner = Scanner(System.`in`)
    override fun read(): Int {
        print("Input: ")
        return scanner.nextInt()
    }
}

val userOutputStream = object : OutputStream() {
    override fun write(b: Int) = println("\tOutput: $b")
}

private fun usageError(): Nothing {
    System.err.println("Usage: <input file> [--debug]")
    exitProcess(-1)
}