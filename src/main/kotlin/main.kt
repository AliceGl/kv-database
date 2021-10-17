import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class InputData(val command: String, val database : String?,
                     val key : String?, val value : String?)

const val MaxSize = (1 shl 16) - 1

fun addLine(filePath: String, line: String) {
    Files.write(Paths.get(filePath), (line + "\n").toByteArray(), StandardOpenOption.APPEND)
}

fun parseArgs(args: Array<String>) : InputData? {
    return InputData(args.getOrNull(0) ?: return null,
    args.getOrNull(1), args.getOrNull(2), args.getOrNull(3))
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

fun newDatabase (parameters: List<String?>) {
    val name = parameters[0]
    require(name != null)
    File(path(name)).mkdir()
    File(getInfoPath(name)).createNewFile()
    File(getFilePath(name, 0)).createNewFile()
    File(getInfoPath(name)).writeText("1")
}

fun deleteDatabase(parameters: List<String?>) {
    val name = parameters[0]
    require(name != null)
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

fun clearDatabase(parameters: List<String?>) {
    val name = parameters[0]
    require(name != null)
    for (i in 0 until getIndex(name))
        File(getFilePath(name, i)).delete()
    File(getInfoPath(name)).delete()
    File(getFilePath(name, 0)).createNewFile()
    File(getInfoPath(name)).writeText("1")
}

fun mergeDatabase(parameters: List<String?>) {
    val name = parameters[0]
    require(name != null)
    val newLines : MutableList<String> = mutableListOf()
    val usedKeys : MutableSet<String> = mutableSetOf()
    val index = getIndex(name)
    for (i in index - 1 downTo 0) {
        File(getFilePath(name, i)).readLines().reversed().forEach { line ->
            val args = line.split(' ')
            if (args[0] == "+" && !usedKeys.contains(args[1]))
                newLines.add(line)
            usedKeys.add(args[1])
        }
        File(getFilePath(name, i)).delete()
    }
    File(getFilePath(name, 0)).createNewFile()
    File(getInfoPath(name)).writeText("1")
    newLines.reversed().forEach { line ->
        writeInDatabase(name, line)
    }
}

fun getValue(parameters: List<String?>) : String? {
    val database = parameters[0]
    val key = parameters.getOrNull(1)
    require(database != null)
    require(key != null)
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
    return value
}

fun insertValueByKey(parameters: List<String?>) {
    val database = parameters[0]
    val key = parameters.getOrNull(1)
    val value = parameters.getOrNull(2)
    require(database != null)
    require(key != null)
    require(value != null)
    writeInDatabase(database, "+ $key $value")
}

fun removeKey(parameters: List<String?>) {
    val database = parameters[0]
    val key = parameters.getOrNull(1)
    require(database != null)
    require(key != null)
    writeInDatabase(database, "- $key")
}

fun printValue(parameters: List<String?>) = println(getValue(parameters) ?: "Key not found")

val commandsMap = mapOf(
    "newdb" to ::newDatabase,
    "deletedb" to ::deleteDatabase,
    "get" to ::printValue,
    "insert" to ::insertValueByKey,
    "remove" to ::removeKey,
    "mergedb" to ::mergeDatabase,
    "cleardb" to ::clearDatabase
)

val numberOfArguments = mapOf(
    "newdb" to 1,
    "deletedb" to 1,
    "get" to 2,
    "insert" to 3,
    "remove" to 2,
    "mergedb" to 1,
    "cleardb" to 1
)

val shouldDatabaseExist = mapOf(
    "newdb" to false,
    "deletedb" to true,
    "get" to true,
    "insert" to true,
    "remove" to true,
    "mergedb" to true,
    "cleardb" to true
)

fun checkArguments(parameters: List<String?>, num: Int, dbExistence: Boolean) {
    for (i in 0 until num)
        if (parameters[i] == null)
            throw NotEnoughArguments()
    val database = parameters[0]
    require(database != null)
    if (dbExistence && !File(path(database)).exists())
        throw DatabaseNotFound(database)
    if (!dbExistence && File(path(database)).exists())
        throw  DatabaseAlreadyExists(database)
}

fun main(args: Array<String>) {
    val inputData = parseArgs(args)
    try {
        if (inputData == null)
            throw NotEnoughArguments()
        commandsMap[inputData.command]?.let {
            val parameters = listOf(inputData.database, inputData.key, inputData.value)
            val num = numberOfArguments[inputData.command]
            val dbExistence = shouldDatabaseExist[inputData.command]
            require(num != null)
            require(dbExistence != null)
            checkArguments(parameters, num, dbExistence)
            it.invoke(parameters)
        } ?:
        throw WrongCommand()
    } catch (exc: Exception) {
        println("Error: " + exc.message)
    }
}
