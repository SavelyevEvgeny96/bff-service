package automation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AutomationConfigTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `parses required and optional arguments`() {
        val source = tempDir.resolve("source.txt").also { it.writeText("test") }
        val output = tempDir.resolve("result.txt")

        val config = AutomationConfig.fromArgs(
            arrayOf(
                "--source", source.toString(),
                "--output", output.toString(),
                "--switches", "5",
                "--min-delay-ms", "10",
                "--max-delay-ms", "20",
                "--notepad-wait-ms", "30",
            ),
        )

        assertEquals(5, config.switches)
        assertEquals(10, config.minDelayMs)
        assertEquals(20, config.maxDelayMs)
        assertEquals(30, config.notepadWaitMs)
    }

    @Test
    fun `rejects missing source file`() {
        assertFailsWith<IllegalArgumentException> {
            AutomationConfig.fromArgs(
                arrayOf("--source", tempDir.resolve("missing.txt").toString(), "--output", "result.txt"),
            )
        }
    }
}
