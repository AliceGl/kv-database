import kotlin.system.measureTimeMillis
import kotlin.test.*

internal class Test1 {

    @Test
    fun parseArgsTest() {
        assertEquals(InputData("newdb", "name", null, null),
            parseArgs(arrayOf("newdb", "name")))
        assertEquals(InputData("get", "db", "key", null),
            parseArgs(arrayOf("get", "db", "key")))
        assertEquals(InputData("newdb", null, null, null),
            parseArgs(arrayOf("newdb")))
        assertEquals(null,
            parseArgs(arrayOf()))
    }

    @Test
    fun databaseTest() {
        val name = "tempDatabaseForTest"
        newDatabase(listOf(name))
        try {
            insertValueByKey(listOf(name, "key1", "value1"))
            assertEquals("value1", getValue(listOf(name, "key1")))
            assertNull(getValue(listOf(name, "key2")))
            insertValueByKey(listOf(name, "key2", "value2"))
            assertEquals("value2", getValue(listOf(name, "key2")))
            removeKey(listOf(name, "key1"))
            assertNull(getValue(listOf(name, "key1")))
            insertValueByKey(listOf(name, "key2", "value3"))
            assertEquals("value3", getValue(listOf(name, "key2")))
            insertValueByKey(listOf(name, "key1", "value4"))
            assertEquals("value4", getValue(listOf(name, "key1")))
            mergeDatabase(listOf(name))
            assertEquals("value4", getValue(listOf(name, "key1")))
            assertEquals("value3", getValue(listOf(name, "key2")))
            clearDatabase(listOf(name))
            assertNull(getValue(listOf(name, "key1")))
        } finally {
            deleteDatabase(listOf(name))
        }
    }

    @Test
    fun databaseEfficiencyTest() {
        val name = "testEfficiencyDatabase"
        newDatabase(listOf(name))
        val elapsedTime = measureTimeMillis {
            for (i in 0..10000)
                insertValueByKey(listOf(name, "key$i", "value$i"))
            for (i in 0..10000)
                getValue(listOf(name, "key$i"))
        }
        deleteDatabase(listOf(name))
        println("$elapsedTime ms") // 7921 ms
    }
}
