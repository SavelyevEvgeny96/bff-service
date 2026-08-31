package automation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AutomationConfigTest {
    @TempDir lateinit var tempDir: Path

    @Test
    fun `parses paths and timing settings`() {
        val source = tempDir.resolve("source.txt").also { it.writeText("test") }
        val config = AutomationConfig.fromArgs(arrayOf(
            "--source", source.toString(), "--output", tempDir.resolve("result.txt").toString(),
            "--switches", "5", "--min-switch-delay-ms", "10", "--max-switch-delay-ms", "20",
            "--min-duration-minutes", "180", "--max-duration-minutes", "300", "--typo-percent", "3.5",
        ))

        assertEquals(5, config.switches)
        assertEquals(10, config.minSwitchDelayMs)
        assertEquals(20, config.maxSwitchDelayMs)
        assertEquals(180, config.minDurationMinutes)
        assertEquals(300, config.maxDurationMinutes)
        assertEquals(3.5, config.typoPercent)
    }

    @Test
    fun `rejects an empty source`() {
        val source = tempDir.resolve("empty.txt").also { it.writeText("") }
        assertFailsWith<IllegalArgumentException> {
            AutomationConfig.fromArgs(arrayOf("--source", source.toString(), "--output", "result.txt"))
        }
    }
}
