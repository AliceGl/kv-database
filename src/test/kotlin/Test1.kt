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

    @Test
    fun databaseTest() {
        val name = "tempDatabaseForTest"
        newDatabase(name)
        try {
            assertFails { newDatabase(name) }
            insertValueByKey(name, "key1", "value1")
            assertEquals("value1", getValue(name, "key1"))
            assertFails { getValue(name, "key2") }
            insertValueByKey(name, "key2", "value2")
            assertEquals("value2", getValue(name, "key2"))
            removeKey(name, "key1")
            assertFails { getValue(name, "key1") }
            insertValueByKey(name, "key2", "value3")
            assertEquals("value3", getValue(name, "key2"))
            insertValueByKey(name, "key1", "value4")
            assertEquals("value4", getValue(name, "key1"))
            mergeDatabase(name)
            assertEquals("value4", getValue(name, "key1"))
            assertEquals("value3", getValue(name, "key2"))
            clearDatabase(name)
            assertFails { getValue(name, "key1") }
        } finally {
            deleteDatabase(name)
        }
    }
}
