package automation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotepadAutomationTest {
    @TempDir lateinit var tempDir: Path

    @Test
    fun `types one character at a time and preserves requested duration`() {
        val source = tempDir.resolve("source.txt").also { it.writeText("тест") }
        val output = tempDir.resolve("output.txt")
        val desktop = RecordingDesktopController()
        val config = AutomationConfig(
            source, output, switches = 1, minSwitchDelayMs = 10, maxSwitchDelayMs = 10,
            notepadWaitMs = 20, minDurationMinutes = 1, maxDurationMinutes = 1,
            typoPercent = 0.0, saveEveryCharacters = 2,
        )

        NotepadAutomation(desktop, Random(1)).run(config)

        assertEquals(listOf("т", "е", "с", "т"), desktop.typed)
        assertEquals(3, desktop.saves)
        assertTrue(desktop.waits.sum() >= 60_000 + 10 + 20 + 500)
        assertEquals(1, desktop.switches)
        assertTrue(desktop.cleared && desktop.closed)
    }

    private class RecordingDesktopController : DesktopController {
        val typed = mutableListOf<String>()
        val waits = mutableListOf<Long>()
        var switches = 0
        var saves = 0
        var cleared = false
        var closed = false
        override fun switchWindow() { switches++ }
        override fun openNotepad(file: Path) = Unit
        override fun clearDocument() { cleared = true }
        override fun typeText(text: String) { typed += text }
        override fun backspace() = Unit
        override fun save() { saves++ }
        override fun closeWindow() { closed = true }
        override fun waitFor(milliseconds: Long) { waits += milliseconds }
    }
}
