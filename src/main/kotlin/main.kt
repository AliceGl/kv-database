import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class InputData(val command: String, val database : String,
                     val key : String?, val value : String?)

const val MaxSize = (1 shl 16) - 1

fun addLine(filePath: String, line: String) {
    Files.write(Paths.get(filePath), (line + "\n").toByteArray(), StandardOpenOption.APPEND)
}

fun parseArgs(args: Array<String>) : InputData? {
    return InputData(args.getOrNull(0) ?: return null,
    args.getOrNull(1) ?: return null,
    args.getOrNull(2), args.getOrNull(3))
}

fun path(name: String) = "database/$name"

fun getFilePath(name: String, index: Int) = path(name) + "/$index.txt"

fun getInfoPath(name: String) = path(name) + "/info.txt"

fun getIndex(name: String) = File(getInfoPath(name)).readText().toInt()

fun createFile(name: String) {
    val index = getIndex(name)
    File(getFilePath(name, index)).createNewFile()
    File(getInfoPath(name)).writeText((index + 1).toString())
}

fun newDatabase (name: String) {
    check (!File(path(name)).exists()) { "Database $name already exist"}
    File(path(name)).mkdir()
    File(getInfoPath(name)).createNewFile()
    File(getFilePath(name, 0)).createNewFile()
    File(getInfoPath(name)).writeText("1")
}

fun deleteDatabase(name: String) {
    check (File(path(name)).exists()) { "Database $name doesn't exist"}
    for (i in 0 until getIndex(name))
        File(getFilePath(name, i)).delete()
    File(getInfoPath(name)).delete()
    File(path(name)).delete()
}

fun writeInDatabase(name: String, text: String) {
    val filePath = getFilePath(name, getIndex(name) - 1)
    addLine(filePath, text)
    if (File(filePath).length() > MaxSize)
        createFile(name)
}

fun getValue(database: String, key: String?) {
    check(key != null) {"Not enough arguments"}
    check(File(path(database)).exists()) {"Database $database doesn't exist"}
    var value : String? = null
    val index = getIndex(database)
    loop@ for (i in index - 1 downTo 0) {
        for (line in File(getFilePath(database, i)).readLines().reversed()) {
            val args = line.split(' ')
            if (args[1] != key)
                continue
            if (args[0] == "+")
                value = args[2]
            break@loop
        }
    }
    check(value != null) {"No key $key in database"}
    println(value)
}

fun insertValueByKey(database: String, key: String?, value: String?) {
    check(key != null && value != null) {"Not enough arguments"}
    check(File(path(database)).exists()) { "Database $database doesn't exist"}
    writeInDatabase(database, "+ $key $value")
}

fun removeKey(database: String, key: String?) {
    check(key != null) {"Not enough arguments"}
    check(File(path(database)).exists()) { "Database $database doesn't exist"}
    writeInDatabase(database, "- $key")
}

fun main(args: Array<String>) {
    val inputData = parseArgs(args)
    check(inputData != null) {"No arguments"}
    when (inputData.command) {
        "newdb" -> newDatabase(inputData.database)
        "deletedb" -> deleteDatabase(inputData.database)
        "get" -> getValue(inputData.database, inputData.key)
        "insert" -> insertValueByKey(inputData.database,
            inputData.key, inputData.value)
        "remove" -> removeKey(inputData.database, inputData.key)
        else -> throw Exception("Wrong command")
    }
}
