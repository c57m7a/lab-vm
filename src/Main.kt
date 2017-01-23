import Command.*
import Value.Number
import Value.Ref
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val fileName = args.getOrNull(0) ?: usageError()
    val inputStream = FileInputStream(fileName)
    val commands = VMLangParser(inputStream).commands
    inputStream.close()

    val byteArrayOutputStream = ByteArrayOutputStream()
    BytecodeOutputStream(byteArrayOutputStream).apply { commands.forEach(this::write) }

    val inputStream1 = byteArrayOutputStream.toByteArray().inputStream()
    val commands2 = BytecodeInputStream(inputStream1).commands

    val vm = VM(commands2, userInputStream, outputStream)
    vm.run()
}

val fibonacci = listOf(
        //<editor-fold desc="">
        //Read(),
        Load(Number(10)),
        Add(Number(-1)),
        Store(Ref(-1)),
        Load(Number(0)),
        Store(Ref(-2)),
        Load(Number(1)),
        Store(Ref(-3)),
        Load(Ref(-2)), //loop
        Store(Ref(-4)),
        Load(Ref(-3)),
        Store(Ref(-2)),
        Add(Ref(-4)),
        Store(Ref(-3)),
        Load(Ref(-1)),
        Add(Number(-1)),
        Store(Ref(-1)),
        Jg(-10), //loop
        Load(Ref(-3)),
        Write(),
        Halt()
        //</editor-fold>
)

val sort = listOf(
        //<editor-fold desc="">
        Read(),
        Store(Ref(-1)),
        Store(Ref(-2)),
        Read(), //readloop
        Store(Ref(Ref(-2))),
        Load(Ref(-2)),
        Add(Number(-1)),
        Store(Ref(-2)),
        Jg(-6),
        Load(Ref(-1)), //iloop
        Add(Number(-1)),
        Store(Ref(-2)),
        Load(Ref(Ref(-2))), //jloop
        Neg(),
        Add(Ref(Ref(-1))),
        Jg(6),
        Load(Ref(Ref(-2))),
        Store(Ref(-3)),
        Load(Ref(Ref(-1))),
        Store(Ref(Ref(-2))),
        Load(Ref(-3)),
        Store(Ref(Ref(-1))),
        Load(Ref(-2)), //endif
        Add(Number(-1)),
        Store(Ref(-2)),
        Jg(-14), //jloop
        Load(Ref(Ref(-1))),
        Write(),
        Load(Ref(-1)),
        Add(Number(-1)),
        Store(Ref(-1)),
        Jg(-23),
        Halt()
        //</editor-fold>
)

private object userInputStream : InputStream() {
    val scanner = Scanner(System.`in`)
    override fun read(): Int {
        print("Input: ")
        return scanner.nextInt()
    }
}

private object outputStream : OutputStream() {
    override fun write(b: Int) = println("\tOutput: $b")
}

fun usageError(): Nothing {
    System.err.print("File name missing\n")
    exitProcess(-1)
}