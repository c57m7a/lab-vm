import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    require(args.size in 1..2) { usageError() }

    val inputPath = args[0]
    val outputPath = args.getOrNull(1) ?: inputPath.replaceAfterLast(
            delimiter = '.', replacement = "vmc", missingDelimiterValue = ".vmc"
    )
    println("Compiling '$inputPath' -> '$outputPath'")
    val inputStream = FileInputStream(inputPath)
    val outputStream = FileOutputStream(outputPath)

    try {
        val parser = VMLangParser(inputStream)
        val commands = parser.commands
        val bytecodeOutputStream = BytecodeOutputStream(outputStream)
        commands.forEachIndexed { i, command ->
            println("#$i writing command '$command'")
            bytecodeOutputStream.write(command)
        }
        println("Done.")
    } finally {
        inputStream.close()
        outputStream.close()
    }
}

private fun usageError(): Nothing {
    System.err.println("Usage: compile <input file> [output file]")
    exitProcess(-1)
}