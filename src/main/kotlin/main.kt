import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

data class InputData(val command: String, val database : String,
                     val key : String?, val value : String?)

fun parseArgs(args: Array<String>) : InputData? {
    return InputData(args.getOrNull(0) ?: return null,
    args.getOrNull(1) ?: return null,
    args.getOrNull(2), args.getOrNull(3))
}

fun newDatabase (name: String) {
    check (!File("database/$name").exists()) { "Database $name already exist"}
    File("database/$name").mkdir()
    File("database/$name/info.txt").createNewFile()
    File("database/$name/0.txt").createNewFile()
    Files.write(Paths.get("database/$name/info.txt"),
        "1".toByteArray(), StandardOpenOption.APPEND)
}

fun deleteDatabase (name: String) {
    check (File("database/$name").exists()) { "Database $name doesn't exist"}
    for (i in 0 until File("database/$name/info.txt").readLines()[0].toInt())
        File("database/$name/$i.txt").delete()
    File("database/$name/info.txt").delete()
    File("database/$name").delete()
}

fun getValue (database: String, key: String?) {
    check(key != null) {"Not enough arguments"}
    check (File("database/$database").exists()) { "Database $database doesn't exist"}
    TODO()
}

fun insertValueByKey (database: String, key: String?, value: String?) {
    check(key != null && value != null) {"Not enough arguments"}
    check (File("database/$database").exists()) { "Database $database doesn't exist"}
    TODO()
}

fun removeKey (database: String, key: String?) {
    check(key != null) {"Not enough arguments"}
    check (File("database/$database").exists()) { "Database $database doesn't exist"}
    TODO()
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
