import kotlin.test.*

internal class Test1 {

    @Test
    fun parseArgsTest() {
        assertEquals(InputData("newdb", "name", null, null),
            parseArgs(arrayOf("newdb", "name")))
        assertEquals(InputData("get", "db", "key", null),
            parseArgs(arrayOf("get", "db", "key")))
        assertEquals(null,
            parseArgs(arrayOf("newdb")))
    }
}
