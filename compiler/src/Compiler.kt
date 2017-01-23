import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    require(args.size in 1..2) { usageError() }

    val inputPath = args[0]
    val outputPath = args.getOrNull(1) ?: inputPath.replaceAfterLast(
            delimiter = '.', replacement = "vmc", missingDelimiterValue = ".vmc"
    )
    val inputStream = FileInputStream(inputPath)
    val outputStream = FileOutputStream(outputPath)

    try {
        val parser = VMLangParser(inputStream)
        val commands = parser.commands
        val bytecodeOutputStream = BytecodeOutputStream(outputStream)
        commands.forEach(bytecodeOutputStream::write)
        println("Done.")
    } finally {
        inputStream.close()
        outputStream.close()
    }
}

private fun usageError(): Nothing {
    System.err.print("Usage: compile <input file> [output file]")
    exitProcess(-1)

}